package org.zhyh.slidelibrary;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by zhyh on 10/15/15.
 */
public class SlideMenuListView extends ListView {

    private static final int TOUCH_STATE_NONE = 1 << 0;
    private static final int TOUCH_STATE_X = 1 << 1;
    private static final int TOUCH_STATE_Y = 1 << 2;

    private SlideDirection mDirection = SlideDirection.DIRECTION_LEFT;

    private int MAX_Y = 5;
    private int MAX_X = 3;

    private float mDownX;
    private float mDownY;

    private int mTouchState;
    private int mTouchPosition;

    private SlideMenuLayout mTouchView;
    private OnSwipeListener mOnSwipListener;

    private SlideMenuCreator mMenuCreator;
    private OnMenuItemClickListener mOnMenuItemClickListener;
    private OnMenuStateChangeListener mOnmenuStateChangeListener;
    private Interpolator mCloseInterpolator;
    private Interpolator mOpenInterpolator;

    public SlideMenuListView(Context context) {
        super(context);
        init();
    }

    public SlideMenuListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlideMenuListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        MAX_X = dp2px(MAX_X);
        MAX_Y = dp2px(MAX_Y);
        mTouchState = TOUCH_STATE_NONE;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(new SlideMenuAdapter(getContext(), adapter) {
            @Override
            protected void createMenu(SlideMenu menu) {
                if (mMenuCreator != null) {
                    mMenuCreator.onMenuCreate(menu);
                }
            }

            @Override
            public void onItemClick(SlideMenuView view, SlideMenu menu, int index) {
                boolean flag = false;
                if (mOnMenuItemClickListener != null) {
                    flag = mOnMenuItemClickListener.onMenuItemClick(view.getPosition(), menu, index);
                }

                if (mTouchView != null && !flag) {
                    mTouchView.smoothCloseMenu();
                }
            }
        });
    }

    public void setCloseInterpolator(Interpolator closeInterpolator) {
        mCloseInterpolator = closeInterpolator;
        if (mTouchView != null) {
            mTouchView.setCloseInterpolator(mCloseInterpolator);
        }
    }

    public void setOpenInterpolator(Interpolator openInterpolator) {
        mOpenInterpolator = openInterpolator;
        if (mTouchView != null) {
            mTouchView.setOpenInterpolator(mOpenInterpolator);
        }
    }

    public void setMenuCreator(SlideMenuCreator creator) {
        mMenuCreator = creator;
    }

    public void smoothOpenMenu(int position) {
        if (position >= getFirstVisiblePosition()
                && position <= getLastVisiblePosition()) {
            View view = getChildAt(position - getFirstVisiblePosition());
            if (view instanceof SlideMenuLayout) {
                mTouchPosition = position;
                if (mTouchView != null && mTouchView.isOpen()) {
                    mTouchView.smoothCloseMenu();
                }
                mTouchView = (SlideMenuLayout) view;
                mTouchView.setSlideDirection(mDirection);
                mTouchView.smoothOpenMenu();
            }
        }
    }

    public void smoothCloseMenu() {
        if (mTouchView != null && mTouchView.isOpen()) {
            mTouchView.smoothCloseMenu();
        }
    }

    public void setOnMenuItemClickListener(
            OnMenuItemClickListener onMenuItemClickListener) {
        this.mOnMenuItemClickListener = onMenuItemClickListener;
    }

    public void setOnSwipeListener(OnSwipeListener onSwipeListener) {
        this.mOnSwipListener = onSwipeListener;
    }

    public void setOnMenuStateChangeListener(OnMenuStateChangeListener onMenuStateChangeListener) {
        mOnmenuStateChangeListener = onMenuStateChangeListener;
    }

    public void setSlideDirection(SlideDirection direction) {
        mDirection = direction;
    }

    private int dp2px(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
        return (int) px;
    }

    public Interpolator getOpenInterpolator() {
        return null;
    }


    public Interpolator getCloseInterpolator() {
        return null;
    }

    public static interface OnSwipeListener {
        void onSwipeStart(int position);

        void onSwipeEnd(int position);
    }

    public static interface OnMenuStateChangeListener {
        void onMenuOpen(int position);

        void onMenuClose(int position);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() != MotionEvent.ACTION_DOWN && mTouchView == null)
            return super.onTouchEvent(ev);
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                int oldPos = mTouchPosition;
                mDownX = ev.getX();
                mDownY = ev.getY();
                mTouchState = TOUCH_STATE_NONE;

                mTouchPosition = pointToPosition((int) ev.getX(), (int) ev.getY());

                if (mTouchPosition == oldPos && mTouchView != null
                        && mTouchView.isOpen()) {
                    mTouchState = TOUCH_STATE_X;
                    mTouchView.postSlide(ev);
                    return true;
                }

                View view = getChildAt(mTouchPosition - getFirstVisiblePosition());

                if (mTouchView != null && mTouchView.isOpen()) {
                    mTouchView.smoothCloseMenu();
                    mTouchView = null;
                    MotionEvent cancelEvent = MotionEvent.obtain(ev);
                    cancelEvent.setAction(MotionEvent.ACTION_CANCEL);
                    onTouchEvent(cancelEvent);
                    if (mOnmenuStateChangeListener != null) {
                        mOnmenuStateChangeListener.onMenuClose(oldPos);
                    }
                }
                if (view instanceof SlideMenuLayout) {
                    mTouchView = (SlideMenuLayout) view;
                    mTouchView.setSlideDirection(mDirection);
                }
                if (mTouchView != null) {
                    mTouchView.postSlide(ev);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = Math.abs((ev.getY() - mDownY));
                float dx = Math.abs((ev.getX() - mDownX));
                if (mTouchState == TOUCH_STATE_X) {
                    if (mTouchView != null) {
                        mTouchView.postSlide(ev);
                    }
                    getSelector().setState(new int[]{0});
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    super.onTouchEvent(ev);
                    return true;
                } else if (mTouchState == TOUCH_STATE_NONE) {
                    if (Math.abs(dy) > MAX_Y) {
                        mTouchState = TOUCH_STATE_Y;
                    } else if (dx > MAX_X) {
                        mTouchState = TOUCH_STATE_X;
                        if (mOnSwipListener != null) {
                            mOnSwipListener.onSwipeStart(mTouchPosition);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mTouchState == TOUCH_STATE_X) {
                    if (mTouchView != null) {
                        boolean isBeforeOpen = mTouchView.isOpen();
                        mTouchView.postSlide(ev);
                        boolean isAfterOpen = mTouchView.isOpen();
                        if (isBeforeOpen != isAfterOpen && mOnmenuStateChangeListener != null) {
                            if (isAfterOpen) {
                                mOnmenuStateChangeListener.onMenuOpen(mTouchPosition);
                            } else {
                                mOnmenuStateChangeListener.onMenuClose(mTouchPosition);
                            }
                        }
                        if (!isAfterOpen) {
                            mTouchPosition = -1;
                            mTouchView = null;
                        }
                    }
                    if (mOnSwipListener != null) {
                        mOnSwipListener.onSwipeEnd(mTouchPosition);
                    }
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    super.onTouchEvent(ev);
                    return true;
                }
                break;
        }
        return super.onTouchEvent(ev);
    }


}
