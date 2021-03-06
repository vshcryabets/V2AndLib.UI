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
package com.v2soft.AndLib.dao;

import java.util.List;

/**
 * Tree node data interface
 * @author V.Shcryabets<vshcryabets@gmail.com>
 *
 */
public interface ITreeData<T extends ITreePureNode> extends ITreePureNode {
    public boolean isExpanded();
    public int getChildsCount();
    public List<T> getSubnodes();
    public void setExpanded(boolean value);
}
