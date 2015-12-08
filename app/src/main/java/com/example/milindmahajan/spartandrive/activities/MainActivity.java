package com.example.milindmahajan.spartandrive.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.example.milindmahajan.spartandrive.R;
import com.example.milindmahajan.spartandrive.fragments.ListViewFragment;
import com.example.milindmahajan.spartandrive.model.AccountInfo;
import com.example.milindmahajan.spartandrive.model.DropboxItem;
import com.example.milindmahajan.spartandrive.singletons.ApplicationSettings;
import com.example.milindmahajan.spartandrive.utils.AccountInfoTask;
import com.example.milindmahajan.spartandrive.utils.Common;
import com.example.milindmahajan.spartandrive.utils.FileTasks;
import com.example.milindmahajan.spartandrive.utils.ListFilesTask;
import com.example.milindmahajan.spartandrive.utils.ShareTask;
import com.example.milindmahajan.spartandrive.utils.UploadTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Stack;

public class MainActivity
        extends AppCompatActivity
        implements ListViewFragment.ListViewFragmentProtocol {

    private ActionMode actionMode;

    private static final int FOLDER_SELECT_ACTIVITY_RESULT_MOVE = 13;
    private static final int FOLDER_SELECT_ACTIVITY_RESULT_COPY = 14;
    public static final int CANNOT_BE_MOVED = 15;

    private boolean mLoggedIn, onResume;
    private int PICK_IMAGE = 0;
    private int PICK_PDF = 1;

    private String selectedFolderPath;

    private Stack <DropboxItem> folderStack = new Stack<DropboxItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#5AC5A7")));

        if (!ApplicationSettings.getSharedSettings().isAuthenticated()) {

            AndroidAuthSession session = buildSession();
            Common.setDropboxObj(new DropboxAPI<AndroidAuthSession>(session));
            Common.getDropboxObj().getSession().startOAuth2Authentication(MainActivity.this);
        }

        if (folderStack.empty()) {

            DropboxItem rootFolder = new DropboxItem();

            rootFolder.setDir(true);
            rootFolder.setName("Spartan Drive");
            rootFolder.setPath(Common.rootDIR);

            folderStack.push(rootFolder);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);

        System.out.println("onWindowFocusChanged");
    }

    @Override
    protected void onStart() {

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
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {

        ListViewFragment listViewFragment = (ListViewFragment)getSupportFragmentManager().findFragmentById(R.id.list_view_fragment);
        if (listViewFragment.isSearchModeOn()) {

            listViewFragment.setSearchMode(false);
            return;
        }

        popFolderFromStack();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        String obj = "";
        Intent pickIntent = null;

        switch (id) {

            case R.id.image:
                pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intentPicker("image",pickIntent);

                return true;

            case R.id.video:
                pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                intentPicker("video",pickIntent);
                return true;

            case R.id.docs:
                showFileChooser();
                return true;

            case R.id.item_create_folder:
                openAlertDialogueForCreateFolder();
                return true;

            case R.id.action_acc_info:
                getAcctInfo();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openAlertDialogueForCreateFolder () {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Folder name");
        builder.setMessage("This folder will be created under " + getTop().getName());

        final EditText folderNameInput = new EditText(this);

        folderNameInput.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(folderNameInput);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(folderNameInput.getWindowToken(), 0);

                MainActivity.this.createFolderWithName(folderNameInput.getText().toString());
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(folderNameInput.getWindowToken(), 0);

                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(Color.parseColor("#F44336"));

        Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setTextColor(Color.parseColor("#2196F3"));

        folderNameInput.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void createFolderWithName(String folderName) {

        FileTasks f = (FileTasks) new FileTasks(MainActivity.this,
                new FileTasks.AsyncResponse() {

                    @Override
                    public void processFinish(boolean result) {

                        if(result) {

                            refreshList();
                        }
                    }
                }).execute(Common.METHOD_CREATE_FOLDER, getTop().getPath()+ File.separator+folderName);
    }

    public void intentPicker(String obj, Intent pickIntent) {

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
        startActivityForResult(intent, PICK_PDF);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        try {

            if ((requestCode == PICK_IMAGE || requestCode == PICK_PDF) && resultCode == Activity.RESULT_OK) {

                if (data == null) {

                    return;
                }
                else {

                    uploadFile(data, requestCode);
                }

            }

            if (requestCode == FOLDER_SELECT_ACTIVITY_RESULT_MOVE) {

                if (resultCode == MainActivity.RESULT_OK) {

                    selectedFolderPath = new String( data.getStringExtra("folderPath"));

                    moveDropboxItems(toBeMovedItems, selectedFolderPath);
                } else if (resultCode == MainActivity.CANNOT_BE_MOVED) {

                    showToast("Cannot be moved!");
                }
            }

            if (requestCode == FOLDER_SELECT_ACTIVITY_RESULT_COPY && resultCode == MainActivity.RESULT_OK) {


                if (resultCode == MainActivity.RESULT_OK) {

                    selectedFolderPath = new String( data.getStringExtra("folderPath"));

                    copyDropboxItems(toBeMovedItems, selectedFolderPath);
                } else if (resultCode == MainActivity.CANNOT_BE_MOVED) {

                    showToast("Cannot be copied!");
                }
            }
        }
        catch(Exception e) {

            e.printStackTrace();
            Log.d("intent_result","reached here");
        }

    }

    private void uploadFile(Intent data, int requestCode) throws IOException {

        InputStream inputStream = getContentResolver().openInputStream(data.getData());
        int size = inputStream.available();
        String fileName = getFileName(data, requestCode);
        Log.i("TEST", "File Size: " + inputStream.available());

        UploadTask u = new UploadTask(MainActivity.this, fileName, inputStream, size);
        u.execute();
        Log.d("intent_result", "reached here");
    }

    private String getFileName(Intent data, int requestCode) {

        String path = data.getData().getPath();
        String[] pathArr = null;
        String fileName = "";
        String baseName = "";
        String type = getContentResolver().getType(data.getData());

        if(requestCode == 0) {

            pathArr = path.split(File.separator);

        }
        else if(requestCode == 1) {

            pathArr = path.split("=");
        }

        fileName = pathArr[pathArr.length-1];
        pathArr = type.split(File.separator);
        baseName = pathArr[0];
        type = pathArr[1];

        fileName = baseName + "_" + fileName + "." + type;
        fileName = getTop().getPath() + File.separator + fileName;

        return  fileName;
    }

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

        if(mLoggedIn) {

            try {

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

    public void refreshList() {

        final DropboxItem rootFolder = getTop();
        setTitle(rootFolder.getName());
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

                listViewFragment.reloadListView(result, !rootFolder.getName().equalsIgnoreCase("Spartan Drive"));
            }
        }).execute(rootFolder.getPath());
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

            ListViewFragment listViewFragment = (ListViewFragment)getSupportFragmentManager()
                    .findFragmentById(R.id.list_view_fragment);

            switch (item.getItemId()) {

                case R.id.item_delete:

                    deleteFromDropbox(listViewFragment.selectedDropboxItems());
                    mode.finish();
                    return true;

                case R.id.item_share:

                    shareFromDropbox(listViewFragment.selectedDropboxItems());
                    mode.finish();
                    return true;

                case R.id.item_download:

                    mode.finish();
                    return true;

                case R.id.item_move:

                    startFolderSelectionIntent(listViewFragment.selectedDropboxItems(), FOLDER_SELECT_ACTIVITY_RESULT_MOVE);
                    mode.finish();
                    return true;

                case R.id.item_copy:

                    startFolderSelectionIntent(listViewFragment.selectedDropboxItems(), FOLDER_SELECT_ACTIVITY_RESULT_COPY);
                    mode.finish();
                    return true;
            }

            return false;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {

            try {

                mode.getMenuInflater().inflate(R.menu.contextual_list_view, menu);
            } catch (Exception exc) {

                exc.printStackTrace();
                System.out.println("Sys exc is "+exc.getLocalizedMessage());
            }

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

                                refreshList();
                            }
                        }
                    }).execute(Common.METHOD_DELETE, item.getPath());
        }
    }

    public void getAcctInfo(){

        AccountInfoTask accountInfoTask = (AccountInfoTask) new AccountInfoTask(MainActivity.this, new AccountInfoTask.AsyncResponse() {

            @Override
            public void processFinish(AccountInfo accountInfo) {

                acctInfoDialogBox(accountInfo);

            }
        }).execute();

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

                            sendEmailDialogBox(builder);
                        }
                    }
                }
            }).execute(item);
        }
    }

    private void sendEmailDialogBox(final StringBuilder stringBuilder) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Share with");

        final EditText emailInput = new EditText(this);
        emailInput.setHint("Use comma to separate multiple emails");

        emailInput.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(emailInput);

        builder.setPositiveButton("Share", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(emailInput.getWindowToken(), 0);

                MainActivity.this.openEmailIntent(stringBuilder, emailInput.getText().toString());
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(emailInput.getWindowToken(), 0);

                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(Color.parseColor("#F44336"));

        Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setTextColor(Color.parseColor("#2196F3"));

        emailInput.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void openEmailIntent (StringBuilder stringBuilder, String toEmail) {

        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"+toEmail+"?subject=" +
                Uri.encode("File shared from SpartaDrive") + "&body=" +
                Uri.encode(stringBuilder.toString())));
        startActivity(intent);
    }


    private void acctInfoDialogBox(final AccountInfo accountInfo) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Account Info");

        final EditText acctName = new EditText(this);
        final EditText email = new EditText(this);
        final EditText quota = new EditText(this);
        final EditText quotaNormal = new EditText(this);
        final EditText sharedQuota = new EditText(this);
        final EditText freeSpace = new EditText(this);

        Context context = getApplicationContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        acctName.setText(accountInfo.getDisplayName());
        acctName.setBackgroundResource(android.R.color.transparent);
        acctName.setFocusable(false);
        acctName.setInputType(InputType.TYPE_CLASS_TEXT);
        acctName.setTextColor(Color.parseColor("#2196F3"));
        layout.addView(acctName);

        email.setText(accountInfo.getEmail());
        email.setBackgroundResource(android.R.color.transparent);
        email.setFocusable(false);
        email.setInputType(InputType.TYPE_CLASS_TEXT);
        email.setTextColor(Color.parseColor("#2196F3"));
        layout.addView(email);

        quota.setText("Total space: " + accountInfo.getQuota() + " MB");
        quota.setBackgroundResource(android.R.color.transparent);
        quota.setFocusable(false);
        quota.setInputType(InputType.TYPE_CLASS_TEXT);
        quota.setTextColor(Color.parseColor("#5AC5A7"));
        layout.addView(quota);

        quotaNormal.setText("Used space: " + accountInfo.getQuotaNormal() + " MB");
        quotaNormal.setBackgroundResource(android.R.color.transparent);
        quotaNormal.setFocusable(false);
        quotaNormal.setInputType(InputType.TYPE_CLASS_TEXT);
        quotaNormal.setTextColor(Color.parseColor("#5AC5A7"));
        layout.addView(quotaNormal);

        sharedQuota.setText("Shared space: " + accountInfo.getQuotaShared() + " MB");
        sharedQuota.setBackgroundResource(android.R.color.transparent);
        sharedQuota.setFocusable(false);
        sharedQuota.setInputType(InputType.TYPE_CLASS_TEXT);
        sharedQuota.setTextColor(Color.parseColor("#5AC5A7"));
        layout.addView(sharedQuota);

        freeSpace.setText("Available space: " + accountInfo.getFreeSpace() + " MB");
        freeSpace.setBackgroundResource(android.R.color.transparent);
        freeSpace.setFocusable(false);
        freeSpace.setInputType(InputType.TYPE_CLASS_TEXT);
        freeSpace.setTextColor(Color.parseColor("#F44336"));
        layout.addView(freeSpace);

        builder.setView(layout);

        builder.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

    private void moveDropboxItems(final ArrayList <DropboxItem> dropboxItems, final String moveToPath) {

        for (DropboxItem item : dropboxItems) {

            FileTasks f = (FileTasks) new FileTasks(MainActivity.this,
                    new FileTasks.AsyncResponse() {

                        @Override
                        public void processFinish(boolean result) {

                            if(result) {

                                refreshList();
                            }
                        }
                    }).execute(Common.METHOD_MOVE, item.getPath(), moveToPath+"/"+item.getName());
        }
    }

    private void copyDropboxItems(final ArrayList <DropboxItem> dropboxItems, final String copyToPath) {

        for (DropboxItem item : dropboxItems) {

            FileTasks f = (FileTasks) new FileTasks(MainActivity.this,
                    new FileTasks.AsyncResponse() {

                        @Override
                        public void processFinish(boolean result) {

                            if(result) {

                                refreshList();
                            }
                        }
                    }).execute(Common.METHOD_COPY, item.getPath(), copyToPath+"/"+item.getName());
        }
    }

    public void createDIR() throws DropboxException {

        ListFilesTask t = (ListFilesTask) new ListFilesTask(new ListFilesTask.AsyncResponse() {

            @Override
            public void processFinish(ArrayList<DropboxAPI.Entry> output) {

                if(output.size()==0) {

                    FileTasks f = (FileTasks)new FileTasks(MainActivity.this, new FileTasks.AsyncResponse() {

                        @Override
                        public void processFinish(boolean result) {


                        }
                    }).execute(Common.METHOD_CREATE_FOLDER, getTop().getPath());
                } else {

                    ListViewFragment listViewFragment = (ListViewFragment) getSupportFragmentManager().
                            findFragmentById(R.id.list_view_fragment);
                    if (!listViewFragment.isSearchModeOn()) {

                        refreshList();
                    } else {

                        ArrayList temp = listViewFragment.dropboxItems();
                        if (temp.size() == 0) {

                            refreshList();
                        }
                    }
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

    public void viewDropboxItem(final DropboxItem dropboxItem) {

        if (dropboxItem.isDir()) {

            pushFolderOnStack(dropboxItem);

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

    ArrayList <DropboxItem> toBeMovedItems = new ArrayList<DropboxItem>();
    public void moveDropboxItem(ArrayList <DropboxItem> toBeMoved) {

        startFolderSelectionIntent(toBeMoved, FOLDER_SELECT_ACTIVITY_RESULT_MOVE);
    }

    public void copyDropboxItem(ArrayList <DropboxItem> toBeMoved) {

        startFolderSelectionIntent(toBeMoved, FOLDER_SELECT_ACTIVITY_RESULT_COPY);
    }

    DropboxItem selectedItem = new DropboxItem();
    public void renameDropboxItem(DropboxItem item) {

        selectedItem = item;
        openAlertDialogueRenameFile();
    }

    private void openAlertDialogueRenameFile () {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New title");
        builder.setMessage(selectedItem.getName()+" will be renamed");

        final EditText fileNameInput = new EditText(this);

        fileNameInput.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(fileNameInput);

        builder.setPositiveButton("Rename", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(fileNameInput.getWindowToken(), 0);

                MainActivity.this.rename(fileNameInput.getText().toString());
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(fileNameInput.getWindowToken(), 0);

                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(Color.parseColor("#F44336"));

        Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setTextColor(Color.parseColor("#2196F3"));

        fileNameInput.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void rename(String newName) {

        String name = null;

        if (!selectedItem.isDir()) {

            name = selectedItem
                    .getParentPath()
                    +newName
                    +"."+selectedItem.getExtension();
        } else {

            name = selectedItem
                    .getParentPath()
                    +newName;
        }

        if (newName.trim().length() != 0) {

            FileTasks f = (FileTasks) new FileTasks(MainActivity.this,
                    new FileTasks.AsyncResponse() {

                        @Override
                        public void processFinish(boolean result) {

                            if(result) {

                                refreshList();
                            }
                        }
                    }).execute(Common.METHOD_RENAME, selectedItem.getPath(), name);
        } else {

            showToast("Enter a valid name for file!");
        }
    }

    private void startFolderSelectionIntent(ArrayList <DropboxItem> toBeMoved, int intentCode) {

        toBeMovedItems.removeAll(toBeMovedItems);
        toBeMovedItems.addAll(toBeMoved);
        Intent folderSelectIntent = new Intent(getApplicationContext(), FolderSelectionActivity.class);
        folderSelectIntent.putExtra("parentFolder", toBeMoved.get(0));
        startActivityForResult(folderSelectIntent, intentCode);
    }

    @Override
    public DropboxItem getRootFolder() {

        return getTop();
    }

    @Override
    public void refreshRootFolder() {

        refreshList();
    }

    public void backPressed () {

        popFolderFromStack();
    }

    private void pushFolderOnStack(DropboxItem folder) {

        folderStack.push(folder);
        refreshList();
    }

    private void popFolderFromStack () {

        if (folderStack.size() > 1) {

            folderStack.pop();
            refreshList();
        }
    }

    private DropboxItem getTop () {

        return folderStack.peek();
    }
}