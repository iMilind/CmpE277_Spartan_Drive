package com.example.milindmahajan.spartandrive.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.milindmahajan.spartandrive.R;
import com.example.milindmahajan.spartandrive.model.DropboxItem;

import java.util.ArrayList;

/**
 * Created by milind.mahajan on 11/28/15.
 */
public class ListViewFragment extends Fragment {

    ListViewFragmentListener listViewFragmentListener;

    public interface  ListViewFragmentListener {

        public void didSelectRow(String id, boolean isFile);
    }

    View rootView;
    private ArrayList<DropboxItem> searchResults = new ArrayList<DropboxItem>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_list_view, container, false);

        ListView favoritesList = (ListView)rootView.findViewById(R.id.list_view);
        favoritesList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        addClickListener();

        return rootView;
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        listViewFragmentListener = (ListViewFragmentListener)context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {

        super.onStart();
    }

    @Override
    public void onStop() {

        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
    }

    private void addClickListener(){

        ListView favoriteVideos = (ListView)rootView.findViewById(R.id.list_view);
        favoriteVideos.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos,
                                    long id) {

                System.out.println("onItemClick Adapter View Favorite fragment");
//                String videoId = searchResults.get(pos).getId();

                listViewFragmentListener.didSelectRow("", true);
            }
        });
    }
}