package com.example.milindmahajan.spartandrive.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Jatin on 11/30/2015.
 */
public class FileTasks extends AsyncTask<String, Void, Boolean>
{

    private DropboxAPI<?> dropbox;
    private Context context;
    private String message = "";
    public AsyncResponse respObj = null;

    public FileTasks(Context context, AsyncResponse respObj) {

        this.context = context.getApplicationContext();
        this.respObj = respObj;
        dropbox = Common.getDropboxObj();
    }


    // #########################Interface code ############################
    public interface AsyncResponse {
        void processFinish(boolean result);
    }
    //#######################################################################

    @Override
    protected Boolean doInBackground(String... params) {

        String method = params[0];
        try {

            switch (method) {

                case Common.METHOD_COPY:
                    Log.d("FileOperation", "Copying File: " + params[1]);
                    message = "copied";
                    dropbox.copy(params[1], params[2]);
                    return true;

                case Common.METHOD_MOVE:
                    Log.d("FileOperation", "Moving File: " + params[1]);
                    message = "moved";
                    dropbox.move(params[1], params[2]);
                    return true;

                case Common.METHOD_DELETE:
                    Log.d("FileOperation", "Deleting File: " + params[1]);
                    message = "deleted";
                    dropbox.delete(params[1]);
                    return true;

                case Common.METHOD_CREATE_FOLDER:
                    Log.d("FileOperation", "Creating Folder: " + params[1]);
                    message = "created";
                    dropbox.createFolder(params[1]);
                    return true;

                case Common.METHOD_RENAME:
                    Log.d("FileOperation", "Moving File: " + params[1]);
                    message = "renamed";
                    dropbox.move(params[1], params[2]);
                    return true;
            }
        } catch (DropboxException e) {

            Log.i("File Dropbox Operation", "Exception: ", e);
            e.printStackTrace();
            message = e.getCause().getMessage();
        }
        return false;
    }


    @Override
    protected void onPostExecute(Boolean result) {

        if (result) {

            Toast.makeText(context, "File " + message + " successfully!",
                    Toast.LENGTH_LONG).show();
        } else {

            Toast.makeText(context, "File operation failed: " + message, Toast.LENGTH_LONG)
                    .show();
        }

        respObj.processFinish(result);
    }
}
