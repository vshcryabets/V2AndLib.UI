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

public abstract class JSONSerializableDataArray<E extends Enum<E>> 
extends JSONSerializable {
    private static final double MAX_DIFF = 0.0001;
    // ==========================================================
    // Class fields
    // ==========================================================
    protected double mValues[];
    // ==========================================================
    // Constructors
    // ==========================================================
    public JSONSerializableDataArray() {
        mValues = new double[getValuesSize()];
    }

    protected abstract int getValuesSize();
    protected abstract E[] getFieldValues();

    public JSONSerializableDataArray(JSONObject object) throws JSONException {
        mValues = new double[getValuesSize()];
        for (E key : getFieldValues()) {
            mValues[key.ordinal()] = object.getDouble(key.toString());
        }
    }
    // ==========================================================
    // Getters
    // ==========================================================
    public double getValue(E cdFields) {
        return mValues[cdFields.ordinal()];
    }
    public void setValue(E cdField, double value) {
        mValues[cdField.ordinal()] = value;
    }
    // ==========================================================
    // JSONSerializable
    // ==========================================================
    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject result = new JSONObject();
        for (E key : getFieldValues()) {
            result.put(key.toString(), mValues[key.ordinal()]);
        }
        return result;
    }
    @Override
    public boolean equals(Object o) {
        if ( !(o instanceof JSONSerializableDataArray)) {
            return false;
        }
        JSONSerializableDataArray<E> object2 = (JSONSerializableDataArray<E>) o;
        final E[] fields = getFieldValues();
        for (E field : fields) {
            double diff = Math.abs(object2.getValue(field)-getValue(field));
            if ( diff > MAX_DIFF ) {
                System.out.println("Wrong field "+field);
                return false;
            }
        }
        return true;
    }
}
