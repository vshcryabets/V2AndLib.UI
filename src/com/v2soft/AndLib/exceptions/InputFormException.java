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
package com.v2soft.AndLib.exceptions;

/**
 * Input form validation helper class
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public class InputFormException extends Exception {
    private static final long serialVersionUID = 1L;
    private int mMessageResource;

    public InputFormException(int resource) {
        setMessageResource(resource);
    }

    public static void assertFalse(boolean value, int resource) throws InputFormException {
        if (value) {
            throw new InputFormException(resource);
        }
    }

    public int getMessageResource() {
        return mMessageResource;
    }

    public void setMessageResource(int mMessageResource) {
        this.mMessageResource = mMessageResource;
    }
}
