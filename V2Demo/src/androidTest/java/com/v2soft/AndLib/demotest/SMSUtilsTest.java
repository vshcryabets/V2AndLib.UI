package com.v2soft.AndLib.demotest;

import android.test.AndroidTestCase;

import com.v2soft.AndLib.sketches.SMSUtils;

import java.util.UUID;


/**
 * Network requests tests.
 * @author V.Shcryabets<vshcryabets@gmail.com>
 */
public class SMSUtilsTest extends AndroidTestCase {

    public void testSendSMS() {
        SMSUtils utils = new SMSUtils();
        utils.sendLongSMS("+380956432518", "Test message with unicode. Тестовое сообщение юникод."+ UUID.randomUUID().toString());
    }

}
