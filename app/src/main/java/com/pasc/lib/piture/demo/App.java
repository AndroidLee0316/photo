package com.pasc.lib.piture.demo;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.squareup.leakcanary.LeakCanary;

/**
 * @author yangzijian
 * @date 2018/11/22
 * @des
 * @modify
 **/
public class App extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate ();
//        PascImageLoader.getInstance ().init (this, PascImageLoader.GLIDE_CORE, com.pasc.lib.picture.R.color.white_ffffff);

        if (LeakCanary.isInAnalyzerProcess(this)) {//1
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }
}
