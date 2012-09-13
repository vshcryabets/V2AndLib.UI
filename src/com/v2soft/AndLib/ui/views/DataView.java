/*
 * Copyright (C) 2012 V.Shcryabets (vshcryabets@gmail.com)
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
package com.v2soft.AndLib.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.text.Format;
import java.util.HashMap;

/**
 * 
 * @author V.Shcryabets<vshcryabets@gmail.com>
 *
 * @param <T>
 */
public class DataView<T> extends LinearLayout implements IDataView<T>{
    protected T mData;
    private TextView mTextViews[];
    // TODO may be it formatters field should be static fields of data class?
    private static final HashMap<Class, Format[]> sFormatters = 
            new HashMap<Class, Format[]>();

    public DataView(Context context, int resource) {
        super(context);
        inflate(context, resource, this);
    }

    public DataView(Context context, AttributeSet attrs, int resource) {
        super(context, attrs);
        inflate(context, resource, this);
    }
    @Override
    public void setData(T data) {
        mData = data;
        final Field[] fields = data.getClass().getDeclaredFields();
        if ( mTextViews == null ) {
            mTextViews = new TextView[fields.length];
        }
        try {
            if ( !sFormatters.containsKey(this.getClass()) ) {
                initializeFormatters(fields);
            }
            final Format [] formatters = sFormatters.get(this.getClass());
            for (int i = 0; i < fields.length; i++ ) {
                final Field field = fields[i];
                if ( field.isAnnotationPresent(DataViewAnnotation.class)) {
                    if ( mTextViews[i] == null ) {
                        // TODO this code shoud work only during first initialization
                        final DataViewAnnotation annotation = 
                                field.getAnnotation(DataViewAnnotation.class);
                        int res = annotation.resource();
                        if ( res > 0 ) {
                            mTextViews[i] = (TextView)findViewById(res);
                        }
                    }
                    if ( mTextViews[i] != null ) {
                        field.setAccessible(true);
                        final Object value = field.get(data);
                        String stringValue = null;
                        if (value == null ) {
                            stringValue = "";
                        } else if ( formatters[i] != null ) {
                            stringValue = formatters[i].format(value);
                        } else {
                            stringValue = value.toString();
                        }
                        mTextViews[i].setText(stringValue);
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void initializeFormatters(Field[] fields) {
        final Format[] formatters = new Format[fields.length];
        for (int i = 0; i < fields.length; i++ ) {
            final Field field = fields[i];
            if ( field.isAnnotationPresent(DataViewAnnotation.class)) {
                final DataViewAnnotation annotation = 
                        field.getAnnotation(DataViewAnnotation.class);
                final String pattern = annotation.patternString();
                final Class<?> formatterClass = annotation.formater();
                if ( !pattern.equals("") && 
                        !formatterClass.equals(Object.class) ) {
                    // instantinate class
                    try {
                        final Constructor<?> ctor = formatterClass.getConstructor(String.class);
                        final Format formatter = (Format)ctor.newInstance(pattern);
                        formatters[i] = formatter;
                    } catch (Exception x) {
                        x.printStackTrace();
                        formatters[i] = null;
                    }
                } else {
                    formatters[i] = null;
                }
            }
        }
        sFormatters.put(this.getClass(), formatters);
    }

    @Override
    public T getData() {
        return mData;
    }

}
