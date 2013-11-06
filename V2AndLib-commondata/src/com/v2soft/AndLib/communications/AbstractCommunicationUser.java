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

import com.v2soft.AndLib.dao.AbstractProfile;

/**
 * 
 * @author vshcryabets@gmail.com
 *
 */
public class AbstractCommunicationUser<ID> extends AbstractProfile<ID> {
    private static final long serialVersionUID = 1L;
    public AbstractCommunicationUser() {
        super();
    }
    public AbstractCommunicationUser(ID id, String name) {
        super(id, name);
    }
}
