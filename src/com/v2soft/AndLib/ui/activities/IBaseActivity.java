package com.v2soft.AndLib.ui.activities;

import com.v2soft.AndLib.application.BaseApplication;
import com.v2soft.AndLib.ui.fonts.FontManager;

/**
 * Base interface for activites.
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 *
 */
public interface IBaseActivity<APP extends BaseApplication<?>> {
    /**
     * @return application custom font manager
     */
    public FontManager getFontManager();
    /**
     * Show error to user 
     * @param message
     */
    public void showError(String message);
    /**
     * Show error to user 
     * @param message
     */
    public void showError(int messageResource);
    /**
     * This method will show some kind of a unblocking progress view, that means that background data loading process is ongoing
     * @param value
     */
    public void setLoadingProcess(boolean value, Object tag);
    /**
     * This method will show some kind of a progress dialog, that block the user
     * @param value
     */
    public void setBlockingProcess(boolean value, Object tag);
    /**
     * Get casted applcation.
     */
    public APP getApplicationObject();
}
