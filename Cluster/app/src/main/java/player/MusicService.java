package player;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import at.hsacevic.cluster.R;
import java.util.ArrayList;
import java.util.Random;

/*
 * Cluster Music App - Sacevic Haris
 */

public class MusicService extends Service implements
MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
MediaPlayer.OnCompletionListener {

	private MediaPlayer player;
	private ArrayList<Song> songs;
	private int songPosn;
	public final IBinder musicBind = new MusicBinder();
	private String songTitle="", songArtist="", songAlbum="";
	private static final int NOTIFY_ID=1;
	private boolean shuffle=false, loop=false;
	private Random rand;

	public void onCreate(){
		super.onCreate();
		songPosn=0;
		rand=new Random();
		player = new MediaPlayer();
		initMusicPlayer();
	}

	public void initMusicPlayer(){
		player.setWakeMode(getApplicationContext(), 
				PowerManager.PARTIAL_WAKE_LOCK);
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		player.setOnPreparedListener(this);
		player.setOnCompletionListener(this);
		player.setOnErrorListener(this);
	}

	public void setList(ArrayList<Song> theSongs){
		songs=theSongs;
	}

	public class MusicBinder extends Binder {
		public MusicService getService() {
			return MusicService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return musicBind;
	}

	@Override
	public boolean onUnbind(Intent intent){
		player.stop();
		player.release();
		return false;
	}

	public void playSong(){
		player.reset();
		Song playSong = songs.get(songPosn);
		songTitle=playSong.getTitle();
        songArtist=playSong.getArtist();
        songAlbum=playSong.getAlbum();
		long currSong = playSong.getID();
		Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);
		try{ 
			player.setDataSource(getApplicationContext(), trackUri);
		}
		catch(Exception e){
			Log.e("CLUSTER", "Error setting data source", e);
		}
        try{
            player.prepareAsync();
        } catch (IllegalStateException ex) {
            Toast.makeText(this, "Corrupt File!\nMaybe the file doesnt exist anymore?",Toast.LENGTH_SHORT).show();
        }

	}

	//SET SONG
	public void setSong(int songIndex){
		songPosn=songIndex;	
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		if(player.getCurrentPosition()>0){
            if(shuffle){
                mp.reset();
                playNext();
            }
		}
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		Log.v("CLUSTER", "Playback Error");
		mp.reset();
		return false;
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		//start playback
		mp.start();
		//notification
		Intent notIntent = new Intent(this, at.hsacevic.cluster.Main.class);
		notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification.Builder builder = new Notification.Builder(this);

		builder.setContentIntent(pendInt)
		.setSmallIcon(R.drawable.play)
		.setTicker(songTitle)
		.setOngoing(true)
		.setContentTitle(getString(R.string.notification_playing))
		.setContentText(songTitle +" by "+ songArtist);
		Notification not = builder.build();
		startForeground(NOTIFY_ID, not);
	}

	//PLAYBACK
	public int getPosn(){
		return player.getCurrentPosition();
	}

	public int getDur(){
		return player.getDuration();
	}

	public boolean isPng(){
		return player.isPlaying();
	}

	public void pausePlayer(){
		player.pause();
	}

	public void seek(int posn){
		player.seekTo(posn);
	}

	public void go(){
		player.start();
	}

	//PREVIOUS TRACK
	public void playPrev(){
		songPosn--;
		if(songPosn<0) songPosn=songs.size()-1;
		playSong();
	}

	//NEXT TRACK
	public void playNext(){
		if(shuffle){
			int newSong = songPosn;
			while(newSong==songPosn){
				newSong=rand.nextInt(songs.size());
			}
			songPosn=newSong;
		}
		else{
			songPosn++;
			if(songPosn>=songs.size()) songPosn=0;
		}
		playSong();
	}

	@Override
	public void onDestroy() {
		stopForeground(true);
	}

	//SHUFFLE
	public void setShuffle(){
		if(shuffle) shuffle=false;
		else shuffle=true;
	}

    public void setLoop(){
        if(loop) loop=false;
        else loop=true;
    }

    public int getSongPosn(){
        return songPosn;
    }

    public String getSongTitle(){
        return songTitle;
    }

    public String getSongArtist(){
        return songArtist;
    }

    public String getSongAlbum(){
        return songAlbum;
    }
}
