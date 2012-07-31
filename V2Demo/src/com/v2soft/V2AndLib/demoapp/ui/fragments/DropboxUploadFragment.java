package com.v2soft.V2AndLib.demoapp.ui.fragments;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.DropboxAPI.UploadRequest;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.v2soft.AndLib.text.NumberFormatters;
import com.v2soft.V2AndLib.demoapp.R;

/**
 * Dialog fragment that allows to uplaod specified file to dropbox application folder
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public class DropboxUploadFragment extends DialogFragment {
    // =========================================================
    // Constants
    // =========================================================
    private static final String KEY_LOCAL_FILE = "local";
    private static final String KEY_REMOTE_FILE = "remote";
    // =========================================================
    // Class fields
    // =========================================================
    private String mLocalPath, mRemotePath;
    private ProgressBar mProgress;
    private long mFileSize;
    private DropboxAPI<AndroidAuthSession> mDropboxApi;
    // =========================================================
    // Constructors
    // =========================================================
    public static DropboxUploadFragment newInstance(String localFilePath, String dropBoxPath) {
        final DropboxUploadFragment result = new DropboxUploadFragment();
        final Bundle bundle = new Bundle();
        bundle.putString(KEY_LOCAL_FILE, localFilePath);
        bundle.putString(KEY_REMOTE_FILE, dropBoxPath);
        result.setArguments(bundle);
        return result;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocalPath = getArguments().getString(KEY_LOCAL_FILE);
        mRemotePath = getArguments().getString(KEY_REMOTE_FILE);
        // We create a new AuthSession so that we can use the Dropbox API.
//        final AndroidAuthSession session = buildSession();
//        mDropboxApi = new DropboxAPI<AndroidAuthSession>(session);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        File local = new File(mLocalPath);
        mFileSize = local.length()/1024;
        final View view = inflater.inflate(R.layout.fragment_dropbox_upload, null);
        ((TextView)view.findViewById(R.id.txtUploadingFile)).setText(
                String.format(getString(R.string.v2andlib_uploading_file), local.getName())
                ); 
        ((TextView)view.findViewById(R.id.txtUploadingFile)).setText(
                String.format(getString(R.string.v2andlib_file_size), 
                        NumberFormatters.sKiloBytesFormat.format(mFileSize))
                ); 
        mProgress = (ProgressBar) view.findViewById(R.id.progress);
        mProgress.setMax((int) mFileSize);

        new UploadAsyncTask().execute(new String[]{mLocalPath, mRemotePath});
        return view;
    }

    private class UploadAsyncTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            UploadRequest request;
            try {
                final InputStream fin = new FileInputStream(params[0]);
                request = mDropboxApi.putFileOverwriteRequest(params[1], fin, 
                        mFileSize, 
                        new ProgressListener() {
                    @Override
                    public long progressInterval() {
                        // Update the progress bar every half-second or so
                        return 500;
                    }
                    @Override
                    public void onProgress(long bytes, long total) {
                        publishProgress(new Integer[]{(int) (total/1024)});
                    }
                });
                if (request != null) {
                    request.upload();
                }
                fin.close();
            } catch (DropboxException e) {
                throw new RuntimeException(e);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }            
            return null;
        }
        
        @Override
        protected void onProgressUpdate(Integer... values) {
            mProgress.setProgress(values[0]);
            super.onProgressUpdate(values);
        }

    }
    
//    private AndroidAuthSession buildSession() {
//        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
//        AndroidAuthSession session;
//
//        String[] stored = getKeys();
//        if (stored != null) {
//            AccessTokenPair accessToken = new AccessTokenPair(stored[0], stored[1]);
//            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE, accessToken);
//        } else {
//            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
//        }
//
//        return session;
//    }     
}
