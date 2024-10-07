package com.pasc.lib.picture.pictureSelect;

import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;

import com.pasc.lib.picture.R;
import com.pasc.lib.widget.dialog.loading.LoadingDialogFragment;

/**
 * Created by duyuan797 on 17/10/3.
 */

public class BaseLoadingActivity extends AppCompatActivity {

    private LoadingDialogFragment mLoadingDialog;
    private boolean isDestroy = false;

    public boolean isActivityDestroy() {
        return isDestroy;
    }

    public void showLoading() {
        if (isDestroy){
            return;
        }
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialogFragment.Builder()
                    .setMessage(null)
                    .setRotateImageRes(R.drawable.picture_loading_big_red)
                    .build();
        }
        mLoadingDialog.show(getSupportFragmentManager(), "LoadingDialogFragment");
    }

    public void showLoading(@StringRes int resId) {
        showLoading(getString(resId));
    }

    public void showLoading(String loadingTip) {
        if (isDestroy){
            return;
        }
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialogFragment.Builder()
                    .setMessage(null)
                    .setRotateImageRes(R.drawable.picture_loading_big_red)
                    .build();
        } else {
            dismissLoading();
            mLoadingDialog = new LoadingDialogFragment.Builder()
                    .setMessage(loadingTip)
                    .setRotateImageRes(R.drawable.picture_loading_big_red)
                    .build();
        }
        mLoadingDialog.show(getSupportFragmentManager(), "LoadingDialogFragment");
    }

    public void dismissLoading() {
        if (mLoadingDialog != null && mLoadingDialog.isVisible()) {
            mLoadingDialog.dismiss();
        }
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        isDestroy = true;
        dismissLoading();
        mLoadingDialog = null;
    }
}
