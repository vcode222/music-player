package Adapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.raagav.musicplayer.R;

import java.util.ArrayList;

import Activity.PlayerActivity;
import Model.Music;

public class AlbumDetailsAdapter extends RecyclerView.Adapter<AlbumDetailsAdapter.ViewHolder> {

    Context context;
    public static ArrayList<Music> albumFiles;

    public AlbumDetailsAdapter(Context context, ArrayList<Music> albumFiles) {
        this.context = context;
        this.albumFiles = albumFiles;
    }

    @NonNull
    @Override
    public AlbumDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.music_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumDetailsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.album_name.setText(albumFiles.get(position).getTitle());
        byte[] image = getAlbumArt(albumFiles.get(position).getPath());
        if (image != null)
        {
            Glide.with(context)
                    .load(image)
                    .into(holder.album_image);
        }
        else
        {
            Glide.with(context)
                    .load(R.drawable.music)
                    .into(holder.album_image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (context, PlayerActivity.class);
                intent.putExtra("sender","albumDetails");
                intent.putExtra("position",position);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumFiles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView album_image;
        TextView album_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            album_image = itemView.findViewById(R.id.music_image);
            album_name = itemView.findViewById(R.id.music_file_name);
        }
    }
    private  byte[] getAlbumArt(String path) {
//        MediaMetadataRetriever class provides a unified interface for retrieving frame and meta data from an input media file.
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
}