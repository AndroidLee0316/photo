//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.luck.video.lib.widget.commondialog;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class RoundTextView extends TextView {
    private RoundViewDelegate delegate;

    public RoundTextView(Context context) {
        this(context, (AttributeSet)null);
    }

    public RoundTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.delegate = new RoundViewDelegate(this, context, attrs);
    }

    public RoundViewDelegate getDelegate() {
        return this.delegate;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(this.delegate.isWidthHeightEqual() && this.getWidth() > 0 && this.getHeight() > 0) {
            int max = Math.max(this.getWidth(), this.getHeight());
            int measureSpec = MeasureSpec.makeMeasureSpec(max, 1073741824);
            super.onMeasure(measureSpec, measureSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if(this.delegate.isRadiusHalfHeight()) {
            this.delegate.setCornerRadius(this.getHeight() / 2);
        } else {
            this.delegate.setBgSelector();
        }

    }
}
