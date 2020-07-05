package com.example.doveletter.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.os.Handler;
import android.os.Looper;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtils {
    private BitmapUtils() {
    }

    //可以直接将网络连接得到的输入流读取到字节流，压缩为bitmpa
    public static Bitmap decodeBitmapFromBytes(byte[] data, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    /*
     * 计算采样率
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * 裁剪
     *
     * @param bitmap 原图
     * @return 裁剪后的图像
     */
    public static Bitmap cropBitmap(Bitmap bitmap, float hRatioW) {
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();
        return Bitmap.createBitmap(bitmap, 0, 0, w, (int) (w * hRatioW), null, false);
    }

    /**
     * 按比例缩放图片
     *
     * @param origin 原图
     * @param ratio  比例
     * @return 新的bitmap
     */
    public static Bitmap scaleBitmap(Bitmap origin, float ratio) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(ratio, ratio);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        return newBM;
    }

    /*
     * 质量压缩法：将图片文件压缩,压缩是耗时操作
     */
    public static void compressFile(CompressFileBean compressFileBean, CompressFileCallback compressFileCallback) {
        new CompressFileThread(compressFileBean, compressFileCallback).start();
    }
    /*
     * 由file转bitmap
     */

    public static Bitmap decodeBitmapFromFilePath(String path, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//如此，无法decode bitmap
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;//如此，方可decode bitmap

        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * 根据路径 创建文件
     *
     * @param pathFile
     * @return
     * @throws IOException
     */
    private static File createFile(String pathFile) throws IOException {
        File fileDir = new File(pathFile.substring(0, pathFile.lastIndexOf(File.separator)));
        File file = new File(pathFile);
        if (!fileDir.exists()) fileDir.mkdirs();
        if (!file.exists()) file.createNewFile();
        return file;
    }
    /**
     * 读取图片的旋转的角度
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */

    public static int getBitmapDegree(String path) {

        int degree = 0;

        try {

            // 从指定路径下读取图片，并获取其EXIF信息

            ExifInterface exifInterface = new ExifInterface(path);

            // 获取图片的旋转信息

            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,

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

            e.printStackTrace();

        }

        return degree;

    }
    /**
     * 将图片按照某个角度进行旋转
     *
     * @param bm     需要旋转的图片
     * @param degree 旋转角度
     * @return 旋转后的图片
     */

    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {

        Bitmap returnBm = null;


        // 根据旋转角度，生成旋转矩阵

        Matrix matrix = new Matrix();

        matrix.postRotate(degree);

        try {

            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片

            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);

        } catch (OutOfMemoryError e) {

        }

        if (returnBm == null) {

            returnBm = bm;

        }

        if (bm != returnBm) {

            bm.recycle();

        }

        return returnBm;

    }
    /**
     * 对View进行量测，布局后截图
     *
     * @param view
     * @return
     */

    public static Object[] captureViewToBitmap(View view) {

        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));


        int width = view.getMeasuredWidth();
        int height = view.getMeasuredHeight();
        view.layout(0, 0, width, height);


        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        Object[] objects = new Object[3];
        objects[0] = bitmap;
        objects[1] = width;
        objects[2] = height;
        return objects;
    }

    /**
     * 对View进行量测，布局后截图
     *
     * @param view
     * @return
     */

    public static Bitmap captureViewToBitmap(View view, int reqWidth, int reqHeight) {

        view.layout(0, 0, reqWidth, reqHeight);

        Bitmap bitmap = Bitmap.createBitmap(reqWidth, reqHeight, Bitmap.Config.ARGB_4444);
//        CYLogUtils.log("bitmap原始width", bitmap.getWidth());
//        CYLogUtils.log("bitmap原始height", bitmap.getHeight());
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }
    /*
   压缩bitmap,会被压缩到指定宽高
    */
    public static Bitmap compressBitmap(Bitmap bitmap, int reqWidth, int reqHeight) {

        return Bitmap.createScaledBitmap(bitmap, reqWidth, reqHeight, true);
    }
    private static class CompressFileThread extends Thread {
        private Handler handler_deliver = new Handler(Looper.getMainLooper());
        private CompressFileBean compressFileBean;
        private CompressFileCallback compressFileCallback;

        public CompressFileThread(CompressFileBean compressFileBean, CompressFileCallback compressFileCallback) {
            this.compressFileBean = compressFileBean;
            this.compressFileCallback = compressFileCallback;
        }

        @Override
        public void run() {
            super.run();
            final Bitmap bitmap = decodeBitmapFromFilePath(compressFileBean.getPathSource(), compressFileBean.getReqWidth(), compressFileBean.getReqHeight());
            if(bitmap==null)
                return;

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int quality = 80;
            //压缩格式选取JPEG就行了，quality，压缩精度尽量不要低于50，否则影响清晰度
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);

            while (byteArrayOutputStream.toByteArray().length / 1024 > compressFileBean.getKb_max() && quality > compressFileBean.getQuality_max()) {
                // 循环判断如果压缩后图片是否大于kb_max kb,大于继续压缩,
                byteArrayOutputStream.reset();
                quality -= 10;
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
            }
            try {
                final File fileCompressed = createFile(compressFileBean.getPathCompressed());
                FileOutputStream fileOutputStream = new FileOutputStream(fileCompressed);
                fileOutputStream.write(byteArrayOutputStream.toByteArray());//写入目标文件
                fileOutputStream.flush();
                fileOutputStream.close();
                byteArrayOutputStream.close();
                if (fileCompressed != null && fileCompressed.length() > 0)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //压缩成功
                            compressFileCallback.onCompressFileFinished(fileCompressed, bitmap);
                        }
                    });
            } catch (final Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //压缩失败
                        compressFileCallback.onCompressFileFailed("压缩图片文件失败" + e.getMessage());
                    }
                });
            }

        }

        private void runOnUiThread(Runnable run) {
            handler_deliver.post(run);
        }

    }

    public static class CompressFileBean {
        private String pathSource;//原图文件路径
        private String pathCompressed;//压缩后的图片文件路径
        private int kb_max = 1000;//压缩到多少KB，不能精确，只能<=kb_max
        private int quality_max = 80;//压缩精度，尽量>=50
        private int reqWidth = 1000;//期望的图片宽度
        private int reqHeight = 1000;//期望的图片高度

        private CompressFileBean(Builder builder) {
            this.pathSource = builder.getFileSource();
            this.pathCompressed = builder.getFileCompressed();
            this.kb_max = builder.getKb_max();
            this.quality_max = builder.getQuality_max();
            this.reqWidth = builder.getReqWidth();
            this.reqHeight = builder.getReqHeight();
        }

        public static class Builder {
            private String pathSource;//原图文件路径
            private String pathCompressed;//压缩后的图片文件路径
            private int kb_max = 1000;//压缩到多少KB，不能精确，只能<=kb_max
            private int quality_max = 80;//压缩精度，尽量>=50
            private int reqWidth = 1000;//期望的图片宽度
            private int reqHeight = 1000;//期望的图片高度

            public String getFileSource() {
                return pathSource;
            }

            public Builder setFileSource(String pathSource) {
                this.pathSource = pathSource;
                return this;
            }

            public String getFileCompressed() {
                return pathCompressed;
            }

            public Builder setFileCompressed(String pathCompressed) {
                this.pathCompressed = pathCompressed;
                return this;
            }

            public int getKb_max() {
                return kb_max;
            }

            public Builder setKb_max(int kb_max) {
                this.kb_max = kb_max;
                return this;
            }

            public int getQuality_max() {
                return quality_max;
            }

            public Builder setQuality_max(int quality_max) {
                this.quality_max = quality_max;
                return this;
            }

            public int getReqWidth() {
                return reqWidth;
            }

            public Builder setReqWidth(int reqWidth) {
                this.reqWidth = reqWidth;
                return this;
            }

            public int getReqHeight() {
                return reqHeight;
            }

            public Builder setReqHeight(int reqHeight) {
                this.reqHeight = reqHeight;
                return this;
            }

            public CompressFileBean build() {
                return new CompressFileBean(this);
            }
        }

        public String getPathSource() {
            return pathSource;
        }

        public String getPathCompressed() {
            return pathCompressed;
        }

        public int getKb_max() {
            return kb_max;
        }

        public int getQuality_max() {
            return quality_max;
        }

        public int getReqWidth() {
            return reqWidth;
        }

        public int getReqHeight() {
            return reqHeight;
        }
    }

    public static interface CompressFileCallback {
        //图片压缩成功
        public void onCompressFileFinished(File file, Bitmap bitmap);
        //图片压缩失败
        public void onCompressFileFailed(String errorMsg);
    }

    //高斯模糊
    public static Bitmap blurBitmap(Context context, Bitmap image, float blurRadius) {
        final double BITMAP_SCALE=0.8;

        // 计算图片缩小后的长宽
        int width =(int)Math.round(image.getWidth() * BITMAP_SCALE);
        int height =(int)Math.round(image.getHeight() * BITMAP_SCALE);

        // 将缩小后的图片做为预渲染的图片
        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        // 创建一张渲染后的输出图片
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        // 创建RenderScript内核对象
        RenderScript rs = RenderScript.create(context);
        // 创建一个模糊效果的RenderScript的工具对象
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        // 由于RenderScript并没有使用VM来分配内存,所以需要使用Allocation类来创建和分配内存空间
        // 创建Allocation对象的时候其实内存是空的,需要使用copyTo()将数据填充进去
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);

        // 设置渲染的模糊程度, 25f是最大模糊度
        blurScript.setRadius(blurRadius);
        // 设置blurScript对象的输入内存
        blurScript.setInput(tmpIn);
        // 将输出数据保存到输出内存中
        blurScript.forEach(tmpOut);

        // 将数据填充到Allocation中
        tmpOut.copyTo(outputBitmap);

        return outputBitmap;
    }

    //生成圆形bitmap
    public static Bitmap makeRoundCorner(Bitmap bitmap)
    {
       /* int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int left = 0, top = 0, right = width, bottom = height;
        float roundPx = height/2;
        if (width > height) {
            left = (width - height)/2;
            top = 0;
            right = left + height;
            bottom = height;
        } else if (height > width) {
            left = 0;
            top = (height - width)/2;
            right = width;
            bottom = top + width;
            roundPx = width/2;
        }
        //ZLog.i(TAG, "ps:"+ left +", "+ top +", "+ right +", "+ bottom);

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        int color = 0xff424242;
        Paint paint = new Paint();
        Rect rect = new Rect(left, top, right, bottom);
        RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;*/
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        paint.setColor(Color.parseColor("#FFFFFF"));
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawOval(rectF, paint);
        canvas.drawBitmap(bitmap,0,0,paint);

        return output;
    }

    public static Bitmap drawBg4Bitmap(int color, Bitmap orginBitmap) {
        Paint paint = new Paint();
        paint.setColor(color);
        Bitmap bitmap = Bitmap.createBitmap(orginBitmap.getWidth(),
                orginBitmap.getHeight(), orginBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(0, 0, orginBitmap.getWidth(), orginBitmap.getHeight(), paint);
        canvas.drawBitmap(orginBitmap, 0, 0, paint);
        return bitmap;
    }

    //安卓高版本上传图片旋转问题
    //获取图片的旋转角度
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
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
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转图片
     *
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }


}
