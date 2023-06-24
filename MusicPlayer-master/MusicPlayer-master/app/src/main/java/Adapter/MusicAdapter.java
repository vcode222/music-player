package Adapter;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.raagav.musicplayer.R;

import java.io.File;
import java.util.ArrayList;

import Activity.PlayerActivity;
import Model.Music;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {

    private Context context;
    public static ArrayList<Music> mFiles;
    ActionBar actionBar;

    public MusicAdapter(Context context, ArrayList<Music> musicFiles) {
        this.context = context;
        this.mFiles = musicFiles;
    }

    @NonNull
    @Override
    public MusicAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        Instantiates a layout XML file into its corresponding View objects. It is never used directly.
        View view = LayoutInflater.from(context).inflate(R.layout.music_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.file_name.setText(mFiles.get(position).getTitle());
        holder.artistName.setText(mFiles.get(position).getArtist());

        byte[] image = getAlbumArt(mFiles.get(position).getPath());
        //String s = new String(image);

        if (image != null) {
            Glide.with(context)
                    .load(image)
                    .placeholder(R.drawable.music)
                    .into(holder.albumArt);
        }
        else {
            Glide.with(context)
                    .load(R.drawable.music)
                    .into(holder.albumArt);
        }

        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete:
                                deleteFile(position, v);
                                break;
                        }
                        return true;
                    }
                });
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (context, PlayerActivity.class);
                intent.putExtra("position",position);
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

    private void deleteFile(int position, View v) {
        Uri contenturi = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                Long.parseLong(mFiles.get(position).getId()));
        mFiles.remove(position);
        File file = new File(mFiles.get(position).getPath());
        boolean deleted = file.delete();
        if (deleted) {
            context.getContentResolver().delete(contenturi, null, null);
            mFiles.remove(position);
            notifyItemChanged(position);
            notifyItemRangeChanged(position, mFiles.size());
            Snackbar.make(v,"File Deleted ", Snackbar.LENGTH_LONG)
                    .show();
        }
        else {
            //may be file in sd card
            Snackbar.make(v,"Can't be deleted ", Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView file_name, artistName;
        ImageView albumArt, menu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            file_name  = itemView.findViewById(R.id.music_file_name);
            artistName = itemView.findViewById(R.id.artistName);
            albumArt   = itemView.findViewById(R.id.music_image);
            menu       = itemView.findViewById(R.id.menuMore);

        }
    }

    public void updateList(ArrayList<Music> musicFilesArrayList) {
        mFiles = new ArrayList<>();
        mFiles.addAll(musicFilesArrayList);
        notifyDataSetChanged();
    }
}
