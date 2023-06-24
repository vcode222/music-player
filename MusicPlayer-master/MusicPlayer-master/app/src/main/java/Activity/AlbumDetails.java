package Activity;

import static Activity.MainActivity.musicFiles;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.raagav.musicplayer.R;

import java.util.ArrayList;

import Adapter.AlbumDetailsAdapter;
import Model.Music;

public class AlbumDetails extends AppCompatActivity {

//    RecyclerView is the ViewGroup that contains the views corresponding to your data.
    RecyclerView recyclerView;
    String albumName;
    ImageView photo;
    ArrayList<Music> albumSongs = new ArrayList<>();
    AlbumDetailsAdapter Adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);
        recyclerView = findViewById(R.id.recyclerView);
        photo = findViewById(R.id.albumPhoto);
        albumName = getIntent().getStringExtra("albumName");
        int j = 0;
        for (int i = 0; i < musicFiles.size() ; i ++)
        {
            if (albumName.equals(musicFiles.get(i).getAlbum()))
            {
                albumSongs.add(j, musicFiles.get(i));
                j ++;
            }
        }
        byte[] image = getAlbumArt(albumSongs.get(0).getPath());
        if (image != null)
        {
            Glide.with(this)
                    .load(image)
                    .into(photo);
        }
        else
        {
            Glide.with(this)
                    .load(R.drawable.music)
                    .into(photo);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!(albumSongs.size() < 1))
        {
            Adapter = new AlbumDetailsAdapter(this, albumSongs);
            recyclerView.setAdapter(Adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this,
                    RecyclerView.VERTICAL, false));
        }
    }

    private byte[] getAlbumArt(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        byte[] arr = retriever.getEmbeddedPicture();
        retriever.release();
        return arr;
    }
}