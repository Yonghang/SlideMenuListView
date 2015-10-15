package org.zhyh.slidelibrary;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.UUID;

/**
 * Created by zhyh on 10/15/15.
 */
class SlideMenuView extends LinearLayout implements View.OnClickListener {

    private SlideMenu mMenu;

    private OnSlideItemClickListener itemClickListener;

    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public SlideMenuView(SlideMenu menu) {
        super(menu.getContext());
        mMenu = menu;
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        for (SlideMenuItem item : mMenu.getItems()) {
            addItem(item);
        }
    }

    public void refreshMenu(SlideMenu menu) {
        mMenu = menu;
        removeAllViewsInLayout();
        for (SlideMenuItem item : mMenu.getItems()) {
            addItem(item);
        }
    }

    private void addItem(SlideMenuItem item) {
        LayoutParams params = new LayoutParams(item.getWidth(), LayoutParams.MATCH_PARENT);
        LinearLayout container = new LinearLayout(getContext());
        container.setId(UUID.randomUUID().hashCode());
        container.setGravity(Gravity.CENTER);
        container.setOrientation(VERTICAL);
        container.setLayoutParams(params);
        container.setClickable(true);
        if (Build.VERSION.SDK_INT >= 16) {
            container.setBackground(item.getBackground());
        } else {
            container.setBackgroundDrawable(item.getBackground());
        }
        container.setOnClickListener(this);
        addView(container);
        if (item.getIcon() != null) {
            container.addView(createIcon(item.getIcon()));
        }
        if (!TextUtils.isEmpty(item.getTitle())) {
            container.addView(createTitle(item.getTitle(), item.getTextColor(), item.getTextSize()));
        }
    }

    private View createTitle(String title, int textColor, int textSize) {
        TextView textView = new TextView(getContext());
        textView.setTextColor(textColor);
        textView.setTextSize(textSize);
        textView.setText(title);
        textView.setGravity(Gravity.CENTER);
        textView.setClickable(false);
        return textView;
    }

    private View createIcon(Drawable icon) {
        ImageView imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setImageDrawable(icon);
        imageView.setClickable(false);
        return imageView;
    }

    @Override
    public void onClick(View v) {
        if (itemClickListener != null) {
            int i = indexOfChild(v);
            itemClickListener.onItemClick(this, mMenu, i);
        }
    }

    public OnSlideItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public void setItemClickListener(OnSlideItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
