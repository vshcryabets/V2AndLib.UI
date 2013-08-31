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
package com.v2soft.AndLib.dao;

import java.io.Serializable;

/**
 * Abstract profile class
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 * @param T id data type
 */
public abstract class AbstractProfile<T> implements Serializable {
    //----------------------------------------------------------------
    // Constants
    //----------------------------------------------------------------
    public static final String KEY_NAME = "name";
    //----------------------------------------------------------------
    // Private fields
    //----------------------------------------------------------------
    protected String mName;
    protected T mId;
    //----------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------
    public AbstractProfile() {
    }
    public AbstractProfile(T id, String name) {
        super();
        mId = id;
        mName = name;
    }
    @Override
    public boolean equals(Object o) {
        if ( !(o instanceof AbstractProfile)) return false;
        final AbstractProfile<?> object = (AbstractProfile<?>) o;
        return mName.equals(object.mName) && mId.equals(object.mId);
    }
    //----------------------------------------------------------------
    // Setetrs
    //----------------------------------------------------------------
    public void setName(String value) {mName=value;}
    public void setId(T id){mId=id;}
    //----------------------------------------------------------------
    // Getters
    //----------------------------------------------------------------
    public String getName(){return mName;}
    public T getId() {return mId;}
}
