/*
 * Copyright (C) 2011 V.Shcryabets (vshcryabets@gmail.com)
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

/**
 * 
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public abstract class JSONSerializable {
    public interface Factory<T> {
        T create(JSONObject data) throws JSONException;
        T create(String name);
    }
    
    public JSONSerializable(){
        
    }
    public JSONSerializable(JSONObject in) {
    }
    
    public abstract JSONObject toJSON() throws JSONException; 
}
