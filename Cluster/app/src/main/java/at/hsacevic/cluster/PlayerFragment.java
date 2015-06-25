package at.hsacevic.cluster;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import player.MusicService;
import player.Song;

/*
 * Cluster Music App - Sacevic Haris
 */
public class PlayerFragment extends Fragment implements View.OnClickListener, MediaController.MediaPlayerControl {

    //Views
    private ImageView play_pause,stop,forward,backward;
    private TextView txtShuffle,txtLoop, txtActualTime, txtTime, txtTitle, txtArtist, txtAlbum;
    private SeekBar barTime;
    private MediaPlayer player;

    //Song Data
    ArrayList<Song> songList;
    int song_nr;

    //Play
    private boolean music_playing = false;

    //@param time : playtime in seconds
    private int time;

    ServerTask task = new ServerTask();
    //Shuffle
    private boolean shuffle = false;

    //Loop
    private boolean loop = false;

    //service
    public  MusicService musicSrv;

    public PlayerFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.player, container, false);
        initViews(rootView);

        player = new MediaPlayer();
        initMusicPlayer();



        Main main = (Main) getActivity();
        musicSrv = main.getServiceData();
        if (musicSrv != null) {
            musicSrv.playSong();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            songList = main.getSongDataList();
            song_nr = main.getSongDataNr();

            if (songList != null) {
                music_playing = true;
                play_pause.setImageResource(R.drawable.pause);

                txtTitle.setText(musicSrv.getSongTitle());
                txtArtist.setText(musicSrv.getSongArtist());
                txtAlbum.setText(musicSrv.getSongAlbum());
                time = musicSrv.getDur() / 1000;
                barTime.setMax(time);
                txtTime.setText(timeConverter(time));

                task.execute();
            }
        }

        return rootView;
    }

    private class ServerTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {

            while(musicSrv.getPosn()/1000 <= time){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                barTime.setProgress(musicSrv.getPosn()/1000);
            }
            return null;
        }
    }

    public void initMusicPlayer(){
        player.setWakeMode(getActivity().getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    private void initViews(View v) {
        txtActualTime = (TextView) v.findViewById(R.id.txtActualTime);
        txtTime = (TextView) v.findViewById(R.id.txtTime);

        barTime = (SeekBar) v.findViewById(R.id.barTime);
        barTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtActualTime.setText(timeConverter(progress));
                if(fromUser){
                    musicSrv.seek(progress*1000);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        play_pause = (ImageView) v.findViewById(R.id.imgPlay_Pause);
        play_pause.setOnClickListener(this);

        stop = (ImageView) v.findViewById(R.id.imgStop);
        stop.setOnClickListener(this);

        forward = (ImageView) v.findViewById(R.id.imgForward);
        forward.setOnClickListener(this);

        backward = (ImageView) v.findViewById(R.id.imgBackward);
        backward.setOnClickListener(this);

        txtShuffle = (TextView) v.findViewById(R.id.txtShuffle);
        txtShuffle.setOnClickListener(this);
        txtLoop = (TextView) v.findViewById(R.id.txtLoop);
        txtLoop.setOnClickListener(this);

        txtTitle = (TextView) v.findViewById(R.id.txtTitle);
        txtArtist = (TextView) v.findViewById(R.id.txtArtist);
        txtAlbum = (TextView) v.findViewById(R.id.txtAlbum);
    }

    @Override
    public void onClick (View v) {

        //Play/Pause Button
        if(v.getId() == R.id.imgPlay_Pause){
            Log.d("ONCLICK","music_playing = "+!music_playing);
            if(music_playing){
                play_pause.setImageResource(R.drawable.play);
                music_playing = false;

                //TODO play music
                musicSrv.pausePlayer();
            } else {
                play_pause.setImageResource(R.drawable.pause);
                music_playing = true;

                //TODO pause music
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                musicSrv.go();
            }
        }

        //Stop Button
        if(v.getId() == R.id.imgStop){
            Log.d("ONCLICK","music stopped");
                play_pause.setImageResource(R.drawable.play);
                music_playing = false;
                barTime.setProgress(0);
                musicSrv.pausePlayer();
                musicSrv.seek(0);
                //TODO stop music
        }

        //Forward Button
        if(v.getId() == R.id.imgForward){
            Log.d("ONCLICK","next song");
            musicSrv.playNext();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            song_nr++;
            if(song_nr > songList.size()-1){
                song_nr = 0;
            }

            Log.d("PLAYER","song nr: "+song_nr);
            txtTitle.setText(musicSrv.getSongTitle());
            txtArtist.setText(musicSrv.getSongArtist());
            txtAlbum.setText(musicSrv.getSongAlbum());
            time = musicSrv.getDur() / 1000;
            barTime.setMax(time);
            barTime.setProgress(0);
            barTime.refreshDrawableState();
            txtTime.setText(timeConverter(time));


        }

        //Backward Button
        if(v.getId() == R.id.imgBackward){
            Log.d("ONCLICK","last song");
            //TODO repeat / last song playing
            if(musicSrv.getPosn() < 3000){
                musicSrv.playPrev();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                song_nr--;
                if(song_nr < 0){
                    song_nr = songList.size()-1;
                }

                txtTitle.setText(musicSrv.getSongTitle());
                txtArtist.setText(musicSrv.getSongArtist());
                txtAlbum.setText(musicSrv.getSongAlbum());
                time = musicSrv.getDur() / 1000;
                barTime.setMax(time);
                txtTime.setText(timeConverter(time));
            } else if(musicSrv.getPosn() > 3000){
                musicSrv.seek(0);
            }

        }

        //Shuffle Text
        if(v.getId() == R.id.txtShuffle){
            Log.d("ONCLICK","shuffle = "+!shuffle);
            if(shuffle){
                txtShuffle.setBackgroundColor(Color.rgb(28,28,28));
                shuffle = false;
                musicSrv.setShuffle();
            } else {
                txtShuffle.setBackgroundColor(Color.rgb(60,60,60));
                shuffle = true;
                musicSrv.setShuffle();
            }
        }

        //Loop Text
        if(v.getId() == R.id.txtLoop){
            Log.d("ONCLICK","loop = "+!loop);
            if(loop){
                txtLoop.setBackgroundColor(Color.rgb(28,28,28));
                loop = false;
                musicSrv.setLoop();
                //TODO loop music
            } else {
                txtLoop.setBackgroundColor(Color.rgb(60,60,60));
                loop = true;
                musicSrv.setLoop();

            }
        }

    }

    private String timeConverter (int time) {
        int m = time/60, s = time%60;
        if(m<10 && s<10) return(""+m+":0"+s);
        if(m<10 && s>9) return(""+m+":"+s);
        if(m>10 && s<10) return(""+m+":0"+s);
        if(m>10 && s>9) return(""+m+":"+s);

        return "";
    }


    @Override
    public void start() {

    }

    @Override
    public void pause() {

    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return 0;
    }

    @Override
    public void seekTo(int pos) {

    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

}
