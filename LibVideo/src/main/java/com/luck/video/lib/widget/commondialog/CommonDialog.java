//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.luck.video.lib.widget.commondialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import com.luck.video.lib.R;
import com.luck.video.lib.tools.DensityUtils;
import com.luck.video.lib.tools.ScreenUtils;

public class CommonDialog extends Dialog {
    public static final int Common_Center = 0;
    public static final int Common_Bottom = 1;
    public static final int Common_Top = 2;
    public static final String Black_333333 = "#333333";
    public static final String Gray_999999 = "#999999";
    public static final String Red_f14431 = "#f14431";
    public static final String Blue_4d73f4 = "#4d73f4";
    public static final String Transparent_00000000 = "#00000000";
    private CommonDialog.OnButtonClickListener onButtonClickListener;
    private int type;
    private RoundTextView tvTitle;
    private RoundTextView tvContent;
    private View vDivider;
    private View vDivider1;
    private View vDivider2;
    private View vDivider3;
    private boolean divider2show;
    private boolean divider3show;
    private RoundTextView tvButton1;
    private RoundTextView tvButton2;
    private RoundTextView tvButton3;
    private String title;
    private String content;
    private String button1;
    private String button2;
    private String button3;
    private String titleColor;
    private String contentColor;
    private String button1Color;
    private String button2Color;
    private String button3Color;
    private int cornerSize;

    public CommonDialog(@NonNull Context context, @CommonDialog.DialogType int type) {
        super(context, R.style.VideoRoundDialog);
        this.type = type;
        this.initView();
    }

    public CommonDialog(@NonNull Context context) {
        this(context, 0);
    }

    private void initView() {
        int widthPixels = ScreenUtils.getScreenWidth();
        View contentView;
        MarginLayoutParams params;
        switch(this.type) {
        case 0:
            contentView = LayoutInflater.from(this.getContext()).inflate( R.layout.video_dialog_common_center, (ViewGroup)null);
            this.setContentView(contentView);
            params = (MarginLayoutParams)contentView.getLayoutParams();
            params.width = widthPixels - DensityUtils.dp2px(100.0F);
            params.leftMargin = DensityUtils.dp2px(100.0F) / 2;
            params.rightMargin = DensityUtils.dp2px(100.0F) / 2;
            contentView.setLayoutParams(params);
            //this.getWindow().setBackgroundDrawableResource(17170445);
            this.tvTitle = this.findViewById( R.id.tv_title);
            this.tvContent = this.findViewById( R.id.tv_content);
            this.tvButton1 = (RoundTextView)this.findViewById( R.id.tv_button1);
            this.vDivider = this.findViewById( R.id.v_divider);
            this.tvButton2 = (RoundTextView)this.findViewById( R.id.tv_button2);
            break;

        }

        this.tvButton1.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                CommonDialog.this.dismiss();
                if(CommonDialog.this.onButtonClickListener != null) {
                    CommonDialog.this.onButtonClickListener.button1Click();
                }

            }
        });
        if(this.tvButton2 != null) {
            this.tvButton2.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    CommonDialog.this.dismiss();
                    if(CommonDialog.this.onButtonClickListener != null) {
                        CommonDialog.this.onButtonClickListener.button2Click();
                    }

                }
            });
        }

        if(this.tvButton3 != null) {
            this.tvButton3.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    CommonDialog.this.dismiss();
                    if(CommonDialog.this.onButtonClickListener != null) {
                        CommonDialog.this.onButtonClickListener.button3Click();
                    }

                }
            });
        }

    }

    private void initCommonCenter() {
        if(this.title != null && !TextUtils.isEmpty(this.title)) {
            this.tvTitle.setVisibility(0);
            this.tvTitle.setText(this.title);
            if(this.titleColor != null) {
                this.tvTitle.setTextColor(Color.parseColor(this.contentColor));
            }
        } else {
            this.tvTitle.setVisibility(8);
        }

        this.tvContent.setText(this.content);
        if(this.contentColor != null) {
            this.tvContent.setTextColor(Color.parseColor(this.contentColor));
        }

        this.tvButton1.setText(this.button1);
        if(this.button1Color != null) {
            this.tvButton1.setTextColor(Color.parseColor(this.button1Color));
        }

        if(this.button2 != null && !TextUtils.isEmpty(this.button2)) {
            this.vDivider.setVisibility(0);
            this.tvButton2.setVisibility(0);
            this.tvButton2.setText(this.button2);
            if(this.button2Color != null) {
                this.tvButton2.setTextColor(Color.parseColor(this.button2Color));
            }
        } else {
            this.vDivider.setVisibility(8);
            this.tvButton2.setVisibility(8);
        }

    }

    private void initCommonBottom() {
        this.cornerSize = DensityUtils.dp2px(3.0F);
        if(this.content != null && !TextUtils.isEmpty(this.content)) {
            this.tvContent.setVisibility(0);
            this.tvContent.setText(this.content);
            this.vDivider1.setVisibility(0);
            if(this.contentColor != null) {
                this.tvContent.setTextColor(Color.parseColor(this.contentColor));
            }
        } else {
            this.tvContent.setVisibility(8);
            this.vDivider1.setVisibility(8);
            this.tvButton1.getDelegate().setCornerRadius_TL(this.cornerSize);
            this.tvButton1.getDelegate().setCornerRadius_TR(this.cornerSize);
        }

        this.tvButton1.setText(this.button1);
        if(this.button1Color != null) {
            this.tvButton1.setTextColor(Color.parseColor(this.button1Color));
        }

        if(this.button2 != null && !TextUtils.isEmpty(this.button2)) {
            this.tvButton2.setVisibility(0);
            this.tvButton2.setText(this.button2);
            if(this.divider2show) {
                this.vDivider2.setVisibility(0);
                this.tvButton1.getDelegate().setCornerRadius_BL(this.cornerSize);
                this.tvButton1.getDelegate().setCornerRadius_BR(this.cornerSize);
                this.tvButton2.getDelegate().setCornerRadius(this.cornerSize);
            } else {
                this.vDivider2.setVisibility(8);
                this.tvButton2.getDelegate().setCornerRadius_BL(this.cornerSize);
                this.tvButton2.getDelegate().setCornerRadius_BR(this.cornerSize);
            }

            if(this.button2Color != null) {
                this.tvButton2.setTextColor(Color.parseColor(this.button2Color));
            }
        } else {
            this.tvButton2.setVisibility(8);
            this.tvButton1.getDelegate().setCornerRadius_BL(this.cornerSize);
            this.tvButton1.getDelegate().setCornerRadius_BR(this.cornerSize);
        }

        if(TextUtils.isEmpty(this.button3)) {
            this.tvButton3.setVisibility(8);
            this.tvButton2.getDelegate().setCornerRadius_BL(this.cornerSize);
            this.tvButton2.getDelegate().setCornerRadius_BR(this.cornerSize);
        } else {
            this.tvButton3.setVisibility(0);
            this.tvButton3.setText(this.button3);
            if(this.divider3show) {
                this.vDivider3.setVisibility(0);
                this.tvButton2.getDelegate().setCornerRadius(this.cornerSize);
                this.tvButton3.getDelegate().setCornerRadius(this.cornerSize);
            } else {
                this.vDivider3.setVisibility(8);
            }

            if(this.button3Color != null) {
                this.tvButton3.setTextColor(Color.parseColor(this.button3Color));
            }
        }

    }

    private void initCommonTop() {
        this.tvButton1.setText(this.button1);
        if(this.button1Color != null) {
            this.tvButton1.setTextColor(Color.parseColor(this.button1Color));
        }

        this.tvButton2.setText(this.button2);
        if(this.button2Color != null) {
            this.tvButton2.setTextColor(Color.parseColor(this.button2Color));
        }

    }

    public CommonDialog setOnButtonClickListener(CommonDialog.OnButtonClickListener onButtonClickListener) {
        this.onButtonClickListener = onButtonClickListener;
        return this;
    }

    public void show() {
        if(this.getContext() != null) {
            super.show();
            switch(this.type) {
            case 0:
                this.initCommonCenter();
                break;
            case 1:
                this.initCommonBottom();
                break;
            case 2:
                this.initCommonTop();
            }

        }
    }

    public CommonDialog setTitle(String title) {
        return this.setTitle(title, (String)null);
    }

    public CommonDialog setTitle(String title, @CommonDialog.DialogColor String titleColor) {
        this.title = title;
        this.titleColor = titleColor;
        return this;
    }

    public CommonDialog setContent(String content) {
        return this.setContent(content, (String)null);
    }

    public CommonDialog setContent(String content, @CommonDialog.DialogColor String contentColor) {
        this.content = content;
        this.contentColor = contentColor;
        return this;
    }

    public CommonDialog setButton1(String button1) {
        return this.setButton1(button1, (String)null);
    }

    public CommonDialog setButton1(String button1, @CommonDialog.DialogColor String button1Color) {
        this.button1 = button1;
        this.button1Color = button1Color;
        return this;
    }

    public CommonDialog setButton2(String button2) {
        return this.setButton2(button2, (String)null);
    }

    public CommonDialog setButton2(String button2, @CommonDialog.DialogColor String button2Color) {
        this.button2 = button2;
        this.button2Color = button2Color;
        return this;
    }

    public CommonDialog setButton3(String button3) {
        return this.setButton3(button3, (String)null);
    }

    public CommonDialog setButton3(String button3, @CommonDialog.DialogColor String button3Color) {
        this.button3 = button3;
        this.button3Color = button3Color;
        return this;
    }

    public CommonDialog setDevider2Show(boolean divider2show) {
        this.divider2show = divider2show;
        return this;
    }

    public CommonDialog setDevider3Show(boolean divider3show) {
        this.divider3show = divider3show;
        return this;
    }

    public abstract static class OnButtonClickListener {
        public OnButtonClickListener() {
        }

        public void button1Click() {
        }

        public void button2Click() {
        }

        public void button3Click() {
        }
    }

    public @interface DialogColor {
    }

    public @interface DialogType {
    }
}
