package com.v2soft.AndLib.ui.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;

import com.v2soft.AndLib.application.BaseApplication;
import com.v2soft.AndLib.application.BaseApplicationSettings;

/**
 * Base fragment class
 * @author diacht
 *
 */
public abstract class BaseFragment<T extends BaseApplication<S>, S extends BaseApplicationSettings> 
    extends Fragment 
    implements OnClickListener {
    protected T mApp;
    protected S mSettings;

    public static final int DIALOG_FILE_SEND_SERVICE = 10;
    
    @Override
    public void onAttach(Activity activity) {
        mApp = (T) activity.getApplication();
        if ( mApp == null ) throw new NullPointerException("Application is null");
        mSettings = mApp.getSettings();
        if ( mSettings == null ) throw new NullPointerException("Settings is null");
        super.onAttach(activity);
    }

    /**
     * Метод для подписки на OnClick события
     * @param is массив идентификаторов вьюшек на которые нужно подписаться
     */
    protected void registerOnClickListener(int[] is, View inview) {
        for (int i : is) {
            final View view = inview.findViewById(i);
            if ( view == null ) throw new NullPointerException("Can't get view with id "+i);
            view.setOnClickListener(this);
        }
    }
}
