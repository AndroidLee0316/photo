package com.pasc.lib.fileselector.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.pasc.lib.base.activity.BaseActivity;
import com.pasc.lib.base.permission.PermissionUtils;
import com.pasc.lib.fileselector.adapter.PublicTabViewPagerAdapter;
import com.pasc.lib.fileselector.fragment.FolderDataFragment;
import com.pasc.lib.fileselector.model.FileInfo;
import com.pasc.lib.fileselector.utils.FileUtil;
import com.pasc.lib.fileselector.utils.PermissionSettingUtils;
import com.pasc.lib.widget.toolbar.PascToolbar;
import com.pasc.pasc.lib.file.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * 使用 Media Store 多媒体库
 */

public class MediaStoreActivity extends BaseActivity {

    private TabLayout tlFile;
    private ViewPager vpFile;
    private PascToolbar toolbar;

    private List<String> mTabTitle = new ArrayList<>();
    private List<Fragment> mFragment = new ArrayList<>();

//    private ArrayList<FileInfo> imageData = new ArrayList<>();
    private ArrayList<FileInfo> wordData = new ArrayList<>();
    private ArrayList<FileInfo> xlsData = new ArrayList<>();
    private ArrayList<FileInfo> pptData = new ArrayList<>();
    private ArrayList<FileInfo> pdfData = new ArrayList<>();

    private ArrayList<String> fileTypes = new ArrayList<>();


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                initData();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(1);
    }

    @Override
    protected int layoutResId() {
        return R.layout.pasc_activity_folder;
    }

    @Override
    protected void onInit(@Nullable Bundle bundle) {

        fileTypes.add("word");
        fileTypes.add("xls");
        fileTypes.add("pdf");


        toolbar = findViewById(R.id.pasc_activity_folder_toolbar);
        toolbar.addCloseImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.addRightTextButton(getResources().getString(R.string.cancel)).setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                onBackPressed ();
            }
        });

        PermissionUtils.request(MediaStoreActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            initView();
                        } else {
                            PermissionSettingUtils.gotoPermissionSetting(MediaStoreActivity.this);
                        }
                    }
                });

    }

    public void initView(){
        tlFile = findViewById(R.id.tl_file);
        vpFile = findViewById(R.id.vp_file);

        showLoading(getString(R.string.list_loading),false);

        new Thread() {
            @Override
            public void run() {
                super.run();
                getFolderData();
            }
        }.start();
    }

    /**
     * 遍历文件夹中资源
     */
    public void getFolderData() {

//        getImages();
        for(String fileType:fileTypes){
            getDocumentData(fileType);

        }

        handler.sendEmptyMessage(1);
    }


    private void initData() {

        mTabTitle = new ArrayList<>();
        mFragment = new ArrayList<>();
        for(String fileType:fileTypes){
            mTabTitle.add(fileType);
            FolderDataFragment folderDataFragment = new FolderDataFragment();
            Bundle bundle = new Bundle();
            switch (fileType){
                case "word":
                    bundle.putParcelableArrayList("file_data", wordData);
                    break;
                case "xls":
                    bundle.putParcelableArrayList("file_data", xlsData);
                    break;
                case "ppt":
                    bundle.putParcelableArrayList("file_data", pptData);
                    break;
                case "pdf":
                    bundle.putParcelableArrayList("file_data", pdfData);
                    break;
            }
            bundle.putBoolean("is_image", false);
            folderDataFragment.setArguments(bundle);
            mFragment.add(folderDataFragment);
        }



//        FolderDataFragment imageFragment = new FolderDataFragment();
//        Bundle imageBundle = new Bundle();
//        imageBundle.putParcelableArrayList("file_data", imageData);
//        imageBundle.putBoolean("is_image", true);
//        imageFragment.setArguments(imageBundle);
//        mFragment.add(imageFragment);

//        FolderDataFragment wordFragment = new FolderDataFragment();
//        Bundle wordBundle = new Bundle();
//        wordBundle.putParcelableArrayList("file_data", wordData);
//        wordBundle.putBoolean("is_image", false);
//        wordFragment.setArguments(wordBundle);
//        mFragment.add(wordFragment);

//        FolderDataFragment xlsFragment = new FolderDataFragment();
//        Bundle xlsBundle = new Bundle();
//        xlsBundle.putParcelableArrayList("file_data", xlsData);
//        xlsBundle.putBoolean("is_image", false);
//        xlsFragment.setArguments(xlsBundle);
//        mFragment.add(xlsFragment);

//        FolderDataFragment pptFragment = new FolderDataFragment();
//        Bundle pptBundle = new Bundle();
//        pptBundle.putParcelableArrayList("file_data", pptData);
//        pptBundle.putBoolean("is_image", false);
//        pptFragment.setArguments(pptBundle);
//        mFragment.add(pptFragment);

//        FolderDataFragment pdfFragment = new FolderDataFragment();
//        Bundle pdfBundle = new Bundle();
//        pdfBundle.putParcelableArrayList("file_data", pdfData);
//        pdfBundle.putBoolean("is_image", false);
//        pdfFragment.setArguments(pdfBundle);
//        mFragment.add(pdfFragment);

        FragmentManager fragmentManager = getSupportFragmentManager();

        PublicTabViewPagerAdapter tabViewPagerAdapter = new PublicTabViewPagerAdapter(fragmentManager, mFragment, mTabTitle);
        vpFile.setAdapter(tabViewPagerAdapter);

        tlFile.setupWithViewPager(vpFile);

        tlFile.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vpFile.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        dismissLoading();
    }

    /**
     * 加载图片
     */
//    public void getImages() {
//
//        String[] projection = new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME};
//
//        //asc 按升序排列
//        //desc 按降序排列
//        //projection 是定义返回的数据，selection 通常的sql 语句，例如  selection=MediaStore.Images.ImageColumns.MIME_TYPE+"=? " 那么 selectionArgs=new String[]{"jpg"};
//        ContentResolver mContentResolver = this.getContentResolver();
//        Cursor cursor = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Images.ImageColumns.DATE_MODIFIED + "  desc");
//
//
//        String imageId = null;
//
//        String fileName;
//
//        String filePath;
//
//        while (cursor.moveToNext()) {
//
//            imageId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID));
//
//            fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
//
//            filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
//
//
//            Log.e("photo", imageId + " -- " + fileName + " -- " + filePath);
//            FileInfo fileInfo = FileUtil.getFileInfoFromFile(new File(filePath));
//            imageData.add(fileInfo);
//        }
//        cursor.close();
//
//        cursor = null;
//    }

    /**
     * 获取手机文档数据
     *
     * @param selectType
     */
    public void getDocumentData(String selectType) {

        String[] columns = new String[]{MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.MIME_TYPE, MediaStore.Files.FileColumns.SIZE, MediaStore.Files.FileColumns.DATE_MODIFIED, MediaStore.Files.FileColumns.DATA};

        String select = "";

        switch (selectType) {
            //word
            case "word":
                select = "(" + MediaStore.Files.FileColumns.DATA + " LIKE '%.doc'" + " or " + MediaStore.Files.FileColumns.DATA + " LIKE '%.docx'" + ")";
                break;
            //xls
            case "xls":
                select = "(" + MediaStore.Files.FileColumns.DATA + " LIKE '%.xls'" + " or " + MediaStore.Files.FileColumns.DATA + " LIKE '%.xlsx'" + ")";
                break;
            //ppt
            case "ppt":
                select = "(" + MediaStore.Files.FileColumns.DATA + " LIKE '%.ppt'" + " or " + MediaStore.Files.FileColumns.DATA + " LIKE '%.pptx'" + ")";
                break;
            //pdf
            case "pdf":
                select = "(" + MediaStore.Files.FileColumns.DATA + " LIKE '%.pdf'" + ")";
                break;
        }

//        List<FileInfo> dataList = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Files.getContentUri("external"), columns, select, null, null);
//        Cursor cursor = contentResolver.query(Uri.parse(Environment.getExternalStorageDirectory() + "/tencent/QQfile_recv/"), columns, select, null, null);

        int columnIndexOrThrow_DATA = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);

        if (cursor != null) {
            while (cursor.moveToNext()) {

                String path = cursor.getString(columnIndexOrThrow_DATA);

                FileInfo document = FileUtil.getFileInfoFromFile(new File(path));

//                dataList.add(document);
                switch (selectType) {
                    //word
                    case "word":
                        wordData.add(document);
                        break;
                    //xls
                    case "xls":
                        xlsData.add(document);
                        break;
                    //ppt
                    case "ppt":
                        pptData.add(document);
                        break;
                    //pdf
                    case "pdf":
                        pdfData.add(document);
                        break;
                }
            }
            cursor.close();
        }
    }

    @Override
    public void onBackPressed() {
        //Intent data = getIntent().putExtra("data","");
        //setResult(-1,data);
        super.onBackPressed();
    }

}
