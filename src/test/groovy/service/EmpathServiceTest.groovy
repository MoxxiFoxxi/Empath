package service

import java.nio.file.Path
import java.nio.file.Paths

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.Resource
import org.springframework.web.multipart.MultipartFile

import client.DatabaseClient
import client.FaceClient
import client.FileClient
import empath.Application
import model.Song
import model.Song.Emotion
import spock.lang.Specification

@SpringBootTest(classes = Application.class)
public class EmpathServiceTest extends Specification {
	EmpathService classUnderTest
	DatabaseClient databaseMock
	FaceClient faceMock
	FileClient fileMock
	
	def setup() {
		//Set up class mocks
		classUnderTest = new EmpathService()
		databaseMock = Mock()
		faceMock = Mock()
		fileMock = Mock()
		
		classUnderTest.faceClient = faceMock
		classUnderTest.databaseClient = databaseMock
		classUnderTest.fileClient = fileMock
	}
	
	def 'Generate playlist'() {
		setup:
		String url = "url"
		List<Song> songs = createSongs()
		com.microsoft.azure.cognitiveservices.vision.faceapi.models.Emotion emotion = getEmotion()
		
		when:
		def actual = classUnderTest.createPlaylist(url)
		
		then:
		1 * databaseMock.getSongsByEmotion(Emotion.HAPPINESS) >> songs
		1 * faceMock.getFaceEmotions(url) >> emotion
		actual.size() == 10
	}
	
	def 'Add file to database'() {
		setup:
		Song song = createSong()
		MultipartFile mockFile = Mock()
		mockFile.getOriginalFilename() >> "filename"
		classUnderTest.songStoragePath = Paths.get("dir").toAbsolutePath().normalize();
		classUnderTest.artStoragePath = Paths.get("dir").toAbsolutePath().normalize();
		
		when:
		classUnderTest.addSongFiles(song.id, mockFile, mockFile)
		
		then:
		1 * databaseMock.getSongById(song.id) >> song
		1 * databaseMock.updateSongLocation(song.id, _)
		1 * databaseMock.updateArtLocation(song.id, _)
		2 * fileMock.addFile(_, _)
	}
	
	def 'Get song file'() {
		setup:
		Resource resource = Mock()
		resource.exists() >> true
		Song song = createSong()
		
		when:
		classUnderTest.getSong(song.id)
		
		then:
		1 * databaseMock.getSongById(_) >> song
		1 * fileMock.getFile(_, _) >> resource
		
		when:
		classUnderTest.getSongArt(song.id)
		
		then:
		1 * databaseMock.getSongById(_) >> song
		1 * fileMock.getFile(_, _) >> resource
	}
	
	// UTILITY FUNCTIONS
	
	Song createSong() {
		return new Song(id: 1, title: "Title", artist: "artist", album: "album", emotion: Emotion.HAPPINESS, favorite:false)
	}
	
	List<Song> createSongs() {
		List<Song> songs = new ArrayList<Song>()
		for (int i = 0; i < 15; i++) {
			Song song = createSong()
			song.id = i
			songs.add(song)
		}
		return songs
	}
	
	com.microsoft.azure.cognitiveservices.vision.faceapi.models.Emotion getEmotion() {
		com.microsoft.azure.cognitiveservices.vision.faceapi.models.Emotion emotion = new com.microsoft.azure.cognitiveservices.vision.faceapi.models.Emotion()
		emotion.happiness = 100.00
		emotion.anger = 0.00
		emotion.neutral = 0.00
		emotion.fear = 0.00
		emotion.sadness = 0.00
		emotion.surprise = 0.00
		return emotion
	}
}