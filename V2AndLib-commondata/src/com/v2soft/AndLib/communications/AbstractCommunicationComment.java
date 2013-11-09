/*
 * Copyright (C) 2013 V.Shcryabets (vshcryabets@gmail.com)
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
package com.v2soft.AndLib.communications;

import java.io.Serializable;

/**
 * 
 * @author vshcryabets@gmail.com
 *
 * @param <U> communication user data type
 * @param <ID> message ID
 */
public class AbstractCommunicationComment<U extends AbstractCommunicationUser<?>, MID> 
    implements Serializable {
    private static final long serialVersionUID = 2L;
    protected MID mId;
    protected U mSender;
    protected boolean isRead;
    
    public AbstractCommunicationComment() {
        isRead = false;
    }
    /**
     * @return the mId
     */
    public MID getId() {
        return mId;
    }
    /**
     * @return the mSender
     */
    public U getSender() {
        return mSender;
    }
    /**
     * @param mId the mId to set
     */
    public void setId(MID mId) {
        this.mId = mId;
    }
    /**
     * @param mSender the mSender to set
     */
    public void setSender(U mSender) {
        this.mSender = mSender;
    }
    /**
     * 
     * @return true if this message marked as read
     */
    public boolean isRead() {
        return isRead;
    }
    /**
     * 
     * @param isRead should be true if this message marked as read
     */
    protected void setRead(boolean isRead) {
        this.isRead = isRead;
    };
    
    @Override
    public int hashCode() {
        return mId.hashCode();
    }
    
    public boolean equals(Object o) {
        if ( o == null ) {
            return false;
        }
        if ( o instanceof AbstractCommunicationComment) {
            AbstractCommunicationComment<?, ?> msg = (AbstractCommunicationComment<?, ?>) o;
            return mId.equals(msg.mId);
        }
        return false;
    }

}
