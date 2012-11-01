package com.v2soft.AndLib.dataproviders;

import android.os.Message;

/**
 * 
 * @author vshcryabets@gmail.com
 *
 */
public interface ITaskHub {
    void sendMessage(ITask from, Message message);
}
