package client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.mysql.cj.jdbc.MysqlDataSource;

import model.Song;
import model.Song.Emotion;
import model.exceptions.DatabaseException;

@Component
public class DatabaseClient {
	
	@Autowired RestTemplate restTempalte;
	
	@Autowired MysqlDataSource dataSource;
	
	public Song getSongById(int id) throws DatabaseException {
		List<Song> songs = new ArrayList<Song>();
		try {
			// Set up connection to database
			Connection conn = dataSource.getConnection();
			Statement stmt = conn.createStatement();
			
			// Execute query
			ResultSet rs = stmt.executeQuery("call empath.select_song("+ id + ")");
			
			// Map results to song objects
			while (rs.next()) {
				Song song = new Song();
				song.setId(rs.getInt("id"));
				song.setTitle(rs.getString("title"));
				song.setAlbum(rs.getString("album"));
				song.setArtist(rs.getString("artist"));
				song.setSongLocation(rs.getString("song_url"));
				song.setAlbumCoverLocation(rs.getString("song_art_url"));
				song.setFavorite(rs.getBoolean("is_favorite"));
				song.setEmotion(Emotion.valueOf(rs.getString("emotion")));
				songs.add(song);
			}
			
			// Close Connection
			rs.close();
			stmt.close();
			conn.close();
		} catch(SQLException e) {
			throw new DatabaseException(e);
		}
		
		
		if(songs.isEmpty())
			return null;
		
		return songs.get(0);
		
	}
	
	public Song getSongByTitleAndArtist(String title, String artist) throws DatabaseException {
		List<Song> songs = new ArrayList<Song>();
		try {
			// Set up connection to database
			Connection conn = dataSource.getConnection();
			Statement stmt = conn.createStatement();
			
			// Execute query
			ResultSet rs = stmt.executeQuery("call empath.select_song_by_title_and_artist('" + title + "', '" + artist + "')");
			
			// Map results to song objects
			while (rs.next()) {
				Song song = new Song();
				song.setId(rs.getInt("id"));
				song.setTitle(rs.getString("title"));
				song.setAlbum(rs.getString("album"));
				song.setArtist(rs.getString("artist"));
				song.setSongLocation(rs.getString("song_url"));
				song.setAlbumCoverLocation(rs.getString("song_art_url"));
				song.setFavorite(rs.getBoolean("is_favorite"));
				song.setEmotion(Emotion.valueOf(rs.getString("emotion")));
				songs.add(song);
			}
			
			// Close Connection
			rs.close();
			stmt.close();
			conn.close();
		} catch(SQLException e) {
			throw new DatabaseException(e);
		}
		
		if(songs.isEmpty())
			return null;
		
		return songs.get(0);
	}
	
	public List<Song> getSongsByEmotion(Emotion emotion) throws DatabaseException {
		List<Song> songs = new ArrayList<Song>();
		try {
			// Set up connection to database
			Connection conn = dataSource.getConnection();
			Statement stmt = conn.createStatement();
			
			// Execute query
			ResultSet rs = stmt.executeQuery("call empath.select_songs_by_emotion('" + emotion.toString() + "')");
			
			// Map results to song objects
			while (rs.next()) {
				Song song = new Song();
				song.setId(rs.getInt("id"));
				song.setTitle(rs.getString("title"));
				song.setAlbum(rs.getString("album"));
				song.setArtist(rs.getString("artist"));
				song.setSongLocation(rs.getString("song_url"));
				song.setAlbumCoverLocation(rs.getString("song_art_url"));
				song.setFavorite(rs.getBoolean("is_favorite"));
				song.setEmotion(Emotion.valueOf(rs.getString("emotion")));
				songs.add(song);
			}
			
			// Close Connection
			rs.close();
			stmt.close();
			conn.close();
		} catch(SQLException e) {
			throw new DatabaseException(e);
		}
		
		return songs;
		
	}
	
	public List<Song> getAllSongs() throws DatabaseException {
		List<Song> songs = new ArrayList<Song>();
		try {
			// Set up connection to database
			Connection conn = dataSource.getConnection();
			Statement stmt = conn.createStatement();
			
			// Execute query
			ResultSet rs = stmt.executeQuery("call empath.select_songs()");
			
			// Map results to song objects
			while (rs.next()) {
				Song song = new Song();
				song.setId(rs.getInt("id"));
				song.setTitle(rs.getString("title"));
				song.setAlbum(rs.getString("album"));
				song.setArtist(rs.getString("artist"));
				song.setSongLocation(rs.getString("song_url"));
				song.setAlbumCoverLocation(rs.getString("song_art_url"));
				song.setFavorite(rs.getBoolean("is_favorite"));
				song.setEmotion(Emotion.valueOf(rs.getString("emotion")));
				songs.add(song);
			}
			
			// Close Connection
			rs.close();
			stmt.close();
			conn.close();
		} catch(SQLException e) {
			throw new DatabaseException(e);
		}
		
		return songs;
	}
	
	public Song addSong(Song song) throws DatabaseException {
		try {
			// Prepare query
			final String SQL_INSERT = "insert into song (artist, album, title, is_favorite, emotion) values ( ? , ? , ? , ? , ? )";

			// Set up connection to database
			Connection conn = dataSource.getConnection();
			PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);

			// Create song in database and return if it was created or not.
			stmt.setString(1, song.getArtist().replaceAll("'", ""));
			stmt.setString(2, song.getAlbum().replaceAll("'", ""));
			stmt.setString(3, song.getTitle().replaceAll("'", ""));
			stmt.setBoolean(4, song.isFavorite());
			stmt.setString(5, song.getEmotion().toString());

			int rowsAffected = stmt.executeUpdate();
			if (rowsAffected == 0)
				throw new DatabaseException("Song " + song.getTitle() + " was unable to be instered into the database");

			ResultSet generatedKeys = stmt.getGeneratedKeys();
			if (generatedKeys.next()) {
				song.setId(generatedKeys.getInt(1));
			} else {
				throw new DatabaseException("Creating song failed, no ID obtained.");
			}

			// Close Connection
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}

		return song;
	}
	
	public void updateSongLocation(int songId, String songLocation) throws DatabaseException {
		try {
			// Set up connection to database
			Connection conn = dataSource.getConnection();
			Statement stmt = conn.createStatement();

			// Update song location
			stmt.executeUpdate("call empath.update_song_location(" + songId + ", '" + songLocation + "')");

			// Close Connection
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}
	
	public void updateArtLocation(int songId, String artLocation) throws DatabaseException {
		try {
			// 
			
			// Set up connection to database
			Connection conn = dataSource.getConnection();
			Statement stmt = conn.createStatement();

			// Update song location
			stmt.executeUpdate("call empath.update_art_location(" + songId + ", '" + artLocation + "')");

			// Close Connection
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}
	
	public void updateEmotion(int songId, Emotion emotion) throws DatabaseException {
		try {
			// Set up connection to database
			Connection conn = dataSource.getConnection();
			Statement stmt = conn.createStatement();
			
			// Update song emotion
			stmt.executeUpdate("call empath.update_song_emotion(" + songId + ", '" + emotion.toString() + "')");
		
			// Close Connection
			stmt.close();
			conn.close();
		} catch(SQLException e) {
			throw new DatabaseException(e);
		}
	}
	
	public void deleteSong(int songId) throws DatabaseException {
		try {
			// Prepare statement
			final String SQL_DELETE = "delete from song where id = ?";

			// Set up connection to database
			Connection conn = dataSource.getConnection();
			PreparedStatement stmt = conn.prepareStatement(SQL_DELETE);

			// Create song in database and return if it was created or not.
			stmt.setInt(1, songId);

			// Check if song was deleted
			int rowsAffected = stmt.executeUpdate();
			if (rowsAffected == 0) {
				throw new DatabaseException("Song could not be deleted from the database");
			}

			// Close Connection
			stmt.close();
			conn.close();
		} catch(SQLException e) {
			throw new DatabaseException(e);
		}
	}
}