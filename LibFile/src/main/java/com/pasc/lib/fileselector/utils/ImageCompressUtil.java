package com.pasc.lib.fileselector.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Copyright (C) 2018 pasc Licensed under the Apache License, Version 2.0 (the "License");
 *
 * @author yangzijian
 * @date 2018/9/7
 * @des
 * @modify
 **/
public class ImageCompressUtil {

    /**
     * 得到bitmap的大小
     */
    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.getAllocationByteCount ();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return bitmap.getByteCount ();
        }
        // 在低版本中用一行的字节x高度
        return bitmap.getRowBytes () * bitmap.getHeight ();                //earlier version
    }

    /**
     *
     * @param fileSrc
     * @param fileDst
     * @param targetW
     * @param targetH
     * @param maxSize  kb 最大大小
     * @return
     */
    public static boolean compressPhoto(String fileSrc, String fileDst, int targetW, int targetH, int maxSize) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options ();
        // 计算缩放
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile (fileSrc, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        int scaleFactor = Math.min (photoW / targetW, photoH / targetH);

//        photoW = photoW / scaleFactor;
//        photoH = photoH / scaleFactor;
//
//        int size = photoW * photoH * 4 / 1024;
//
//        if (size > maxSize) {
//            scaleFactor = (int) (scaleFactor * ((size + 0.0f) / maxSize));
//        }
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        int angle = readPictureDegree (fileSrc);
        Bitmap bitmap = rotaingImageView (angle, BitmapFactory.decodeFile (fileSrc, bmOptions));

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress (Bitmap.CompressFormat.JPEG, 100, stream);
        FileOutputStream fos;
        try {
            File file = new File(fileDst);
            fos = new FileOutputStream(file);
            fos.write (stream.toByteArray ());

            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace ();
        } catch (IOException e) {
            e.printStackTrace ();
        }
        return false;
    }

    /**
     * 压缩图片
     */
    public static boolean compressPhoto(String fileSrc, String fileDst, int targetW, int targetH) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options ();
        // 计算缩放
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile (fileSrc, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        int scaleFactor = Math.min (photoW / targetW, photoH / targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        int angle = readPictureDegree (fileSrc);
        Bitmap bitmap = rotaingImageView (angle, BitmapFactory.decodeFile (fileSrc, bmOptions));
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress (Bitmap.CompressFormat.JPEG, 100, stream);
        FileOutputStream fos;
        try {
            File file = new File(fileDst);
            fos = new FileOutputStream(file);
            fos.write (stream.toByteArray ());
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace ();
        } catch (IOException e) {
            e.printStackTrace ();
        }
        return false;
    }

    /**
     * 图片旋转回原来的
     */

    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        if (angle == 0) {
            return bitmap;
        }
        Bitmap returnBm = null;
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate (angle);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap (bitmap, 0, 0, bitmap.getWidth (), bitmap.getHeight (),
                    matrix, true);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        if (returnBm == null) {
            returnBm = bitmap;
        }
        if (bitmap != returnBm) {
            bitmap.recycle ();
        }
        return returnBm;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt (ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace ();
        }
        return degree;
    }
}
