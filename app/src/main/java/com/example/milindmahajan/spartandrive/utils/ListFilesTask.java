package com.example.milindmahajan.spartandrive.utils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.example.milindmahajan.spartandrive.model.DropboxItem;

import java.util.ArrayList;

/**
 * Created by Jatin on 11/28/2015.
 */

// ToDo:
//   1. Exception Handling and Logging
//   2. Testing

/**
 * Use this task with a handler;
 * Sample Code for the handler
 *
 *  private final Handler handler = new Handler() {
    public void handleMessage(Message msg) {
        ArrayList<String> result = msg.getData().getStringArrayList("data");
        for (String fileName : result) {
            Log.i("ListFiles", fileName);
            TextView tv = new TextView(DropboxActivity.this);
            tv.setText(fileName);
        }
    }
 };
 */
public class ListFilesTask extends AsyncTask<String, Void, ArrayList<String>> {

    private DropboxAPI<?> dropbox;
    private Handler handler;

    public ListFilesTask(Handler handler) {

        this.dropbox = Common.getDropboxObj();
        this.handler = handler;
    }

    @Override
    protected ArrayList<String> doInBackground(String... params) {

        ArrayList<String> fileList = new ArrayList<String>();
        try {

            DropboxAPI.Entry dir = dropbox.metadata(params[0], 1000, null, true, null);
            for (DropboxAPI.Entry entry : dir.contents) {

                fileList.add(entry.path);
            }
        } catch (DropboxException e) {

            e.printStackTrace();
        }

        return fileList;
    }

    @Override
    protected void onPostExecute(ArrayList<String> result) {

        Message msgObj = handler.obtainMessage();
        Bundle b = new Bundle();
        b.putStringArrayList("data", result);
        msgObj.setData(b);
        handler.sendMessage(msgObj);
    }
}