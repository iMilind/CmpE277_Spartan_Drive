package com.example.milindmahajan.spartandrive.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.Session;
import com.example.milindmahajan.spartandrive.activities.MainActivity;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Jatin on 11/28/2015.
 */
public class Common
{
    final static public String APP_KEY = "xxxx";
    final static public String APP_SECRET = "xxxx";

    final static public String ACCOUNT_PREFS_NAME = "prefs";
    final static public String ACCESS_KEY_NAME = "ACCESS_KEY";
    final static public String ACCESS_SECRET_NAME = "ACCESS_SECRET";

    final static public String METHOD_COPY = "COPY";
    final static public String METHOD_MOVE = "MOVE";
    final static public String METHOD_DELETE = "DELETE";
    final static public String METHOD_CREATE_FOLDER = "CREATE_FOLDER";
    final static public String METHOD_UPLOAD = "UPLOAD";
    final static public String METHOD_RENAME = "RENAME";

    public static final String rootDIR = "/SpartanDrive";


    private static DropboxAPI<AndroidAuthSession> mApi;

    public static DropboxAPI<AndroidAuthSession> getDropboxObj() {
        return mApi;
    }

    public static void setDropboxObj(DropboxAPI<AndroidAuthSession> dropboxObj) {
        mApi = dropboxObj;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public static void showNetworkAlert(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Network Alert");
        builder.setMessage("Please check your network connection and try again");
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public static String getRootDIR()
    {
        return rootDIR;
    }


    public static String getPath() {
        String path = "";
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else if ((new File("/mnt/emmc")).exists()) {
            path = "/mnt/emmc";
        } else {
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return path + "/SpartanDrive";
    }

    public static String getLocalRoot()
    {
        String p2 = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

        return p2+"/SpartanDrive";
    }
}
