/*
 * Copyright (C) 2013 V.Shcryabets (vshcryabets@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.v2soft.AndLib.ui.dialogs;

import java.util.Calendar;
import java.util.Date;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.TimePicker;

import com.v2soft.AndLib.ui.fragments.BaseFragment;

/**
 * Класс для установки времени через TimePicker.
 *
 */
public class TimePickerDialogFragment extends DialogFragment {
    private static final String KEY_DATE = "date";
    private static final String KEY_24 = "24format";
    private static final String KEY_TITLE = "title";
    
    public static TimePickerDialogFragment newInstance(Date date, boolean use24hFormat, String title) {
        final TimePickerDialogFragment res = new TimePickerDialogFragment();
        final Bundle params = new Bundle();
        params.putLong(KEY_DATE, date.getTime());
        params.putBoolean(KEY_24, use24hFormat);
        params.putString(KEY_TITLE, title);
        res.setArguments(params);
        return res;
    }
    
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Date date = new Date(getArguments().getLong(KEY_DATE));
        final Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        final TimePickerDialog dlg = new TimePickerDialog(getActivity(), 
                mTimeSetListener, 
                cl.get(Calendar.HOUR_OF_DAY), 
                cl.get(Calendar.MINUTE),
                getArguments().getBoolean(KEY_24));
        final String title = getArguments().getString(KEY_TITLE);
        if ( title != null ) {
            final TextView titleView = new TextView(getActivity());
            titleView.setText(title);
            titleView.setGravity(Gravity.CENTER_HORIZONTAL);
            dlg.setCustomTitle(titleView);
        }
        return dlg;
    }
    
    // Timepicker dialog generation
    private TimePickerDialog.OnTimeSetListener mTimeSetListener = 
            new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            final Calendar cl = Calendar.getInstance();
            cl.setTime(new Date());
            cl.set(Calendar.HOUR_OF_DAY, hourOfDay);
            cl.set(Calendar.MINUTE, minute);
            Fragment fragment = getTargetFragment();
            if ( fragment != null && (fragment instanceof BaseFragment) ) {
                BaseFragment<?,?> target = (BaseFragment<?,?>) fragment;
                target.onFragmentResult(cl.getTime(), getTargetRequestCode());
            }
            dismiss();
        }
    };
}