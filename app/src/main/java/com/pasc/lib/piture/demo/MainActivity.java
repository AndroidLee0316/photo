package com.pasc.lib.piture.demo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.luck.video.lib.PictureVideoPlayActivity;
import com.pasc.lib.base.permission.PermissionUtils;
import com.pasc.lib.fileselector.activity.MediaStoreActivity;
import com.pasc.lib.picture.pictureSelect.ImagePicker;
import com.pasc.lib.picture.takephoto.uitl.TConstant;
import com.pasc.lib.picture.util.GlideUtil;
import com.pasc.lib.widget.permission.PermissionSettingUtils;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;

import static com.pasc.lib.piture.demo.TakePhotoTActivity.REQUEST_CODE_CAMERA;

public class MainActivity extends AppCompatActivity {

    private String[] storePermission = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    String[] needCameraPermission = new String[]{
            Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    String[] needSdcardPermission = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int REQUEST_CODE_FILES_SELECT = 200;
    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
        iv = findViewById (R.id.iv);
    }

    public void viewClick(View view) {
        switch (view.getId ()) {
            case R.id.takePic:
                ActivityCompat.requestPermissions (this,
                        needCameraPermission,
                        1001);
                break;
            case R.id.getPic:
                ActivityCompat.requestPermissions (this,
                        storePermission,
                        1002);
                break;


            case R.id.takeVideo:
               Intent intent = new Intent();
                intent.setAction("android.media.action.VIDEO_CAPTURE");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                // 录制视频最大时长15s
                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 3);
                intent.putExtra("android.intent.extras.CAMERA_FACING",2);
                startActivityForResult(intent, REQUEST_CODE_CAMERA);
                startActivity(new Intent(this,VideoDemoActivity.class));

//                Intent intent = new Intent(this, PictureVideoPlayActivity.class);
//                intent.putExtra("video_path", "http://vip.iobs.pingan.com.cn/download/szsc-smt-app-dmz-prd//51562834288720");
//                intent.putExtra(PictureVideoPlayActivity.VIDEO_PLAY_MODE,
//                    PictureVideoPlayActivity.AUTO_PLAY);
//                intent.putExtra(PictureVideoPlayActivity.VIDEO_USE_MODE,
//                    PictureVideoPlayActivity.PREVIEW_MODE);
//                startActivity(intent);
                break;
            case R.id.takeFile:

                PermissionUtils.request(this, needSdcardPermission)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if (aBoolean) {
                                    Intent intent = new Intent(MainActivity.this, MediaStoreActivity.class);
                                    startActivityForResult(intent, REQUEST_CODE_FILES_SELECT);
                                } else {
                                    PermissionSettingUtils.gotoPermissionSetting(MainActivity.this);
                                }
                            }
                        });
                break;

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult (requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1001: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity (new Intent (this,TakePhotoTActivity.class));
                } else {
                    Toast.makeText (this, "必须需要Sdcard权限", Toast.LENGTH_SHORT).show ();

                }
                return;
            }
            case 1002: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    NewPictureSelectActivity.actionStart (this, 100, 4);
                    ImagePicker.getInstance().setEnableCompress(true).
                            setSelectLimit(4).pickMutlPhoto(this,100);
                } else {
                    Toast.makeText (this, "必须需要相机权限", Toast.LENGTH_SHORT).show ();
                }
                return;
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        if (requestCode == 101 && data != null) {
            ArrayList<String> pictures = data.getStringArrayListExtra (TConstant.INTENT_EXTRA_IMAGES);
            Log.e ("picTag", pictures.toString ());
        } else if (requestCode == 102 && resultCode==RESULT_OK) {
            Log.e ("picTag", "camera");
        }else if(requestCode == 100 && resultCode==RESULT_OK) {
            ArrayList<String> pictures = data.getStringArrayListExtra("images");
            Log.e ("pictures", pictures.toString());
            GlideUtil.loadImage (this,iv, pictures.get(0),
                    com.pasc.lib.picture.R.drawable.picture_bg_default_image_color, com.pasc.lib.picture.R.drawable.picture_bg_default_image_color);
        }else if (requestCode == REQUEST_CODE_FILES_SELECT && data != null) {
            String filePath = data.getStringExtra("selectFile");
            if(TextUtils.isEmpty(filePath)){
                return;
            }
            Log.e("aaaaa","=========== filePath : " + filePath);
        }
    }
    public static void main(String[] args){
        for (int i = 0; i < 10; i++) {
            System.out.println();
        }
    }
}
