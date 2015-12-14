package com.example.milindmahajan.spartandrive.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxFileSizeException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.example.milindmahajan.spartandrive.model.DropboxItem;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

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

    public DownloadFile(Context c, DropboxItem d, AsyncResponse response)
    {
        mContext = c;
        item = d;
        mFileLen = 1000;
        mApi = Common.getDropboxObj();
        this.response = response;
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
            // TODO Auto-generated catch block
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

    /*private void showFileExitsDialog(final Entry fileSelected,
                                     final File localFile) {
        AlertDialog.Builder alertBuilder = new Builder(DropboxDownload.this);
        alertBuilder.setMessage(Constants.OVERRIDEMSG);
        alertBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        copy(fileSelected, localFile);
                    }
                });
        alertBuilder.setNegativeButton("Cancel", null);
        alertBuilder.create().show();

    }
*/
}
