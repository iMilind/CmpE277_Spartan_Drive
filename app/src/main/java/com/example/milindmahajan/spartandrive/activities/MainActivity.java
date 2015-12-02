package com.example.milindmahajan.spartandrive.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.example.milindmahajan.spartandrive.R;
import com.example.milindmahajan.spartandrive.fragments.ListViewFragment;
import com.example.milindmahajan.spartandrive.model.DropboxItem;
import com.example.milindmahajan.spartandrive.singletons.ApplicationSettings;
import com.example.milindmahajan.spartandrive.utils.Common;
import com.example.milindmahajan.spartandrive.utils.FileTasks;
import com.example.milindmahajan.spartandrive.utils.ListFilesTask;
import com.example.milindmahajan.spartandrive.utils.ShareTask;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ListViewFragment.ListViewFragmentProtocol {

    private ActionMode actionMode;

    private DropboxItem rootFolder = new DropboxItem();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!ApplicationSettings.getSharedSettings().isAuthenticated()) {

            AndroidAuthSession session = buildSession();
            Common.setDropboxObj(new DropboxAPI<AndroidAuthSession>(session));
            Common.getDropboxObj().getSession().startOAuth2Authentication(MainActivity.this);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            rootFolder = extras.getParcelable("rootFolder");
        } else {

            rootFolder.setName("Spartan Drive");
            rootFolder.setPath(Common.rootDIR);
        }

        try {

            setTitle(rootFolder.getName());
        } catch (Exception exc) {

            rootFolder = new DropboxItem();

            rootFolder.setName("Spartan Drive");
            rootFolder.setPath(Common.rootDIR);

            setTitle(rootFolder.getName());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
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
                        + e.getLocalizedMessage());
            }
        }

        if(mLoggedIn)
        {
            try
            {
                createDIR();
            }
            catch (DropboxException e) {

                Log.i("DbAuthLog", "Error creating SpartanDrive Folder..", e);
                showToast("Error creating SpartanDrive Folder..Please contact Administrator");
            }
        }

    }

    private void showToast(String msg) {

        Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        error.show();
    }

    public void setLoggedIn(boolean loggedIn) {

        mLoggedIn = loggedIn;
        ApplicationSettings.getSharedSettings().setAuthenticated(loggedIn);
        if (loggedIn) {

            onResume = false;
        }

    }

    public void refreshList(String path)
    {
        ListFilesTask t = (ListFilesTask) new ListFilesTask(new ListFilesTask.AsyncResponse() {

            @Override
            public void processFinish(ArrayList<DropboxAPI.Entry> output) {
                ArrayList<DropboxItem> result = new ArrayList<DropboxItem>();


                for(DropboxAPI.Entry e : output) {

                    DropboxItem dropboxItem = new DropboxItem(e);
                    result.add(dropboxItem);
                }
                ListViewFragment listViewFragment = (ListViewFragment)getSupportFragmentManager()
                        .findFragmentById(R.id.list_view_fragment);
                listViewFragment.reloadListView(result);
            }
        }).execute(path);
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

    class ActionBarCallBack implements ActionMode.Callback {

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch (item.getItemId()) {

                case R.id.item_delete:

                    ListViewFragment listViewFragment = (ListViewFragment)getSupportFragmentManager()
                            .findFragmentById(R.id.list_view_fragment);

                    deleteFromDropbox(listViewFragment.selectedDropboxItems());

                case R.id.item_share:
                    ListViewFragment listViewFragment1 = (ListViewFragment)getSupportFragmentManager()
                            .findFragmentById(R.id.list_view_fragment);
                    shareFromDropbox(listViewFragment1.selectedDropboxItems());

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

    private void deleteFromDropbox(ArrayList <DropboxItem> dropboxItems) {

        for (DropboxItem item : dropboxItems) {

            FileTasks f = (FileTasks) new FileTasks(MainActivity.this,
                    new FileTasks.AsyncResponse() {

                @Override
                public void processFinish(boolean result) {

                    if(result) {

                        refreshList(rootFolder.getPath());
                    }
                }
            }).execute(Common.METHOD_DELETE, item.getPath());
        }
    }

    public void shareFromDropbox(final ArrayList<DropboxItem> dropboxItems) {

        final ArrayList <String> shareUrls = new ArrayList<String>();
        for (DropboxItem item : dropboxItems) {

            ShareTask f = (ShareTask) new ShareTask(MainActivity.this, new ShareTask.AsyncResponse() {

                @Override
                public void processFinish(String result) {

                    if (result != null) {

                        shareUrls.add(result);

                        if (shareUrls.size() == dropboxItems.size()) {

                            StringBuilder builder = new StringBuilder();
                            for(int i = 0; i < shareUrls.size(); i++) {

                                if (i < shareUrls.size()-1) {

                                    builder.append(shareUrls.get(i)).append(", ");
                                } else {

                                    builder.append(shareUrls.get(i));
                                }
                            }

                            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:deepakrkole@gmail.com?subject=" +
                                    Uri.encode("File shared from SpartaDrive") + "&body=" +
                                    Uri.encode(builder.toString())));
                            startActivity(intent);
                        }
                    }
                }
            }).execute(item);
        }
    }



    public void createDIR() throws DropboxException {

        ListFilesTask t = (ListFilesTask) new ListFilesTask(new ListFilesTask.AsyncResponse() {

            @Override
            public void processFinish(ArrayList<DropboxAPI.Entry> output) {

                // SpartanDrive Folder does not exist.. create it
                if(output.size()==0) {

                    FileTasks f = (FileTasks)new FileTasks(MainActivity.this, new FileTasks.AsyncResponse() {

                        @Override
                        public void processFinish(boolean result) {

                            //refreshList(Common.rootDIR);

                        }
                    }).execute(Common.METHOD_CREATE_FOLDER, rootFolder.getPath());
                } else {

                    refreshList(rootFolder.getPath());
                }
            }
        }).execute(Common.rootDIR);

    }

    public void viewFile(final DropboxItem dropboxItem) {

        ShareTask f = (ShareTask) new ShareTask(MainActivity.this, new ShareTask.AsyncResponse() {

            @Override
            public void processFinish(String result) {

                if (result != null) {

                    dropboxItem.setShareLink(result);
                    Intent filePreviewIntent = new Intent(getApplicationContext(), FilePreviewActivity.class);
                    filePreviewIntent.putExtra("dropboxItem", dropboxItem);
                    startActivity(filePreviewIntent);
                }
            }
        }).execute(dropboxItem);
    }

    public void openFolder (DropboxItem dropboxItem) {

        Intent folderNavigatorIntent = new Intent(getApplicationContext(), MainActivity.class);
        folderNavigatorIntent.putExtra("rootFolder", dropboxItem);
        startActivity(folderNavigatorIntent);
    }

    public void viewDropboxItem(final DropboxItem dropboxItem) {

        if (dropboxItem.isDir()) {

            openFolder(dropboxItem);
        } else {

            viewFile(dropboxItem);
        }
    }

    public void beginContextualActionMode(ArrayList <DropboxItem> selectedItems) {

        actionMode = MainActivity.this.startActionMode(new ActionBarCallBack());
    }

    public void endContextualActionMode() {

        actionMode.finish();
    }

    public void deleteDropboxItems(ArrayList <DropboxItem> toBeDeleted) {

        deleteFromDropbox(toBeDeleted);
    }
}