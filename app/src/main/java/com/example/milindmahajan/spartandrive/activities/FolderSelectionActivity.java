package com.example.milindmahajan.spartandrive.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.example.milindmahajan.spartandrive.R;
import com.example.milindmahajan.spartandrive.model.DropboxItem;
import com.example.milindmahajan.spartandrive.utils.Common;
import com.example.milindmahajan.spartandrive.utils.ListFilesTask;

import java.util.ArrayList;
import java.util.List;

public class FolderSelectionActivity extends AppCompatActivity {

    String selectedPath = new String();
    ArrayList<DropboxItem> dropboxItems = new ArrayList<DropboxItem>();
    private ListViewAdapter listViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_selection);

        addClickListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {

        refreshList(Common.rootDIR);
        super.onStart();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {

    }

    private void addClickListener() {

        final ListView listView = (ListView)findViewById(R.id.select_folder_list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos,
                                    long id) {

                deselectPreviousCheckbox();
                selectCheckbox(v, pos);
            }
        });
    }

    private void deselectPreviousCheckbox () {

        ListView listView = (ListView) findViewById(R.id.select_folder_list_view);
        int childCount = listView.getChildCount();
        for (int i = 0; i < childCount; ++i) {

            View view = listView.getChildAt(i);

            CheckBox checkBox = (CheckBox)view.findViewById(R.id.select_folder_checkBox);
            checkBox.setChecked(false);
        }
    }

    private void selectCheckbox (View view, int position) {

        CheckBox checkBox = (CheckBox)view.findViewById(R.id.select_folder_checkBox);
        checkBox.setChecked(true);

        selectedPath = dropboxItems.get(position).getPath();
    }

    public void didTouchCancelButton (View cancelButton) {

        Intent cancelIntent = new Intent();
        setResult(MainActivity.RESULT_CANCELED, cancelIntent);
        finish();
    }

    public void didTouchSelectButton (View cancelButton) {

        if (selectedPath.length() != 0) {

            Intent selectIntent = new Intent();
            selectIntent.putExtra("folderPath", selectedPath);
            setResult(MainActivity.RESULT_OK, selectIntent);
            finish();
        } else {

            Toast error = Toast.makeText(this, "No folder selected!", Toast.LENGTH_LONG);
            error.show();
        }
    }

    final ArrayList<DropboxItem> result = new ArrayList<DropboxItem>();

    public void refreshList(String path) {

        ListFilesTask listFilesTask = (ListFilesTask) new ListFilesTask(new ListFilesTask.AsyncResponse() {

            @Override
            public void processFinish(ArrayList<DropboxAPI.Entry> output) {


                for(DropboxAPI.Entry e : output) {

                    if (e.isDir) {

                        DropboxItem dropboxItem = new DropboxItem(e);

                        refreshList(dropboxItem.getPath());
                        result.add(dropboxItem);
                    }
                }

                reloadListView(result);
            }
        }).execute(path);
    }

    public void reloadListView (ArrayList<DropboxItem> dropboxItems) {

        this.dropboxItems.removeAll(this.dropboxItems);
        this.dropboxItems.addAll(dropboxItems);

        reloadData(this.dropboxItems);
    }

    private void reloadData(ArrayList <DropboxItem> dropboxItems) {

        listViewAdapter = new ListViewAdapter(getApplicationContext(),
                R.layout.dropbox_folder_item, 0, this.dropboxItems);

        ListView listView = (ListView)findViewById(R.id.select_folder_list_view);
        listView.setAdapter(listViewAdapter);
    }

    private class ListViewAdapter extends ArrayAdapter<DropboxItem> {

        public ListViewAdapter(Context context, int resource, int textViewResourceId, List<DropboxItem> objects) {

            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if(convertView == null) {

                convertView = getLayoutInflater().inflate(R.layout.dropbox_folder_item, parent, false);
            }

            DropboxItem dropboxItem = dropboxItems.get(position);

            ImageView imageView = (ImageView)convertView.findViewById(R.id.select_folder_icon);
            imageView.setImageResource(dropboxItem.getIcon());

            TextView title = (TextView)convertView.findViewById(R.id.select_folder_title);
            title.setText(dropboxItem.getPath());

            final CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.select_folder_checkBox);

            final View finalConvertView = convertView;
            checkBox.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    deselectPreviousCheckbox();
                    selectCheckbox(finalConvertView, position);
                }
            });

            return convertView;
        }
    }
}