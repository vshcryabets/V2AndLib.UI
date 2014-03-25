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
package com.v2soft.AndLib.containers;

import com.v2soft.AndLib.dao.ITreeData;
import com.v2soft.AndLib.dao.ITreePureNode;

import java.util.List;

/**
 * Base tree data class.
 * @author V.Shcryabets<vshcryabets@gmail.com>
 *
 * @param <T>
 */
public abstract class TreeDataContainer<T extends ITreePureNode> implements ITreeData<T> {
    protected boolean isExpanded;
    protected List<T> mChilds;

    public TreeDataContainer() {
        isExpanded = false;
        mChilds = null;
    }

    @Override
    public boolean isExpanded() {
        return isExpanded;
    }

    @Override
    public void setExpanded(boolean value) {
        isExpanded = value;
    }

    @Override
    public int getChildsCount() {
        if ( mChilds != null ) {
            return mChilds.size();
        } else {
            return 0;
        }
    }
    
    @Override
    public List<T> getSubnodes() {
        return mChilds;
    }
    
    protected void setChilds(List<T> childs) {
        mChilds = childs;
    }
}
