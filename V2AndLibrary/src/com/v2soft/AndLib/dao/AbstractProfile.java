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
package com.v2soft.AndLib.dao;

import org.json.JSONException;
import org.json.JSONObject;

import com.v2soft.AndLib.dao.JSONSerializable;

/**
 * Abstract profile class
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public abstract class AbstractProfile extends JSONSerializable {
    //----------------------------------------------------------------
    // Constants
    //----------------------------------------------------------------
    public static final String KEY_NAME = "name";
    //----------------------------------------------------------------
    // Private fields
    //----------------------------------------------------------------
    protected String mName;
    //----------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------
    public AbstractProfile() {
        this("");
    }
    public AbstractProfile(String name) {
        super();
        mName = name;
    }
    public AbstractProfile(JSONObject in) throws JSONException {
        super(in);
        mName = in.getString(KEY_NAME);
    }
    
    @Override
    public boolean equals(Object o) {
        if ( !(o instanceof AbstractProfile)) return false;
        final AbstractProfile object = (AbstractProfile) o;
        return mName.equals(object.getName());
    }
    public abstract void updateFrom(AbstractProfile profile);
    //----------------------------------------------------------------
    // Setetrs
    //----------------------------------------------------------------
    public void setName(String value) {mName=value;}
    //----------------------------------------------------------------
    // Getters
    //----------------------------------------------------------------
    public String getName(){return mName;}
    //----------------------------------------------------------------
    // JSON methods
    //----------------------------------------------------------------
    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject result = new JSONObject();
        result.put(KEY_NAME, mName);
        return result;
    }
}
