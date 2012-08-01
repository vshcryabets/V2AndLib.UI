package com.v2soft.V2AndLib.demoapp.ui.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.DropboxAPI.UploadRequest;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.android.AuthActivity;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.TokenPair;
import com.dropbox.client2.session.Session.AccessType;
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
    private static final String KEY_APP_SECRET = "appsecret";
    private static final String KEY_APP_KEY = "appkey";
    // If you'd like to change the access type to the full Dropbox instead of
    // an app folder, change this value.
    private final static AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
    private static final String LOG_TAG = DropboxUploadFragment.class.getSimpleName();
    // You don't need to change these, leave them alone.
    final static private String ACCOUNT_PREFS_NAME = "prefs";
    final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
    final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";

    // =========================================================
    // Class fields
    // =========================================================
    private String mLocalPath, mRemotePath;
    private String mDropBoxAppKey, mDropBoxAppSecret;
    private ProgressBar mProgress;
    private long mFileSize;
    private DropboxAPI<AndroidAuthSession> mDropboxApi;
    // =========================================================
    // Constructors
    // =========================================================
    public static DropboxUploadFragment newInstance(Context context, String localFilePath, String dropBoxPath,
            String dropboxAppKey, String dropboxAppSecret) throws Exception {
        final DropboxUploadFragment result = new DropboxUploadFragment();
        if ( !checkAppKeySetup(context, dropboxAppKey) ) {
            throw new Exception("DropBox application not installed");
        }
        final Bundle bundle = new Bundle();
        bundle.putString(KEY_LOCAL_FILE, localFilePath);
        bundle.putString(KEY_REMOTE_FILE, dropBoxPath);
        bundle.putString(KEY_APP_KEY, dropboxAppKey);
        bundle.putString(KEY_APP_SECRET, dropboxAppSecret);
        result.setArguments(bundle);
        return result;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocalPath = getArguments().getString(KEY_LOCAL_FILE);
        mRemotePath = getArguments().getString(KEY_REMOTE_FILE);
        mDropBoxAppKey = getArguments().getString(KEY_APP_KEY);
        mDropBoxAppSecret = getArguments().getString(KEY_APP_SECRET);

        // We create a new AuthSession so that we can use the Dropbox API.
        AndroidAuthSession session = buildSession();
        mDropboxApi = new DropboxAPI<AndroidAuthSession>(session);
        // Start the remote authentication
        mDropboxApi.getSession().startAuthentication(getActivity());
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    private static boolean checkAppKeySetup(Context context, String dropboxAppKey) {
        // Check if the app has set up its manifest properly.
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        String scheme = "db-" + dropboxAppKey;
        String uri = scheme + "://" + AuthActivity.AUTH_VERSION + "/test";
        testIntent.setData(Uri.parse(uri));
        PackageManager pm = context.getPackageManager();
        if (0 == pm.queryIntentActivities(testIntent, 0).size()) {
            //            showToast("URL scheme in your app's " +
            //                    "manifest is not set up correctly. You should have a " +
            //                    "com.dropbox.client2.android.AuthActivity with the " +
            //                    "scheme: " + scheme);
            return false;
        }
        return true;
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

    @Override
    public void onResume() {
        super.onResume();
        AndroidAuthSession session = mDropboxApi.getSession();

        // The next part must be inserted in the onResume() method of the
        // activity from which session.startAuthentication() was called, so
        // that Dropbox authentication completes properly.
        if (session.authenticationSuccessful()) {
            try {
                // Mandatory call to complete the auth
                session.finishAuthentication();

                // Store it locally in our app for later use
                TokenPair tokens = session.getAccessTokenPair();
                //                storeKeys(tokens.key, tokens.secret);
                //                setLoggedIn(true);
            } catch (IllegalStateException e) {
                Toast.makeText(getActivity(), 
                        "Couldn't authenticate with Dropbox:" + e.getLocalizedMessage(), 
                        Toast.LENGTH_LONG).show();
                Log.i(LOG_TAG, "Error authenticating", e);
            }
        }
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

    private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(mDropBoxAppKey, mDropBoxAppSecret);
        AndroidAuthSession session;

        String[] stored = getKeys();
        if (stored != null) {
            AccessTokenPair accessToken = new AccessTokenPair(stored[0], stored[1]);
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE, accessToken);
        } else {
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
        }

        return session;
    }  

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     *
     * @return Array of [access_key, access_secret], or null if none stored
     */
    private String[] getKeys() {
        SharedPreferences prefs = getActivity().getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (key != null && secret != null) {
            String[] ret = new String[2];
            ret[0] = key;
            ret[1] = secret;
            return ret;
        } else {
            return null;
        }
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     */
    private void storeKeys(String key, String secret) {
        // Save the access key for later
        SharedPreferences prefs = getActivity().getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.putString(ACCESS_KEY_NAME, key);
        edit.putString(ACCESS_SECRET_NAME, secret);
        edit.commit();
    }

    private void clearKeys() {
        SharedPreferences prefs = getActivity().getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
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
