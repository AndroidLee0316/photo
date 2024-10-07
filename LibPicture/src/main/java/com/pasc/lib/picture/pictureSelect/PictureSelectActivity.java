package com.pasc.lib.picture.pictureSelect;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.pasc.lib.picture.R;
import com.pasc.lib.picture.takephoto.uitl.TConstant;
import com.pasc.lib.picture.util.AndroidPUtil;
import com.pasc.lib.picture.util.DensityUtils;
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

public class PictureSelectActivity extends BaseLoadingActivity implements View.OnClickListener {


    private CompositeDisposable compositeDisposable=new CompositeDisposable ();

    private TextView tvSelect;
    private RecyclerView recyclerView;
    private TextView tvBrowse;
    private TextView tvTitleName;
    private TextView tvTitleRight;
    private TextView tvPicturePosition;
    private View ll_title;
    private ViewPager viewpager;

    private static final int deafultNum = 1;
    private int canSelect = deafultNum;

    private ArrayList<LocalPicture> allLocalPictures = new ArrayList<> ();
    private ArrayList<LocalPicture> selectPictures = new ArrayList<> ();
    private LocalPictureAdapter adapter;
    private BrowsePicturesAdapter browseAdapter;
    private ImagePicker imagePicker;

    @Deprecated
    public static void actionStart(Activity context, int requestCode, int canSelect) {
        Intent intent = new Intent (context, PictureSelectActivity.class);
        intent.putExtra(TConstant.INTENT_EXTRA_LIMIT, canSelect);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_activity_picture_select);
        tvSelect = findViewById(R.id.tv_select);
        recyclerView = findViewById(R.id.recycler_view);
        tvBrowse = findViewById(R.id.tv_browse);
        tvTitleName = findViewById(R.id.tv_title_name);
        tvTitleRight = findViewById(R.id.tv_title_right);
        tvPicturePosition = findViewById(R.id.tv_picture_position);
        ll_title = findViewById(R.id.ll_title);
        viewpager = findViewById(R.id.viewpager);
        imagePicker = ImagePicker.getInstance();
        initEvent();
        Intent in = getIntent();
        if (in != null) {
            canSelect = in.getIntExtra(TConstant.INTENT_EXTRA_LIMIT, deafultNum);
        }

        int selectLimit = imagePicker.getSelectLimit();
        if(selectLimit >1){
            canSelect= selectLimit>9?9:selectLimit;
        }
        initView();
        initData();
        clickBack();
        if (Build.VERSION.SDK_INT >= 28) {
            ll_title.post(new Runnable () {
                @Override
                public void run() {
                    int paddingTop = AndroidPUtil.getPaddingTop(getActivity(), DensityUtils.dip2px(getActivity(), 20));
                    ll_title.setPadding(0, paddingTop, 0, 0);
                }
            });

        }
    }

    private void initView() {
        int spanCount=4;
        recyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));
        adapter = new LocalPictureAdapter(this,spanCount,allLocalPictures, canSelect, selectPictures);
        recyclerView.setAdapter(adapter);
        browseAdapter = new BrowsePicturesAdapter(this, allLocalPictures);
        viewpager.setAdapter(browseAdapter);
        adapter.setItemClick (new LocalPictureAdapter.ItemClick () {
            @Override
            public void click(View view, int position) {
                int i = view.getId();
                if (i == R.id.fl_icon) {
                    itemSelectorNot(position);

                } else if (i == R.id.img_local) {
                    StatusBarUtils.setStatusBarLightMode(getActivity (), true, false);
                    ll_title.setBackgroundColor (Color.parseColor ("#bf000000"));

                    tvTitleRight.setVisibility(View.GONE);
                    tvTitleName.setVisibility(View.GONE);
                    tvPicturePosition.setVisibility(View.VISIBLE);
                    viewpager.setVisibility(View.VISIBLE);
                    tvPicturePosition.setSelected(allLocalPictures.get(position).isSelect());
                    tvPicturePosition.setText(
                            getSelectPosition(allLocalPictures.get(position)));
                    viewpager.setCurrentItem(position);
                    browseAdapter.notifyDataSetChanged();

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

                tvPicturePosition.setSelected(allLocalPictures.get(position).isSelect());
                tvPicturePosition.setText(getSelectPosition(allLocalPictures.get(position)));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void showErrorDialog() {
        final CommonDialog commonDialog = new CommonDialog(this);
        commonDialog.setContent("最多只能选择" + canSelect + "张照片").setButton1("我知道了", Blue_4d73f4).show();

        commonDialog.setOnButtonClickListener(new CommonDialog.OnButtonClickListener() {
            @Override
            public void button1Click() {
                commonDialog.dismiss();
            }


        });
    }

    private void initData() {
        setSelectedSize(0);
        showLoading("正在加载图片");
        ArrayList<File> files = new ArrayList<> ();
        files.add(new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                .getAbsolutePath()));
        files.add(new File (
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        .getAbsolutePath()));
        files.add(new File (
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        .getAbsolutePath()));
        files.add(new File (Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/tencent/MicroMsg/WeiXin/"));
        files.add(new File (
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/news_aticle"));
        files.add(new File (
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/sina/weibo/"));
       Disposable disposable= Flowable.fromArray(files)
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
                        dismissLoading();

                    }
                });
        compositeDisposable.add (disposable);
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
                            || file.getName().endsWith(".mp4")
                            || file.getName().endsWith(".MP4")
                            || file.getName().endsWith(".PNG");
                }
            });
        }
    }

    private void initEvent() {
        findViewById(R.id.iv_title_left).setOnClickListener(this);
        findViewById(R.id.tv_title_right).setOnClickListener(this);
        findViewById(R.id.tv_picture_position).setOnClickListener(this);
        tvSelect.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.iv_title_left) {
            if (viewpager.getVisibility() == View.GONE) {
                finish();
                return;
            }
            clickBack();

        } else if (i == R.id.tv_title_right) {
            selectPictures.clear();
            finish();

        } else if (i == R.id.tv_select) {
            if (selectPictures != null && selectPictures.size() > 0) {
                ArrayList<String> images=new ArrayList<> ();
                for (int index = 0; index < selectPictures.size(); index++) {
                    images.add (selectPictures.get(index).path);
                }
                Intent intent = new Intent ();
                intent.putStringArrayListExtra (TConstant.INTENT_EXTRA_IMAGES, images);
                setResult(RESULT_OK, intent);
            }
            finish();

        } else if (i == R.id.tv_picture_position) {
            itemSelectorNot(viewpager.getCurrentItem());

        }
    }

    private void itemSelectorNot(int position) {
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
        setSelectedSize(selectPictures.size());
    }

    private void setSelectedSize(int position) {
        String text = String.format("已选择(%s/" + canSelect + ")", position);
        int len = 4 + ("" + position).length();
        SpannableString msp = new SpannableString (text);
        int color=Color.parseColor ("#4d73f4");
        msp.setSpan(new ForegroundColorSpan (color), 4, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvBrowse.setText(msp);
    }

    private void clickBack() {
        ll_title.setBackgroundColor(Color.WHITE);
        StatusBarUtils.setStatusBarLightMode(getActivity (), true, true);
        ll_title.setBackgroundColor(Color.WHITE);
        tvTitleRight.setVisibility(View.VISIBLE);
        tvTitleName.setVisibility(View.VISIBLE);
        tvPicturePosition.setVisibility(View.GONE);
        viewpager.setVisibility(View.GONE);

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
        super.onDestroy ();
        compositeDisposable.clear ();
    }

    protected AppCompatActivity getActivity(){
        return this;
    }

}
