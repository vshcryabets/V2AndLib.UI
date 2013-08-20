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
package com.v2soft.AndLib.ui.views;

import com.v2soft.AndLib.dao.ITreeData;

import android.content.Context;

/**
 * Tree node view
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
@SuppressWarnings("rawtypes")
public class TreeNodeView<T extends ITreeData> extends DataView<T> {
    public TreeNodeView(Context context, int layoutRes, int level) {
        super(context, layoutRes);
        mLevel = level;
    }
    private int mLevel;
    public int getNodeLevel() {return mLevel;}
    public void setNodeLevel(int value) {mLevel = value;}
} 