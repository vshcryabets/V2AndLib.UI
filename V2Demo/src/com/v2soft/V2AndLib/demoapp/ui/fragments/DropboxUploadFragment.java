package com.v2soft.V2AndLib.demoapp.ui.fragments;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Dialog fragment that allows to uplaod specified file to dropbox application folder
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public class DropboxUploadFragment extends DialogFragment {
    // =========================================================
    // Constants
    // =========================================================
    private static final String KEY_LOCAL_FILE = null;
    private static final String KEY_REMOTE_FILE = null;
    // =========================================================
    // Class fields
    // =========================================================
    private String mLocalPath, mRemotePath;
    // =========================================================
    // Constructors
    // =========================================================
    public static Fragment newInstance(String localFilePath, String dropBoxPath) {
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
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
