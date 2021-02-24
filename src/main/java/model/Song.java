package model;

import org.springframework.lang.NonNull;

public class Song {
	private int id;
	@NonNull private String artist;
	@NonNull private String album;
	@NonNull private String title;
	@NonNull private Emotion emotion;
	private String songLocation;
	private String albumCoverLocation;
	@NonNull private boolean favorite;
	
	public enum Emotion {
		ANGER, FEAR, HAPPINESS, NEUTRAL, SADNESS, SURPRISE
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Emotion getEmotion() {
		return emotion;
	}

	public void setEmotion(Emotion emotion) {
		this.emotion = emotion;
	}

	public String getSongLocation() {
		return songLocation;
	}

	public void setSongLocation(String songLocation) {
		this.songLocation = songLocation;
	}

	public String getAlbumCoverLocation() {
		return albumCoverLocation;
	}

	public void setAlbumCoverLocation(String albumCoverLocation) {
		this.albumCoverLocation = albumCoverLocation;
	}
	
	public boolean isFavorite() {
		return favorite;
	}
	
	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}
	
}
