package com.example.milindmahajan.spartandrive.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
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

import com.example.milindmahajan.spartandrive.R;
import com.example.milindmahajan.spartandrive.model.DropboxItem;

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
    private ArrayList<DropboxItem> results = new ArrayList<DropboxItem>();
    private ActionMode mActionMode;
    private ListViewAdapter mAdapter;


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

    public void reloadListView (ArrayList<DropboxItem> dropboxItems) {

        this.results.removeAll(this.results);
        this.results.addAll(dropboxItems);

        updateVideosFound();
    }

    private void updateVideosFound() {

        mAdapter = new ListViewAdapter(getContext(),
                R.layout.dropbox_item, 0, this.results);
        ListView listView = (ListView)rootView.findViewById(R.id.list_view);
        listView.setAdapter(mAdapter);
    }

    private class ListViewAdapter extends ArrayAdapter<DropboxItem> {

        ArrayList <DropboxItem> videoList = new ArrayList<DropboxItem>();
        ArrayList <DropboxItem> selectedFiles = new ArrayList<DropboxItem>();

        public ListViewAdapter(Context context, int resource, int textViewResourceId, List <DropboxItem> objects) {

            super(context, resource, textViewResourceId, objects);
            videoList.addAll(objects);
        }

        public void setNewSelection(int position, boolean value) {

            selectedFiles.add(videoList.get(position));
            notifyDataSetChanged();
        }

        public void removeSelection(int position) {

            removeFromSelection(videoList.get(position));
            notifyDataSetChanged();
        }

        private void removeFromSelection (DropboxItem file) {

            for (int i = 0; i < selectedFiles.size(); i++) {

//                if (selectedFiles.get(i).getId().equals(file.getId())) {
//
//                    selectedFiles.remove(i);
//                    break;
//                }
            }
        }

        public ArrayList <DropboxItem> getSelectedFiles () {

            return this.selectedFiles;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if(convertView == null) {

                convertView = getActivity().getLayoutInflater().inflate(R.layout.dropbox_item, parent, false);
            }

            DropboxItem searchResult = videoList.get(position);

            ImageView imageView = (ImageView)convertView.findViewById(R.id.icon);
            TextView title = (TextView)convertView.findViewById(R.id.title);
            CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.checkBox);
            checkBox.setVisibility(View.VISIBLE);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {

                        mAdapter.setNewSelection(position, isChecked);
                    } else {

                        mAdapter.removeSelection(position);
                    }

                    if (mAdapter.getSelectedFiles().size() != 0) {

//                        mActionMode = getActivity().startActionMode(new ActionBarCallBack());
                    } else {

                        mActionMode.finish();
                    }
                }
            });

            return convertView;
        }
    }
}