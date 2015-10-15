package org.zhyh.slidelibrary;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

/**
 * Created by zhyh on 10/15/15.
 */
class SlideMenuAdapter implements WrapperListAdapter, OnSlideItemClickListener {

    private ListAdapter mAdapter;
    private Context mContext;

    public SlideMenuAdapter(@NonNull Context context, @NonNull ListAdapter adapter) {
        mAdapter = adapter;
        mContext = context;
    }

    @Override
    public ListAdapter getWrappedAdapter() {
        return mAdapter;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return mAdapter.areAllItemsEnabled();
    }

    @Override
    public boolean isEnabled(int position) {
        return mAdapter.isEnabled(position);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        mAdapter.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        mAdapter.unregisterDataSetObserver(observer);
    }

    @Override
    public int getCount() {
        return mAdapter.getCount();
    }

    @Override
    public Object getItem(int position) {
        return mAdapter.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return mAdapter.getItemId(position);
    }

    @Override
    public boolean hasStableIds() {
        return mAdapter.hasStableIds();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SlideMenuLayout layout = null;
        SlideMenu menu = new SlideMenu(mContext);
        menu.setViewType(mAdapter.getItemViewType(position));
        createMenu(menu);
        if (convertView == null) {
            View contentView = mAdapter.getView(position, convertView, parent);
            SlideMenuView menuView = new SlideMenuView(menu);
            menuView.setItemClickListener(this);
            if (parent instanceof SlideMenuListView) {
                SlideMenuListView listView = (SlideMenuListView) parent;
                layout = new SlideMenuLayout(contentView, menuView, listView.getCloseInterpolator(), listView.getOpenInterpolator(), position);
            } else {
                layout = new SlideMenuLayout(contentView, menuView, position);
            }
        } else {
            layout = (SlideMenuLayout) convertView;
            layout.closeMenu();
            View contentView = mAdapter.getView(position, layout.getContentView(), parent);
            if (parent instanceof SlideMenuListView) {
                SlideMenuListView listView = (SlideMenuListView) parent;
                layout.setCloseInterpolator(listView.getCloseInterpolator());
                layout.setOpenInterpolator(listView.getOpenInterpolator());
            }
            SlideMenuView menuView = layout.getMenuView();
            menuView.refreshMenu(menu);
            layout.setContentView(contentView);
            layout.setMenuView(menuView);
            layout.setPosition(position);
        }
        return layout;
    }

    protected void createMenu(SlideMenu menu) {

    }

    @Override
    public int getItemViewType(int position) {
        return mAdapter.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return mAdapter.getViewTypeCount();
    }

    @Override
    public boolean isEmpty() {
        return mAdapter.isEmpty();
    }

    @Override
    public void onItemClick(SlideMenuView view, SlideMenu menu, int index) {
    }
}
