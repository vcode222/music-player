package Adapter;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.material.snackbar.Snackbar;
import com.raagav.musicplayer.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import Activity.AlbumDetails;
import Model.Music;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Music> albumFiles;
    View view;

    public AlbumAdapter(Context context, ArrayList<Music> musicFiles) {
        this.context = context;
        this.albumFiles = musicFiles;
    }

    @NonNull
    @Override
    public AlbumAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.album_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumAdapter.ViewHolder holder, int position) {
        holder.album_name.setText(albumFiles.get(position).getAlbum());
        byte[] image = getAlbumArt(albumFiles.get(position).getPath());
        //String s = new String(image);

        if (image != null) {
            Glide.with(context)
                    .load(image)
                    .placeholder(R.drawable.music)
                    .into(holder.album_image);
        }
        else {
            Glide.with(context)
                    .load(R.drawable.music)
                    .into(holder.album_image);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(context, AlbumDetails.class);
                intent.putExtra("albumName",albumFiles.get(position).getAlbum());
                context.startActivity(intent);
            }
        });
    }

    private byte[] getAlbumArt(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
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
            album_image = itemView.findViewById(R.id.album_image);
            album_name = itemView.findViewById(R.id.album_name);
        }

    }
}
