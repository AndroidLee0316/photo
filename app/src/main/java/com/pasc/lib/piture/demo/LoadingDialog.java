//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.pasc.lib.piture.demo;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LoadingDialog extends Dialog {
    TextView textView;
    View dialogView;
    ProgressBar progressBar;

    public LoadingDialog(Context context) {
        super(context, R.style.common_loading_dialog);
        this.setContentView(R.layout.temp_common_loading);
        this.dialogView = (RelativeLayout)this.findViewById(R.id.temp_common_dialog_loading_relativelayout);
        this.progressBar = (ProgressBar)this.findViewById(R.id.temp_common_dialog_loading_progressbar);
        this.textView = (TextView)this.findViewById(R.id.temp_common_dialog_loading_textview);
    }

    public LoadingDialog(Context context, String loadingMsg) {
        this(context);
        this.setContent(loadingMsg);
    }

    public LoadingDialog(Context context, int loadingMsgId) {
        this(context);
        this.setContent(loadingMsgId);
    }

    public void setHasContent(boolean hasContent) {
        this.textView.setVisibility(hasContent?0:8);
    }

    public void setLoadingDialogBackGround(int resourceId) {
        if(null != this.dialogView) {
            this.dialogView.setBackgroundResource(resourceId);
        }

    }

    public void setLoadingDialogBackGround(Drawable drawable) {
        if(null != this.dialogView) {
            this.dialogView.setBackgroundDrawable(drawable);
        }

    }

    public View getLoadingDialogView() {
        return this.dialogView;
    }

    public TextView getTextView() {
        return this.textView;
    }

    public void setContent(String content) {
        if(null != this.textView) {
            this.textView.setText(content);
        }

    }

    public void setContent(int resourceId) {
        if(null != this.textView) {
            this.textView.setText(resourceId);
        }

    }

    /** @deprecated */
    @Deprecated
    public static class LoadingDialogDelegate {
        private LoadingDialog mLoadingDialog;

        public LoadingDialogDelegate(Context context) {
            this.mLoadingDialog = new LoadingDialog(context);
        }

        public LoadingDialogDelegate(Context context, String loadingMsg) {
            this.mLoadingDialog = new LoadingDialog(context, loadingMsg);
        }

        public LoadingDialogDelegate(Context context, int loadingMsgId) {
            this.mLoadingDialog = new LoadingDialog(context, loadingMsgId);
        }

        public void showLoadingDialog(int msg) {
            if(this.mLoadingDialog != null && !this.mLoadingDialog.isShowing()) {
                this.mLoadingDialog.setContent(msg);
                this.mLoadingDialog.show();
            }

        }

        public void showLoadingDialog() {
            if(this.mLoadingDialog != null && !this.mLoadingDialog.isShowing()) {
                this.mLoadingDialog.show();
            }

        }

        public void showLoadingDialog(String msg) {
            if(this.mLoadingDialog != null && !this.mLoadingDialog.isShowing()) {
                this.mLoadingDialog.setContent(msg);
                this.mLoadingDialog.show();
            }

        }

        public void setContent(String content) {
            this.mLoadingDialog.setContent(content);
        }

        public boolean isShowingDialog() {
            return this.mLoadingDialog != null && this.mLoadingDialog.isShowing();
        }

        public void dismissLoadingDialog() {
            if(this.mLoadingDialog != null && this.mLoadingDialog.isShowing()) {
                this.mLoadingDialog.dismiss();
            }

        }
    }
}
