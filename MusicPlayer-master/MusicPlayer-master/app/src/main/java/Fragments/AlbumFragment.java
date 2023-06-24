package Fragments;

import static Activity.MainActivity.albums;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raagav.musicplayer.R;

import Adapter.AlbumAdapter;

public class AlbumFragment extends Fragment {


    AlbumAdapter albumAdapter;
    RecyclerView recyclerView;

    public AlbumFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        if (!(albums.size() < 1))
        {
            albumAdapter = new AlbumAdapter(getContext(), albums);
            recyclerView.setAdapter(albumAdapter);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(new GridLayoutManager( getContext(), 2));
        }
        return view;
    }
}