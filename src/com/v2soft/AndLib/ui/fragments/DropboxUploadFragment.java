package com.v2soft.AndLib.ui.fragments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.UploadRequest;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.android.AuthActivity;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;
import com.v2soft.AndLib.application.BaseApplication;
import com.v2soft.AndLib.application.BaseApplicationSettings;
import com.v2soft.AndLib.text.NumberFormatters;
import com.v2soft.AndLib.ui.R;

/**
 * Dialog fragment that allows to uplaod specified file to dropbox application folder
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public class DropboxUploadFragment 
extends DialogFragment 
implements OnClickListener {
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
	private Button mBtnClose;
	private BaseApplicationSettings mSettings;
	private UploadRequest mUploadRequest;
	private UploadAsyncTask mTask;
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
		if ( ! mDropboxApi.getSession().authenticationSuccessful() ) {
			mDropboxApi.getSession().startAuthentication(getActivity());
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mSettings = ((BaseApplication<BaseApplicationSettings>)activity.getApplication()).getSettings();

	}

	private static boolean checkAppKeySetup(Context context, String dropboxAppKey) {
		// Check if the app has set up its manifest properly.
		Intent testIntent = new Intent(Intent.ACTION_VIEW);
		String scheme = "db-" + dropboxAppKey;
		String uri = scheme + "://" + AuthActivity.AUTH_VERSION + "/test";
		testIntent.setData(Uri.parse(uri));
		PackageManager pm = context.getPackageManager();
		return (0 != pm.queryIntentActivities(testIntent, 0).size());
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		File local = new File(mLocalPath);
		mFileSize = local.length();
		final View view = inflater.inflate(R.layout.v2andlib_fragment_dropbox_upload, null);
		((TextView)view.findViewById(R.id.txtFileSize)).setText(
				String.format(getString(R.string.v2andlib_file_size), 
						NumberFormatters.sKiloBytesFormat.format(mFileSize/1024))
				);
		getDialog().setTitle(String.format(getString(R.string.v2andlib_uploading_file), local.getName()));
		mProgress = (ProgressBar) view.findViewById(R.id.progress);
		mProgress.setMax((int) mFileSize/1024);
		mBtnClose = (Button) view.findViewById(R.id.btnClose);
		mBtnClose.setOnClickListener(this);
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
				mSettings.putString(ACCESS_KEY_NAME, tokens.key);
				mSettings.putString(ACCESS_SECRET_NAME, tokens.secret);
				mSettings.saveSettings();
				mTask = new UploadAsyncTask();
				mTask.execute(new String[]{mLocalPath, mRemotePath});
			} catch (IllegalStateException e) {
				Toast.makeText(getActivity(), 
						"Couldn't authenticate with Dropbox:" + e.getLocalizedMessage(), 
						Toast.LENGTH_LONG).show();
				Log.i(LOG_TAG, "Error authenticating", e);
			}
		}
	}

	private ProgressListener mListenr = new ProgressListener() {
		@Override
		public long progressInterval() {
			// Update the progress bar every half-second or so
			return 250;
		}
		@Override
		public void onProgress(long bytes, long total) {
			mProgress.setProgress((int) (bytes/1024));
		}
	};

	private void startUpload() throws FileNotFoundException, DropboxException {
		final InputStream fin = new FileInputStream(mLocalPath);
		mUploadRequest = mDropboxApi.putFileOverwriteRequest(mRemotePath, fin, 
				mFileSize, 
				mListenr);
			mUploadRequest.upload();
	}

	private class UploadAsyncTask extends AsyncTask<String, Integer, Exception> {

		@Override
		protected Exception doInBackground(String... params) {
			try {
				startUpload();
				return null;
			} catch (Exception e) {
				return e;
			}
		}

		@Override
		protected void onPostExecute(Exception result) {
			mUploadRequest = null;
			mProgress.setProgress(mProgress.getMax());
			mBtnClose.setText(R.string.v2andlib_btn_close);
			super.onPostExecute(result);
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
		if ( mSettings.isKeyStored(ACCESS_KEY_NAME) ) {
			String[] ret = new String[2];
			ret[0] = mSettings.getString(ACCESS_KEY_NAME);
			ret[1] = mSettings.getString(ACCESS_SECRET_NAME);
			return ret;
		} else {
			return null;
		}
	}

	@Override
	public void onClick(View v) {
		if ( mUploadRequest == null ) {
			getDialog().dismiss();
		} else {
			new Thread(new Runnable() {
				@Override
				public void run() {
					mUploadRequest.abort();
				}
			}).start();
		}
	}   
}
