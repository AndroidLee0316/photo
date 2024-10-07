package com.pasc.lib.picture.pictureSelect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.pasc.lib.picture.takephoto.uitl.TConstant;
import com.soundcloud.android.crop.CropImageView;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ImagePicker implements Serializable {

    public static final String TAG = ImagePicker.class.getSimpleName();
    private boolean multiMode = true;    //图片选择模式
    private static int selectLimit =-1;         //最大选择图片数量
    private boolean enableCompress;


    private boolean showCamera = false;   //显示相机
    private static volatile ImagePicker mInstance;

    private ImagePicker() {
    }

    public static  ImagePicker getInstance() {
        if (mInstance == null) {
            synchronized (ImagePicker.class) {
                if (mInstance == null) {
                    mInstance = new ImagePicker();
                }
            }
        }
        return mInstance;
    }

    public static boolean isEnable() {
        return getSelectLimit()>0;
    }

    public boolean isMultiMode() {
        return multiMode;
    }

    public ImagePicker setMultiMode(boolean multiMode) {
        this.multiMode = multiMode;
        return this;
    }

    public static int getSelectLimit() {
        return selectLimit;
    }

    public ImagePicker setSelectLimit(int limit) {
        selectLimit = limit;
        return this;
    }

    public boolean isShowCamera() {
        return showCamera;
    }

    public ImagePicker setShowCamera(boolean showCamera) {
        this.showCamera = showCamera;
        return this;
    }

    public ImagePicker setEnableCompress(boolean enableCompress) {
        this.enableCompress = enableCompress;
        return this;
    }

    /**
     * 选择多张照片的方法
     * @param context
     * @param requestCode
     */
    public void pickMutlPhoto(Activity context, int requestCode) {
        Intent intent = new Intent (context, NewPictureSelectActivity.class);
        NewPictureSelectActivity.setIsHeadImg(false);
        context.startActivityForResult(intent, requestCode);
    }


    /**
     * 选择头像的方法
     * @param context
     * @param requestCode
     */
    public void pickHeadPhoto(Activity context, int requestCode) {
        Intent intent = new Intent (context, NewPictureSelectActivity.class);
        NewPictureSelectActivity.setIsHeadImg(true);
        context.startActivityForResult(intent, requestCode);
    }


    public boolean isEnableCompress() {
        return enableCompress;
    }
}