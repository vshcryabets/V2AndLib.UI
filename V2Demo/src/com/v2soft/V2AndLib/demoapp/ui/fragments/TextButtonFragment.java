package com.v2soft.V2AndLib.demoapp.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.v2soft.AndLib.ui.activities.BaseActivity;
import com.v2soft.AndLib.ui.fragments.BaseFragment;
import com.v2soft.V2AndLib.demoapp.DemoAppSettings;
import com.v2soft.V2AndLib.demoapp.DemoApplication;
import com.v2soft.V2AndLib.demoapp.R;

/**
 * Fragment for testing back stack
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public class TextButtonFragment 
extends BaseFragment<DemoApplication, DemoAppSettings> {
    public static final String KEY_ID = "id";
    private static final String KEY_STR = "str";
    private String mTag;
    private int mId;
    private EditText mEdit;

    public TextButtonFragment() {
    }

    public static Fragment newInstance(String text, int id) {
        final Fragment result = new TextButtonFragment();
        final Bundle args = new Bundle();
        args.putString(KEY_STR, text);
        args.putInt(KEY_ID, id);
        result.setArguments(args);
        return result;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ( getArguments() != null ) {
            mTag = getArguments().getString(KEY_STR);
            mId = getArguments().getInt(KEY_ID);
        } else {
            mTag = "U";
            mId= -100;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_button, null);
        mEdit = (EditText) view.findViewById(R.id.edit);
        final Button btn = (Button) view.findViewById(R.id.btnStart);
        btn.setOnClickListener(this);
        btn.setText(mTag+"."+mId);
        return view;
    }
    @Override
    public void onClick(View v) {
        final Fragment fragment = newInstance(mTag, ++mId);
        ((BaseActivity<?,?>)getActivity()).getBackStack().startFragmentAt(fragment, null);
    }
}