package com.pasc.lib.fileselector.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.pasc.lib.widget.dialognt.CommonDialog;

/**
 * Created by ex-lingchun001 on 2018/3/22.
 */

public class PermissionSettingUtils {
    public static final int REQUEST_CODE_APPLICATION_DETAILS_SETTINGS = 123;


    /**
     * 打开权限设置页面
     * 特殊手机型号特殊处理
     */
    public static void gotoPermissionSetting(final Context context) {
        new CommonDialog(context)
                .setContent("请在“设置->权限管理“选项中，选择“允许”本应用的访问")
                .setButton1("取消")
                .setButton2("设置", CommonDialog.Blue_4d73f4)
                .setOnButtonClickListener(new CommonDialog.OnButtonClickListener() {
                    @Override
                    public void button2Click() {
                        gotoApplicationDetails(context);
                    }
                })
                .show();
    }


    /**
     * 打开权限设置页面
     * 无差别打开应用详情页
     */
    public static void gotoApplicationDetails(Context context) {
        Intent appIntent;

//        //Vivo手机
//        Intent appIntent =
//                context.getPackageManager().getLaunchIntentForPackage("com.iqoo.secure");
//        if (appIntent != null) {
//            context.startActivity(appIntent);
//            return;
//        }
//        //Oppo手机
//        appIntent = context.getPackageManager().getLaunchIntentForPackage("com.oppo.safe");
//        if (appIntent != null) {
//            context.startActivity(appIntent);
//            return;
//        }
//        //华为手机
//        appIntent = context.getPackageManager()
//                .getLaunchIntentForPackage("com.huawei.systemmanager");
//        if (appIntent != null) {
//            context.startActivity(appIntent);
//            return;
//        }
//        //小米手机
//        appIntent = context.getPackageManager()
//                .getLaunchIntentForPackage("com.miui.securitycenter");
//        if (appIntent != null) {
//            context.startActivity(appIntent);
//            return;
//        }
//
        //魅族(目前发现魅族 M6 Note（Android 7.1.2，Flyme 6.1.4.7A）出现在应用信息页打开权限不管用的情况，必须在管家中打开方可生效，所以魅族手机暂定跳转手机管家)
        appIntent = context.getPackageManager()
                .getLaunchIntentForPackage("com.meizu.safe");
        if (appIntent != null) {
            context.startActivity(appIntent);
            return;
        }
//        //360
//        appIntent = context.getPackageManager()
//                .getLaunchIntentForPackage("com.qihoo360.mobilesafe");
//        if (appIntent != null) {
//            context.startActivity(appIntent);
//            return;
//        }
//        //乐视
//        appIntent = context.getPackageManager()
//                .getLaunchIntentForPackage("com.letv.android.letvsafe");
//        if (appIntent != null) {
//            context.startActivity(appIntent);
//            return;
//        }
//        //索尼
//        appIntent = context.getPackageManager()
//                .getLaunchIntentForPackage("com.sonymobile.cta");
//        if (appIntent != null) {
//            context.startActivity(appIntent);
//            return;
//        }

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings",
                    "com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(intent);
    }
}
