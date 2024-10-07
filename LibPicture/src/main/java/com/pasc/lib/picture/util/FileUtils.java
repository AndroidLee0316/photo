//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.pasc.lib.picture.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Environment;
import android.os.Build.VERSION;
import android.support.v4.content.FileProvider;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtils {
    private static final String TAG = "FileUtils";
    private static final int BUFF_SIZE = 2048;
    private static final String APP_FOLDER_PATH = "smt/";
    private static final String APP_IMG_FOLDER_PATH = "img/";
    private static String FILE_NAME = "userIcon.jpg";
    public static String PATH_PHOTOGRAPH = "/smt/";

    public FileUtils() {
    }

    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals("mounted");
        if(sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        }

        return sdDir.toString();
    }

    public static String getRootFolderPath() {
        return getSDPath() + "/" + "smt/";
    }

    public static String getImgFolderPath() {
        String path = getSDPath() + "/" + "smt/" + "img/";
        File file = new File(path);
        if(!file.exists()) {
            file.mkdirs();
        }

        return path;
    }

    public static String getRandomFileName() {
        Date todate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        return sdf.format(todate);
    }

    public static String getRandomImgPath() {
        return getImgFolderPath() + getRandomFileName() + ".jpeg";
    }

    public static void createDirs(String path) {
        File file = new File(path);
        if(!file.exists()) {
            file.mkdirs();
        }

    }

    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return "mounted".equals(state);
    }

    public static void saveBitmap(Bitmap bitmap, String filePath) {
        FileOutputStream bos = null;

        try {
            File file = new File(filePath);
            if(!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            bos = new FileOutputStream(file);
            bitmap.compress(CompressFormat.PNG, 100, bos);
        } catch (FileNotFoundException var12) {
            var12.printStackTrace();
        } finally {
            try {
                bos.flush();
                bos.close();
            } catch (IOException var11) {
                var11.printStackTrace();
            }

        }

    }

    public static File getDCIMFile(String filePath, String imageName) {
        if(Environment.getExternalStorageState().equals("mounted")) {
            File dirs = new File(Environment.getExternalStorageDirectory(), "DCIM" + filePath);
            if(!dirs.exists()) {
                dirs.mkdirs();
            }

            File file = new File(Environment.getExternalStorageDirectory(), "DCIM" + filePath + imageName);
            if(!file.exists()) {
                try {
                    file.createNewFile();
                } catch (Exception var5) {
                    ;
                }
            }

            return file;
        } else {
            return null;
        }
    }

    public static File saveBitmap2(Bitmap bitmap, String fileName, File baseFile) {
        FileOutputStream bos = null;
        File imgFile = new File(baseFile, "/" + fileName);

        try {
            bos = new FileOutputStream(imgFile);
            bitmap.compress(CompressFormat.PNG, 100, bos);
        } catch (FileNotFoundException var14) {
            var14.printStackTrace();
        } finally {
            try {
                bos.flush();
                bos.close();
            } catch (IOException var13) {
                var13.printStackTrace();
            }

        }

        return imgFile;
    }

    public static File getBaseFile(String filePath) {
        if(Environment.getExternalStorageState().equals("mounted")) {
            File f = new File(Environment.getExternalStorageDirectory(), filePath);
            if(!f.exists()) {
                f.mkdirs();
            }

            return f;
        } else {
            return null;
        }
    }

    public static String getFileName() {
        String fileName = FILE_NAME;
        return fileName;
    }

    public static File createFile(String path, String name) throws IOException {
        File folder = new File(path);
        if(!folder.exists()) {
            folder.mkdirs();
        }

        File file = new File(path + name);
        if(!file.exists()) {
            file.createNewFile();
        }

        return file;
    }

    public static Uri getUri(Activity activity, File file) {
        Uri uri;
        if(VERSION.SDK_INT >= 23) {
            uri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileprovider", file);
        } else {
            uri = Uri.fromFile(file);
        }

        return uri;
    }

    public static boolean zipFiles(File[] fs, String zipFilePath) {
        if(fs == null) {
            throw new NullPointerException("fs == null");
        } else {
            boolean result = false;
            ZipOutputStream zos = null;

            try {
                zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFilePath)));
                File[] var4 = fs;
                int var5 = fs.length;

                for(int var6 = 0; var6 < var5; ++var6) {
                    File file = var4[var6];
                    if(file != null && file.exists()) {
                        if(file.isDirectory()) {
                            recursionZip(zos, file, file.getName() + File.separator);
                        } else {
                            recursionZip(zos, file, "");
                        }
                    }
                }

                result = true;
                zos.flush();
            } catch (Exception var16) {
                var16.printStackTrace();
            } finally {
                try {
                    if(zos != null) {
                        zos.closeEntry();
                        zos.close();
                    }
                } catch (IOException var15) {
                    var15.printStackTrace();
                }

            }

            return result;
        }
    }

    private static void recursionZip(ZipOutputStream zipOut, File file, String baseDir) throws Exception {
        int len;
        if(file.isDirectory()) {
            File[] files = file.listFiles();
            File[] var4 = files;
            len = files.length;

            for(int var6 = 0; var6 < len; ++var6) {
                File fileSec = var4[var6];
                if(fileSec != null) {
                    if(fileSec.isDirectory()) {
                        baseDir = file.getName() + File.separator + fileSec.getName() + File.separator;
                        recursionZip(zipOut, fileSec, baseDir);
                    } else {
                        recursionZip(zipOut, fileSec, baseDir);
                    }
                }
            }
        } else {
            byte[] buf = new byte[2048];
            InputStream input = new BufferedInputStream(new FileInputStream(file));
            zipOut.putNextEntry(new ZipEntry(baseDir + file.getName()));

            while((len = input.read(buf)) != -1) {
                zipOut.write(buf, 0, len);
            }

            input.close();
        }

    }

    public static String getDataSize(long var0) {
        DecimalFormat var2 = new DecimalFormat("###.00");
        return var0 < 1024L?var0 + "bytes":(var0 < 1048576L?var2.format((double)((float)var0 / 1024.0F)) + "KB":(var0 < 1073741824L?var2.format((double)((float)var0 / 1024.0F / 1024.0F)) + "MB":(var0 < 0L?var2.format((double)((float)var0 / 1024.0F / 1024.0F / 1024.0F)) + "GB":"error")));
    }



    public static void deleteAllFiles(File root) {
        File[] files = root.listFiles();
        if(files != null) {
            File[] var2 = files;
            int var3 = files.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                File f = var2[var4];
                if(f.isDirectory()) {
                    deleteAllFiles(f);
                } else if(f.exists()) {
                    try {
                        f.delete();
                    } catch (Exception var7) {
                        ;
                    }
                }
            }
        }

    }

    public static void deleteAllFilesAndDirectory(File root) {
        File[] files = root.listFiles();
        if(files != null) {
            File[] var2 = files;
            int var3 = files.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                File f = var2[var4];
                if(f.isDirectory()) {
                    deleteAllFiles(f);

                    try {
                        f.delete();
                    } catch (Exception var8) {
                        ;
                    }
                } else if(f.exists()) {
                    try {
                        f.delete();
                    } catch (Exception var7) {
                        ;
                    }
                }
            }
        }

    }

    public static String getAppDir(Context context, String specDir) {
        String dir = null;
        File file = context.getExternalFilesDir(specDir);
        if(file != null) {
            dir = file.getAbsolutePath();
        } else {
            dir = (new File(context.getCacheDir(), specDir)).getAbsolutePath();
        }

        return dir;
    }

    public interface OnHttpHeaderListener {
        void httpHeader(String var1);
    }
}
