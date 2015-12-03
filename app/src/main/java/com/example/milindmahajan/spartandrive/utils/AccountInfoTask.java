package com.example.milindmahajan.spartandrive.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.example.milindmahajan.spartandrive.model.AccountInfo;

/**
 * Created by deepakkole on 12/2/15.
 */
public class AccountInfoTask extends AsyncTask <String, Void, AccountInfo> {

    private DropboxAPI<?> dropbox;
    private Context context;
    private String message = "";
    public AsyncResponse respObj = null;

    public AccountInfoTask(Context context, AsyncResponse respObj) {

        this.context = context.getApplicationContext();
        this.respObj = respObj;
        dropbox = Common.getDropboxObj();
    }

    public interface AsyncResponse {

        void processFinish(AccountInfo accountInfo);
    }

    @Override
    protected AccountInfo doInBackground(String... params) {

        AccountInfo accountInfo = null;

        try {

            accountInfo = new AccountInfo(dropbox.accountInfo());
        } catch(DropboxException ex) {

            ex.printStackTrace();
        }

        return accountInfo;
    }

    @Override
    protected void onPostExecute(AccountInfo accountInfo) {

        respObj.processFinish(accountInfo);
    }
}