package com.example.milindmahajan.spartandrive.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jatin on 11/28/2015.
 */
public class FileOperations {

    // TODO:
    //    1. Check Network before making all these calls
    //    2. Test with a common task obj instead of creating new everytime
    //    3. Uploading logic.
    //    4. File Sharing operation
    //    5. Better Pattern to execute these methods.
    //          Refer: http://stackoverflow.com/questions/7294533/passing-parameters-to-asynctask

    public static void createFolder(Context context, String path) {

        Task t = new Task(context);
        if(Common.getDropboxObj()!=null) {

            t.execute("CREATE", path);
        }
    }

    public static void copy(Context context, String oldPath, String newPath) {

        Task t = new Task(context);
        if(Common.getDropboxObj()!=null) {

            t.execute("COPY", oldPath, newPath);
        }
    }

    public static void move(Context context, String oldPath, String newPath) {

        Task t = new Task(context);
        if(Common.getDropboxObj()!=null) {

            t.execute("MOVE", oldPath, newPath);
        }
    }

    public static void upload(Context context) {

        Task t = new Task(context);
        if(Common.getDropboxObj()!=null) {

            t.execute("UPLOAD");
        }
    }

    public static void delete(Context context, String path) {

        Task t = new Task(context);
        if(Common.getDropboxObj()!=null) {

            t.execute("DELETE", path);
        }
    }

    static class Task extends AsyncTask<String, Void, Boolean> {

        private DropboxAPI<?> dropbox;
        private Context context;
        private String message = "";

        public Task(Context context) {

            this.context = context.getApplicationContext();
            dropbox = Common.getDropboxObj();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            String method = params[0];
            try {

                switch (method) {

                    case "COPY":
                        Log.d("FileOperation", "Copying File: " + params[1]);
                        message = "copied";
                        dropbox.copy(params[1], params[2]);
                        return true;

                    case "MOVE":
                        Log.d("FileOperation", "Moving File: " + params[1]);
                        message = "moved";
                        dropbox.move(params[1], params[2]);
                        return true;

                    case "DELETE":
                        Log.d("FileOperation", "Deleting File: " + params[1]);
                        message = "deleted";
                        dropbox.delete(params[1]);
                        return true;

                    case "CREATE":
                        Log.d("FileOperation", "Creating Folder: " + params[1]);
                        message = "created";
                        dropbox.createFolder(params[1]);
                        return true;

                    case "UPLOAD":
                        final File tempDir = context.getCacheDir();
                        File tempFile;
                        FileWriter fr;

                        tempFile = File.createTempFile("file", ".txt", tempDir);
                        fr = new FileWriter(tempFile);
                        fr.write("Sample text file created for demo purpose. You may use some other file format for your app ");
                        fr.close();

                        FileInputStream fileInputStream = new FileInputStream(tempFile);
                        dropbox.putFile("/textfile.txt", fileInputStream,
                                tempFile.length(), null, null);

                        tempFile.delete();
                }
            } catch (DropboxException e) {

                Log.i("File Dropbox Operation", "Exception: ", e);
                e.printStackTrace();
                message = e.getCause().getMessage();
            }
            catch (IOException e) {

                Log.i("File IO Operation", "Exception: ", e);
                e.printStackTrace();
                message = e.getCause().getMessage();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (result) {

                Toast.makeText(context, "File "+ message + " sucesfully!",
                        Toast.LENGTH_LONG).show();
            } else {

                Toast.makeText(context, "File operation failed: " + message, Toast.LENGTH_LONG)
                        .show();
            }
        }
    }
}