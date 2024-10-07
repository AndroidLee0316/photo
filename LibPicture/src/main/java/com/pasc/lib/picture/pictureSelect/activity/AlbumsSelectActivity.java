package com.pasc.lib.picture.pictureSelect.activity;

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.pasc.lib.picture.R;
import com.pasc.lib.picture.pictureSelect.BrowsePicturesAdapter;
import com.pasc.lib.picture.pictureSelect.ImagePicker;
import com.pasc.lib.picture.pictureSelect.LocalPicture;
import com.pasc.lib.picture.pictureSelect.LocalPictureAdapter;
import com.pasc.lib.picture.pictureSelect.NewPictureSelectActivity;
import com.pasc.lib.picture.pictureSelect.adapter.PreviewImageAdapter;
import com.pasc.lib.picture.pictureSelect.bean.PhotoUpImageBucket;
import com.pasc.lib.picture.takephoto.uitl.TConstant;
import com.pasc.lib.picture.util.AndroidPUtil;
import com.pasc.lib.picture.util.DensityUtils;
import com.pasc.lib.picture.util.StatusBarUtils;
import com.pasc.lib.widget.dialognt.CommonDialog;

import org.reactivestreams.Publisher;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

import static com.pasc.lib.picture.pictureSelect.activity.AlbumsActivity.IS_HEAD;
import static com.pasc.lib.widget.dialognt.CommonDialog.Blue_4d73f4;

public class AlbumsSelectActivity extends AppCompatActivity implements View.OnClickListener {

  private CompositeDisposable compositeDisposable = new CompositeDisposable();

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
  private boolean isPreView = false;

  private ArrayList<LocalPicture> allLocalPictures = new ArrayList<>();
  private ArrayList<LocalPicture> selectPictures = new ArrayList<>();
  private ArrayList<LocalPicture> preViewPictures = new ArrayList<>();
  private ArrayList<LocalPicture> preImagePictures = new ArrayList<>();

  private LocalPictureAdapter adapter;
  private BrowsePicturesAdapter browseAdapter;
  private PreviewImageAdapter previewImageAdapter;
  private ImagePicker imagePicker;
  private boolean isHead;
  private View rlBottom;
  private RecyclerView rvPreview;
  private int mPosition;

  public static void actionStart(Activity context, int requestCode, int canSelect,
      PhotoUpImageBucket photoUpImageBucket, boolean ishead) {
    Intent intent = new Intent(context, AlbumsSelectActivity.class);
    intent.putExtra(TConstant.INTENT_EXTRA_LIMIT, canSelect);
    intent.putExtra("imagelist", photoUpImageBucket);
    intent.putExtra(IS_HEAD, ishead);
    context.startActivityForResult(intent, requestCode);
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    StatusBarUtils.setStatusBarLightMode(this, true, true);
    setContentView(R.layout.picture_activity_ablums_select);
    tvSelect = findViewById(R.id.tv_select);
    recyclerView = findViewById(R.id.recycler_view);
    tvBrowse = findViewById(R.id.tv_browse);
    tvTitleName = findViewById(R.id.tv_title_name);
    tvTitleRight = findViewById(R.id.tv_title_right);
    tvPicturePosition = findViewById(R.id.tv_picture_position);
    ll_title = findViewById(R.id.ll_title);
    rlBottom = findViewById(R.id.rl_bottom);
    viewpager = findViewById(R.id.viewpager);
    rvPreview = findViewById(R.id.rv_preview);

    imagePicker = ImagePicker.getInstance();
    initEvent();
    Intent in = getIntent();
    if (in != null) {
      canSelect = in.getIntExtra(TConstant.INTENT_EXTRA_LIMIT, deafultNum);
      isHead = in.getBooleanExtra(IS_HEAD, false);
      rlBottom.setVisibility(isHead ? View.GONE : View.VISIBLE);
    }
    if (ImagePicker.isEnable()) {
      canSelect = ImagePicker.getSelectLimit();
    }
    if (canSelect > 0) {
      canSelect = canSelect > 9 ? 9 : canSelect;
    }
    initView();
    initData();
    if (Build.VERSION.SDK_INT >= 28) {
      ll_title.post(new Runnable() {
        @Override
        public void run() {
          int paddingTop =
              AndroidPUtil.getPaddingTop(getActivity(), DensityUtils.dip2px(getActivity(), 20));
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
    browseAdapter = new BrowsePicturesAdapter(this, preViewPictures);
    viewpager.setAdapter(browseAdapter);
    adapter.setItemClick(new LocalPictureAdapter.ItemClick() {
      @Override
      public void click(View view, int position) {
        int i = view.getId();
        mPosition = position;
        if (isHead) {
          StatusBarUtils.setStatusBarLightMode(AlbumsSelectActivity.this, true, false);
          ll_title.setBackgroundColor(Color.parseColor("#bf000000"));
          itemSelectorNot(position);
          tvTitleRight.setVisibility(View.GONE);
          tvTitleName.setVisibility(View.GONE);
          tvPicturePosition.setSelected(allLocalPictures.get(position).isSelect());
          tvPicturePosition.setText(getSelectPosition(allLocalPictures.get(position)));
          setResult();
        } else {
          if (i == R.id.fl_icon) {
            itemSelectorNot(position);
          } else if (i == R.id.img_local) {
            if (isPreView) {
              showPreview();
            } else {
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
        if (isPreView) {
          tvPicturePosition.setSelected(preViewPictures.get(position).isSelect());
          tvPicturePosition.setText(getSelectPosition(preViewPictures.get(position)));
        } else {
          tvPicturePosition.setSelected(allLocalPictures.get(position).isSelect());
          tvPicturePosition.setText(getSelectPosition(allLocalPictures.get(position)));
        }
        previewImageAdapter.setSelectedItem(position);
      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });

    rvPreview.setLayoutManager(
        new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    previewImageAdapter = new PreviewImageAdapter(preImagePictures);
    rvPreview.setAdapter(previewImageAdapter);
    previewImageAdapter.setOnItemChildClickListener(
        new BaseQuickAdapter.OnItemChildClickListener() {
          @Override
          public void onItemChildClick(BaseQuickAdapter baseadapter, View view, int position) {
            LocalPicture localPicture = preImagePictures.get(position);
            int pos = allLocalPictures.indexOf(localPicture);

            if (view.getId() == R.id.img_remove) {
              itemSelectorNot(pos);
              viewpager.setCurrentItem(pos);
            } else if (view.getId() == R.id.img_opinion) {
              if (isPreView) {
                viewpager.setCurrentItem(position);
                previewImageAdapter.setSelectedItem(position);
              } else {
                viewpager.setCurrentItem(pos);
                previewImageAdapter.setSelectedItem(pos);
              }
            }
          }
        });

    tvBrowse.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (viewpager.getVisibility() == View.GONE) {
          showPreview();
        } else {
          clickBack();
        }
      }
    });
  }

  /**
   * 点击 预览 模式
   */
  private void showPreview() {
    isPreView = true;
    tvTitleRight.setVisibility(View.GONE);
    tvTitleName.setVisibility(View.INVISIBLE);
    tvPicturePosition.setVisibility(View.VISIBLE);
    viewpager.setVisibility(View.VISIBLE);
    tvBrowse.setVisibility(View.GONE);
    rvPreview.setVisibility(View.VISIBLE);
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
    rlBottom.setVisibility(View.VISIBLE);
    ll_title.setBackgroundColor(getResources().getColor(R.color.photo_mask_gray));
    rlBottom.setBackgroundColor(getResources().getColor(R.color.gray_333333));
  }

  /**
   * /**
   * 显示预览
   */
  private void showPreview(int position) {
    tvTitleRight.setVisibility(View.GONE);
    tvTitleName.setVisibility(View.GONE);
    tvPicturePosition.setVisibility(View.VISIBLE);
    viewpager.setVisibility(View.VISIBLE);
    rvPreview.setVisibility(View.VISIBLE);
    tvBrowse.setVisibility(View.GONE);
    tvPicturePosition.setSelected(allLocalPictures.get(position).isSelect());
    tvPicturePosition.setText(
        getSelectPosition(allLocalPictures.get(position)));
    viewpager.setCurrentItem(position);
    browseAdapter.notifyDataSetChanged();
    previewImageAdapter.notifyDataSetChanged();
    ll_title.setBackgroundColor(getResources().getColor(R.color.photo_mask_gray));
    rlBottom.setVisibility(View.VISIBLE);
    rlBottom.setBackgroundColor(getResources().getColor(R.color.gray_333333));
  }

  private void setResult() {
    if (selectPictures != null && selectPictures.size() > 0) {
      ArrayList<String> images = new ArrayList<>();
      for (int index = 0; index < selectPictures.size(); index++) {
        images.add(selectPictures.get(index).path);
      }
      Intent intent = new Intent();
      intent.putStringArrayListExtra(TConstant.INTENT_EXTRA_IMAGES, images);
      setResult(RESULT_OK, intent);
    }
    finish();
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
    PhotoUpImageBucket photoUpImageBucket =
        (PhotoUpImageBucket) getIntent().getSerializableExtra("imagelist");
    if (photoUpImageBucket != null && photoUpImageBucket.getImageList() != null) {
      tvTitleName.setText(photoUpImageBucket.getBucketName());
      allLocalPictures.addAll(photoUpImageBucket.getImageList());
      adapter.notifyDataChanged(selectPictures.size(), selectPictures);
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
      setResult();
    } else if (i == R.id.tv_picture_position) {
      itemSelectorNot(viewpager.getCurrentItem());
    }
  }

  private void itemSelectorNot(int position) {

    if (isPreView) {
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
    } else {

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
      previewImageAdapter.notifyDataSetChanged();
      setSelectedSize(selectPictures.size());
    }
  }

  private void setSelectedSize(int position) {
    boolean hasSelected = position > 0;//是否有选中任何图片
    tvBrowse.setAlpha(hasSelected ? 1.0f : 0.3f);
    tvSelect.setAlpha(hasSelected ? 1.0f : 0.3f);
    tvSelect.setEnabled(hasSelected);
    tvBrowse.setEnabled(hasSelected);
    String select = hasSelected ? String.format("确定(%s" + ")", position) : "确定";
    tvSelect.setText(select);
  }

  private void clickBack() {
    isPreView=false;
    ll_title.setBackgroundColor(Color.WHITE);
    StatusBarUtils.setStatusBarLightMode(getActivity(), true, true);
    ll_title.setBackgroundColor(Color.WHITE);
    tvTitleRight.setVisibility(View.VISIBLE);
    tvTitleName.setVisibility(View.VISIBLE);
    tvPicturePosition.setVisibility(View.GONE);
    viewpager.setVisibility(View.GONE);
    rvPreview.setVisibility(View.GONE);
    tvBrowse.setVisibility(View.VISIBLE);

    recyclerView.setVisibility(View.VISIBLE);

    preViewPictures.clear();
    preViewPictures.addAll(allLocalPictures);
    browseAdapter.notifyDataSetChanged();
    rlBottom.setBackgroundColor(getResources().getColor(R.color.white));

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

  protected AppCompatActivity getActivity() {
    return this;
  }
}
