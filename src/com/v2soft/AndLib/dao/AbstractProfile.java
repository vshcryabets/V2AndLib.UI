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
    protected static final String KEY_NAME = "name";
    //----------------------------------------------------------------
    // Private fields
    //----------------------------------------------------------------
    protected String mName;
    //----------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------
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
