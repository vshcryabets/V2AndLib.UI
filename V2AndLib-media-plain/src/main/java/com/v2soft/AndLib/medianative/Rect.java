package com.v2soft.AndLib.medianative;

/**
 * Created by vshcryabets on 2/5/15.
 */
public class Rect {
    public int left;
    public int right;
    public int top;
    public int bottom;

    /**
     * Create a new rectangle with the specified coordinates.
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public Rect(int left, int top, int right, int bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    public int[] toArray() {
        return new int[]{left, top, right, bottom};
    }
}
