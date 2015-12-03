package com.example.milindmahajan.spartandrive.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by deepakkole on 12/2/15.
 */
public class AccountInfo extends AsyncTask<LinkedHashMap<String, String>,Void,LinkedHashMap<String, String>> {

    private DropboxAPI<?> dropbox;
    private Context context;
    private String message = "";
    public AsyncResponse respObj = null;


    private String acctName=null;
    private String size=null;
    private String numberOfFiles=null;
    private long quotaShared=0;
    private String email = null;
    private String nameDetails=null;
    private String teamInfo=null;
    private long quotaNormal=0;
    private long quota=0;
    private long freeSpace=0;
    private LinkedHashMap<String, String> details;

    public AccountInfo(Context context, AsyncResponse respObj) {
        this.context = context.getApplicationContext();
        this.respObj = respObj;
        dropbox = Common.getDropboxObj();

    }


    public interface AsyncResponse {

        void processFinish(Map<String, String> result);
    }



    @Override
    protected LinkedHashMap<String,String> doInBackground(LinkedHashMap<String, String>... params) {
        try{
            details = new LinkedHashMap<String,String>();
            acctName = dropbox.accountInfo().displayName;
            quota=dropbox.accountInfo().quota;
            quotaShared=dropbox.accountInfo().quotaShared;
            quotaNormal=dropbox.accountInfo().quotaNormal;
            email=dropbox.accountInfo().email;

            quota =  quota   /   1024    /   1024;
            quotaShared =  quotaShared   /   1024    /   1024;
            quotaNormal =  quotaNormal / 1024 /1024;

            freeSpace =quota-quotaNormal-quotaShared;
            freeSpace = freeSpace / 1024 / 1024;

            details.put("accountName", acctName);
            details.put("sharedQuota",  String.valueOf(quotaShared));
            details.put("quota", String.valueOf(quota));
            details.put("email", email);
            details.put("freeSpace", String.valueOf(freeSpace));



        }catch(DropboxException ex){
            ex.printStackTrace();
        }
        return details;
    }

    @Override
    protected void onPostExecute(LinkedHashMap<String, String> s) {

        respObj.processFinish(s);
    }
}
