//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.pasc.lib.picture.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.util.Base64;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BitmapUtils {
    private static final int CV_ROTATE_90_CLOCKWISE = 0;
    private static final int CV_ROTATE_180 = 1;
    private static final int CV_ROTATE_90_COUNTERCLOCKWISE = 2;
    public static final int CV_ROTATE_360 = 3;
    private static final String TAG = BitmapUtils.class.getSimpleName();

    public BitmapUtils() {
    }

    public static Bitmap revitionImageSize(String path) throws IOException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(path)));
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(in, (Rect)null, options);
        in.close();
        Bitmap bitmap = null;
        int wRatio = (int)Math.ceil((double)((float)options.outWidth / 720.0F));
        int hRatio = (int)Math.ceil((double)((float)options.outHeight / 1280.0F));
        if(wRatio > 1 && hRatio > 1) {
            if(wRatio > hRatio) {
                options.inSampleSize = wRatio;
            } else {
                options.inSampleSize = hRatio;
            }
        }

        in = new BufferedInputStream(new FileInputStream(new File(path)));
        options.inJustDecodeBounds = false;

        try {
            bitmap = BitmapFactory.decodeStream(in, (Rect)null, options);
            return bitmap;
        } catch (Exception var10) {
            var10.printStackTrace();
            wRatio = (int)Math.ceil((double)((float)options.outWidth / 480.0F));
            hRatio = (int)Math.ceil((double)((float)options.outHeight / 800.0F));
            if(wRatio > 1 && hRatio > 1) {
                if(wRatio > hRatio) {
                    options.inSampleSize = wRatio;
                } else {
                    options.inSampleSize = hRatio;
                }
            }

            in = new BufferedInputStream(new FileInputStream(new File(path)));
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeStream(in, (Rect)null, options);
            int degree = readPictureDegree(path);
            if(degree <= 0) {
                return bitmap;
            } else {
                Matrix matrix = new Matrix();
                matrix.postRotate((float)degree);
                Bitmap rotaBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                return rotaBitmap;
            }
        }
    }

    public static int readPictureDegree(String path) {
        short degree = 0;

        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt("Orientation", 1);
            switch(orientation) {
            case 3:
                degree = 180;
                break;
            case 6:
                degree = 90;
                break;
            case 8:
                degree = 270;
            }
        } catch (IOException var4) {
            var4.printStackTrace();
        }

        return degree;
    }

    public static String saveBitmap2File(Bitmap bitmap, String filePath) {
        File targetFile = new File(filePath);
        if(bitmap == null) {
            return "";
        } else {
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(targetFile));
                bitmap.compress(CompressFormat.JPEG, 70, bos);

                try {
                    bos.flush();
                    bos.close();
                } catch (IOException var5) {
                    var5.printStackTrace();
                }
            } catch (FileNotFoundException var6) {
                var6.printStackTrace();
            }

            return filePath;
        }
    }

    public static Bitmap compress(Bitmap bitmap, int toBmpSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();

        double i;
        for(double mid = (double)(b.length / 1024); mid > (double)toBmpSize; bitmap = zoomImage(bitmap, (double)bitmap.getWidth() / Math.sqrt(i), (double)bitmap.getHeight() / Math.sqrt(i))) {
            i = mid / (double)toBmpSize;
        }

        return bitmap;
    }

    public static Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
        float width = (float)bgimage.getWidth();
        float height = (float)bgimage.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = (float)newWidth / width;
        float scaleHeight = (float)newHeight / height;
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int)width, (int)height, matrix, true);
        return bitmap;
    }

    public static Bitmap getURLImage(String url) {
        Bitmap bmp = null;

        try {
            URL myurl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)myurl.openConnection();
            conn.setConnectTimeout(6000);
            conn.setDoInput(true);
            conn.setUseCaches(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return bmp;
    }

    public static boolean compressPhoto(String fileSrc, String fileDst, int targetW, int targetH) {
        Options bmOptions = new Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileSrc, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        int angle = readPictureDegree(fileSrc);
        Bitmap bitmap = rotaingImageView(angle, BitmapFactory.decodeFile(fileSrc, bmOptions));
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if(bitmap != null){
            bitmap.compress(CompressFormat.JPEG, 100, stream);

        }
        try {
            File file = new File(fileDst);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(stream.toByteArray());
            return true;
        } catch (FileNotFoundException var13) {
            var13.printStackTrace();
        } catch (IOException var14) {
            var14.printStackTrace();
        }
        return false;
    }

    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        if(angle == 0) {
            return bitmap;
        } else {
            Bitmap returnBm = null;
            Matrix matrix = new Matrix();
            matrix.postRotate((float)angle);

            try {
                returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            } catch (OutOfMemoryError var5) {
                ;
            }

            if(returnBm == null) {
                returnBm = bitmap;
            }

            if(bitmap != returnBm) {
                bitmap.recycle();
            }

            return returnBm;
        }
    }

    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap drawableToBitamp(Drawable drawable) {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        @SuppressLint("WrongConstant") Config config = drawable.getOpacity() != -1?Config.ARGB_8888:Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    public static String BitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, 0);
        return encodedImage;
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public static Bitmap getBitmap(int imageWidth, int imageHeight, byte[] frame, int ori) {
        Bitmap bitmap = null;

        try {
            YuvImage image = new YuvImage(frame, 17, imageWidth, imageHeight, (int[])null);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compressToJpeg(new Rect(0, 0, imageWidth, imageHeight), 100, stream);
            bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
            stream.close();
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        float rotate;
        if(ori == 0) {
            rotate = 90.0F;
        } else if(ori == 2) {
            rotate = 270.0F;
        } else if(ori == 1) {
            rotate = 180.0F;
        } else {
            rotate = 360.0F;
        }

        if(bitmap != null) {
            bitmap = rotateBitmap(bitmap, rotate);
        }

        return bitmap;
    }

    public static Bitmap rotateBitmap(Bitmap origin, float alpha) {
        if(origin == null) {
            return null;
        } else {
            int width = origin.getWidth();
            int height = origin.getHeight();
            Matrix matrix = new Matrix();
            matrix.setRotate(alpha);
            Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
            if(newBM.equals(origin)) {
                return newBM;
            } else {
                origin.recycle();
                return newBM;
            }
        }
    }
}
