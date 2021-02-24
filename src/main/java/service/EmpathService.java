package service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import client.DatabaseClient;
import client.FaceClient;
import client.FileClient;
import model.Song;
import model.Song.Emotion;
import model.exceptions.DatabaseException;
import model.exceptions.InvalidSongException;

@Service
public class EmpathService {
	
	@Autowired DatabaseClient databaseClient;
	
	@Autowired FaceClient faceClient;
	
	@Autowired FileClient fileClient;
	
	@Value("${storage.location.song}")
	String songStorageLocation;
	
	@Value("${storage.location.art}")
	String artStorageLocation;
	
	Path songStoragePath;
	
	Path artStoragePath;
	
	@PostConstruct
	public void init() {
		// Initialize the song and art pathways 
		this.songStoragePath = Paths.get(songStorageLocation).toAbsolutePath().normalize();
		this.artStoragePath = Paths.get(artStorageLocation).toAbsolutePath().normalize();
	}
	
	public List<Song> createPlaylist(String url) throws DatabaseException, URISyntaxException {
		//Determine emotion from face
		Emotion emotion = determineEmotion(url);
		
		List<Song> songs = databaseClient.getSongsByEmotion(emotion);
		Collections.shuffle(songs);
		// If there are more than 10 songs, only return 10, else return all
		if(songs.size() >= 10)
			return songs.subList(0, 10);
		return songs;
	}
	
	public void addSongFiles(int id, MultipartFile songFile, MultipartFile artFile) throws InvalidSongException, DatabaseException {
		//Get the song filename
		String songFileName = StringUtils.cleanPath(songFile.getOriginalFilename());
		String artFileName = StringUtils.cleanPath(artFile.getOriginalFilename());
		
		// Get the song from the database
		Song savedSong = databaseClient.getSongById(id);
		
		try {
            // Check if the file's name contains invalid characters
            if(songFileName.contains("..") || artFileName.contains("..")) {
                throw new InvalidSongException("Sorry! Filename contains invalid path sequence");
            }
            //Remove spaces from files
            songFileName = songFileName.replaceAll(" ", "_").replaceAll("'", "");
            artFileName = artFileName.replaceAll(" ", "_").replaceAll("'", "");
            
            //Add Unique Identifier to prevent overwrite of songs with same names
            UUID uuid = UUID.randomUUID();
            songFileName = uuid.toString() + "-" + songFileName;
            artFileName = uuid.toString() + "-" + artFileName;

            // Copy file to the target location 
            Path songTargetLocation = this.songStoragePath.resolve(songFileName);
            Path artTargetLocation = this.artStoragePath.resolve(artFileName);
            fileClient.addFile(songFile, songTargetLocation);
            fileClient.addFile(artFile, artTargetLocation);
            
            // Set song location for database
            databaseClient.updateSongLocation(savedSong.getId(), songTargetLocation.toString());
            databaseClient.updateArtLocation(savedSong.getId(), artTargetLocation.toString());

        } catch (IOException ex) {
            throw new InvalidSongException("Could not store file. Please try again! " + ex);
        }
	}
	
	public Resource getSong(int id) throws DatabaseException, FileNotFoundException {
		try {
			// Get song storage location from database.
			Song song = databaseClient.getSongById(id);

			// Get song from disk
			Resource songFile = fileClient.getFile(song.getSongLocation(), this.songStoragePath);
		
			// Make sure song exists
			if (songFile.exists())
				return songFile;
			else 
				throw new FileNotFoundException("File not found");
				
		} catch (MalformedURLException ex) {
            throw new FileNotFoundException("File not found " + ex);
        }
	}
	
	public Resource getSongArt(int id) throws DatabaseException, FileNotFoundException {
		try {
			// Get art storage location from database
			Song song = databaseClient.getSongById(id);
			
			// Get art from disk
			Resource artFile = fileClient.getFile(song.getAlbumCoverLocation(), this.artStoragePath);
		
			// Make sure art exists
			if (artFile.exists())
				return artFile;
			else 
				throw new FileNotFoundException("File not found");
				
		} catch (MalformedURLException ex) {
            throw new FileNotFoundException("File not found " + ex);
        }
	}
	
	private Emotion determineEmotion(String url) throws URISyntaxException {
		com.microsoft.azure.cognitiveservices.vision.faceapi.models.Emotion emotions = faceClient.getFaceEmotions(url);
		
		// load emotions in map
		Map<Emotion, Double> emotionMap = new HashMap<Emotion, Double>();
		emotionMap.put(Emotion.ANGER, emotions.anger());
		emotionMap.put(Emotion.FEAR, emotions.fear());
		emotionMap.put(Emotion.SADNESS, emotions.sadness());
		emotionMap.put(Emotion.SURPRISE, emotions.surprise());
		emotionMap.put(Emotion.HAPPINESS, emotions.happiness());
		emotionMap.put(Emotion.NEUTRAL, emotions.neutral());
		
		// Find emotion that is the highest value
		Entry<Emotion, Double> maxEntry = Collections.max(emotionMap.entrySet(), (Entry<Emotion, Double> e1, Entry<Emotion, Double> e2) -> e1.getValue().compareTo(e2.getValue()));
		return maxEntry.getKey();
	}
	
}
