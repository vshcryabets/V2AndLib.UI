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

import java.lang.reflect.Field;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @author V.Shcryabets<vshcryabets@gmail.com>
 *
 * @param <T>
 */
public class DataView<T> extends LinearLayout implements IDataView<T>{
	private T mData;
	private TextView mTextViews[];

	public DataView(Context context, int resource) {
		super(context);
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
			for (int i = 0; i < fields.length; i++ ) {
				final Field field = fields[i];
				if ( field.isAnnotationPresent(DataViewAnnotation.class)) {
					final Object value = field.get(data);
					if ( mTextViews[i] == null ) {
						final DataViewAnnotation annotation = field.getAnnotation(DataViewAnnotation.class);
						int res = annotation.resource();
						if ( res > 0 ) {
							mTextViews[i] = (TextView)findViewById(res);
							field.setAccessible(true);
						}
					}
					if ( mTextViews[i] != null ) {
						mTextViews[i].setText(value.toString());
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

	@Override
	public T getData() {
		return mData;
	}

}
