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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.v2soft.AndLib.dao.AbstractProfile;
import com.v2soft.AndLib.dao.JSONSerializable;
import com.v2soft.AndLib.dao.Storage;

public class ProfileStorage<T extends AbstractProfile>
extends Storage
implements Collection<T> {
    public interface ProfileStorageListener<T> {
        void onProfileBeforeDeleted(T profile);
        void onProfileAfterAdded(T profile);
        void onProfileAfterUpdated(T profile);        
    }
    private List<T> mProfiles = new ArrayList<T>();
    private JSONSerializable.Factory<T> mFactory;
    private List<ProfileStorageListener<T>> mEventListeners;
            

    public ProfileStorage(JSONSerializable.Factory<T> factory) {
        mFactory = factory;
        mEventListeners = new LinkedList<ProfileStorageListener<T>>();
    }

    public T addProfile(String profileName) {
        T newProfile = mFactory.create(profileName);
        add(newProfile);
        return newProfile;
    }
    @Override
    public boolean add(T object) {
        boolean result = mProfiles.add(object);
        if ( result ) {
            for (ProfileStorageListener<T> listener : mEventListeners) {
                listener.onProfileAfterAdded(object);
            }
        }
        return result;
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        return mProfiles.addAll(mProfiles);
    }

    @Override
    public void clear() {
        mProfiles.clear();
    }

    @Override
    public boolean contains(Object object) {
        return mProfiles.contains(object);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return mProfiles.containsAll(collection);
    }

    @Override
    public boolean isEmpty() {
        return mProfiles.isEmpty();
    }

    @Override
    public Iterator<T> iterator() {
        return mProfiles.iterator();
    }

    @Override
    public boolean remove(Object object) {
        // notify listeners
        for (ProfileStorageListener<T> listener : mEventListeners) {
            listener.onProfileBeforeDeleted((T) object);
        }
        return mProfiles.remove(object);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return mProfiles.removeAll(collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return mProfiles.retainAll(collection);
    }

    @Override
    public int size() {
        return mProfiles.size();
    }

    @Override
    public Object[] toArray() {
        return mProfiles.toArray();
    }

    @Override
    public <T> T[] toArray(T[] array) { 
        return mProfiles.toArray(array);
    }

    @Override
    public String toString() {
        return mProfiles.toString();
    }

    @Override
    public void load(InputStream input) throws Exception {
        byte buffer[] = new byte[4096];
        StringBuilder builder = new StringBuilder();
        int readed = 0;
        while ( (readed = input.read(buffer)) > 0 ) {
            String string = new String(buffer, 0, readed, "utf-8");
            builder.append(string);
        }

        JSONArray profilesJSON = new JSONArray(builder.toString());
        for ( int i = 0; i < profilesJSON.length(); i ++ ) {
            JSONObject profile = profilesJSON.getJSONObject(i);
            add(mFactory.create(profile));
        }
    }

    @Override
    public void save(OutputStream out) throws JSONException, UnsupportedEncodingException, IOException {
        JSONArray profiles = new JSONArray();
        for (T profile : this) {
            profiles.put(profile.toJSON());
        }
        out.write(profiles.toString().getBytes("utf-8"));
    }

    public boolean update(T profile) {
        for (T destprofile : this) {
            if ( destprofile.equals(profile)) {
                destprofile.updateFrom(profile);
                // notify listeners
                // notify listeners
                for (ProfileStorageListener<T> listener : mEventListeners) {
                    listener.onProfileAfterUpdated(destprofile);
                }
                return true;
            }
        }
        return false;
    }
    //-----------------------------------------------------------------------------------------------------
    // Listener methods
    //-----------------------------------------------------------------------------------------------------
    public boolean addListener(ProfileStorageListener<T> listener) {
        return mEventListeners.add(listener);
    }
    public boolean removeListener(ProfileStorageListener<T> listener) {
        return mEventListeners.remove(listener);
    }

}
