package org.zhyh.slidelibrary;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by zhyh on 10/15/15.
 */
public class SlideMenuItem implements Serializable {
    private static final long serialVersionUID = 4521237611699466729L;

    private int id;
    private Context mContext;
    private String title;
    private Drawable icon;
    private Drawable background;
    private int width;
    private int textColor;
    private int textSize;


    public SlideMenuItem(Context context) {
        mContext = context;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Context getContext() {
        return mContext;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTitle(int resId) {
        this.title = mContext.getResources().getString(resId);
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public void setIcon(int resId) {
        if (Build.VERSION.SDK_INT >= 21) {
            this.icon = mContext.getDrawable(resId);
        } else {
            this.icon = mContext.getResources().getDrawable(resId);
        }
    }

    public Drawable getBackground() {
        return background;
    }

    public void setBackground(Drawable background) {
        this.background = background;
    }

    public void setBackground(int color) {
        this.background = new ColorDrawable(Color.rgb(Color.red(color), Color.green(color), Color.blue(color)));
    }

    public void setBackground(String color) {
        this.background = new ColorDrawable(Color.parseColor(color));
    }

    public void setBackground(int resId, Object flag) {
        int color = 0;
        if (Build.VERSION.SDK_INT >= 23) {
            color = mContext.getColor(resId);
        } else {
            color = mContext.getResources().getColor(resId);
        }
        setBackground(color);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }
}
