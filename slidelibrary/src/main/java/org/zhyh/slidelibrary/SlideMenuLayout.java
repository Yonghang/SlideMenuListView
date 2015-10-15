package org.zhyh.slidelibrary;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;

import java.util.UUID;

/**
 * Created by zhyh on 10/15/15.
 */
class SlideMenuLayout extends FrameLayout {
    private final int CONTENT_VIEW_ID = UUID.randomUUID().hashCode();
    private final int MENU_VIEW_ID = UUID.randomUUID().hashCode();

    private static final int STATE_CLOSE = 1 << 0;
    private static final int STATE_OPEN = 1 << 1;

    private Interpolator mCloseInterpolator;
    private Interpolator mOpenInterpoloator;

    private View mContentView;
    private SlideMenuView mMenuView;

    private int mDownX;
    private int state = STATE_CLOSE;
    private GestureDetectorCompat mGestureDetector;
    private OnGestureListener mGestureListener;
    private boolean isFling;
    private final int MIN_FLING = dp2px(15);
    private final int MAX_VELOCITYX = -dp2px(500);

    private ScrollerCompat mOpenScroller;
    private ScrollerCompat mCloseScroller;

    private int mBaseX;
    private int position;

    public void setPosition(int position) {
        this.position = position;
        mMenuView.setPosition(position);
    }

    private SlideDirection mSlideDirection;

    private int dp2px(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
        return (int) px;
    }

    public SlideMenuLayout(View contentView, SlideMenuView menuView, int position) {
        this(contentView, menuView, null, null, position);
    }

    public SlideMenuLayout(View contentView, SlideMenuView menuView, Interpolator closeInterpolater, Interpolator openInterpolator, int position) {
        this(contentView.getContext());
        mCloseInterpolator = closeInterpolater;
        mOpenInterpoloator = openInterpolator;

        mContentView = contentView;
        mMenuView = menuView;

        this.position = position;

        init();
    }

    public void setSlideDirection(SlideDirection direction) {
        mSlideDirection = direction;
    }

    public void setOpenInterpolator(Interpolator openInterpolator) {
        mOpenInterpoloator = openInterpolator;
        flashInterpolator();
    }

    public void setCloseInterpolator(Interpolator closeInterpolator) {
        mCloseInterpolator = closeInterpolator;
        flashInterpolator();
    }

    public void setMenuView(SlideMenuView view) {
        removeMenuView();
        mMenuView = view;
        mMenuView.setPosition(position);
        flashMenuView();
    }

    public void setContentView(View contentView) {
        removeContentView();
        mContentView = contentView;
        flashContentView();
    }

    private SlideMenuLayout(Context context) {
        this(context, null);
    }

    private SlideMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void init() {
        setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        mGestureListener = new SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                isFling = false;
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (Math.abs(e1.getX() - e2.getX()) > MIN_FLING && velocityX < MAX_VELOCITYX) {
                    isFling = true;
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        };

        mGestureDetector = new GestureDetectorCompat(getContext(), mGestureListener);

        flashInterpolator();

        flashContentView();

        flashMenuView();
    }

    private void flashInterpolator() {
        if (mCloseInterpolator != null) {
            mCloseScroller = ScrollerCompat.create(getContext(), mCloseInterpolator);
        } else {
            mCloseScroller = ScrollerCompat.create(getContext());
        }

        if (mOpenInterpoloator != null) {
            mOpenScroller = ScrollerCompat.create(getContext(), mOpenInterpoloator);
        } else {
            mOpenScroller = ScrollerCompat.create(getContext());
        }
    }

    private void flashContentView() {
        LayoutParams contentParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mContentView.setLayoutParams(contentParams);

        if (mContentView.getId() < 1) {
            mContentView.setId(CONTENT_VIEW_ID);
        }
        addView(mContentView);
    }

    private void flashMenuView() {
        mMenuView.setId(MENU_VIEW_ID);
        mMenuView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        addView(mMenuView);
    }

    public boolean postSlide(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = (int) event.getX();
                isFling = false;
                break;
            case MotionEvent.ACTION_MOVE:
                int dis = (int) (mDownX - event.getX());
                if (state == STATE_OPEN) {
                    dis += mMenuView.getWidth() * mSlideDirection.value();
                }
                slide(dis);
                break;
            case MotionEvent.ACTION_UP:
                if ((isFling || Math.abs(mDownX - event.getX()) > (mMenuView.getWidth() / 2)) &&
                        Math.signum(mDownX - event.getX()) == mSlideDirection.value()) {
                    // open
                    smoothOpenMenu();
                } else {
                    // close
                    smoothCloseMenu();
                    return false;
                }
                break;
        }
        return true;
    }

    public boolean isOpen() {
        return state == STATE_OPEN;
    }

    public void smoothCloseMenu() {
        state = STATE_CLOSE;
        if (mSlideDirection == SlideDirection.DIRECTION_LEFT) {
            mBaseX = -mContentView.getLeft();
            mCloseScroller.startScroll(0, 0, mMenuView.getWidth(), 0, 350);
        } else {
            mBaseX = mMenuView.getRight();
            mCloseScroller.startScroll(0, 0, mMenuView.getWidth(), 0, 350);
        }
        postInvalidate();
    }

    public void smoothOpenMenu() {
        state = STATE_OPEN;
        if (mSlideDirection == SlideDirection.DIRECTION_LEFT) {
            mOpenScroller.startScroll(-mContentView.getLeft(), 0, mMenuView.getWidth(), 0, 350);
        } else {
            mOpenScroller.startScroll(mContentView.getLeft(), 0, mMenuView.getWidth(), 0, 350);
        }
        postInvalidate();

    }

    private void slide(int dis) {
        if (Math.signum(dis) != mSlideDirection.value()) {
            dis = 0;
        } else if (Math.abs(dis) > mMenuView.getWidth()) {
            dis = mMenuView.getWidth() * mSlideDirection.value();
        }

        mContentView.layout(-dis, mContentView.getTop(), mContentView.getWidth() - dis, getMeasuredHeight());

        if (mSlideDirection == SlideDirection.DIRECTION_LEFT) {
            mMenuView.layout(mContentView.getWidth() - dis, mMenuView.getTop(), mContentView.getWidth() + mMenuView.getWidth() - dis, mMenuView.getBottom());
        } else {
            mMenuView.layout(-mMenuView.getWidth() - dis, mMenuView.getTop(), -dis, mMenuView.getBottom());
        }
    }

    @Override
    public void computeScroll() {
        if (state == STATE_OPEN) {
            if (mOpenScroller.computeScrollOffset()) {
                slide(mOpenScroller.getCurrX() * mSlideDirection.value());
                postInvalidate();
            }
        } else {
            if (mCloseScroller.computeScrollOffset()) {
                slide((mBaseX - mCloseScroller.getCurrX()) * mSlideDirection.value());
                postInvalidate();
            }
        }
    }

    public void closeMenu() {
        if (mCloseScroller.computeScrollOffset()) {
            mCloseScroller.abortAnimation();
        }
        if (state == STATE_OPEN) {
            state = STATE_CLOSE;
            slide(0);
        }
    }

    public void openMenu() {
        if (state == STATE_CLOSE) {
            state = STATE_OPEN;
            slide(mMenuView.getWidth() * mSlideDirection.value());
        }
    }

    public View getContentView() {
        return mContentView;
    }

    public SlideMenuView getMenuView() {
        return mMenuView;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMenuView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mContentView.layout(0, 0, getMeasuredWidth(),
                mContentView.getMeasuredHeight());
        if (mSlideDirection == SlideDirection.DIRECTION_LEFT) {
            mMenuView.layout(getMeasuredWidth(), 0, getMeasuredWidth() + mMenuView.getMeasuredWidth(), mContentView.getMeasuredHeight());
        } else {
            mMenuView.layout(-mMenuView.getMeasuredWidth(), 0, 0, mContentView.getMeasuredHeight());
        }
    }

    private void removeContentView() {
        removeView(mContentView);
    }

    private void removeMenuView() {
        removeView(mMenuView);
    }
}
