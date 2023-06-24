package Activity;

import static com.raagav.musicplayer.ApplicationClass.ACTION_NEXT;
import static com.raagav.musicplayer.ApplicationClass.ACTION_PLAY;
import static com.raagav.musicplayer.ApplicationClass.ACTION_PREVIOUS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;


import com.google.android.material.tabs.TabLayout;
import com.raagav.musicplayer.R;

import java.security.Permission;
import java.util.ArrayList;
import java.util.Locale;

import Adapter.ViewPagerAdapter;
import Fragments.AlbumFragment;
import Fragments.SongFragment;
import Model.Music;

import com.raagav.musicplayer.ApplicationClass;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    public static ArrayList<Music> musicFiles;
    public static ArrayList<Music> albums = new ArrayList<>();
    private static final int REQUEST_CODE = 1;
    public ActionBar actionBar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permission();

        actionBar = getSupportActionBar();
//        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#FFFFFFFF"));
//        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setElevation(0);
    }

    private void permission() {
        //Check if  the permission is already  available or not.
        if (ContextCompat.checkSelfPermission( getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            //Requesting the permission.
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}
                    , REQUEST_CODE);
        }
        else
        {
            musicFiles = getAllAudio(this);
            initViewPager();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE)
        {//Received permission result for storage permission.

            //Check is the only request permission has been granted.
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                musicFiles = getAllAudio(this);
                initViewPager();
            }
            else {
                //Requesting the permission.
                ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}
                        , REQUEST_CODE);
            }
        }
    }

    private void initViewPager() {
        ViewPager viewPager = findViewById(R.id.viewpager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new SongFragment(), "Songs");
        viewPagerAdapter.addFragments(new AlbumFragment(),"Albums");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }
    public  static  ArrayList<Music> getAllAudio(Context context)
    {
        ArrayList<String> duplicate = new ArrayList<>();
        ArrayList<Music> tempAudioList = new ArrayList<>();
        // Path to Storage.
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA, //FOR PATH.
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media._ID
        };
        // context -> Interface to global information about an application environment.
        // contentResolver -> provides applications access to the content model.
        Cursor cursor = context.getContentResolver().query(uri,projection,
                null, null, null);
        if (cursor != null)
        {
            while (cursor.moveToNext())
            {
                String album = cursor.getString(0);
                String title = cursor.getString(1);
                String duration = cursor.getString(2);
                String path = cursor.getString(3);
                String artist = cursor.getString(4);
                String id = cursor.getString(5);

                Music musicFiles = new Music(title,album,path,duration,artist,id);
                // take log.e for check
                Log.e("Path : " + path, "Album : " + album);
                tempAudioList.add(musicFiles);
                if (!duplicate.contains(album))
                    albums.add(musicFiles);
                duplicate.add(album);
            }
            cursor.close();
        }
        return tempAudioList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String userInput = newText.toLowerCase();
        ArrayList<Music> myFiles = new ArrayList<>();
        for (Music song : musicFiles) {
            if (song.getTitle().toLowerCase().contains(userInput)) {
                myFiles.add(song);
            }
        }
        SongFragment.musicAdapter.updateList(myFiles);
        return true;
    }


}