/*
 * Copyright (C) 2012-2013 V.Shcryabets (vshcryabets@gmail.com)
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
package com.v2soft.AndLib.exceptions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.widget.EditText;

/**
 * Input form validation helper class
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public class InputFormException extends Exception {
    private static final long serialVersionUID = 1L;
    private int mMessageResource;
    public static final Pattern EMAIL_PATTERN = Pattern
            .compile("^[A-Za-z0-9.%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,4}");

    public InputFormException(int resource) {
        setMessageResource(resource);
    }

    public static void assertFalse(boolean value, int resource) throws InputFormException {
        if (value) {
            throw new InputFormException(resource);
        }
    }
    public static void assertTrue(boolean value, int resource) throws InputFormException {
        if (!value) {
            throw new InputFormException(resource);
        }
    }
    public static void assertEmailValid(String value, int resource) throws InputFormException {
        final Matcher matcher = EMAIL_PATTERN.matcher(value);
        if (!matcher.find()) {
            throw new InputFormException(resource);
        }
    }
    public static void assertEmailValid(EditText value, int resource) throws InputFormException {
        assertEmailValid(value.getEditableText().toString(), resource);
    }

    public static void assertBlankEditText(EditText edit, int resource) throws InputFormException {
        final String text = edit.getEditableText().toString().trim();
        if ( text.length() == 0 ) {
            assertFalse(true, resource);
            return;
        }
    }

    public int getMessageResource() {
        return mMessageResource;
    }

    public void setMessageResource(int mMessageResource) {
        this.mMessageResource = mMessageResource;
    }
}
