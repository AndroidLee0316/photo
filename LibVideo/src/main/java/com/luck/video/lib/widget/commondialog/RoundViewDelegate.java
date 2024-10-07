//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.luck.video.lib.widget.commondialog;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.luck.video.lib.R.styleable;

public class RoundViewDelegate {
    private View view;
    private Context context;
    private GradientDrawable gd_background = new GradientDrawable();
    private GradientDrawable gd_background_press = new GradientDrawable();
    private int backgroundColor;
    private int backgroundPressColor;
    private int cornerRadius;
    private int cornerRadius_TL;
    private int cornerRadius_TR;
    private int cornerRadius_BL;
    private int cornerRadius_BR;
    private int strokeWidth;
    private int strokeColor;
    private int strokePressColor;
    private int textPressColor;
    private boolean isRadiusHalfHeight;
    private boolean isWidthHeightEqual;
    private boolean isRippleEnable;
    private float[] radiusArr = new float[8];

    public RoundViewDelegate(View view, Context context, AttributeSet attrs) {
        this.view = view;
        this.context = context;
        this.obtainAttributes(context, attrs);
    }

    private void obtainAttributes(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, styleable.VideoRoundTextView);
        this.backgroundColor = ta.getColor(styleable.VideoRoundTextView_rv_backgroundColor, 0);
        this.backgroundPressColor = ta.getColor(styleable.VideoRoundTextView_rv_backgroundPressColor, 2147483647);
        this.cornerRadius = ta.getDimensionPixelSize(styleable.VideoRoundTextView_rv_cornerRadius, 0);
        this.strokeWidth = ta.getDimensionPixelSize(styleable.VideoRoundTextView_rv_strokeWidth, 0);
        this.strokeColor = ta.getColor(styleable.VideoRoundTextView_rv_strokeColor, 0);
        this.strokePressColor = ta.getColor(styleable.VideoRoundTextView_rv_strokePressColor, 2147483647);
        this.textPressColor = ta.getColor(styleable.VideoRoundTextView_rv_textPressColor, 2147483647);
        this.isRadiusHalfHeight = ta.getBoolean(styleable.VideoRoundTextView_rv_isRadiusHalfHeight, false);
        this.isWidthHeightEqual = ta.getBoolean(styleable.VideoRoundTextView_rv_isWidthHeightEqual, false);
        this.cornerRadius_TL = ta.getDimensionPixelSize(styleable.VideoRoundTextView_rv_cornerRadius_TL, 0);
        this.cornerRadius_TR = ta.getDimensionPixelSize(styleable.VideoRoundTextView_rv_cornerRadius_TR, 0);
        this.cornerRadius_BL = ta.getDimensionPixelSize(styleable.VideoRoundTextView_rv_cornerRadius_BL, 0);
        this.cornerRadius_BR = ta.getDimensionPixelSize(styleable.VideoRoundTextView_rv_cornerRadius_BR, 0);
        this.isRippleEnable = ta.getBoolean(styleable.VideoRoundTextView_rv_isRippleEnable, true);
        ta.recycle();
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        this.setBgSelector();
    }

    public void setBackgroundPressColor(int backgroundPressColor) {
        this.backgroundPressColor = backgroundPressColor;
        this.setBgSelector();
    }

    public void setCornerRadius(int cornerRadius) {
        this.cornerRadius = this.dp2px((float)cornerRadius);
        this.setBgSelector();
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = this.dp2px((float)strokeWidth);
        this.setBgSelector();
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
        this.setBgSelector();
    }

    public void setStrokePressColor(int strokePressColor) {
        this.strokePressColor = strokePressColor;
        this.setBgSelector();
    }

    public void setTextPressColor(int textPressColor) {
        this.textPressColor = textPressColor;
        this.setBgSelector();
    }

    public void setIsRadiusHalfHeight(boolean isRadiusHalfHeight) {
        this.isRadiusHalfHeight = isRadiusHalfHeight;
        this.setBgSelector();
    }

    public void setIsWidthHeightEqual(boolean isWidthHeightEqual) {
        this.isWidthHeightEqual = isWidthHeightEqual;
        this.setBgSelector();
    }

    public void setCornerRadius_TL(int cornerRadius_TL) {
        this.cornerRadius_TL = cornerRadius_TL;
        this.setBgSelector();
    }

    public void setCornerRadius_TR(int cornerRadius_TR) {
        this.cornerRadius_TR = cornerRadius_TR;
        this.setBgSelector();
    }

    public void setCornerRadius_BL(int cornerRadius_BL) {
        this.cornerRadius_BL = cornerRadius_BL;
        this.setBgSelector();
    }

    public void setCornerRadius_BR(int cornerRadius_BR) {
        this.cornerRadius_BR = cornerRadius_BR;
        this.setBgSelector();
    }

    public int getBackgroundColor() {
        return this.backgroundColor;
    }

    public int getBackgroundPressColor() {
        return this.backgroundPressColor;
    }

    public int getCornerRadius() {
        return this.cornerRadius;
    }

    public int getStrokeWidth() {
        return this.strokeWidth;
    }

    public int getStrokeColor() {
        return this.strokeColor;
    }

    public int getStrokePressColor() {
        return this.strokePressColor;
    }

    public int getTextPressColor() {
        return this.textPressColor;
    }

    public boolean isRadiusHalfHeight() {
        return this.isRadiusHalfHeight;
    }

    public boolean isWidthHeightEqual() {
        return this.isWidthHeightEqual;
    }

    public int getCornerRadius_TL() {
        return this.cornerRadius_TL;
    }

    public int getCornerRadius_TR() {
        return this.cornerRadius_TR;
    }

    public int getCornerRadius_BL() {
        return this.cornerRadius_BL;
    }

    public int getCornerRadius_BR() {
        return this.cornerRadius_BR;
    }

    protected int dp2px(float dp) {
        float scale = this.context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5F);
    }

    protected int sp2px(float sp) {
        float scale = this.context.getResources().getDisplayMetrics().scaledDensity;
        return (int)(sp * scale + 0.5F);
    }

    private void setDrawable(GradientDrawable gd, int color, int strokeColor) {
        gd.setColor(color);
        if(this.cornerRadius_TL <= 0 && this.cornerRadius_TR <= 0 && this.cornerRadius_BR <= 0 && this.cornerRadius_BL <= 0) {
            gd.setCornerRadius((float)this.cornerRadius);
        } else {
            this.radiusArr[0] = (float)this.cornerRadius_TL;
            this.radiusArr[1] = (float)this.cornerRadius_TL;
            this.radiusArr[2] = (float)this.cornerRadius_TR;
            this.radiusArr[3] = (float)this.cornerRadius_TR;
            this.radiusArr[4] = (float)this.cornerRadius_BR;
            this.radiusArr[5] = (float)this.cornerRadius_BR;
            this.radiusArr[6] = (float)this.cornerRadius_BL;
            this.radiusArr[7] = (float)this.cornerRadius_BL;
            gd.setCornerRadii(this.radiusArr);
        }

        gd.setStroke(this.strokeWidth, strokeColor);
    }

    public void setBgSelector() {
        StateListDrawable bg = new StateListDrawable();
        if(VERSION.SDK_INT >= 21 && this.isRippleEnable) {
            this.setDrawable(this.gd_background, this.backgroundColor, this.strokeColor);
            RippleDrawable rippleDrawable = new RippleDrawable(this.getPressedColorSelector(this.backgroundColor, this.backgroundPressColor), this.gd_background, (Drawable)null);
            this.view.setBackground(rippleDrawable);
        } else {
            this.setDrawable(this.gd_background, this.backgroundColor, this.strokeColor);
            bg.addState(new int[]{-16842919}, this.gd_background);
            if(this.backgroundPressColor != 2147483647 || this.strokePressColor != 2147483647) {
                this.setDrawable(this.gd_background_press, this.backgroundPressColor == 2147483647?this.backgroundColor:this.backgroundPressColor, this.strokePressColor == 2147483647?this.strokeColor:this.strokePressColor);
                bg.addState(new int[]{16842919}, this.gd_background_press);
            }

            if(VERSION.SDK_INT >= 16) {
                this.view.setBackground(bg);
            } else {
                this.view.setBackgroundDrawable(bg);
            }
        }

        if(this.view instanceof TextView && this.textPressColor != 2147483647) {
            ColorStateList textColors = ((TextView)this.view).getTextColors();
            ColorStateList colorStateList = new ColorStateList(new int[][]{{-16842919}, {16842919}}, new int[]{textColors.getDefaultColor(), this.textPressColor});
            ((TextView)this.view).setTextColor(colorStateList);
        }

    }

    @TargetApi(11)
    private ColorStateList getPressedColorSelector(int normalColor, int pressedColor) {
        return new ColorStateList(new int[][]{{16842919}, {16842908}, {16843518}, new int[0]}, new int[]{pressedColor, pressedColor, pressedColor, normalColor});
    }
}
