package org.zhyh.slidelibrary;

/**
 * Created by zhyh on 10/15/15.
 */
public enum SlideDirection {
    DIRECTION_LEFT(1), DIRECTION_RIGHT(-1);

    private int value;
    SlideDirection(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
