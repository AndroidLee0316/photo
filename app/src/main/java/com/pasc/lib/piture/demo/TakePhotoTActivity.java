package com.pasc.lib.piture.demo;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.pasc.lib.picture.pictureSelect.NewPictureSelectActivity;
import com.pasc.lib.picture.takephoto.app.TakePhoto;
import com.pasc.lib.picture.takephoto.app.TakePhotoFragmentActivity;
import com.pasc.lib.picture.takephoto.compress.CompressConfig;
import com.pasc.lib.picture.takephoto.model.CropOptions;
import com.pasc.lib.picture.takephoto.model.LubanOptions;
import com.pasc.lib.picture.takephoto.model.TResult;
import com.pasc.lib.picture.takephoto.model.TakePhotoOptions;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author yangzijian
 * @date 2018/11/25
 * @des
 * @modify
 **/
public class TakePhotoTActivity extends TakePhotoFragmentActivity {
    public static final int REQUEST_CODE_CAMERA=10008;
    ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_takephoto);
        iv=findViewById (R.id.iv);

        // 传入自定义相册
        NewPictureSelectActivity.setIsHeadImg(false);
        getTakePhoto ().customPickActivity (NewPictureSelectActivity.class);
    }

    public void viewClick(View view) {
        configTakePhotoOption (getTakePhoto ());
        configCompress (getTakePhoto ());
        switch (view.getId ()) {
            case R.id.getPic:

                if (true/********/) {
                    getTakePhoto ().onPickMultipleWithCrop(1, getCropOptions());
                } else {
                    getTakePhoto ().onPickMultiple(3);
                }

                /****文件***/
//                if (rgCrop.getCheckedRadioButtonId() == R.id.rbCropYes) {
//                    getTakePhoto ().onPickFromDocumentsWithCrop(imageUri, getCropOptions());
//                } else {
//                    getTakePhoto ().onPickFromDocuments();
//                }

                /***相册***/
//                if (rgCrop.getCheckedRadioButtonId() == R.id.rbCropYes) {
//                    getTakePhoto ().onPickFromGalleryWithCrop(imageUri, getCropOptions());
//                } else {
//                    getTakePhoto ().onPickFromGallery();
//                }
                break;
            case R.id.takePic:
                //目前暂且 相册默认用 PictureSelectActivity 和 系统自带的
                // 稍后 可由 用户传入自定义 相册
                File file = new File (Environment.getExternalStorageDirectory (), "/temp/" + System.currentTimeMillis () + ".jpg");
                if (!file.getParentFile ().exists ()) {
                    file.getParentFile ().mkdirs ();
                }
                Uri imageUri = Uri.fromFile (file);

                /**裁剪***/
                getTakePhoto ().onPickFromCaptureWithCrop (imageUri, getCropOptions ());
                /**不裁剪***/
//                getTakePhoto ().onPickFromCapture(imageUri);

                break;



        }
    }


    private void configTakePhotoOption(TakePhoto takePhoto) {
        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder ();
        //是否使用TakePhoto自带相册
        builder.setWithOwnGallery (false);
        //纠正拍照的照片旋转角度
        builder.setCorrectImage (true);
        takePhoto.setTakePhotoOptions (builder.create ());

    }

    private void configCompress(TakePhoto takePhoto) {
        int maxSize = 102400; // 最大 100kb
        int width = 800;
        int height = 800;
        /***是否显示进度**/
        boolean showProgressBar = false;
        /***压缩后是否保留原图***/
        boolean enableRawFile =  true;
        CompressConfig config;
        if (true /****android 原生压缩***/) {
            config = new CompressConfig.Builder ().setMaxSize (maxSize)
                    .setMaxPixel (width >= height ? width : height)
                    .enableReserveRaw (enableRawFile)
                    .create ();
        } else {
            LubanOptions option = new LubanOptions.Builder ().setMaxHeight (height).setMaxWidth (width).setMaxSize (maxSize).create ();
            config = CompressConfig.ofLuban (option);
            config.enableReserveRaw (enableRawFile);
        }
        takePhoto.onEnableCompress (config, showProgressBar);


    }

    private CropOptions getCropOptions() {
        if (false /****是否需要裁剪****/) {
            return null;
        }
        int height = 800;
        int width = 800;
        /***是否为自带的裁剪工具**/
        boolean withWonCrop =  false;

        CropOptions.Builder builder = new CropOptions.Builder ();

        if (true /***比例**/) {
            builder.setAspectX (width).setAspectY (height);
        } else {
            builder.setOutputX (width).setOutputY (height);
        }
        builder.setWithOwnCrop (withWonCrop);
        return builder.create ();
    }

    @Override
    public void takeSuccess(TResult result) {
        Log.e ("yzj",result.getImages ().toString ());
        iv.setImageURI (Uri.fromFile (new File (result.getImage ().getOriginalPath ())));

    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail (result, msg);
    }

    @Override
    public void takeCancel() {
        super.takeCancel ();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) {
                Uri uri = data.getData();
                Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.VideoColumns._ID));
                    // 视频路径
                    String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA));
                    // ThumbnailUtils类2.2以上可用  Todo 获取视频缩略图
                    Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Images.Thumbnails.MICRO_KIND);
                    // 图片Bitmap转file
                    File file = saveBitmapFile(bitmap,null);
                    // 保存成功后插入到图库，其中的file是保存成功后的图片path。这里只是插入单张图片
                    // 通过发送广播将视频和图片插入相册
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                    cursor.close();
                }
            }
        }
    }


    /**
     * 把batmap 转file
     * @param bitmap
     * @param filepath
     */
    public static File saveBitmapFile(Bitmap bitmap, String filepath){
        File file=null;
        if (TextUtils.isEmpty(filepath)){
             file = new File(Environment.getExternalStorageDirectory() + "/temp.jpg");
        }else {
            file=new File(filepath);
        }

        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


}
