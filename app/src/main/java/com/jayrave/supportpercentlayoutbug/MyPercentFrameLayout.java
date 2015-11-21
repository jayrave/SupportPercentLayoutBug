package com.jayrave.supportpercentlayoutbug;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.percent.PercentFrameLayout;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class MyPercentFrameLayout extends PercentFrameLayout {

    public MyPercentFrameLayout(Context context) {
        super(context);
    }

    public MyPercentFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyPercentFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        LayoutParams lp = new LayoutParams(getContext(), attrs);
        if (isInvalidLayoutParams(lp)) {
            throw new UnsupportedOperationException("width or height is missing");
        }

        return lp;
    }


    private static boolean isInvalidLayoutParams(LayoutParams lp) {
        // Either layout_width or layout_widthPercent must be mentioned. Likewise either
        // layout_height or layout_heightPercent must be mentioned to be valid

        return ((lp.width == LayoutParams.INVALID_DIMENSION &&
                lp.getPercentLayoutInfo().widthPercent < 0) ||
                (lp.height == LayoutParams.INVALID_DIMENSION &&
                        lp.getPercentLayoutInfo().heightPercent < 0));
    }


    public static class LayoutParams extends PercentFrameLayout.LayoutParams {

        public static final int INVALID_DIMENSION = Integer.MIN_VALUE;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(FrameLayout.LayoutParams source) {
            super(source);
        }

        public LayoutParams(PercentFrameLayout.LayoutParams source) {
            super(source);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height, gravity);
        }

        @Override
        protected void setBaseAttributes(TypedArray a, int widthAttr, int heightAttr) {
            // If layout_width or layout_height aren't present, don't set to 0 as
            // PercentLayoutHelper#fetchWidthAndHeight does. Use an recognizable
            // invalid value to check later and decide whether this is a valid
            // layout params object

            width = a.getLayoutDimension(widthAttr, INVALID_DIMENSION);
            height = a.getLayoutDimension(heightAttr, INVALID_DIMENSION);
        }
    }
}
