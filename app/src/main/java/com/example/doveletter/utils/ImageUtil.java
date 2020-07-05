package com.example.doveletter.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ImageUtil {
    /**通过图片url生成Drawable对象
     * @param urlpath
     * @return Bitmap
     * 根据url获取布局背景的对象
     */
    public static Drawable getDrawableFromUrl(String urlpath){
        Drawable drawable = null;
        try {
            URL url = new URL(urlpath);
            URLConnection conn = url.openConnection();
            conn.connect();
            InputStream in;
            in = conn.getInputStream();
            drawable = Drawable.createFromStream(in, "background.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return drawable;
    }

    //从网络地址获取Bitmap格式图片
    public static Bitmap getBitmapFromUrl(String imgUrl) {
        InputStream inputStream=null;
        ByteArrayOutputStream outputStream=null;
        URL url = null;
        try {
            url=new URL(imgUrl);
            HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setReadTimeout(2000);
            httpURLConnection.connect();
            if(httpURLConnection.getResponseCode()==200) {
                //网络连接成功
                inputStream = httpURLConnection.getInputStream();
                outputStream = new ByteArrayOutputStream();
                byte buffer[] = new byte[1024 * 8];
                int len = -1;
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
                byte[] bu = outputStream.toByteArray();
                Bitmap bitmap = BitmapFactory.decodeByteArray(bu, 0, bu.length);
                return bitmap;
            }else {
                //Log.(TAG,"网络连接失败----"+httpURLConnection.getResponseCode())
            }
        } catch (Exception e) {
            // TODO: handle exception
        }finally{
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if(outputStream!=null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static Bitmap getBitmapFromUrl(String imgUrl, int messageType, Handler handler) {
        InputStream inputStream=null;
        ByteArrayOutputStream outputStream=null;
        URL url = null;
        try {
            url=new URL(imgUrl);
            HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setReadTimeout(2000);
            httpURLConnection.connect();
            if(httpURLConnection.getResponseCode()==200) {
                //网络连接成功
                inputStream = httpURLConnection.getInputStream();
                outputStream = new ByteArrayOutputStream();
                byte buffer[] = new byte[1024 * 8];
                int len = -1;
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
                byte[] bu = outputStream.toByteArray();
                Bitmap bitmap = BitmapFactory.decodeByteArray(bu, 0, bu.length);

                Message message=handler.obtainMessage();
                message.what=messageType;
                message.obj=bitmap;
                handler.sendMessage(message);
                return bitmap;
            }else {
                //Log.(TAG,"网络连接失败----"+httpURLConnection.getResponseCode())
            }
        } catch (Exception e) {
            // TODO: handle exception
        }finally{
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if(outputStream!=null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;
    }




    //将Bitmap保存到本地
    public static String saveImageToGallery(Bitmap bmp,Context mContext,String fn) {
        if (bmp==null){
            Log.d("WANGYU","bmp==null");
            return "";
        }
        String galleryPath= Environment.getExternalStorageDirectory()
                + File.separator;

        String dirPath=galleryPath+"htimg";
        File dir=new File(dirPath);
        if(!dir.exists())
            dir.mkdir();

        String fileName =fn + ".jpg";
        File file = new File(galleryPath, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.flush();
            fos.close();
            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(file);
            mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            if (isSuccess) {
                //Log.e(TAG,"图片保存成功 保存在:" + file.getPath()));
            } else {
                //Log.e(TAG,"图片保存失败"));
            }
        } catch (IOException e) {
            //Log.e(TAG,"保存图片找不到文件夹");
            e.printStackTrace();
        }

        return file.getPath();
    }

    //将网络url图片保存成本地图片
    public static String saveImageFromUrl(Context mContext,String imgUrl,String filename)
    {
        Bitmap bmp=returnBitMap(imgUrl);
        String filePath=saveImageToGallery(bmp,mContext,filename);
        return filePath;
    }

    //从本地路径获取Bitmap
    public static Bitmap getBitmapFromFile(String imgPath)
    {
        try
        {
            FileInputStream fs = new FileInputStream(imgPath);
            Bitmap bitmap  = BitmapFactory.decodeStream(fs);
            return bitmap;
        }
        catch (Exception e)
        {
            return null;
        }
    }


    public static Bitmap returnBitMap(final String url){
        Bitmap bitmap=null;

        URL imageurl = null;

        try {
            imageurl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection)imageurl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }


    /**
     * 获取和保存当前屏幕的截图
     * @param relativePath /AndyDemo/ScreenImage
     * @param fileName Screen_1.png
     */
    public static String GetandSaveScreenImage(Activity activity,String relativePath,String fileName)
    {
        //1.构建Bitmap
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int w = display.getWidth();
        int h = display.getHeight();

        Bitmap Bmp = Bitmap.createBitmap( w, h, Bitmap.Config.ARGB_8888 );

        //2.获取屏幕
        View decorview = activity.getWindow().getDecorView();
        decorview.setDrawingCacheEnabled(true);
        Bmp = decorview.getDrawingCache();

        String SavePath = getSDCardPath()+relativePath;
        File dir=new File(SavePath);
        if(!dir.exists())
            dir.mkdir();

        //3.保存Bitmap
        try {
            File path = new File(SavePath);
            //文件
            String filepath = SavePath +"/"+ fileName;
            File file = new File(filepath);
            if(!path.exists()){
                path.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fos = null;
            fos = new FileOutputStream(file);
            if (null != fos) {
                Bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();

                //Log.d("WANGYU","截图文件已保存，路径："+filepath);

                return filepath;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        return "";
    }

    /**
     * 获取SDCard的目录路径功能
     * @return
     */
    private static String getSDCardPath(){
        File sdcardDir = null;
        //判断SDCard是否存在
        boolean sdcardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if(sdcardExist){
            sdcardDir = Environment.getExternalStorageDirectory();
        }
        return sdcardDir.toString();
    }

    //获取屏幕截图，返回Bitmap
    public static Bitmap GetScreenShotBitmap(Activity activity) {
        //1.构建Bitmap
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int w = display.getWidth();
        int h = display.getHeight();

        Bitmap Bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        //2.获取屏幕
        View decorview = activity.getWindow().getDecorView();
        decorview.setDrawingCacheEnabled(true);
        Bmp = decorview.getDrawingCache();

        return Bmp;
    }

    public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix,true);
    }

}
