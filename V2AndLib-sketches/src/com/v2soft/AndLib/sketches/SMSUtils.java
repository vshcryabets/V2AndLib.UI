package com.v2soft.AndLib.sketches;

import android.telephony.SmsManager;

import java.util.ArrayList;

/**
 * Created by V.Shcryabets on 5/21/14.
 *
 * @author V.Shcryabets (vshcryabets@gmail.com)
 */
public class SMSUtils {
    protected SmsManager mSMSManager;

    public SMSUtils() {
        mSMSManager = SmsManager.getDefault();
    }

    public void sendLongSMS(String phone, String message) {
        ArrayList<String> parts = mSMSManager.divideMessage(message);
        mSMSManager.sendMultipartTextMessage(phone, null, parts, null, null);
    }
}
