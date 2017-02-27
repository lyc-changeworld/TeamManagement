package com.example.achuan.teammanagement.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.achuan.teammanagement.app.App;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;

/**
 * Created by achuan on 16-10-19.
 * 功能：实现图片的优化加载功能
 * */
public class ImageUtil {

    /***转换图片成圆形***/
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            left = 0;
            top = 0;
            right = width;
            bottom = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);
        paint.setAntiAlias(true);// 设置画笔无锯齿
        canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas
        paint.setColor(color);
        // 以下有两种方法画圆,drawRounRect和drawCircle
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
        canvas.drawCircle(roundPx, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
        canvas.drawBitmap(bitmap, src, dst, paint); //以Mode.SRC_IN模式合并bitmap和已经draw了的Circle
        return output;
    }

    /*裁剪图片的方法*/
    public static void cropImageFromUri(Context context,Uri uriFrom, Uri uriTo, int aspectX, int aspectY, int outputX,
                                 int outputY, int requestCode){
        Intent intent = new Intent("com.android.camera.action.CROP");//拍照成功后添加意图为:裁剪
        //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);//允许缩放
        /***下面为个性化参数设置语句***/
        intent.setDataAndType(uriFrom, "image/*");//设置传送的图片源和类型
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriTo);//设置裁剪后图片的输出地址
        intent.putExtra("aspectX", aspectX); // 宽高比例(1:1)
        intent.putExtra("aspectY", aspectY);
        intent.putExtra("outputX", outputX);//宽高(像素)
        intent.putExtra("outputY", outputY);
        Activity activity=(Activity)context;
        activity.startActivityForResult(intent, requestCode);//根据意图,启动对应的裁剪程序
    }

    /*通过Uri加载获取图片资源*/
    public static Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(App.getInstance().getContentResolver()
                    .openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    /*通过file路径采样加载图片*/
    public static Bitmap decodeSampledBitmapFromFile(String file_path,
                                                     int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file_path, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file_path, options);
    }
    /*通过获取工程图片目录中的resources并采样处理*/
    public static Bitmap decodeSampledBitmapFromResource(Resources res,
            int resId, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //上面的这个参数设置为true后,只会解析图片的原始高/宽信息,并不会去真正地加载图片
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize(采样率)
        //inSampleSize的取值应该总是为2的指数,比如1 2 4 8 16等,该值越大说明图片压缩程度越高
        //内存缩放比例为 1/(inSampleSize的2次方)
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // 根据上面计算出来的采样率重新加载图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**/
    public static Bitmap decodeSampledBitmapFromFileDescriptor(FileDescriptor fd, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions（尺寸）
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        //通过文件符来创建流并加载图片资源（消除不同开发平台下图片加载机制不同造成的问题）
        return BitmapFactory.decodeFileDescriptor(fd, null, options);
    }

    /*根据图片的大小和显示的大小计算采样系数*/
    public static int calculateInSampleSize(BitmapFactory.Options options,
            int reqWidth, int reqHeight) {
        if (reqWidth == 0 || reqHeight == 0) {
            return 1;
        }

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        //Log.d(TAG, "origin, w= " + width + " h=" + height);
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        //Log.d(TAG, "sampleSize:" + inSampleSize);
        return inSampleSize;
    }
}
