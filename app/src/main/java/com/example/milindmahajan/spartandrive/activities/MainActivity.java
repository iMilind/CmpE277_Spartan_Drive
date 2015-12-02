package com.example.milindmahajan.spartandrive.activities;

<<<<<<< HEAD
import android.app.Activity;
import android.content.Context;
=======
>>>>>>> dev
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
<<<<<<< HEAD
import android.provider.MediaStore;
=======
import android.os.Parcelable;
>>>>>>> dev
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


import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ListViewFragment.ListViewFragmentProtocol {

    private ActionMode actionMode;

<<<<<<< HEAD
    private static final int CONTEXTMENU_OPTION_VIEW = 1;
    private static final int CONTEXTMENU_OPTION_DELETE = 2;
    private static final int CONTEXTMENU_OPTION_SHARE = 3;
    private static final int CONTEXTMENU_OPTION_DOWNLOAD = 4;
    private static final int CONTEXTMENU_OPTION_MOVE = 5;
    private static final int CONTEXTMENU_OPTION_COPY = 6;
    private static final int CONTEXTMENU_OPTION_CANCEL = 7;
    private boolean mLoggedIn, onResume;
    private int PICK_IMAGE;
=======
    private DropboxItem rootFolder = new DropboxItem();
>>>>>>> dev

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

<<<<<<< HEAD
        if(!mLoggedIn)
        {
            AndroidAuthSession session = buildSession();
            Common.setDropboxObj(new DropboxAPI<AndroidAuthSession>(session));
            Common.getDropboxObj().getSession().startOAuth2Authentication(MainActivity.this);
        }
=======
        if (!ApplicationSettings.getSharedSettings().isAuthenticated()) {
>>>>>>> dev

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

        int id = item.getItemId();
        String obj = "";
        Intent pickIntent = null;
        switch (id)
        {
            case R.id.image:
                pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intentPicker("image",pickIntent);

                return true;

            case R.id.video:
                pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                intentPicker("video",pickIntent);
                return true;

            case R.id.docs:
     /*           pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Files.getContentUri("external"));
                intentPicker("files",pickIntent);
*/
                showFileChooser();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void intentPicker(String obj, Intent pickIntent)
    {

        String type = obj+"/*";

        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType(type);

        Intent chooserIntent = Intent.createChooser(getIntent, "Select " + obj);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);

    }
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select txt file"),
                    PICK_IMAGE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        try
        {
            if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    //Display an error
                    return;
                }
                Context context = MainActivity.this;
                InputStream inputStream = context.getContentResolver().openInputStream(data.getData());
                //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...

                Log.d("intent_result","reached here");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Log.d("intent_result","reached here");
        }

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

                    mode.finish();
                    return true;

                case R.id.item_share:
                    ListViewFragment listViewFragment1 = (ListViewFragment)getSupportFragmentManager()
                            .findFragmentById(R.id.list_view_fragment);
                    shareFromDropbox(listViewFragment1.selectedDropboxItems());

                    mode.finish();
                    return true;

                case R.id.item_download:

                    mode.finish();
                    return true;

                case R.id.item_move:

                    mode.finish();
                    return true;

                case R.id.item_copy:

                    mode.finish();
                    return true;
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

    private void deleteFromDropbox(final ArrayList <DropboxItem> dropboxItems) {

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