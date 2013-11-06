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
 * Parent class for chat message.
 * @author vshcryabets@gmail.com
 *
 * @param <U> communication user data type
 * @param <ID> message ID
 */
public class AbstractCommunicationMessage<U extends AbstractCommunicationUser<?>, MID>
    extends AbstractCommunicationComment<U, MID>
    implements Serializable {
    private static final long serialVersionUID = 2L;
    protected U mRecepient;
    
    public AbstractCommunicationMessage() {
        isRead = false;
    }
    /**
     * Return message recipient.
     * @return message recipient.
     */
    public U getRecepient() {
        return mRecepient;
    }
    /**
     * Set message recipient.
     * @param user message recipient.
     */
    public void setRecepient(U user) {
        mRecepient = user;
    }
    @Override
    public boolean equals(Object o) {
        if ( o == null ) {
            return false;
        }
        if ( o instanceof AbstractCommunicationMessage) {
            AbstractCommunicationMessage<?, ?> msg = (AbstractCommunicationMessage<?, ?>) o;
            return mId.equals(msg.mId);
        }
        return false;
    }
}
