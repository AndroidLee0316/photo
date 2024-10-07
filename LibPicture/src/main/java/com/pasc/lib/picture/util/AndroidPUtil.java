package com.pasc.lib.picture.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.IntDef;

import java.lang.reflect.Method;
//import android.view.DisplayCutout;
//import android.view.WindowInsets;
//import android.view.WindowManager;

public class AndroidPUtil {

    public static int getPaddingTop(Activity context, int defaultValue){

        if (Build.VERSION.SDK_INT>=28){
//            try {
//                // 需要View 绘制完成之后
//                final WindowInsets windowInsets = context.getWindow().getDecorView().getRootWindowInsets();
//                DisplayCutout displayCutout = null;
//                if (windowInsets != null) {
//                    displayCutout = windowInsets.getDisplayCutout();
//                }
//                if (displayCutout != null)
//                    defaultValue = displayCutout.getSafeInsetTop();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }else if (Build.VERSION.SDK_INT >= 27) {
            if (DisplayCutoutUtil.supportCutout(context)) {
                if (Rom.isMiui()) {
                    defaultValue = getNotchSizeAtXiaoMi(context);
                } else if (Rom.isEmui()) {
                    defaultValue = getNotchSizeAtHuawei(context)[1];
                } else if (Rom.isOppo()) {
                    //OPPO  目前都为 80px
                    defaultValue = 80;
                } else if (Rom.isVivo()) {
                    defaultValue = dp2px(context, 32);
                }
            }
        }


        return defaultValue;

    }


    /***
     * 设置刘海类型
     * @param activity
     */
    public static void setCutOutType(Activity activity, @CutOutType int cutOutType) {

        if (activity != null) {
//            if (Build.VERSION.SDK_INT >= 28) {
//                WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
//                switch (cutOutType) {
//                    case CUTOUT_MODE_DEFAULT:
//                        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT;
//                        break;
//                    case CUTOUT_MODE_SHORT_EDGES:
//                        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
//                        break;
//                    case CUTOUT_MODE_NEVER:
//                        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
//                        break;
//                }
//                activity.getWindow().setAttributes(lp);
//            }
        }
    }

    /*****该窗口决不允许与DisplayCutout区域重叠。****/
    public static final int CUTOUT_MODE_DEFAULT = 0;
    /**** 只有当DisplayCutout完全包含在系统栏中时，才允许窗口延伸到DisplayCutout区域。 否则，窗口布局不与DisplayCutout区域重叠。****/
    public static final int CUTOUT_MODE_SHORT_EDGES = 1;
    /**** 该窗口始终允许延伸到屏幕短边上的DisplayCutout区域。*****/
    public static final int CUTOUT_MODE_NEVER = 2;

    @IntDef({CUTOUT_MODE_DEFAULT, CUTOUT_MODE_SHORT_EDGES, CUTOUT_MODE_NEVER})
    public @interface CutOutType {
    }

    /**
     * 小米手机获取刘海的高度
     */
    private static int getNotchSizeAtXiaoMi(Context context) {
        int statusBarHeight = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    /**
     * 华为手机获取刘海的宽高
     * int[0]值为刘海宽度 int[1]值为刘海高度
     */
    private static int[] getNotchSizeAtHuawei(Context context) {
        int[] ret = new int[]{0, 0};
        try {
            ClassLoader cl = context.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("getNotchSize");
            ret = (int[]) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
        } catch (NoSuchMethodException e) {
        } catch (Exception e) {
        } finally {
            return ret;
        }
    }

    private static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
