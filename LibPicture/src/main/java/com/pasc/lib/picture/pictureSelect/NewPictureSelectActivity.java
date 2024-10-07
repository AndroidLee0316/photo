package com.pasc.lib.picture.pictureSelect;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.pasc.lib.picture.R;
import com.pasc.lib.picture.pictureSelect.activity.AlbumsActivity;
import com.pasc.lib.picture.pictureSelect.adapter.PreviewImageAdapter;
import com.pasc.lib.picture.takephoto.app.TakePhoto;
import com.pasc.lib.picture.takephoto.app.TakePhotoFragmentActivity;
import com.pasc.lib.picture.takephoto.compress.CompressConfig;
import com.pasc.lib.picture.takephoto.model.CropOptions;
import com.pasc.lib.picture.takephoto.model.LubanOptions;
import com.pasc.lib.picture.takephoto.model.TResult;
import com.pasc.lib.picture.takephoto.model.TakePhotoOptions;
import com.pasc.lib.picture.takephoto.uitl.TConstant;
import com.pasc.lib.picture.util.AndroidPUtil;
import com.pasc.lib.picture.util.BitmapUtils;
import com.pasc.lib.picture.util.DensityUtils;
import com.pasc.lib.picture.util.FileUtils;
import com.pasc.lib.picture.util.StatusBarUtils;
import com.pasc.lib.widget.dialognt.CommonDialog;

import org.reactivestreams.Publisher;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

import static com.pasc.lib.widget.dialognt.CommonDialog.Blue_4d73f4;


public class NewPictureSelectActivity extends TakePhotoFragmentActivity implements View.OnClickListener {


    public static final int REQUEST_CODE_PICTURE_SELECT = 100;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private TextView tvSelect;
    private RecyclerView recyclerView;
    private TextView tvBrowse;
    private TextView tvTitleName;
    private TextView tvTitleRight;
    private TextView tvPicturePosition;
    private View ll_title;
    private ViewPager viewpager;
    private View viewLine;

    private static final int deafultNum = 1;
    private int canSelect = deafultNum;
    private boolean isCompress;
    private boolean isPreView=false;

    private ArrayList<LocalPicture> allLocalPictures = new ArrayList<>();
    private ArrayList<LocalPicture> preViewPictures = new ArrayList<>();
    private ArrayList<LocalPicture> selectPictures = new ArrayList<>();
    private ArrayList<LocalPicture> preImagePictures = new ArrayList<>();
    private LocalPictureAdapter adapter;
    private BrowsePicturesAdapter browseAdapter;
    private PreviewImageAdapter previewImageAdapter;
    private ImageView ivBack;
    private TextView tvAlumn;
    private RelativeLayout rlBottom;
    private static boolean isHeadImg;
    private RecyclerView rvPreview;
    private int mPosition;

    public static void setIsHeadImg(boolean isheadImg) {
        isHeadImg = isheadImg;
    }


    public static void actionStart(Activity context, int requestCode, int canSelect) {
        Intent intent = new Intent(context, NewPictureSelectActivity.class);
        intent.putExtra(TConstant.INTENT_EXTRA_LIMIT, canSelect);
        context.startActivityForResult(intent, requestCode);
    }

    public static void actionStart(Activity context, int requestCode, int canSelect, boolean compress) {
        Intent intent = new Intent(context, NewPictureSelectActivity.class);
        intent.putExtra(TConstant.INTENT_EXTRA_LIMIT, canSelect);
        intent.putExtra(TConstant.INTENT_EXTRA_COMPRESS, compress);
        context.startActivityForResult(intent, requestCode);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.setStatusBarLightMode(this, true, true);
        setContentView(R.layout.picture_activity_new_picture_select);
        tvSelect = findViewById(R.id.tv_select);
        recyclerView = findViewById(R.id.recycler_view);
        tvBrowse = findViewById(R.id.tv_browse);
        tvTitleName = findViewById(R.id.tv_title_name);
        tvTitleRight = findViewById(R.id.tv_title_right);
        tvPicturePosition = findViewById(R.id.tv_picture_position);
        ll_title = findViewById(R.id.ll_title);
        viewpager = findViewById(R.id.viewpager);
        ivBack = findViewById(R.id.iv_title_left);
        tvAlumn = findViewById(R.id.tv_title_left);
        rlBottom = findViewById(R.id.rl_bottom);
        rvPreview = findViewById(R.id.rv_preview);
        viewLine = findViewById(R.id.view_line);

        rlBottom.setVisibility(View.GONE);
        initEvent();
        Intent in = getIntent();
        if (in != null) {
            canSelect = in.getIntExtra(TConstant.INTENT_EXTRA_LIMIT, deafultNum);
            isCompress = in.getBooleanExtra(TConstant.INTENT_EXTRA_COMPRESS, false);
            rlBottom.setVisibility(isHeadImg ? View.GONE : View.VISIBLE);
        }
        if (ImagePicker.isEnable()) {
            canSelect = ImagePicker.getSelectLimit();
        }

        initView();
        initData();

        if (Build.VERSION.SDK_INT >= 28) {
            ll_title.post(new Runnable() {
                @Override
                public void run() {
                    int paddingTop = AndroidPUtil.getPaddingTop(NewPictureSelectActivity.this, DensityUtils.dip2px(NewPictureSelectActivity.this, 20));
                    ll_title.setPadding(0, paddingTop, 0, 0);
                }
            });

        }
    }

    private void initView() {
        int spanCount = 4;
        recyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));
        adapter = new LocalPictureAdapter(this, spanCount, allLocalPictures, canSelect, selectPictures);
        recyclerView.setAdapter(adapter);

        rvPreview.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        previewImageAdapter=new PreviewImageAdapter(preImagePictures);
        rvPreview.setAdapter(previewImageAdapter);
        previewImageAdapter.setOnItemChildClickListener(
            new BaseQuickAdapter.OnItemChildClickListener() {
                @Override
                public void onItemChildClick(BaseQuickAdapter baseadapter, View view, int position) {
                    LocalPicture localPicture = preImagePictures.get(position);
                    int pos = allLocalPictures.indexOf(localPicture);

                    if (view.getId()==R.id.img_remove){
                        itemSelectorNot(pos);
                        viewpager.setCurrentItem(pos);
                    }else if(view.getId()==R.id.img_opinion){
                        if (isPreView){
                            viewpager.setCurrentItem(position);
                            previewImageAdapter.setSelectedItem(position);
                        }else{
                            viewpager.setCurrentItem(pos);
                            previewImageAdapter.setSelectedItem(pos);
                        }
                    }

                }
            });

        browseAdapter = new BrowsePicturesAdapter(this, preViewPictures);
        viewpager.setAdapter(browseAdapter);
        adapter.setItemClick(new LocalPictureAdapter.ItemClick() {
            @Override
            public void click(View view, int position) {
                int i = view.getId();
                mPosition =position;
                if (isHeadImg) {
                    //StatusBarUtils.setStatusBarLightMode(NewPictureSelectActivity.this, true, false);
                    //ll_title.setBackgroundColor(Color.parseColor("#bf000000"));
                    itemSelectorNot(position);
                    tvTitleRight.setVisibility(View.GONE);
                    tvTitleName.setVisibility(View.INVISIBLE);
                    ivBack.setVisibility(View.VISIBLE);
                    tvAlumn.setVisibility(View.GONE);
                    tvPicturePosition.setSelected(allLocalPictures.get(position).isSelect());
                    tvPicturePosition.setText(getSelectPosition(allLocalPictures.get(position)));

                    setResultData();
                } else {
                    if (i == R.id.fl_icon) {
                        itemSelectorNot(position);

                    } else if (i == R.id.img_local) {
                        //StatusBarUtils.setStatusBarLightMode(NewPictureSelectActivity.this, true, false);
                        //ll_title.setBackgroundColor(Color.parseColor("#bf000000"));
                        if (isPreView){
                            showPreview();
                        }else{
                            showPreview(position);
                        }

                    }
                }

            }


        });
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (isPreView){
                    tvPicturePosition.setSelected(preViewPictures.get(position).isSelect());
                    tvPicturePosition.setText(getSelectPosition(preViewPictures.get(position)));
                }else{
                    tvPicturePosition.setSelected(allLocalPictures.get(position).isSelect());
                    tvPicturePosition.setText(getSelectPosition(allLocalPictures.get(position)));
                }
                previewImageAdapter.setSelectedItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tvBrowse.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if(viewpager.getVisibility()==View.GONE){
                   showPreview();
                }else{
                    clickBack();
                }

            }
        });
    }

    /**
     * 点击 预览 模式
     */
    private void showPreview() {
        isPreView=true;
        tvTitleRight.setVisibility(View.GONE);
        tvTitleName.setVisibility(View.INVISIBLE);
        tvPicturePosition.setVisibility(View.VISIBLE);
        viewpager.setVisibility(View.VISIBLE);
        tvBrowse.setVisibility(View.GONE);
        rvPreview.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.VISIBLE);
        tvAlumn.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        preViewPictures.clear();
        preViewPictures.addAll(selectPictures);
        browseAdapter.notifyDataSetChanged();

        preImagePictures.clear();
        preImagePictures.addAll(selectPictures);
        previewImageAdapter.notifyDataSetChanged();

        tvPicturePosition.setSelected(selectPictures.get(0).isSelect());
        tvPicturePosition.setText(
            getSelectPosition(selectPictures.get(0)));
        viewpager.setCurrentItem(0);
        ll_title.setBackgroundColor(getResources().getColor(R.color.photo_mask_gray));
        viewLine.setVisibility(View.INVISIBLE);
        rlBottom.setVisibility(View.VISIBLE);
        rlBottom.setBackgroundColor(getResources().getColor(R.color.gray_333333));

    }

    /**
     * 显示预览
     * @param position
     */
    private void showPreview(int position) {
        tvTitleRight.setVisibility(View.GONE);
        tvTitleName.setVisibility(View.INVISIBLE);
        tvPicturePosition.setVisibility(View.VISIBLE);
        viewpager.setVisibility(View.VISIBLE);
        tvBrowse.setVisibility(View.GONE);
        rvPreview.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.VISIBLE);
        tvAlumn.setVisibility(View.GONE);
        tvPicturePosition.setSelected(allLocalPictures.get(position).isSelect());
        tvPicturePosition.setText(
            getSelectPosition(allLocalPictures.get(position)));
        viewpager.setCurrentItem(position);
        browseAdapter.notifyDataSetChanged();
        ll_title.setBackgroundColor(getResources().getColor(R.color.photo_mask_gray));
        rlBottom.setVisibility(View.VISIBLE);
        rlBottom.setBackgroundColor(getResources().getColor(R.color.gray_333333));
        viewLine.setVisibility(View.INVISIBLE);
    }
    public void showErrorDialog() {
        final CommonDialog commonDialog = new CommonDialog(this);
        commonDialog.setContent("最多只能选择" + canSelect + "张照片").setButton1("我知道了", Blue_4d73f4).show();

        commonDialog.setOnButtonClickListener(new CommonDialog.OnButtonClickListener() {
            @Override
            public void button1Click() {
                commonDialog.dismiss();
            }


        });
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    private void initData() {
        setSelectedSize(0);
        ArrayList<File> files = new ArrayList<>();
        files.add(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                .getAbsolutePath()));
        files.add(new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        .getAbsolutePath()));
        files.add(new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        .getAbsolutePath()));
        files.add(new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/tencent/MicroMsg/WeiXin/"));
        files.add(new File(
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/news_aticle"));
        files.add(new File(
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/sina/weibo/"));
        Disposable disposable = Flowable.fromArray(files)
                .flatMap(new Function<ArrayList<File>, Publisher<File>>() {
                    @Override
                    public Publisher<File> apply(ArrayList<File> files) throws Exception {
                        return Flowable.fromIterable(files);
                    }
                })
                .flatMap(new Function<File, Publisher<File>>() {
                    @Override
                    public Publisher<File> apply(File file) throws Exception {
                        return listFiles(file);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) throws Exception {
                        allLocalPictures.add(new LocalPicture(file.getPath()));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        adapter.notifyDataChanged(selectPictures.size(), selectPictures);
                        previewImageAdapter.notifyDataSetChanged();

                        preViewPictures.clear();
                        preViewPictures.addAll(allLocalPictures);
                        browseAdapter.notifyDataSetChanged();
                    }
                });
        compositeDisposable.add(disposable);
    }

    private Publisher<File> listFiles(final File f) {
        if (f.isDirectory()) {
            return Flowable.fromArray(f.listFiles()).flatMap(new Function<File, Publisher<File>>() {
                @Override
                public Publisher<File> apply(File file) {
                    return listFiles(file);
                }
            });
        } else {
            return Flowable.just(f).filter(new Predicate<File>() {
                @Override
                public boolean test(File file) {
                    return file.getName().endsWith(".jpeg")
                            || file.getName().endsWith(".JPEG")
                            || file.getName().endsWith(".jpg")
                            || file.getName().endsWith(".JPG")
                            || file.getName().endsWith(".png")
                            || file.getName().endsWith(".PNG");
                }
            });
        }
    }

    private void initEvent() {
        ivBack.setOnClickListener(this);
        tvAlumn.setOnClickListener(this);
        findViewById(R.id.tv_title_right).setOnClickListener(this);
        findViewById(R.id.tv_picture_position).setOnClickListener(this);
        tvSelect.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.iv_title_left) {
            clickBack();

        } else if (i == R.id.tv_title_right) {
            selectPictures.clear();
            finish();

        } else if (i == R.id.tv_select) {
            setResultData();

        } else if (i == R.id.tv_picture_position) {
            itemSelectorNot(viewpager.getCurrentItem());

        } else if (i == R.id.tv_title_left) {
            chooseFromGallery();
        }
    }

    private void itemSelectorNot(int position) {
        if (isPreView){
            LocalPicture localPicture = preViewPictures.get(position);
            if (localPicture.isSelect()) {
                //取消勾选
                localPicture.setSelect(false);
                tvPicturePosition.setSelected(false);
                selectPictures.remove(localPicture);
            } else { //勾选
                localPicture.setSelect(true);
                tvPicturePosition.setSelected(true);
                selectPictures.add(localPicture);
            }
            tvPicturePosition.setText(getSelectPosition(preViewPictures.get(position)));
            adapter.notifyDataChanged(selectPictures.size(), selectPictures);
            browseAdapter.notifyDataSetChanged();
            previewImageAdapter.notifyDataSetChanged();
            setSelectedSize(selectPictures.size());

        }else{
            LocalPicture localPicture = allLocalPictures.get(position);
            if (selectPictures.size() >= canSelect && !selectPictures.contains(localPicture)) {
                showErrorDialog();
                return;
            }
            if (localPicture.isSelect()) {
                //取消勾选
                localPicture.setSelect(false);
                tvPicturePosition.setSelected(false);
                selectPictures.remove(localPicture);
            } else { //勾选
                localPicture.setSelect(true);
                tvPicturePosition.setSelected(true);
                selectPictures.add(localPicture);

            }


            tvPicturePosition.setText(getSelectPosition(allLocalPictures.get(position)));
            adapter.notifyDataChanged(selectPictures.size(), selectPictures);
            browseAdapter.notifyDataSetChanged();

            preImagePictures.clear();
            preImagePictures.addAll(selectPictures);
            previewImageAdapter.notifyDataSetChanged();

            setSelectedSize(selectPictures.size());
        }

    }

    private void setSelectedSize(int position) {
        /*String text = String.format("已选择(%s/" + canSelect + ")", position);
        int len = 4 + ("" + position).length();
        SpannableString msp = new SpannableString(text);
        int color = Color.parseColor("#4d73f4");
        msp.setSpan(new ForegroundColorSpan(color), 4, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvBrowse.setText(msp);*/
        boolean hasSelected = position > 0;//是否有选中任何图片
        tvBrowse.setAlpha(hasSelected?1.0f:0.3f);
        tvSelect.setAlpha(hasSelected?1.0f:0.3f);
        tvSelect.setEnabled(hasSelected);
        tvBrowse.setEnabled(hasSelected);
        String select = hasSelected ?String.format("确定(%s" +")", position):"确定";
        tvSelect.setText(select);
    }

    private void clickBack() {
        isPreView=false;
        ll_title.setBackgroundColor(Color.WHITE);
        tvTitleRight.setVisibility(View.VISIBLE);
        tvTitleName.setVisibility(View.VISIBLE);
        tvPicturePosition.setVisibility(View.GONE);
        viewpager.setVisibility(View.GONE);
        rvPreview.setVisibility(View.GONE);
        tvBrowse.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.GONE);
        tvAlumn.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        viewLine.setVisibility(View.VISIBLE);
        rlBottom.setBackgroundColor(getResources().getColor(R.color.white));
        preViewPictures.clear();
        preViewPictures.addAll(allLocalPictures);
        browseAdapter.notifyDataSetChanged();

    }

    private String getSelectPosition(LocalPicture picture) {

        for (int i = 0; i < selectPictures.size(); i++) {
            LocalPicture localPicture = selectPictures.get(i);
            if (localPicture.equals(picture)) {
                return String.valueOf(i + 1);
            }
        }
        return "";
    }

    @Override
    public void onBackPressed() {
        if (viewpager.getVisibility() == View.GONE) {
            super.onBackPressed();
        } else {
            clickBack();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    //从相册选择
    private void chooseFromGallery() {
        TakePhoto cameraTakePhoto = getTakePhoto();
        configCompress(cameraTakePhoto);
        configTakePhotoOption(cameraTakePhoto);

//            getTakePhoto().onPickFromGallery();
        AlbumsActivity.actionStart(this, REQUEST_CODE_PICTURE_SELECT, canSelect,isHeadImg);

    }


    private void configTakePhotoOption(TakePhoto takePhoto) {
        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        //是否使用TakePhoto自带相册
        builder.setWithOwnGallery(false);
        //纠正拍照的照片旋转角度
        builder.setCorrectImage(true);
        takePhoto.setTakePhotoOptions(builder.create());

    }

    private void configCompress(TakePhoto takePhoto) {
       /* if (!ImagePicker.getInstance().isEnableCompress()) {
            takePhoto.onEnableCompress(null, false);
            return;
        }*/
        int maxSize = 102400; // 最大 100kb
        int width = 800;
        int height = 800;
        /***是否显示进度**/
        boolean showProgressBar = true;
        /***压缩后是否保留原图***/
        boolean enableRawFile = true;
        CompressConfig config;
        if (true /****android 原生压缩***/) {
            config = new CompressConfig.Builder().setMaxSize(maxSize)
                    .setMaxPixel(width >= height ? width : height)
                    .enableReserveRaw(enableRawFile)
                    .create();
        } else {
            LubanOptions option = new LubanOptions.Builder().setMaxHeight(height).setMaxWidth(width).setMaxSize(maxSize).create();
            config = CompressConfig.ofLuban(option);
            config.enableReserveRaw(enableRawFile);
        }
        takePhoto.onEnableCompress(config, showProgressBar);

    }

    //裁剪图片属性
    private CropOptions getCropOptions() {
        CropOptions.Builder builder = new CropOptions.Builder();
        builder.setAspectX(800).setAspectY(800);//裁剪时的尺寸比例
        //builder.setOutputX(800).setOutputY(800);
        builder.setWithOwnCrop(false);//s使用第三方还是takephoto自带的裁剪工具
        return builder.create();
    }

    @Override
    public void takeSuccess(TResult result) {

        Log.e("takeSuccess", result.getImages().toString());
        ivBack.setVisibility(View.VISIBLE);
        ivBack.setImageURI(Uri.fromFile(new File(result.getImage().getCompressPath())));
        selectPictures.clear();
        selectPictures.add(new LocalPicture(result.getImages().get(0).getCompressPath()));
        setResultData();

    }

    private void setResultData() {
        if (selectPictures != null && selectPictures.size() > 0) {
            ArrayList<String> images = new ArrayList<>();
            for (int index = 0; index < selectPictures.size(); index++) {
                images.add(selectPictures.get(index).path);
            }
            Intent intent = new Intent();
            if (ImagePicker.getInstance().isEnableCompress() || isCompress) {
                intent.putStringArrayListExtra(TConstant.INTENT_EXTRA_IMAGES, getCompressPicturePaths(images));
            } else {
                intent.putStringArrayListExtra(TConstant.INTENT_EXTRA_IMAGES, images);
            }
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
        Log.e("takeFail", result.getImages().toString());

    }

    @Override
    public void takeCancel() {
        super.takeCancel();
        Log.e("takeCancel", "cancel");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICTURE_SELECT && data != null) {
            ArrayList<String> pictures = data.getStringArrayListExtra("images");
            Intent intent = new Intent();
            if (ImagePicker.getInstance().isEnableCompress() || isCompress) {
                intent.putStringArrayListExtra(TConstant.INTENT_EXTRA_IMAGES, getCompressPicturePaths(pictures));
            } else {
                intent.putStringArrayListExtra(TConstant.INTENT_EXTRA_IMAGES, pictures);
            }
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    /**
     * 压缩图片
     *
     * @param pictures
     * @return
     */
    private ArrayList<String> getCompressPicturePaths(ArrayList<String> pictures) {
        ArrayList<String> destPicturePaths = new ArrayList<>();
        for (String picturePath : pictures) {
            String[] split = picturePath.split("/");
            String destPicturePath =
                    FileUtils.getImgFolderPath() + split[split.length - 1];
            BitmapUtils.compressPhoto(picturePath, destPicturePath, 300, 1);
            destPicturePaths.add(destPicturePath);
        }
        return destPicturePaths;
    }
}
