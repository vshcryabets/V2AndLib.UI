package com.v2soft.AndLib.UI.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.v2soft.AndLib.UI.R;

public class LoadingView extends LinearLayout {

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(getContext(), R.layout.v2andlib_listitem_loading, this);
    }

    public LoadingView(Context context) {
        this(context,null);
    }

}