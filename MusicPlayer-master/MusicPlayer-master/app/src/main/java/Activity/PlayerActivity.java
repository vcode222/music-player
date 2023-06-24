package Activity;

import static com.raagav.musicplayer.ApplicationClass.ACTION_NEXT;
import static com.raagav.musicplayer.ApplicationClass.ACTION_PLAY;
import static com.raagav.musicplayer.ApplicationClass.ACTION_PREVIOUS;
import static com.raagav.musicplayer.ApplicationClass.CHANNEL_ID_2;
import static Activity.MainActivity.musicFiles;
import static Adapter.AlbumDetailsAdapter.albumFiles;
import static Adapter.MusicAdapter.mFiles;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.ActionBar;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.raagav.musicplayer.R;

import java.util.ArrayList;
import java.util.Random;

import Model.Music;

public class PlayerActivity extends AppCompatActivity {


    TextView songName, artistName, leftTime, rightTime;
    Button back, menu, previous, next, fav;
    ImageView albumArt, shuffleBtn, repeat;
    FloatingActionButton playPauseBtn;
    SeekBar seekBar;
    static Uri uri;
    int position = -1;
    static ArrayList<Music> listSongs = new ArrayList<>();
//    MediaPlayer class can be used to control playback of audio/video files and streams.
//    MediaPlayer is not thread-safe.
    static MediaPlayer mediaPlayer;
//    A Handler allows you to send and process Message and Runnable objects associated with a thread's MessageQueue.
    private Handler handler = new Handler();
    private Thread playThread, prevThread, nextThread;
    boolean shuffleBoolean = false, repeatBoolean = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        initViews();
        getIntentMethod();

        songName.setText(listSongs.get(position).getTitle());
        artistName.setText(listSongs.get(position).getArtist());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            //boolean from user is used to conform the touch event from the user.
            //int progress is the where is the seekBar.

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null)
                {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    int duration = mediaPlayer.getDuration() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                    leftTime.setText(formattedTime(mCurrentPosition));
                    rightTime.setText(formattedTime(duration- mCurrentPosition));
                }
                handler.postDelayed(this,1000);
            }


        });
        shuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shuffleBoolean)
                {
                    shuffleBoolean = false;
                    shuffleBtn.setImageResource(R.drawable.ic_baseline_shuffle_24);
                }
                else
                {
                    shuffleBoolean = true;
                    shuffleBtn.setImageResource(R.drawable.ic_baseline_shuffle_on_24);
                }
            }
        });
        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (repeatBoolean)
                {
                    repeatBoolean = false;
                    repeat.setImageResource(R.drawable.ic_baseline_repeat_24);
                }
                else {
                    repeatBoolean = true;
                    repeat.setImageResource(R.drawable.ic_baseline_repeat_on_24);
                }
            }
        });

    }
    private String formattedTime(int mCurrentPosition) {

        String totalOut = "";
        String totalNew = "";
        String seconds = String.valueOf(mCurrentPosition % 60);
        String minutes = String.valueOf(mCurrentPosition / 60);
        totalOut = minutes + ":" + seconds;
        totalNew = minutes + ":" + "0" + seconds;
        if (seconds.length() == 1)
        {
            return totalNew;
        }
        else
        {
            return totalOut;
        }
    }

    private void getIntentMethod() {
        // intent is used to start or play activity.
        position = getIntent().getIntExtra("position",-1);
        String sender = getIntent().getStringExtra("sender");
        if(sender != null && sender.equals("albumDetails"))
        {

            listSongs = albumFiles;
        }
        else {
            listSongs = musicFiles;
        }

        if (listSongs != null)
        {
            playPauseBtn.setImageResource(R.drawable.ic_play);
            uri = Uri.parse(listSongs.get(position).getPath());
        }
        if (mediaPlayer != null)
        {
            mediaPlayer.stop();
            // after stopping mediaPlayer it is necessary to release mediaPlayer.
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
        }
        else {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
        }
        seekBar.setMax(mediaPlayer.getDuration()/1000);// to get the duration at which we move the seekBar.
        metaData(uri);
    }
    @Override
    protected void onPostResume() {
        playThreadBtn();
        prevThreadBtn();
        nextThreadBtn();
        super.onPostResume();
    }

    private void playThreadBtn() {
        playThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                playPauseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playPauseBtnClicked();
                    }

                });
            }
        };
        playThread.start();
    }


    private void playPauseBtnClicked() {
        if (mediaPlayer.isPlaying())
        {
            playPauseBtn.setImageResource(R.drawable.ic_pause);
            mediaPlayer.pause();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
//            Runs the specified action on the UI thread.
//            If the current thread is the UI thread, then the action is executed immediately.
//            If the current thread is not the UI thread, the action is posted to the event queue of the UI thread.
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
        }
        else
        {
            playPauseBtn.setImageResource(R.drawable.ic_play);
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
//                        Sets the current progress to the specified value.
//                        This method will immediately update the visual position of the progress indicator.
                        seekBar.setProgress(mCurrentPosition);
                    }
                    // run after specified amount of time elapses.
                    handler.postDelayed(this,1000);
                }
            });

        }

    }
    private void prevThreadBtn() {
        prevThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                previous.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prevBtnClicked();
                    }

                });
            }
        };
        prevThread.start();

    }

    private void prevBtnClicked() {
        if (mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffleBoolean && !repeatBoolean)
            {
                position = getRandom(listSongs.size() - 1);
            }
            else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position - 1) < 0 ?  (listSongs.size() - 1) : (position - 1));
            }
//            position = ((position - 1) < 0 ?  (listSongs.size() - 1) : (position - 1));
            uri =  Uri.parse(listSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            songName.setText(listSongs.get(position).getTitle());
            artistName.setText(listSongs.get(position).getAlbum());
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
            playPauseBtn.setImageResource(R.drawable.ic_play);
            mediaPlayer.start();

        }
        else
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffleBoolean && !repeatBoolean)
            {
                position = getRandom(listSongs.size() - 1);
            }
            else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position - 1) < 0 ?  (listSongs.size() - 1) : (position - 1));
            }
//            position = ((position - 1) < 0 ?  (listSongs.size() - 1) : (position - 1));
            uri =  Uri.parse(listSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData (uri);
            songName.setText(listSongs.get(position).getTitle());
            artistName.setText(listSongs.get(position).getAlbum());
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        int duration = mediaPlayer.getDuration() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                        seekBar.setMax(duration);
                    }
                    handler.postDelayed(this,1000);
                }
            });
            playPauseBtn.setImageResource(R.drawable.ic_pause);
        }
    }


    private void nextThreadBtn() {
        nextThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextBtnClicked();
                    }

                });
            }
        };
        nextThread.start();

    }

    private void nextBtnClicked() {
        if (mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffleBoolean && !repeatBoolean)
            {
                position = getRandom(listSongs.size() - 1);
            }
            else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position + 1) % listSongs.size());
            }
            //else position will be position
            uri =  Uri.parse(listSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            // metaData contains album photo, song name and related information.
            metaData (uri);
            songName.setText(listSongs.get(position).getTitle());
            artistName.setText(listSongs.get(position).getAlbum());
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        int duration = mediaPlayer.getDuration() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                        seekBar.setMax(duration);
                    }
                    handler.postDelayed(this,1000);
                }
            });
            playPauseBtn.setImageResource(R.drawable.ic_play);
            mediaPlayer.start();

        }
        else
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffleBoolean && !repeatBoolean)
            {
                position = getRandom(listSongs.size() - 1);
            }
            else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position + 1) % listSongs.size());
            }
            uri =  Uri.parse(listSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData (uri);
            songName.setText(listSongs.get(position).getTitle());
            artistName.setText(listSongs.get(position).getAlbum());
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
            playPauseBtn.setImageResource(R.drawable.ic_pause);
        }
    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(1 + 1);
    }

    private void metaData(Uri uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int duration = Integer.parseInt(listSongs.get(position).getDuration()) / 1000;
        rightTime.setText(formattedTime(duration));
        byte[] art = retriever.getEmbeddedPicture();
        if (art != null)
        {
            Glide.with(this)
                    .asBitmap()
                    .load(art)
                    .into(albumArt);
        }
        else
        {
            Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.ic_launcher_background)
                    .into(albumArt);
        }
    }



    private void initViews() {

        songName = findViewById(R.id.title_name);
        songName.setSelected(true);
        artistName = findViewById(R.id.artist_name);
        artistName.setSelected(true);
        leftTime = findViewById(R.id.left_time);
        rightTime = findViewById(R.id.right_time);

        back = findViewById(R.id.back);
        menu = findViewById(R.id.menu);
        previous = findViewById(R.id.prev_btn);
        next = findViewById(R.id.next_btn);
        shuffleBtn = findViewById(R.id.shuffle_off);
        repeat = findViewById(R.id.repeat_off);
        fav = findViewById(R.id.fav_btn);

        seekBar = findViewById(R.id.seekBar);

        playPauseBtn = findViewById(R.id.pauseBtn);

        albumArt = findViewById(R.id.album_image);

    }
}