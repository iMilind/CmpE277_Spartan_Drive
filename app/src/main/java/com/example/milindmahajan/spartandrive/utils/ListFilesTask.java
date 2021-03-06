package com.example.milindmahajan.spartandrive.utils;

import android.os.AsyncTask;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;

import java.util.ArrayList;

/**
 * Created by Jatin on 11/28/2015.
 */

// ToDo:
//   1. Exception Handling and Logging
//   2. Testing

public class ListFilesTask extends AsyncTask<String, Void, ArrayList<DropboxAPI.Entry>> {

    private DropboxAPI<?> dropbox = Common.getDropboxObj();
    public AsyncResponse respObj = null;


    // #########################Interface code ############################

    public interface AsyncResponse {

        void processFinish(ArrayList<DropboxAPI.Entry> output);
    }

    public ListFilesTask(AsyncResponse respObj){

        this.respObj = respObj;
    }


    //#######################################################################

    @Override
    protected ArrayList<DropboxAPI.Entry> doInBackground(String... params) {

        ArrayList<DropboxAPI.Entry> fileList = new ArrayList<DropboxAPI.Entry>();
        try {

            DropboxAPI.Entry dir = dropbox.metadata(params[0], 1000, null, true, null);
            fileList.addAll(dir.contents);

        } catch (DropboxException e) {

            e.printStackTrace();
        }

        return fileList;
    }

    @Override
    protected void onPostExecute(ArrayList<DropboxAPI.Entry> result) {

        respObj.processFinish(result);
    }
}