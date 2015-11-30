package com.example.milindmahajan.spartandrive.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.example.milindmahajan.spartandrive.R;
import com.example.milindmahajan.spartandrive.utils.Common;
import com.example.milindmahajan.spartandrive.utils.FileOperations;
import com.example.milindmahajan.spartandrive.utils.ListFilesTask;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> results = new ArrayList<String>();
    private ListViewAdapter listViewAdapter;
    private ActionMode actionMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AndroidAuthSession session = buildSession();
        Common.setDropboxObj(new DropboxAPI<AndroidAuthSession>(session));
        Common.getDropboxObj().getSession().startOAuth2Authentication(MainActivity.this);

        ListView listView = (ListView)findViewById(R.id.list_view);
        registerForContextMenu(listView);

        addClickListener();

        if(mLoggedIn)
        {

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private static final int CONTEXTMENU_OPTION_DELETE = 1;
    private static final int CONTEXTMENU_OPTION_SHARE = 2;
    private static final int CONTEXTMENU_OPTION_DOWNLOAD = 3;
    private static final int CONTEXTMENU_OPTION_MOVE = 4;
    private static final int CONTEXTMENU_OPTION_COPY = 5;
    private static final int CONTEXTMENU_OPTION_CANCEL = 6;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(this.results.get(contextMenuInfo.position));

        menu.add(Menu.NONE, CONTEXTMENU_OPTION_DELETE, 0, "Delete");
        menu.add(Menu.NONE, CONTEXTMENU_OPTION_SHARE, 1, "Share");
        menu.add(Menu.NONE, CONTEXTMENU_OPTION_DOWNLOAD, 2, "Download");
        menu.add(Menu.NONE, CONTEXTMENU_OPTION_MOVE, 3, "Move");
        menu.add(Menu.NONE, CONTEXTMENU_OPTION_COPY, 4, "Copy");
        menu.add(Menu.NONE, CONTEXTMENU_OPTION_CANCEL, 5, "Cancel");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        switch (item.getItemId()) {

            case CONTEXTMENU_OPTION_DELETE:

                ArrayList <String> selectedItem = new ArrayList<String>();
                selectedItem.add(this.results.get(contextMenuInfo.position));
                deleteFromDropbox(selectedItem);

                break;

            case CONTEXTMENU_OPTION_SHARE:

                showToast("Share");
                break;

            case CONTEXTMENU_OPTION_DOWNLOAD:

                showToast("Download");
                break;

            case CONTEXTMENU_OPTION_MOVE:

                showToast("Move");
                break;

            case CONTEXTMENU_OPTION_COPY:

                showToast("Copy");
                break;

            case CONTEXTMENU_OPTION_CANCEL:

                showToast("Cancel");
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }


    private void addClickListener(){

        ListView listView = (ListView)findViewById(R.id.list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos,
                                    long id) {

                System.out.println("onItemClick Adapter View Favorite fragment");
            }
        });
    }


    private boolean mLoggedIn, onResume;

    private AndroidAuthSession buildSession() {

        AppKeyPair appKeyPair = new AppKeyPair(Common.APP_KEY, Common.APP_SECRET);
        AndroidAuthSession session = null;
        String[] stored = getKeys();
        if (stored != null) {

            AccessTokenPair accessToken = new AccessTokenPair(stored[0],
                    stored[1]);
            session = new AndroidAuthSession(appKeyPair,accessToken);
        } else {

            session = new AndroidAuthSession(appKeyPair);
        }

        return session;
    }

    protected void onResume() {

        super.onResume();

        DropboxAPI<AndroidAuthSession> mApi = Common.getDropboxObj();
        if (Common.getDropboxObj().getSession().authenticationSuccessful()) {

            try {

                mApi.getSession().finishAuthentication();

                String accessToken = mApi.getSession().getOAuth2AccessToken();
                System.out.print(accessToken);
                storeKeys("oauth2:", accessToken);
                setLoggedIn(true);
            } catch (IllegalStateException e) {

                Log.i("DbAuthLog", "Error authenticating", e);
                showToast("Couldn't authenticate with Dropbox:"
                        + e.getLocalizedMessage());showToast("Couldn't authenticate with Dropbox:"
                        + e.getLocalizedMessage());
            }
        }
        if(mLoggedIn)
        {
            ListFilesTask t = (ListFilesTask) new ListFilesTask(new ListFilesTask.AsyncResponse() {
                @Override
                public void processFinish(ArrayList<DropboxAPI.Entry> output) {
                    ArrayList<String> result = new ArrayList<String>();


                    for(DropboxAPI.Entry e : output)
                    {
                        result.add(e.path);
                    }
                    reloadListView(result);
                }
            }).execute("/SpartanDrive");
        }

    }

    private void showToast(String msg) {

        Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        error.show();
    }

    public void setLoggedIn(boolean loggedIn) {

        mLoggedIn = loggedIn;
        if (loggedIn) {

            onResume = false;
        }

    }

    private void storeKeys(String key, String secret) {

        SharedPreferences prefs = getSharedPreferences(Common.ACCOUNT_PREFS_NAME, 0);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(Common.ACCESS_KEY_NAME, key);
        edit.putString(Common.ACCESS_SECRET_NAME, secret);
        edit.commit();
    }

    private void logOut() {

        Common.getDropboxObj().getSession().unlink();
        clearKeys();
    }

    private void clearKeys() {

        SharedPreferences prefs = getSharedPreferences(
                Common.ACCOUNT_PREFS_NAME, 0);
        SharedPreferences.Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }

    private String[] getKeys() {

        SharedPreferences prefs = getSharedPreferences(Common.ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(Common.ACCESS_KEY_NAME, null);
        String secret = prefs.getString(Common.ACCESS_SECRET_NAME, null);
        if (key != null && secret != null) {

            String[] ret = new String[2];
            ret[0] = key;
            ret[1] = secret;

            return ret;
        } else {

            return null;
        }
    }

    public void reloadListView (ArrayList<String> dropboxItems) {

        this.results.removeAll(this.results);
        this.results.addAll(dropboxItems);

        reloadData(this.results);
    }

    private void reloadData(ArrayList <String> dropboxItems) {

        listViewAdapter = new ListViewAdapter(getApplicationContext(),
                R.layout.dropbox_item, 0, dropboxItems);


        ListView listView = (ListView)findViewById(R.id.list_view);
        listView.setAdapter(listViewAdapter);
    }

    private class ListViewAdapter extends ArrayAdapter<String> {

        ArrayList <String> dropboxItems = new ArrayList<String>();
        ArrayList <String> selectedFiles = new ArrayList<String>();

        public ListViewAdapter(Context context, int resource, int textViewResourceId, List<String> objects) {

            super(context, resource, textViewResourceId, objects);
            this.dropboxItems.addAll(objects);
        }

        public void setNewSelection(int position, boolean value) {

            selectedFiles.add(dropboxItems.get(position));
            notifyDataSetChanged();
        }

        public void removeSelection(int position) {

            removeFromSelection(dropboxItems.get(position));
            notifyDataSetChanged();
        }

        private void removeFromSelection (String stringPath) {

            for (int i = 0; i < selectedFiles.size(); i++) {

                if (selectedFiles.get(i).equals(stringPath)) {

                    selectedFiles.remove(i);
                    break;
                }
            }
        }

        public ArrayList <String> getSelectedFiles () {

            return this.selectedFiles;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if(convertView == null) {

                convertView = MainActivity.this.getLayoutInflater().inflate(R.layout.dropbox_item, parent, false);
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

                    if(isChecked) {

                        listViewAdapter.setNewSelection(position, isChecked);
                    }
                    else {

                        listViewAdapter.removeSelection(position);
                    }

                    if (listViewAdapter.getSelectedFiles().size() != 0) {

                        actionMode = MainActivity.this.startActionMode(new ActionBarCallBack());
                    } else {

                        actionMode.finish();
                    }
                }
            });

            return convertView;
        }
    }


    class ActionBarCallBack implements ActionMode.Callback {

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch (item.getItemId()) {

                case R.id.item_delete:

                    deleteFromDropbox(listViewAdapter.getSelectedFiles());

                case R.id.item_share:

                case R.id.item_download:

                case R.id.item_move:

                case R.id.item_copy:

                    mode.finish();
            }

            return false;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {

            mode.getMenuInflater().inflate(R.menu.contextual_list_view, menu);

            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

            return false;
        }
    }

    private void deleteFromDropbox(ArrayList <String> dropboxItems) {

        for (String path : dropboxItems) {

            FileOperations.delete(getApplicationContext(), path);
        }
    }
}