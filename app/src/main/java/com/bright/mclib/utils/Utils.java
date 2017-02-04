package com.bright.mclib.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.TextPaint;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tci_mi on 16/9/2.
 */
public class Utils {

    private static long lastClickTime;

    public static void canShowComplete(final Context context,
                                       final TextView textView) {
        if (textView == null) {
            return;
        }

        textView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                TextPaint paint = textView.getPaint();
                float textWidth = Layout.getDesiredWidth(textView.getText(),
                        paint);
                int textViewWidth = textView.getMeasuredWidth();

                if (textWidth > textViewWidth) {
                    Toast toast = Toast.makeText(context, textView.getText(),
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    public static int getStatusHeight(Activity activity) {
        int statusHeight = 0;
        Rect localRect = new Rect();
        activity.getWindow().getDecorView()
                .getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass
                        .getField("status_bar_height").get(localObject)
                        .toString());
                statusHeight = activity.getResources()
                        .getDimensionPixelSize(i5);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

    /**
     * EditText是否为空的判断
     */
    public static boolean isViewEmpty(TextView view) {

        return isStringEmpty(strFromView(view));
    }

    /**
     * 从TextView或者EditText组件中获得内容
     */
    public static String strFromView(View view) {
        String strText = "";
        if (null != view) {
            if ((view instanceof TextView)) {
                strText = ((TextView) view).getText().toString().trim();
            } else if (view instanceof EditText) {
                strText = ((EditText) view).getText().toString().trim();
            }
        }
        return strText;
    }

    /**
     * 两个string 是否相等
     */
    public static boolean isEqual(String str1, String str2) {
        if (null == str2) {
            return false;
        }
        return str1.equals(str2);
    }

    public static boolean isViewEmpty(View view) {
        if (view == null) {
            return true;
        }
        String strText = "";
        if ((view instanceof TextView)) {
            strText = ((TextView) view).getText().toString().trim();
        } else if (view instanceof EditText) {
            strText = ((EditText) view).getText().toString().trim();
        }
        return isStringEmpty(strText);
    }

    /**
     * String 是否为空判断
     */
    public static boolean isStringEmpty(String str) {

        return null == str || "".equals(str);
    }

    /**
     * bitmap To base64
     *
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream byteArrayoutStream = null;
        try {
            if (bitmap != null) {
                byteArrayoutStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                        byteArrayoutStream);

                byteArrayoutStream.flush();
                byteArrayoutStream.close();

                byte[] bitmapBytes = byteArrayoutStream.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);

            }
        } catch (IOException e) {
        } finally {
            try {
                if (byteArrayoutStream != null) {
                    byteArrayoutStream.flush();
                    byteArrayoutStream.close();
                }
            } catch (IOException e) {
            }
        }
        return result;
    }

    /**
     * base64 To bitmap
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        Bitmap bitmap = null;
        try {
            byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (OutOfMemoryError e) {
        } catch (IllegalArgumentException e) {
        }

        return bitmap;
    }

    public static Bitmap stringToBitmap(String data) {
        Bitmap bitmap = null;
        try {
            byte[] bytes = data.getBytes("UTF-8");
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (OutOfMemoryError e) {
        } catch (IllegalArgumentException e) {
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    /**
     * UTF-8格式化
     */
    public static String strURLEncoder(String strValue) {
        String newValue = "";
        try {
            newValue = URLEncoder.encode(strValue, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        return newValue;
    }

    /**
     * drawable 对象转为 bitmap
     *
     * @param drawable
     * @return　Bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
                .getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
                : Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 从文件路径下取得图片
     *
     * @param filepath
     * @return bitmap
     */
    public static Bitmap getImageFromSDCard(String filepath) {
        File file = new File(filepath);
        if (file.exists()) {
            Bitmap bm = BitmapFactory.decodeFile(filepath);
            return bm;
        }
        return null;
    }

    /**
     * format expires date
     *
     * @param strExpires
     * @return
     */
    @SuppressWarnings("deprecation")
    public static String formatExpires(String strExpires) {
        Date date = new Date(strExpires);
        return String.valueOf(date.getTime());
    }

    /**
     * 判断数组是否越界
     *
     * @param mCookieValues
     * @return
     */
    public static boolean isArrayIndexOutOfBounds(String[] mCookieValues) {
        return mCookieValues.length > 1;
    }

    /**
     * 日期格式化
     *
     * @param strNoticeDate
     * @param oldFormat
     * @param newFormat
     * @return
     */

    public static String formatDate(String strNoticeDate, String oldFormat,
                                    String newFormat) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(oldFormat);
            SimpleDateFormat sdf2 = new SimpleDateFormat(newFormat);
            strNoticeDate = sdf2.format(sdf.parse(strNoticeDate));
        } catch (ParseException e) {
        }
        return strNoticeDate;
    }

    /**
     * 快速连击
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }


    /**
     * 判断指定的文件名是否存在
     */
    public static boolean fileIsExists(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static Drawable bitmapToDrawable(Resources resources, Bitmap bitmap) {
        BitmapDrawable bitmapDrawable = new BitmapDrawable(resources, bitmap);
        Drawable drawable = bitmapDrawable.getCurrent();
        return drawable;
    }

    // use for camera
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static Point getScreenMetrics(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        int h_screen = dm.heightPixels;
        return new Point(w_screen, h_screen);
    }

    public static float getScreenRate(Context context) {
        Point P = getScreenMetrics(context);
        float H = P.y;
        float W = P.x;
        return (H / W);
    }

    public static Bitmap getRotateBitmap(Bitmap b, float rotateDegree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotateDegree);
        Bitmap rotaBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
                b.getHeight(), matrix, false);
        return rotaBitmap;
    }

    // use for camera
    public static byte[] compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (baos.toByteArray().length > 1024 * 400) {
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 1;
        }
        return baos.toByteArray();
    }

    /**
     * bytes to bitmap
     */
    public static Bitmap bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
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
                default:
                    degree = 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Log.e("旋转角度", "angle=" + angle);
        if (angle > 0) {

            // 创建新的图片
            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            return resizedBitmap;
        }
        return bitmap;
    }


    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    // bgimg0 = getImageFromAssetsFile("Cat_Blink/cat_blink0000.png");

    /**
     * 从Assets中读取图片
     */
    @SuppressWarnings("deprecation")
    public static Bitmap getImageFromAssetsFile(Context context, String fileName) {
        Bitmap image = null;
        InputStream is = null;
        AssetManager am = context.getResources().getAssets();
        try {
            is = am.open(fileName);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            // options.inSampleSize = 4;
            options.inPreferredConfig = Config.RGB_565;
            options.inPurgeable = true;
            options.inInputShareable = true;
            image = BitmapFactory.decodeStream(is, null, options);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {

        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return image;

    }

    @SuppressWarnings("deprecation")
    public static Bitmap compressImageFromAsset(Context context,
                                                String srcPath, int reqWidth, int reqHeight) {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {
            @SuppressWarnings("unused")
            int maxNumOfPixels = 1280 * 1280;
            int be = 1;
            InputStream is = am.open(srcPath);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;// 只读边,不读内容
            // options.inSampleSize = 4;
            image = BitmapFactory.decodeStream(is, null, options);
            // CCLog.e("图片原始宽:" + image.getWidth() + "  图片原始高:"
            // + image.getHeight() + " 图片原始大小：" + image.getByteCount());
            is.close();
            // be = computeSampleSize(options, -1, maxNumOfPixels);
            int w = options.outWidth;
            int h = options.outHeight;
            float hh = 800f;//
            float ww = 480f;//
            if (w > h && w > ww) {
                be = (int) (options.outWidth / ww);
            } else if (w < h && h > hh) {
                be = (int) (options.outHeight / hh);
            }
            options.inSampleSize = be;
            options.inJustDecodeBounds = false;

            // int width = options.outWidth;
            // int height = options.outHeight;
            CCLog.e("图片宽:" + h + "  图片高:" + w);
            CCLog.e("采样率:" + be + "");

            // if (height > reqHeight || width > reqWidth) {
            // final int heightRatio = Math.round((float) height
            // / (float) reqHeight);
            // final int widthRatio = Math.round((float) width
            // / (float) reqWidth);
            // be = heightRatio < widthRatio ? heightRatio : widthRatio;
            // }
            // float reqHeight = 800f;//
            // float reqWidth = 480f;//

            // if (width > height && width > reqWidth) {
            // be = (int) (options.outWidth / reqWidth);
            // } else if (width < height && height > reqHeight) {
            // be = (int) (options.outHeight / reqHeight);
            // }
            // if (be <= 0)
            // be = 1;
            // options.inSampleSize = be;// 设置采样率

            options.inPreferredConfig = Config.RGB_565;
            options.inPurgeable = true;// 同时设置才会有效
            options.inInputShareable = true;// 。当系统内存不够时候图片自动被回收

            InputStream isNew = am.open(srcPath);
            image = BitmapFactory.decodeStream(isNew, null, options);
            isNew.close();

            CCLog.e("bitmap 原来大小：",
                    "宽 " + image.getWidth() + " 高 " + image.getHeight() + " "
                            + image.getByteCount());
            image = zoomImage(image, getScreenMetrics(context).x,
                    getScreenMetrics(context).y);
            CCLog.e("bitmap 新的大小：",
                    "宽 " + image.getWidth() + " 高 " + image.getHeight() + " "
                            + image.getByteCount());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

        return image;
    }

    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
                                   double newHeight) {
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }

    /**
     * 最大返回maxNumOfPixels = 1280*1280像素的图片
     */
    public static Bitmap getSuitableBitmap(ContentResolver resolver, Uri uri)
            throws FileNotFoundException {
        int maxNumOfPixels = 1280 * 1280;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(resolver.openInputStream(uri), null, opts);
        opts.inSampleSize = computeSampleSize(opts, -1, maxNumOfPixels);
        opts.inJustDecodeBounds = false;
        try {
            return BitmapFactory.decodeStream(resolver.openInputStream(uri),
                    null, opts);
        } catch (OutOfMemoryError err) {
        }
        return null;
    }

    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }

    }

    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm
     *
     * @return yyyy-MM-dd HH:mm
     */
    @SuppressLint("SimpleDateFormat")
    public static String getStringDate(Long date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateString = formatter.format(date);

        return dateString;
    }

    /**
     * 将一个字符串转化为输入流
     */
    public static InputStream getStringStream(String sInputString) {
        if (sInputString != null && !sInputString.trim().equals("")) {
            try {
                ByteArrayInputStream tInputStringStream = new ByteArrayInputStream(
                        sInputString.getBytes());
                return tInputStringStream;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 将一个输入流转化为字符串
     */
    public static String getStreamString(InputStream tInputStream) {
        if (tInputStream != null) {
            try {
                BufferedReader tBufferedReader = new BufferedReader(
                        new InputStreamReader(tInputStream));
                StringBuffer tStringBuffer = new StringBuffer();
                String sTempOneLine = new String("");
                while ((sTempOneLine = tBufferedReader.readLine()) != null) {
                    tStringBuffer.append(sTempOneLine);
                }
                return tStringBuffer.toString();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static void saveBitmap2(Bitmap mBitmap, String imageURL, Context cxt) {
        String bitmapName = imageURL.substring(imageURL.lastIndexOf("/") + 1);
        FileOutputStream fos = null;
        try {
            fos = cxt.openFileOutput(bitmapName, Context.MODE_PRIVATE);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            // 这里是保存文件产生异常
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // fos流关闭异常
                    e.printStackTrace();
                }
            }
        }
    }

    // 通过这种方式保存在本地的图片，是可以看到的

    @SuppressWarnings("deprecation")
    public static Bitmap getBitmap2(String fileName, Context cxt) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Config.ARGB_4444;
        opt.inPurgeable = true;
        opt.inInputShareable = true;

        String bitmapName = fileName.substring(fileName.lastIndexOf("/") + 1);
        FileInputStream fis = null;
        try {
            fis = cxt.openFileInput(bitmapName);
            byte[] b = new byte[fis.available()];
            fis.read(b);
            fis.close();
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length, opt);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            // 这里是读取文件产生异常
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // fis流关闭异常
                    e.printStackTrace();
                }
            }
        }
        // 读取产生异常，返回null
        return null;
    }

    /**
     * 判断本地的私有文件夹里面是否存在当前名字的文件
     */
    public static boolean isFileExist(String fileName, Context cxt) {
        String bitmapName = fileName.substring(fileName.lastIndexOf("/") + 1);
        List<String> nameLst = Arrays.asList(cxt.fileList());
        if (nameLst != null && nameLst.size() > 0) {
            for (int i = 0; i < nameLst.size(); i++) {
                CCLog.i("本地私有文件：", "" + nameLst.get(i));
            }
        }
        return nameLst.contains(bitmapName);

    }

    /**
     * 验证邮箱的真实性
     *
     * @param strEmail
     * @return
     */
    public static boolean isEmail(String strEmail) {
        String strPattern = "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(strEmail);
        return m.matches();
    }

    /**
     * 验证手机号码的真实性
     *
     * @param mobiles
     * @return
     */
    // public static boolean isMobileNum(String mobiles) {
    // Pattern p = Pattern
    // .compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
    // Matcher m = p.matcher(mobiles);
    // return m.matches();
    // }

    /**
     * 验证身份证号是否符合规则
     *
     * @param text 身份证号
     * @return
     */
    public static boolean personIdValidation(String text) {
        String regx = "[0-9]{17}x";
        String reg1 = "[0-9]{15}";
        String regex = "[0-9]{18}";
        if (text.length() == 0) {
            return false;
        } else {
            return text.matches(regx) || text.matches(reg1)
                    || text.matches(regex);
        }

    }

    public static String resetMobile(String tel) {
        String result = "";
        if (Utils.isStringEmpty(tel)) {
            return tel;
        }
        if (tel.length() != 11) {
            return tel;
        }
        result = tel.substring(0, tel.length() - (tel.substring(3)).length())
                + "****" + tel.substring(7);

        return result;
    }

    @SuppressLint("NewApi")
    public static boolean checkPermission(Context context, String permission) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(permission) ==
                    PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        } else {
            PackageManager pm = context.getPackageManager();
            if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        }
        return result;
    }

    public static String getUriPath(Context context, Uri uri) {
        String imgPath;
        if (!Utils.isStringEmpty((uri.getAuthority()))) {
            Cursor cursor = context.getContentResolver().query(uri,
                    new String[]{MediaStore.Images.Media.DATA}, null, null,
                    null);
            if (null == cursor) {
                return null;
            }
            cursor.moveToFirst();
            imgPath = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Images.Media.DATA));
            CCLog.i("从相册选取图片路径", imgPath + " ");

            cursor.close();
        } else {
            imgPath = uri.getPath();
        }
        return imgPath;
    }

    public static Bitmap getQQThumbBitmap(String imageUri) {
        CCLog.i("QQ登录 getbitmap:", "" + imageUri);
        // 显示网络上的图片
        Bitmap bitmap = null;
        try {
            URL myFileUrl = new URL(imageUri);
            HttpURLConnection conn = (HttpURLConnection) myFileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();

        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            bitmap = null;
        } catch (IOException e) {
            e.printStackTrace();
            bitmap = null;
        }
        return bitmap;
    }

    public static byte[] bmpToByteArray(final Bitmap bmp,
                                        final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void setMaxEcplise(final TextView mTextView,
                                     final int maxLines, final String content) {

        ViewTreeObserver observer = mTextView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mTextView.setText(content);
                if (mTextView.getLineCount() > maxLines) {
                    int lineEndIndex = mTextView.getLayout().getLineEnd(
                            maxLines - 1);
                    // 下面这句代码中：我在项目中用数字3发现效果不好，改成1了
                    String text = content.subSequence(0, lineEndIndex - 5)
                            + "...";
                    mTextView.setText(text);
                } else {
                    removeGlobalOnLayoutListener(
                            mTextView.getViewTreeObserver(), this);
                }
            }
        });
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    private static void removeGlobalOnLayoutListener(ViewTreeObserver obs,
                                                     ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (obs == null)
            return;
        if (Build.VERSION.SDK_INT < 16) {
            obs.removeGlobalOnLayoutListener(listener);
        } else {
            obs.removeOnGlobalLayoutListener(listener);
        }
    }

    /**
     * 获取新闻创建时间
     *
     * @param date
     * @return yyyy-MM-dd HH:mm:ss
     */
    @SuppressLint("SimpleDateFormat")
    public static String getNewsCreatTime(Long date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(date);

        return dateString;
    }

    // 最省内存的读法
    public Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Config.ARGB_4444;

        Bitmap result = null;
        InputStream is = null;
        try {
            is = context.getResources().openRawResource(resId);
            result = BitmapFactory.decodeStream(is, null, opt);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }


}
