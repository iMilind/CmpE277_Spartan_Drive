package com.example.milindmahajan.spartandrive.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.example.milindmahajan.spartandrive.R;
import com.example.milindmahajan.spartandrive.model.DropboxItem;
import com.example.milindmahajan.spartandrive.utils.Common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by milind.mahajan on 11/28/15.
 */
public class ListViewFragment extends Fragment {

    ListViewFragmentListener listViewFragmentListener;

    public interface  ListViewFragmentListener {

        public void didSelectRow(String id, boolean isFile);
    }

    View rootView;
    private ArrayList<String> results = new ArrayList<String>();
    private ListViewAdapter listViewAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_list_view, container, false);

        ListView listView = (ListView)rootView.findViewById(R.id.list_view);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

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

        ListView listView = (ListView)rootView.findViewById(R.id.list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos,
                                    long id) {

                System.out.println("onItemClick Adapter View Favorite fragment");
//                String videoId = searchResults.get(pos).getId();

                listViewFragmentListener.didSelectRow(results.get(pos), true);
            }
        });
    }

    public void reloadListView (ArrayList<String> dropboxItems, Context context) {

        this.results.removeAll(this.results);
        this.results.addAll(dropboxItems);

        reloadData(this.results, context);
    }

    private void reloadData(ArrayList <String> dropboxItems, Context context) {

        listViewAdapter = new ListViewAdapter(context,
                R.layout.dropbox_item, 0, dropboxItems);


        ListView listView = (ListView)rootView.findViewById(R.id.list_view);
        listView.setAdapter(listViewAdapter);
    }

    private class ListViewAdapter extends ArrayAdapter<String> {

        ArrayList <String> dropboxItems = new ArrayList<String>();

        public ListViewAdapter(Context context, int resource, int textViewResourceId, List <String> objects) {

            super(context, resource, textViewResourceId, objects);
            this.dropboxItems.addAll(objects);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if(convertView == null) {

                convertView = getActivity().getLayoutInflater().inflate(R.layout.dropbox_item, parent, false);
            }

            String searchResult = this.dropboxItems.get(position);

            ImageView imageView = (ImageView)convertView.findViewById(R.id.icon);
            TextView title = (TextView)convertView.findViewById(R.id.title);
            title.setText(searchResult);
            CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.checkBox);
            checkBox.setVisibility(View.VISIBLE);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                }
            });

            return convertView;
        }
    }
}