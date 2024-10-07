package com.pasc.lib.piture.demo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.iceteck.silicompressorr.SiliCompressor;
import com.luck.video.lib.PictureSelector;
import com.luck.video.lib.config.PictureConfig;
import com.luck.video.lib.config.PictureMimeType;
import com.luck.video.lib.entity.LocalMedia;
import com.luck.video.lib.permissions.RxPermissions;
import com.luck.video.lib.tools.PictureFileUtils;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class VideoDemoActivity extends AppCompatActivity implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener {
    private final static String TAG = MainActivity.class.getSimpleName();
    private List<LocalMedia> selectList = new ArrayList<>();
    private RecyclerView recyclerView;
    private GridImageAdapter adapter;
    private int maxSelectNum = 9;
    private TextView tv_select_num;
    private ImageView left_back, minus, plus;
    private RadioGroup  rgb_style, rgb_photo_mode;
    private CheckBox cb_voice, cb_choose_mode, cb_isCamera,
            cb_preview_img, cb_preview_video, cb_compress,
            cb_mode,cb_video_quality;
    private int themeId;
    private int chooseMode = PictureMimeType.ofVideo();
    private LoadingDialog loadingDialog;
    private EditText etVideoDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_demo);
        themeId = R.style.picture_default_style;
        minus = (ImageView) findViewById(R.id.minus);
        plus = (ImageView) findViewById(R.id.plus);
        tv_select_num = (TextView) findViewById(R.id.tv_select_num);
        rgb_style = (RadioGroup) findViewById(R.id.rgb_style);
        rgb_photo_mode = (RadioGroup) findViewById(R.id.rgb_photo_mode);
        cb_voice = (CheckBox) findViewById(R.id.cb_voice);
        cb_choose_mode = (CheckBox) findViewById(R.id.cb_choose_mode);
        cb_isCamera = (CheckBox) findViewById(R.id.cb_isCamera);
        cb_preview_img = (CheckBox) findViewById(R.id.cb_preview_img);
        cb_preview_video = (CheckBox) findViewById(R.id.cb_preview_video);
        cb_compress = (CheckBox) findViewById(R.id.cb_compress);
        cb_video_quality = (CheckBox) findViewById(R.id.cb_video_quality);
        cb_mode = (CheckBox) findViewById(R.id.cb_mode);
        rgb_style.setOnCheckedChangeListener(this);
        rgb_photo_mode.setOnCheckedChangeListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        left_back = (ImageView) findViewById(R.id.left_back);
        etVideoDuration = findViewById(R.id.et_video_duration);

        left_back.setOnClickListener(this);
        minus.setOnClickListener(this);
        plus.setOnClickListener(this);
        FullyGridLayoutManager manager = new FullyGridLayoutManager(VideoDemoActivity.this, 4, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new GridImageAdapter(VideoDemoActivity.this, onAddPicClickListener);
        adapter.setList(selectList);
        adapter.setSelectMax(maxSelectNum);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (selectList.size() > 0) {
                    LocalMedia media = selectList.get(position);
                    String pictureType = media.getPictureType();
                    int mediaType = PictureMimeType.pictureToVideo(pictureType);
                    switch (mediaType) {
                        case 2:
                            // 预览视频
                            PictureSelector.create(VideoDemoActivity.this).externalPictureVideo(media.getPath());
                            break;
                    }
                }
            }
        });

        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        RxPermissions permissions = new RxPermissions(this);
        permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    PictureFileUtils.deleteCacheDirFile(VideoDemoActivity.this);
                } else {
                    Toast.makeText(VideoDemoActivity.this,
                            getString(R.string.picture_jurisdiction), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });

    }

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {
            boolean mode = cb_mode.isChecked();
            if (mode) {
                // 进入相册 以下是例子：不需要的api可以不写
                PictureSelector.create(VideoDemoActivity.this)
                        .openGallery(chooseMode)// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                        .theme(themeId)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                        .maxSelectNum(maxSelectNum)// 最大图片选择数量
                        .minSelectNum(1)// 最小选择数量
                        .imageSpanCount(4)// 每行显示个数
                        .selectionMode(cb_choose_mode.isChecked() ?
                                PictureConfig.MULTIPLE : PictureConfig.SINGLE)// 多选 or 单选
                        .previewImage(cb_preview_img.isChecked())// 是否可预览图片
                        .previewVideo(cb_preview_video.isChecked())// 是否可预览视频
                        .isCamera(cb_isCamera.isChecked())// 是否显示拍照按钮
                        .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                        .compress(cb_compress.isChecked())// 是否压缩
                        .synOrAsy(true)//同步true或异步false 压缩 默认同步
                        .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                        .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
                        .openClickSound(cb_voice.isChecked())// 是否开启点击声音
                        .selectionMedia(selectList)// 是否传入已选图片
                        .minimumCompressSize(100)// 小于100kb的图片不压缩
                        .videoQuality(cb_video_quality.isChecked()?1:0)// 视频录制质量 0 or 1
                        .videoMaxSize(100)//视频最大大小 M
                        .recordVideoSecond(Integer.parseInt(etVideoDuration.getText().toString()))//录制视频秒数 默认60s
                        .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
            } else {
                Log.e("aaaa","==========qqqqqq");
                // 单独拍照
                PictureSelector.create(VideoDemoActivity.this)
                        .openCamera(PictureMimeType.ofVideo())// 单独拍照，也可录像或也可音频 看你传入的类型是图片or视频
                        //.theme(themeId)// 主题样式设置 具体参考 values/styles
                        //.maxSelectNum(maxSelectNum)// 最大图片选择数量
                        //.minSelectNum(1)// 最小选择数量
                        //.selectionMode(cb_choose_mode.isChecked() ?
                        //        PictureConfig.MULTIPLE : PictureConfig.SINGLE)// 多选 or 单选
                        //.previewImage(cb_preview_img.isChecked())// 是否可预览图片
                        //.previewVideo(cb_preview_video.isChecked())// 是否可预览视频
                        //.isCamera(cb_isCamera.isChecked())// 是否显示拍照按钮
                        .compress(true)// 是否压缩
                        //.glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                        //.openClickSound(cb_voice.isChecked())// 是否开启点击声音
                        //.selectionMedia(selectList)// 是否传入已选图片
                        //.previewEggs(false)//预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                        //.minimumCompressSize(100)// 小于100kb的图片不压缩
                        .videoMaxSize(100) //视频最大大小 M
                        .videoQuality(1)// 视频录制质量 0 or 1
                        .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
            }
        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的

                    if (cb_compress.isChecked()){
                        for (LocalMedia media : selectList) {
                            Log.i("图片-----》", media.getPath());
                            File f = new File( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/Silicompressor/videos");
                            if (f.mkdirs() || f.isDirectory())
                                //compress and output new video specs
                                new VideoCompressAsyncTask(this).execute(media.getPath(), f.getPath());
                        }
                    }else{
                        for (LocalMedia media : selectList) {
                            Log.i("图片-----》", media.getPath());
                        }
                        adapter.setList(selectList);
                        adapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_back:
                finish();
                break;
            case R.id.minus:
                if (maxSelectNum > 1) {
                    maxSelectNum--;
                }
                tv_select_num.setText(maxSelectNum + "");
                adapter.setSelectMax(maxSelectNum);
                break;
            case R.id.plus:
                maxSelectNum++;
                tv_select_num.setText(maxSelectNum + "");
                adapter.setSelectMax(maxSelectNum);
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.rb_video:
                chooseMode = PictureMimeType.ofVideo();
                cb_preview_img.setChecked(false);
                cb_preview_video.setChecked(true);
                cb_preview_video.setChecked(true);
                cb_preview_video.setVisibility(View.VISIBLE);
                cb_preview_img.setVisibility(View.GONE);
                cb_preview_img.setChecked(false);
                cb_compress.setVisibility(View.GONE);
                break;
            case R.id.rb_default_style:
                themeId = R.style.picture_default_style;
                break;
            case R.id.rb_white_style:
                themeId = R.style.picture_white_style;
                break;
            case R.id.rb_num_style:
                themeId = R.style.picture_QQ_style;
                break;
            case R.id.rb_sina_style:
                themeId = R.style.picture_Sina_style;
                break;
        }
    }


    /**
     * 自定义压缩存储地址
     *
     * @return
     */
    private String getPath() {
        String path = Environment.getExternalStorageDirectory() + "/Luban/image/";
        File file = new File(path);
        if (file.mkdirs()) {
            return path;
        }
        return path;
    }

    class VideoCompressAsyncTask extends AsyncTask<String, String, String> {

        Context mContext;

        public VideoCompressAsyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (loadingDialog==null){
                loadingDialog = new LoadingDialog(VideoDemoActivity.this, "视屏压缩中...");
            }
            loadingDialog.show();
        }

        @Override

        protected String doInBackground(String... paths) {
            String filePath = null;
            try {

                filePath = SiliCompressor.with(mContext).compressVideo(paths[0], paths[1]);

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return filePath;

        }


        @Override
        protected void onPostExecute(String compressedFile) {
            super.onPostExecute(compressedFile);
            File imageFile = new File(compressedFile);
            float length = imageFile.length() / 1024f; // Size in KB
            String value;
            if (length >= 1024)
                value = length / 1024f + " MB";
            else
                value = length + " KB";
            int duration = PictureMimeType.getLocalVideoDuration(compressedFile);
            LocalMedia media = new LocalMedia();
            media.setPath(compressedFile);
            media.setDuration(duration);
            media.setPictureType(PictureMimeType.createVideoType(compressedFile));
            selectList.clear();
            selectList.add(media);
            adapter.setList(selectList);
            adapter.notifyDataSetChanged();
            loadingDialog.dismiss();
            Log.i("Silicompressor", "Path: " + compressedFile);
        }
    }

}
