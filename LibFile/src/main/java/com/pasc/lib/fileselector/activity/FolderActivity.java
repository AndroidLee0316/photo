package com.pasc.lib.fileselector.activity;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.pasc.lib.base.activity.BaseActivity;
import com.pasc.lib.fileselector.adapter.PublicTabViewPagerAdapter;
import com.pasc.lib.fileselector.fragment.FolderDataFragment;
import com.pasc.lib.fileselector.model.FileInfo;
import com.pasc.lib.fileselector.utils.FileUtil;
import com.pasc.lib.widget.toolbar.PascToolbar;
import com.pasc.pasc.lib.file.R;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 使用遍历文件夹的方式
 */
public class FolderActivity extends BaseActivity {

    private TabLayout tlFile;
    private ViewPager vpFile;
    private PascToolbar toolbar;

    private List<String> mTabTitle = new ArrayList<>();
    private List<Fragment> mFragment = new ArrayList<>();

    private ArrayList<FileInfo> imageData = new ArrayList<>();
    private ArrayList<FileInfo> wordData = new ArrayList<>();
    private ArrayList<FileInfo> xlsData = new ArrayList<>();
    private ArrayList<FileInfo> pptData = new ArrayList<>();
    private ArrayList<FileInfo> pdfData = new ArrayList<>();

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

        tlFile = findViewById(R.id.tl_file);
        vpFile = findViewById(R.id.vp_file);
        toolbar = findViewById (R.id.pasc_activity_folder_toolbar);
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

        scanDirNoRecursion(Environment.getExternalStorageDirectory().toString());
        handler.sendEmptyMessage(1);
    }


    private void initData() {

        mTabTitle = new ArrayList<>();
        mFragment = new ArrayList<>();

        mTabTitle.add("image");
        mTabTitle.add("word");
        mTabTitle.add("xls");
        mTabTitle.add("ppt");
        mTabTitle.add("pdf");

        FolderDataFragment imageFragment = new FolderDataFragment();
        Bundle imageBundle = new Bundle();
        imageBundle.putParcelableArrayList("file_data", imageData);
        imageBundle.putBoolean("is_image", true);
        imageFragment.setArguments(imageBundle);
        mFragment.add(imageFragment);

        FolderDataFragment wordFragment = new FolderDataFragment();
        Bundle wordBundle = new Bundle();
        wordBundle.putParcelableArrayList("file_data", wordData);
        wordBundle.putBoolean("is_image", false);
        wordFragment.setArguments(wordBundle);
        mFragment.add(wordFragment);

        FolderDataFragment xlsFragment = new FolderDataFragment();
        Bundle xlsBundle = new Bundle();
        xlsBundle.putParcelableArrayList("file_data", xlsData);
        xlsBundle.putBoolean("is_image", false);
        xlsFragment.setArguments(xlsBundle);
        mFragment.add(xlsFragment);

        FolderDataFragment pptFragment = new FolderDataFragment();
        Bundle pptBundle = new Bundle();
        pptBundle.putParcelableArrayList("file_data", pptData);
        pptBundle.putBoolean("is_image", false);
        pptFragment.setArguments(pptBundle);
        mFragment.add(pptFragment);

        FolderDataFragment pdfFragment = new FolderDataFragment();
        Bundle pdfBundle = new Bundle();
        pdfBundle.putParcelableArrayList("file_data", pdfData);
        pdfBundle.putBoolean("is_image", false);
        pdfFragment.setArguments(pdfBundle);
        mFragment.add(pdfFragment);

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
     * 非递归
     *
     * @param path
     */
    public void scanDirNoRecursion(String path) {
        LinkedList list = new LinkedList();
        File dir = new File(path);
        File file[] = dir.listFiles();
        if (file == null) return;
        for (int i = 0; i < file.length; i++) {
            if (file[i].isDirectory())
                list.add(file[i]);
        }
        File tmp;
        while (!list.isEmpty()) {
            tmp = (File) list.removeFirst();//首个目录
            if (tmp.isDirectory()) {
                file = tmp.listFiles();
                if (file == null)
                    continue;
                for (int i = 0; i < file.length; i++) {
                    if (file[i].isDirectory())
                        list.add(file[i]);//目录则加入目录列表，关键
                    else {
//                        System.out.println(file[i]);
                        if (file[i].getName().endsWith(".png") || file[i].getName().endsWith(".jpg") || file[i].getName().endsWith(".jpeg") || file[i].getName().endsWith(".gif")) {
                            //往图片集合中 添加图片的路径
                            FileInfo document = FileUtil.getFileInfoFromFile(new File(file[i].getAbsolutePath()));
                            imageData.add(document);
                        } else if (file[i].getName().endsWith(".doc") || file[i].getName().endsWith(".docx")) {
                            FileInfo document = FileUtil.getFileInfoFromFile(new File(file[i].getAbsolutePath()));
                            wordData.add(document);
                        } else if (file[i].getName().endsWith(".xls") || file[i].getName().endsWith(".xlsx")) {
                            //往图片集合中 添加图片的路径
                            FileInfo document = FileUtil.getFileInfoFromFile(new File(file[i].getAbsolutePath()));
                            xlsData.add(document);
                        } else if (file[i].getName().endsWith(".ppt") || file[i].getName().endsWith(".pptx")) {
                            //往图片集合中 添加图片的路径
                            FileInfo document = FileUtil.getFileInfoFromFile(new File(file[i].getAbsolutePath()));
                            pptData.add(document);
                        } else if (file[i].getName().endsWith(".pdf")) {
                            //往图片集合中 添加图片的路径
                            FileInfo document = FileUtil.getFileInfoFromFile(new File(file[i].getAbsolutePath()));
                            pdfData.add(document);
                        }
                    }
                }
            }
        }
    }

    /**
     * 遍历手机所有文件 并将路径名存入集合中 参数需要 路径和集合 - 递归
     */
    public void recursionFile(File dir) {
        //得到某个文件夹下所有的文件
        File[] files = dir.listFiles();
        //文件为空
        if (files == null) {
            return;
        }
        //遍历当前文件下的所有文件
        for (File file : files) {
            //如果是文件夹
            if (file.isDirectory()) {
                //则递归(方法自己调用自己)继续遍历该文件夹
                recursionFile(file);
            } else { //如果不是文件夹 则是文件
                //如果文件名以 .mp3结尾则是mp3文件
                if (file.getName().endsWith(".doc") || file.getName().endsWith(".docx")) {
                    //往图片集合中 添加图片的路径
                    FileInfo document = FileUtil.getFileInfoFromFile(new File(file.getAbsolutePath()));
                    wordData.add(document);
                } else if (file.getName().endsWith(".xls") || file.getName().endsWith(".xlsx")) {
                    //往图片集合中 添加图片的路径
                    FileInfo document = FileUtil.getFileInfoFromFile(new File(file.getAbsolutePath()));
                    xlsData.add(document);
                } else if (file.getName().endsWith(".ppt") || file.getName().endsWith(".pptx")) {
                    //往图片集合中 添加图片的路径
                    FileInfo document = FileUtil.getFileInfoFromFile(new File(file.getAbsolutePath()));
                    pptData.add(document);
                } else if (file.getName().endsWith(".pdf")) {
                    //往图片集合中 添加图片的路径
                    FileInfo document = FileUtil.getFileInfoFromFile(new File(file.getAbsolutePath()));
                    pdfData.add(document);
                }
            }
        }
    }

}
