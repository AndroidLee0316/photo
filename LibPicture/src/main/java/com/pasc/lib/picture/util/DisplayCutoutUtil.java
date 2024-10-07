package com.pasc.lib.picture.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.IntDef;
import android.util.Log;

import java.lang.reflect.Method;

public class DisplayCutoutUtil {
    private final static String TAG = "NotchTag";

    /**
     * 是否支持刘海
     *
     * @param context
     * @return
     */
    public static boolean supportCutout(Context context) {
        if (Build.VERSION.SDK_INT >= 28) {
            //Android P
            return true;
        } else if (Build.VERSION.SDK_INT >= 27) {
            /***目前只有android O 以上才有 刘海****/
            if (Rom.isMiui()) {
                return hasNotchAtMiUi();
            } else if (Rom.isEmui()) {
                return hasNotchAtHuaWei(context);
            } else if (Rom.isVivo()) {
                return hasNotchAtViVo(context);
            } else if (Rom.isOppo()) {
                return hasNotchAtOPPO(context);
            }
        }
        return false;
    }

    /**
     * 华为手机  刘海
     *
     * @param context
     * @return
     */
    private static boolean hasNotchAtHuaWei(Context context) {
        boolean ret = false;
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class HwNotchSizeUtil = classLoader.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (boolean) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "hasNotchAtHuaWei ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "hasNotchAtHuaWei NoSuchMethodException");
        } catch (Exception e) {
            Log.e(TAG, "hasNotchAtHuaWei Exception");
        } finally {
            return ret;
        }
    }


    /**
     * Vivo 刘海
     *
     * @param context
     * @return
     */
    private static boolean hasNotchAtViVo(Context context) {
        int VIVO_NOTCH = 0x00000020;//是否有刘海
//        int VIVO_FILLET = 0x00000008;//是否有圆角
        boolean ret = false;
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class FtFeature = classLoader.loadClass("android.util.FtFeature");
            Method method = FtFeature.getMethod("isFeatureSupport", int.class);
            ret = (boolean) method.invoke(FtFeature, VIVO_NOTCH);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "hasNotchAtViVo ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "hasNotchAtViVo NoSuchMethodException");
        } catch (Exception e) {
            Log.e(TAG, "hasNotchAtViVo Exception");
        } finally {
            return ret;
        }
    }

    /***
     * OPPO 刘海
     * @param context
     * @return
     */
    private static boolean hasNotchAtOPPO(Context context) {
        try {
            return context != null && context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
        } catch (Exception e) {
            Log.e(TAG, "hasNotchAtOPPO Exception");
        }
        return false;
    }


    private static boolean hasNotchAtMiUi() {
//        return android.os.SystemProperties.getInt("ro.miui.notch", 0) == 1 ? true : false;
        try {
            Class aClass=Class.forName("android.os.SystemProperties");
            Method method=aClass.getDeclaredMethod("getInt",String.class,int.class);
            method.setAccessible(true);
            Integer integer= (Integer) method.invoke(null,"ro.miui.notch", 0);
            return integer==1? true : false;
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }


    /***
     * 设置刘海类型
     * @param activity
     */
    public static void setCutOutType(Activity activity, @CutOutType int cutOutType) {

        if (activity != null) {
            if (Build.VERSION.SDK_INT >= 28) {
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
            } else if (supportCutout(activity)) {
                try {
                    if (Rom.isMiui()) {
                    } else if (Rom.isEmui()) {
                    } else if (Rom.isVivo()) {
                    } else if (Rom.isOppo()) {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
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

}
