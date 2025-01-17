package com.luck.video.lib;

import android.Manifest;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.luck.video.lib.adapter.PictureAlbumDirectoryAdapter;
import com.luck.video.lib.adapter.PictureImageGridAdapter;
import com.luck.video.lib.config.PictureConfig;
import com.luck.video.lib.config.PictureMimeType;
import com.luck.video.lib.decoration.GridSpacingItemDecoration;
import com.luck.video.lib.dialog.CustomDialog;
import com.luck.video.lib.entity.EventEntity;
import com.luck.video.lib.entity.LocalMedia;
import com.luck.video.lib.entity.LocalMediaFolder;
import com.luck.video.lib.model.LocalMediaLoader;
import com.luck.video.lib.observable.ImagesObservable;
import com.luck.video.lib.permissions.RxPermissions;
import com.luck.video.lib.rxbus2.RxBus;
import com.luck.video.lib.rxbus2.Subscribe;
import com.luck.video.lib.rxbus2.ThreadMode;
import com.luck.video.lib.tools.DateUtils;
import com.luck.video.lib.tools.DoubleUtils;
import com.luck.video.lib.tools.PictureFileUtils;
import com.luck.video.lib.tools.ScreenUtils;
import com.luck.video.lib.tools.StringUtils;
import com.luck.video.lib.tools.ToastManage;
import com.luck.video.lib.widget.FolderPopWindow;
import com.luck.video.lib.widget.PhotoPopupWindow;

import com.luck.video.lib.widget.commondialog.CommonDialog;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static com.luck.video.lib.widget.commondialog.CommonDialog.Blue_4d73f4;

/**
 * @author：luck
 * @data：2018/1/27 19:12
 * @描述: Media 选择页面
 */
public class PictureSelectorActivity extends PictureBaseActivity implements View.OnClickListener,
        PictureAlbumDirectoryAdapter.OnItemClickListener,
        PictureImageGridAdapter.OnPhotoSelectChangedListener, PhotoPopupWindow.OnItemClickListener {
    private final static String TAG = PictureSelectorActivity.class.getSimpleName();
    private static final int SHOW_DIALOG = 0;
    private static final int DISMISS_DIALOG = 1;
    private ImageView picture_left_back;
    private TextView picture_title, picture_right, picture_tv_ok, tv_empty,
            picture_tv_img_num, picture_id_preview, tv_PlayPause, tv_Stop, tv_Quit,
            tv_musicStatus, tv_musicTotal, tv_musicTime;
    private RelativeLayout rl_picture_title;
    private LinearLayout id_ll_ok;
    private RecyclerView picture_recycler;
    private PictureImageGridAdapter adapter;
    private List<LocalMedia> images = new ArrayList<>();
    private List<LocalMediaFolder> foldersList = new ArrayList<>();
    private FolderPopWindow folderWindow;
    private Animation animation = null;
    private boolean anim = false;
    private RxPermissions rxPermissions;
    private PhotoPopupWindow popupWindow;
    private LocalMediaLoader mediaLoader;
    private MediaPlayer mediaPlayer;
    private SeekBar musicSeekBar;
    private boolean isPlayAudio = false;
    private CustomDialog audioDialog;
    private int audioH;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_DIALOG:
                    showPleaseDialog();
                    break;
                case DISMISS_DIALOG:
                    dismissDialog();
                    break;
            }
        }
    };
    private LocalMedia media;
    private List<LocalMedia> images1;

    /**
     * EventBus 3.0 回调
     *
     * @param obj
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBus(EventEntity obj) {
        switch (obj.what) {
            case PictureConfig.UPDATE_FLAG:
                // 预览时勾选图片更新回调
                List<LocalMedia> selectImages = obj.medias;
                anim = selectImages.size() > 0 ? true : false;
                int position = obj.position;
                Log.i("刷新下标:", String.valueOf(position));
                adapter.bindSelectImages(selectImages);
                adapter.notifyItemChanged(position);

                break;
            case PictureConfig.PREVIEW_DATA_FLAG:
                List<LocalMedia> medias = obj.medias;
                if (medias.size() > 0) {
                    // 取出第1个判断是否是图片，视频和图片只能二选一，不必考虑图片和视频混合
                    String pictureType = medias.get(0).getPictureType();
                    if (config.isCompress && pictureType.startsWith(PictureConfig.IMAGE)) {
                        compressImage(medias);
                    } else {
                        onResult(medias);
                    }
                }
                break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!RxBus.getDefault().isRegistered(this)) {
            RxBus.getDefault().register(this);
        }

        rxPermissions = new RxPermissions(this);
        if (config.camera) {
            if (savedInstanceState == null) {
                rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .subscribe(new Observer<Boolean>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                            }

                            @Override
                            public void onNext(Boolean aBoolean) {
                                if (aBoolean) {
                                    onTakePhoto();
                                } else {
                                    ToastManage.s(mContext, getString(R.string.picture_camera));
                                    closeActivity();
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
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                    , WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.picture_empty);
        } else {
            setContentView(R.layout.picture_selector);
            initView(savedInstanceState);
        }
    }


    /**
     * init views
     */
    private void initView(Bundle savedInstanceState) {

        rl_picture_title = (RelativeLayout) findViewById(R.id.rl_picture_title);
        picture_left_back = (ImageView) findViewById(R.id.picture_left_back);
        picture_title = (TextView) findViewById(R.id.picture_title);
        picture_right = (TextView) findViewById(R.id.picture_right);
        picture_tv_ok = (TextView) findViewById(R.id.picture_tv_ok);
        picture_id_preview = (TextView) findViewById(R.id.picture_id_preview);
        picture_tv_img_num = (TextView) findViewById(R.id.picture_tv_img_num);
        picture_recycler = (RecyclerView) findViewById(R.id.picture_recycler);
        id_ll_ok = (LinearLayout) findViewById(R.id.id_ll_ok);
        tv_empty = (TextView) findViewById(R.id.tv_empty);
        isNumComplete(numComplete);
        if (config.mimeType == PictureMimeType.ofAll()) {
            popupWindow = new PhotoPopupWindow(this);
            popupWindow.setOnItemClickListener(this);
        }
        picture_id_preview.setOnClickListener(this);
        if (config.mimeType == PictureMimeType.ofAudio()) {
            picture_id_preview.setVisibility(View.GONE);
            audioH = ScreenUtils.getScreenHeight(mContext)
                    + ScreenUtils.getStatusBarHeight(mContext);
        } else {
            picture_id_preview.setVisibility(config.mimeType == PictureConfig.TYPE_VIDEO
                    ? View.GONE : View.VISIBLE);
        }
        picture_left_back.setOnClickListener(this);
        picture_right.setOnClickListener(this);
        id_ll_ok.setOnClickListener(this);
        picture_title.setOnClickListener(this);
        String title = config.mimeType == PictureMimeType.ofAudio() ?
                getString(R.string.picture_all_audio)
                : getString(R.string.picture_camera_roll);
        picture_title.setText(title);
        folderWindow = new FolderPopWindow(this, config.mimeType);
        folderWindow.setPictureTitleView(picture_title);
        folderWindow.setOnItemClickListener(this);
        picture_recycler.setHasFixedSize(true);
        picture_recycler.addItemDecoration(new GridSpacingItemDecoration(config.imageSpanCount,
                ScreenUtils.dip2px(this, 2), false));
        picture_recycler.setLayoutManager(new GridLayoutManager(this, config.imageSpanCount));
        // 解决调用 notifyItemChanged 闪烁问题,取消默认动画
        ((SimpleItemAnimator) picture_recycler.getItemAnimator())
                .setSupportsChangeAnimations(false);
        mediaLoader = new LocalMediaLoader(this, config.mimeType, config.isGif, config.videoMaxSecond, config.videoMinSecond);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            mHandler.sendEmptyMessage(SHOW_DIALOG);
                            readLocalMedia();
                        } else {
                            ToastManage.s(mContext, getString(R.string.picture_jurisdiction));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
        tv_empty.setText(config.mimeType == PictureMimeType.ofAudio() ?
                getString(R.string.picture_audio_empty)
                : getString(R.string.picture_empty));
        StringUtils.tempTextFont(tv_empty, config.mimeType);
        if (savedInstanceState != null) {
            // 防止拍照内存不足时activity被回收，导致拍照后的图片未选中
            selectionMedias = PictureSelector.obtainSelectorList(savedInstanceState);
        }
        adapter = new PictureImageGridAdapter(mContext, config);
        adapter.setOnPhotoSelectChangedListener(PictureSelectorActivity.this);
        adapter.bindSelectImages(selectionMedias);
        picture_recycler.setAdapter(adapter);
        String titleText = picture_title.getText().toString().trim();
        if (config.isCamera) {
            config.isCamera = StringUtils.isCamera(titleText);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (adapter != null) {
            List<LocalMedia> selectedImages = adapter.getSelectedImages();
            PictureSelector.saveSelectorList(outState, selectedImages);
        }
    }

    /**
     * none number style
     */
    private void isNumComplete(boolean numComplete) {
        picture_tv_ok.setText(numComplete ? getString(R.string.picture_done_front_num,
                0, config.selectionMode == PictureConfig.SINGLE ? 1 : config.maxSelectNum)
                : getString(R.string.picture_please_select));
        if (!numComplete) {
            animation = AnimationUtils.loadAnimation(this, R.anim.modal_in);
        }
        animation = numComplete ? null : AnimationUtils.loadAnimation(this, R.anim.modal_in);
    }

    /**
     * get LocalMedia s
     */
    private void readLocalMedia() {
        mediaLoader.loadAllMedia(new LocalMediaLoader.LocalMediaLoadListener() {
            @Override
            public void loadComplete(List<LocalMediaFolder> folders) {
                if (folders.size() > 0) {
                    foldersList = folders;
                    LocalMediaFolder folder = folders.get(0);
                    folder.setChecked(true);
                    List<LocalMedia> localImg = folder.getImages();
                    // 这里解决有些机型会出现拍照完，相册列表不及时刷新问题
                    // 因为onActivityResult里手动添加拍照后的照片，
                    // 如果查询出来的图片大于或等于当前adapter集合的图片则取更新后的，否则就取本地的
                    if (localImg.size() >= images.size()) {
                        images = localImg;
                        folderWindow.bindFolder(folders);
                    }
                }
                if (adapter != null) {
                    if (images == null) {
                        images = new ArrayList<>();
                    }
                    if (images1 == null) {
                        adapter.bindImagesData(images);
                    }
                    tv_empty.setVisibility(images.size() > 0
                            ? View.INVISIBLE : View.VISIBLE);
                }
                mHandler.sendEmptyMessage(DISMISS_DIALOG);
            }
        });
    }

    /**
     * open camera
     */
    public void startCamera() {
        // 防止快速点击，但是单独拍照不管
        if (!DoubleUtils.isFastDoubleClick() || config.camera) {
            switch (config.mimeType) {
                case PictureConfig.TYPE_ALL:
                    // 如果是全部类型下，单独拍照就默认图片 (因为单独拍照不会new此PopupWindow对象)
                    if (popupWindow != null) {
                        if (popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }
                        popupWindow.showAsDropDown(rl_picture_title);
                    } else {
                        startOpenCamera();
                    }
                    break;
                case PictureConfig.TYPE_IMAGE:
                    // 拍照
                    startOpenCamera();
                    break;
                case PictureConfig.TYPE_VIDEO:
                    // 录视频
                    startOpenCameraVideo();
                    break;
                case PictureConfig.TYPE_AUDIO:
                    // 录音
                    startOpenCameraAudio();
                    break;
            }
        }
    }

    /**
     * start to camera、preview、crop
     */
    public void startOpenCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            int type = config.mimeType == PictureConfig.TYPE_ALL ? PictureConfig.TYPE_IMAGE : config.mimeType;
            File cameraFile = PictureFileUtils.createCameraFile(this,
                    type,
                    outputCameraPath, config.suffixType);
            cameraPath = cameraFile.getAbsolutePath();
            Uri imageUri = parUri(cameraFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(cameraIntent, PictureConfig.REQUEST_CAMERA);
        }
    }

    /**
     * start to camera、video
     */
    public void startOpenCameraVideo() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File cameraFile = PictureFileUtils.createCameraFile(this, config.mimeType ==
                            PictureConfig.TYPE_ALL ? PictureConfig.TYPE_VIDEO : config.mimeType,
                    outputCameraPath, config.suffixType);
            cameraPath = cameraFile.getAbsolutePath();
            Uri imageUri = parUri(cameraFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, config.recordVideoSecond);
            cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, config.videoQuality);
            startActivityForResult(cameraIntent, PictureConfig.REQUEST_CAMERA);
        }
    }

    /**
     * start to camera audio
     */
    public void startOpenCameraAudio() {
        rxPermissions.request(Manifest.permission.RECORD_AUDIO).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    Intent cameraIntent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                    if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(cameraIntent, PictureConfig.REQUEST_CAMERA);
                    }
                } else {
                    ToastManage.s(mContext, getString(R.string.picture_audio));
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

    /**
     * 生成uri
     *
     * @param cameraFile
     * @return
     */
    private Uri parUri(File cameraFile) {
        Uri imageUri;
        String authority = getPackageName() + ".provider";
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            //通过FileProvider创建一个content类型的Uri
            imageUri = FileProvider.getUriForFile(mContext, authority, cameraFile);
        } else {
            imageUri = Uri.fromFile(cameraFile);
        }
        return imageUri;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.picture_left_back || id == R.id.picture_right) {
            if (folderWindow.isShowing()) {
                folderWindow.dismiss();
            } else {
                closeActivity();
            }
        }
        if (id == R.id.picture_title) {
            if (folderWindow.isShowing()) {
                folderWindow.dismiss();
            } else {
                if (images != null && images.size() > 0) {
                    folderWindow.showAsDropDown(rl_picture_title);
                    List<LocalMedia> selectedImages = adapter.getSelectedImages();
                    folderWindow.notifyDataCheckedStatus(selectedImages);
                }
            }
        }

        if (id == R.id.picture_id_preview) {
            List<LocalMedia> selectedImages = adapter.getSelectedImages();

            List<LocalMedia> medias = new ArrayList<>();
            for (LocalMedia media : selectedImages) {
                medias.add(media);
            }
            Bundle bundle = new Bundle();
            bundle.putSerializable(PictureConfig.EXTRA_PREVIEW_SELECT_LIST, (Serializable) medias);
            bundle.putSerializable(PictureConfig.EXTRA_SELECT_LIST, (Serializable) selectedImages);
            bundle.putBoolean(PictureConfig.EXTRA_BOTTOM_PREVIEW, true);
           /* startActivity(PicturePreviewActivity.class, bundle,
                    config.selectionMode == PictureConfig.SINGLE ? UCrop.REQUEST_CROP : UCropMulti.REQUEST_MULTI_CROP);
            overridePendingTransition(R.anim.a5, 0);*/
        }

        if (id == R.id.id_ll_ok) {
            List<LocalMedia> images = adapter.getSelectedImages();
            LocalMedia image = images.size() > 0 ? images.get(0) : null;
            String pictureType = image != null ? image.getPictureType() : "";
            // 如果设置了图片最小选择数量，则判断是否满足条件
            int size = images.size();
            boolean eqImg = pictureType.startsWith(PictureConfig.IMAGE);
            if (config.minSelectNum > 0 && config.selectionMode == PictureConfig.MULTIPLE) {
                if (size < config.minSelectNum) {
                    /*String str = eqImg ? getString(R.string.picture_min_img_num, config.minSelectNum)
                            : getString(R.string.picture_min_video_num, config.minSelectNum);
                    ToastManage.s(mContext, str);*/
                    return;
                }
            }
            if (config.enableCrop && eqImg) {
                if (config.selectionMode == PictureConfig.SINGLE) {
                    originalPath = image.getPath();
                } else {
                    // 是图片和选择压缩并且是多张，调用批量压缩
                    ArrayList<String> medias = new ArrayList<>();
                    for (LocalMedia media : images) {
                        medias.add(media.getPath());
                    }
                }
            } else if (config.isCompress && eqImg) {
                // 图片才压缩，视频不管
                compressImage(images);
            } else {
                onResult(images);
            }
        }
    }

    /**
     * 播放音频
     *
     * @param path
     */
    private void audioDialog(final String path) {
        audioDialog = new CustomDialog(mContext,
                LinearLayout.LayoutParams.MATCH_PARENT, audioH
                ,
                R.layout.picture_audio_dialog, R.style.Theme_dialog);
        audioDialog.getWindow().setWindowAnimations(R.style.Dialog_Audio_StyleAnim);
        tv_musicStatus = (TextView) audioDialog.findViewById(R.id.tv_musicStatus);
        tv_musicTime = (TextView) audioDialog.findViewById(R.id.tv_musicTime);
        musicSeekBar = (SeekBar) audioDialog.findViewById(R.id.musicSeekBar);
        tv_musicTotal = (TextView) audioDialog.findViewById(R.id.tv_musicTotal);
        tv_PlayPause = (TextView) audioDialog.findViewById(R.id.tv_PlayPause);
        tv_Stop = (TextView) audioDialog.findViewById(R.id.tv_Stop);
        tv_Quit = (TextView) audioDialog.findViewById(R.id.tv_Quit);

        musicSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser == true) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        handler.post(runnable);
        audioDialog.show();
    }

    //  通过 Handler 更新 UI 上的组件状态
    public Handler handler = new Handler();
    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (mediaPlayer != null) {
                    tv_musicTime.setText(DateUtils.timeParse(mediaPlayer.getCurrentPosition()));
                    musicSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                    musicSeekBar.setMax(mediaPlayer.getDuration());
                    tv_musicTotal.setText(DateUtils.timeParse(mediaPlayer.getDuration()));
                    handler.postDelayed(runnable, 200);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    public void onItemClick(String folderName, List<LocalMedia> images) {
        boolean camera = StringUtils.isCamera(folderName);
        camera = config.isCamera ? camera : false;
        adapter.setShowCamera(camera);
        picture_title.setText(folderName);
        images1 = images;
        adapter.bindImagesData(images1);
        folderWindow.dismiss();

    }

    @Override
    public void onTakePhoto() {
        // 启动相机拍照,先判断手机是否有拍照权限
        rxPermissions.request(Manifest.permission.CAMERA).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    startCamera();
                } else {
                    ToastManage.s(mContext, getString(R.string.picture_camera));
                    if (config.camera) {
                        closeActivity();
                    }
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

    @Override
    public void onChange(List<LocalMedia> selectImages) {
        changeImageNumber(selectImages);
    }

    @Override
    public void onPictureClick(LocalMedia media, int position) {
        if (media.getDuration() / 1000 >
                config.recordVideoSecond) {
            showErrorDialog("所选视频超过最大时长");
            return;
        }else if(media.getSize()>config.videoMaxSize *1024*1024L){
            showErrorDialog("所选视频超过最大限制");
            return;
        }else if (media==null||media.getDuration()==0){
            showErrorDialog("视频文件已损坏，请重新拍摄");
            adapter.getImages().remove(media);
            adapter.notifyItemChanged(position);
            return;
        }
        List<LocalMedia> images = adapter.getImages();
        startPreview(images, position);
    }

    public void showErrorDialog( String content) {
        final CommonDialog commonDialog = new CommonDialog(this);
        commonDialog.setContent(content).setButton1("确定", Blue_4d73f4).show();

        commonDialog.setOnButtonClickListener(new CommonDialog.OnButtonClickListener() {
            @Override
            public void button1Click() {
                commonDialog.dismiss();
            }


        });
    }

    /**
     * preview image and video
     *
     * @param previewImages
     * @param position
     */
    public void startPreview(List<LocalMedia> previewImages, int position) {
        media = previewImages.get(position);
        String pictureType = media.getPictureType();
        Bundle bundle = new Bundle();
        List<LocalMedia> result = new ArrayList<>();
        int mediaType = PictureMimeType.isPictureType(pictureType);
        switch (mediaType) {
            case PictureConfig.TYPE_IMAGE:
                // image
                List<LocalMedia> selectedImages = adapter.getSelectedImages();
                ImagesObservable.getInstance().saveLocalMedia(previewImages);
                bundle.putSerializable(PictureConfig.EXTRA_SELECT_LIST, (Serializable) selectedImages);
                bundle.putInt(PictureConfig.EXTRA_POSITION, position);
               /* startActivity(PicturePreviewActivity.class, bundle,
                        config.selectionMode == PictureConfig.SINGLE ? UCrop.REQUEST_CROP : UCropMulti.REQUEST_MULTI_CROP);
                overridePendingTransition(R.anim.a5, 0);*/
                break;
            case PictureConfig.TYPE_VIDEO:
               /* // video
                if (config.selectionMode == PictureConfig.SINGLE) {*/
                result.add(media);
//                    onResult(result);
                bundle.putString("video_path", media.getPath());
                bundle.putParcelable("LocalMedia", media);
                Intent intent = new Intent(this, PictureVideoPlayActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, PictureConfig.REQUEST_DISPLAY);
               /* } else {
                    result.add(media);
                    bundle.putString("video_path", media.getPath());
                    startActivity(PictureVideoPlayActivity.class, bundle);
                }*/
                break;
            case PictureConfig.TYPE_AUDIO:
                // audio
                if (config.selectionMode == PictureConfig.SINGLE) {
                    result.add(media);
                    onResult(result);
                } else {
                    audioDialog(media.getPath());
                }
                break;
        }
    }


    /**
     * change image selector state
     *
     * @param selectImages
     */
    public void changeImageNumber(List<LocalMedia> selectImages) {
        // 如果选择的视频没有预览功能
        if (selectImages.size()>0&&(selectImages.get(0)==null||selectImages.get(0).getDuration()==0)){
            showErrorDialog("视频文件已损坏，请重新拍摄");
            adapter.getImages().remove(media);
            adapter.notifyDataSetChanged();
            return;
        }
        String pictureType = selectImages.size() > 0
                ? selectImages.get(0).getPictureType() : "";
        if (config.mimeType == PictureMimeType.ofAudio()) {
            picture_id_preview.setVisibility(View.GONE);
        } else {
            boolean isVideo = PictureMimeType.isVideo(pictureType);
            boolean eqVideo = config.mimeType == PictureConfig.TYPE_VIDEO;
            picture_id_preview.setVisibility(isVideo || eqVideo ? View.GONE : View.VISIBLE);
        }
        boolean enable = selectImages.size() != 0;
        if (enable) {
            id_ll_ok.setEnabled(true);
            picture_id_preview.setEnabled(true);
            picture_id_preview.setSelected(true);
            picture_tv_ok.setSelected(true);
            if (numComplete) {
                picture_tv_ok.setText(getString
                        (R.string.picture_done_front_num, selectImages.size(),
                                config.selectionMode == PictureConfig.SINGLE ? 1 : config.maxSelectNum));
            } else {
                if (!anim) {
                    picture_tv_img_num.startAnimation(animation);
                }
                picture_tv_img_num.setVisibility(View.VISIBLE);
                picture_tv_img_num.setText(String.valueOf(selectImages.size()));
                picture_tv_ok.setText(getString(R.string.picture_completed));
                anim = false;
            }
        } else {
            id_ll_ok.setEnabled(false);
            picture_id_preview.setEnabled(false);
            picture_id_preview.setSelected(false);
            picture_tv_ok.setSelected(false);
            if (numComplete) {
                picture_tv_ok.setText(getString(R.string.picture_done_front_num, 0,
                        config.selectionMode == PictureConfig.SINGLE ? 1 : config.maxSelectNum));
            } else {
                picture_tv_img_num.setVisibility(View.INVISIBLE);
                picture_tv_ok.setText(getString(R.string.picture_please_select));
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            List<LocalMedia> medias = new ArrayList<>();
            LocalMedia media;
            String imageType;
            switch (requestCode) {
                case PictureConfig.REQUEST_CAMERA:
                    if (config.mimeType == PictureMimeType.ofAudio()) {
                        cameraPath = getAudioPath(data);
                    }
                    // on take photo success
                    final File file = new File(cameraPath);
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                    String toType = PictureMimeType.fileToType(file);
                    if (config.mimeType != PictureMimeType.ofAudio()) {
                        int degree = PictureFileUtils.readPictureDegree(file.getAbsolutePath());
                        rotateImage(degree, file);
                    }
                    // 生成新拍照片或视频对象
                    media = new LocalMedia();
                    media.setPath(cameraPath);

                    boolean eqVideo = toType.startsWith(PictureConfig.VIDEO);
                    int duration = eqVideo ? PictureMimeType.getLocalVideoDuration(cameraPath) : 0;
                    String pictureType = "";
                    if (config.mimeType == PictureMimeType.ofAudio()) {
                        pictureType = "audio/mpeg";
                        duration = PictureMimeType.getLocalVideoDuration(cameraPath);
                    } else {
                        pictureType = eqVideo ? PictureMimeType.createVideoType(cameraPath)
                                : PictureMimeType.createImageType(cameraPath);
                    }
                    media.setPictureType(pictureType);
                    media.setDuration(duration);
                    media.setMimeType(config.mimeType);

                    // 因为加入了单独拍照功能，所有如果是单独拍照的话也默认为单选状态
                    if (config.camera) {
                        // 如果是单选 拍照后直接返回
                        boolean eqImg = toType.startsWith(PictureConfig.IMAGE);
                        if (config.enableCrop && eqImg) {
                            // 去裁剪
                            originalPath = cameraPath;
//                            startCrop(cameraPath);
                        } else if (config.isCompress && eqImg) {
                            // 去压缩
                            medias.add(media);
                            compressImage(medias);
                            if (adapter != null) {
                                images.add(0, media);
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            // 不裁剪 不压缩 直接返回结果
                            medias.add(media);
                            onResult(medias);
                        }
                    } else {
                        // 多选 返回列表并选中当前拍照的
                        images.add(0, media);
                        if (adapter != null) {
                            List<LocalMedia> selectedImages = adapter.getSelectedImages();
                            // 没有到最大选择量 才做默认选中刚拍好的
                            if (selectedImages.size() < config.maxSelectNum) {
                                pictureType = selectedImages.size() > 0 ? selectedImages.get(0).getPictureType() : "";
                                boolean toEqual = PictureMimeType.mimeToEqual(pictureType, media.getPictureType());
                                // 类型相同或还没有选中才加进选中集合中
                                if (toEqual || selectedImages.size() == 0) {
                                    if (selectedImages.size() < config.maxSelectNum) {
                                        // 如果是单选，则清空已选中的并刷新列表(作单一选择)
                                        if (config.selectionMode == PictureConfig.SINGLE) {
                                            singleRadioMediaImage();
                                        }
                                        selectedImages.add(media);
                                        adapter.bindSelectImages(selectedImages);
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                    if (adapter != null) {
                        // 解决部分手机拍照完Intent.ACTION_MEDIA_SCANNER_SCAN_FILE
                        // 不及时刷新问题手动添加
                        manualSaveFolder(media);
                        tv_empty.setVisibility(images.size() > 0
                                ? View.INVISIBLE : View.VISIBLE);
                    }

                    if (config.mimeType != PictureMimeType.ofAudio()) {
                        int lastImageId = getLastImageId(eqVideo);
                        if (lastImageId != -1) {
                            removeImage(lastImageId, eqVideo);
                        }
                    }
                    break;

                case PictureConfig.REQUEST_DISPLAY:
                    List<LocalMedia> result = new ArrayList<>();
                    result.add(PictureSelectorActivity.this.media);
                    onResult(result);

                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            if (config.camera) {
                closeActivity();
            }
        } /*else if (resultCode == UCrop.RESULT_ERROR) {
            Throwable throwable = (Throwable) data.getSerializableExtra(UCrop.EXTRA_ERROR);
            ToastManage.s(mContext, throwable.getMessage());
        }*/
    }


    /**
     * 单选图片
     */
    private void singleRadioMediaImage() {
        if (adapter != null) {
            List<LocalMedia> selectImages = adapter.getSelectedImages();
            if (selectImages != null
                    && selectImages.size() > 0) {
                selectImages.clear();
            }
        }
    }


    /**
     * 手动添加拍照后的相片到图片列表，并设为选中
     *
     * @param media
     */
    private void manualSaveFolder(LocalMedia media) {
        try {
            createNewFolder(foldersList);
            LocalMediaFolder folder = getImageFolder(media.getPath(), foldersList);
            LocalMediaFolder cameraFolder = foldersList.size() > 0 ? foldersList.get(0) : null;
            if (cameraFolder != null && folder != null) {
                // 相机胶卷
                cameraFolder.setFirstImagePath(media.getPath());
                cameraFolder.setImages(images);
                cameraFolder.setImageNum(cameraFolder.getImageNum() + 1);
                // 拍照相册
                int num = folder.getImageNum() + 1;
                folder.setImageNum(num);
                folder.getImages().add(0, media);
                folder.setFirstImagePath(cameraPath);
                folderWindow.bindFolder(foldersList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeActivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (RxBus.getDefault().isRegistered(this)) {
            RxBus.getDefault().unregister(this);
        }
        ImagesObservable.getInstance().clearLocalMedia();
        if (animation != null) {
            animation.cancel();
            animation = null;
        }
        if (mediaPlayer != null && handler != null) {
            handler.removeCallbacks(runnable);
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onItemClick(int position) {
        switch (position) {
            case 0:
                // 拍照
                startOpenCamera();
                break;
            case 1:
                // 录视频
                startOpenCameraVideo();
                break;
        }
    }


}
