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
package com.v2soft.AndLib.ui.adapters;

import java.util.List;

import android.content.Context;

import com.v2soft.AndLib.dao.ITreeData;
import com.v2soft.AndLib.dao.ITreePureNode;
import com.v2soft.AndLib.ui.views.IDataView;

/**
 * List adapter for tree data structures
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public abstract class TreeAdapter extends CustomViewAdapter<ITreePureNode> {
    private static final String LOG_TAG = TreeAdapter.class.getSimpleName();
    private ITreeData<?> mRoot;

    public TreeAdapter(Context context, ITreeData<?> root, 
            CustomViewAdapterFactory<ITreePureNode, IDataView<ITreePureNode>> viewFactory) {
        super(context, viewFactory);
        setRootNode(root);
    }

    public void setRootNode(ITreeData<?> root) {
        mRoot = root;
        notifyDataSetChanged();
    }
    @Override
    public final long getItemId(int position) {
        return 0;
    }
    @Override
    public boolean hasStableIds() {
        return false;
    }
    @Override
    public int getViewTypeCount() {
        return 4;
    }
    @Override
    public int getItemViewType(int position) {
        final int level = ((ITreePureNode)getItem(position)).getNodeLevel();
        return level;
    }

    /**
     * Handle click on tree item.
     * @param position
     * @param node
     */
    public void onItemClicked(int position, ITreePureNode node) {
        if ( !node.isExpandable() ) {
            return;
        }
        ITreeData<?> expandableNode = (ITreeData<?>) node;
        if ( expandableNode.isExpanded() ) {
            collapse(position, expandableNode);
        } else {
            expand(position, expandableNode);
        }
    }

    private void expand(int position, ITreeData<?> node) {
        synchronized (node) {
            if ( node.isExpanded() ) return;
            final int count = node.getChildsCount();
            if(  count > 0 ) {
                node.setExpanded(true);
                notifyDataSetChanged();
            }
        }
    }

    private void collapse(int position, ITreeData<?> node) {
        synchronized (node) {
            if ( !node.isExpanded() ) return;
            final int count = node.getChildsCount();
            if ( count > 0 ) {
                node.setExpanded(false);
                notifyDataSetChanged();
            }

        }
    }

    private void buildFlatList(ITreeData node) {
        if ( !node.isExpanded() ) return;
        // inititalize list
        final int count = node.getChildsCount();
        if ( count > 0 ) {
            final List<ITreePureNode> subnodes = node.getSubnodes();
            for ( int i = 0; i < count; i++ ) {
                final ITreePureNode subnode = subnodes.get(i);
                mItems.add(subnode);
                if ( subnode.isExpandable() ) {
                    if ( ((ITreeData<?>)subnode).isExpanded() ) {
                        buildFlatList((ITreeData<?>)subnode);
                    }
                }
            }
        }
    }

    @Override
    public void notifyDataSetChanged() {
        // rebuild flat list
        mItems.clear();
        buildFlatList(mRoot);
        super.notifyDataSetChanged();
    }
}
