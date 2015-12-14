package com.example.milindmahajan.spartandrive.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.example.milindmahajan.spartandrive.model.DropboxItem;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Jatin on 12/13/2015.
 */
public class DownloadFile extends AsyncTask<Void, Long, Boolean>{

    private DropboxAPI<?> mApi;
    private DropboxAPI.UploadRequest mRequest;
    private Context mContext;
    private ProgressDialog mDialog;
    private String mErrorMsg;
    DropboxItem item;
    private long mFileLen;
    AsyncResponse response;

    public interface AsyncResponse {

        void processFinish(boolean result);
    }

    public DownloadFile(Context c, DropboxItem d, AsyncResponse response) {

        mContext = c;
        item = d;
        mFileLen = 1000;
        mApi = Common.getDropboxObj();
        this.response = response;

        invokeProgressDialog();
    }

    public void invokeProgressDialog() {

        mDialog = new ProgressDialog(mContext);
        mDialog.setMessage("Downloading ");
        mDialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        File rootDirect = new File(Common.getLocalRoot());

        if(!rootDirect.exists()) {

            boolean result = rootDirect.mkdir();
            if(rootDirect.mkdir()); //directory is created;
        }
        File localFile = new File(rootDirect.getPath(), item.getName());

        if (!localFile.exists()) {

            copy(item, localFile);
        }

        return Boolean.TRUE;
    }

    @Override
    protected void onPostExecute(Boolean result) {

        mDialog.dismiss();
        if (result) {

            this.response.processFinish(true);
        } else {

            showToast(mErrorMsg);
        }
    }

    private void showToast(String msg) {

        Toast error = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
        error.show();
    }

    private void copy(final DropboxItem fileSelected, final File localFile) {

        BufferedInputStream br = null;
        BufferedOutputStream bw = null;
        DropboxAPI.DropboxInputStream fd;
        try {

            localFile.createNewFile();
            fd = mApi.getFileStream(fileSelected.getPath(),
                    fileSelected.getRev());
            br = new BufferedInputStream(fd);
            bw = new BufferedOutputStream(new FileOutputStream(
                    localFile));

            byte[] buffer = new byte[4096];
            int read;
            while (true) {

                read = br.read(buffer);
                if (read <= 0) {

                    break;
                }

                bw.write(buffer, 0, read);
            }
        } catch (DropboxException e) {

            e.printStackTrace();
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        } finally {

            if (bw != null) {

                try {

                    bw.close();
                    if (br != null) {

                        br.close();
                    }
                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
        }
    }
}