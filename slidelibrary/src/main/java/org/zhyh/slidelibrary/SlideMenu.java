package org.zhyh.slidelibrary;

import android.content.Context;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhyh on 10/15/15.
 */
public class SlideMenu implements Serializable {
    private static final long serialVersionUID = 8373519064669711547L;

    private Context mContext;
    private List<SlideMenuItem> mItems;
    private int mViewType;

    public SlideMenu(Context context) {
        mContext = context;
        mItems = new LinkedList<SlideMenuItem>();
    }

    public Context getContext() {
        return mContext;
    }

    public List<SlideMenuItem> getItems() {
        return mItems;
    }

    public void setItems(List<SlideMenuItem> mItems) {
        this.mItems = mItems;
    }

    public int getViewType() {
        return mViewType;
    }

    public void setViewType(int mViewType) {
        this.mViewType = mViewType;
    }

    public int addMenuItem(SlideMenuItem item) {
        int index = mItems.size();
        mItems.add(item);
        return index;
    }

    public int removeMenuItem(SlideMenuItem item) {
        int index = mItems.indexOf(item);
        mItems.remove(item);
        return index;
    }

    public SlideMenuItem getMenuItem(int index) {
        if (index < 0 || index >= mItems.size()) {
            return null;
        }
        return mItems.get(index);
    }
}
