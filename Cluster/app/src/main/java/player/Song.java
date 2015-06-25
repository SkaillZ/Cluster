package player;

/*
 * Cluster Music App - Sacevic Haris
 */

import android.graphics.Bitmap;

public class Song {
	
	private long id;
	private String title;
	private String artist;
    private String album;
    //private Bitmap cover;
	
	public Song(long songID, String songTitle, String songArtist, String songAlbum /*,Bitmap songCover*/){
		id=songID;
		title=songTitle;
		artist=songArtist;
        album=songAlbum;
        //cover=songCover;
	}
	
	public long getID(){return id;}
	public String getTitle(){return title;}
	public String getArtist(){return artist;}
    public String getAlbum(){return album;}
    //public Bitmap getCover() {return cover;}
}
