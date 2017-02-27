package com.example.achuan.teammanagement.util;

import android.content.Context;
import android.os.Environment;

import com.example.achuan.teammanagement.app.App;

import java.io.File;

/**
 * Created by achuan on 16-11-18.
 * 功能：文件操作工具类
 */
public class FileUtil {

    //删除某个目录下的所有文件
    public static void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
    }
    //获取磁盘缓存地址的方法,并设定文件夹名称
    public static File getDiskCacheDir(String uniqueName) {
        //获得整个应用的context
        Context context= App.getInstance().getContext();
        //缓存路径
        final String cachePath;
        /*当SD卡存在或者SD卡不可被移除的时候，就调用getExternalCacheDir()方法来获取缓存路径，
        否则就调用getCacheDir()方法来获取缓存路径。前者获取到的就是 /sdcard/Android/data/
        <application package>/cache 这个路径，而后者获取到的是 /data/data/<application
        package>/cache 这个路径*/
        boolean externalStorageAvailable = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable();
        if (externalStorageAvailable) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        //内存路径+/（分隔符）+子文件夹名称
        return new File(cachePath + File.separator + uniqueName);
    }


}
