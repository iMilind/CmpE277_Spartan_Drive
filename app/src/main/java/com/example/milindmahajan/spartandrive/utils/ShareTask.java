package com.example.milindmahajan.spartandrive.utils;

/**
 * Created by deepakkole on 11/30/15.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ShareTask extends AsyncTask<String, Void, String>{
    private DropboxAPI<?> dropbox;
    private Context context;
    private String message = "";
    public AsyncResponse respObj = null;
    public String shareAddress=null;
    public ShareTask(Context context, AsyncResponse respObj) {

        this.context = context.getApplicationContext();
        this.respObj = respObj;
        dropbox = Common.getDropboxObj();
    }


    public interface AsyncResponse {
        void processFinish(String result);
    }

    private static String getShareURL(String strURL) {
        URLConnection conn = null;
        String redirectedUrl = null;
        try {
            URL inputURL = new URL(strURL);
            conn = inputURL.openConnection();
            conn.connect();

            InputStream is = conn.getInputStream();
            System.out.println("Redirected URL: " + conn.getURL());
            redirectedUrl = conn.getURL().toString();
            is.close();

        } catch (MalformedURLException e) {
            Log.d("xxxxx", "Please input a valid URL");
        } catch (IOException ioe) {
            Log.d("xxx", "Can not connect to the URL");
        }

        return redirectedUrl;
    }

    @Override
    protected String doInBackground(String... params) {
    try {
        String method = params[0];
        dropbox = Common.getDropboxObj();
        DropboxAPI.Entry dir = dropbox.metadata(params[0], 1000, null, true, null);
        Log.i("Directory", dir.toString());
        for (DropboxAPI.Entry e : dir.contents) {
            Log.i("Files", e.fileName().toString());
            shareAddress = null;
            if (!e.isDir && e.path.toString().equals(params[1])) {
                DropboxAPI.DropboxLink shareLink = dropbox.share(e.path);
                shareAddress = getShareURL(shareLink.url).replaceFirst("https://www", "https://dl");
                Log.d("XXXXXX", "dropbox share link " + shareAddress);
                break;
            }

        }
    }catch (DropboxException e) {

            Log.i("File Dropbox Operation", "Exception: ", e);
            e.printStackTrace();
            message = e.getCause().getMessage();
        }

        return shareAddress;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result!=null) {
            Toast.makeText(context, "File " + message + " successfully!",
                    Toast.LENGTH_LONG).show();
        } else {

            Toast.makeText(context, "File operation failed: " + message, Toast.LENGTH_LONG)
                    .show();
        }

        respObj.processFinish(result);
    }



}
