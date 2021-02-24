package rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import client.DatabaseClient;
import model.Song;
import model.Song.Emotion;
import model.exceptions.DatabaseException;
import model.exceptions.SongAlreadyExistsException;

@RestController
@RequestMapping("database")
public class DatabaseController {
	@Autowired DatabaseClient databaseClient;
	
	@GetMapping(value = "/songs", produces = "application/json")
	public List<Song> songs() throws DatabaseException {
		return databaseClient.getAllSongs();
	}
	
	@GetMapping(value = "/song/{title}/{artist}", produces = "application/json")
	public Song song(@PathVariable String title, @PathVariable String artist) throws DatabaseException {
		return databaseClient.getSongByTitleAndArtist(title, artist);
	}
	
	@GetMapping(value = "/song/emotion/{emotion}", produces = "application/json")
	public List<Song> song(@PathVariable Emotion emotion) throws DatabaseException {
		return databaseClient.getSongsByEmotion(emotion);
	}
	
	@PostMapping(value = "/song", consumes= "application/json", produces = "application/json")
	public Song connect(@RequestBody Song song) throws DatabaseException, SongAlreadyExistsException {
		// Check to see if the song is already in the database
		Song savedSong = databaseClient.getSongByTitleAndArtist(song.getTitle(), song.getArtist());
		if (null != savedSong) {
			throw new SongAlreadyExistsException("Song already exists");
		}
		return databaseClient.addSong(song);
	}
	
	@PutMapping(value = "/song/{songId}/emotion/{emotion}", produces = "application/json") 
	public void updateEmotion(@PathVariable int songId, @PathVariable Emotion emotion) throws DatabaseException {
		databaseClient.updateEmotion(songId, emotion);
	}
	
	@DeleteMapping(value = "/song/{songId}")
	public void deleteSong(@PathVariable int songId) throws DatabaseException {
		databaseClient.deleteSong(songId);
	}

}