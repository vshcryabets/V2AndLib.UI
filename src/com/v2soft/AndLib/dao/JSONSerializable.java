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
