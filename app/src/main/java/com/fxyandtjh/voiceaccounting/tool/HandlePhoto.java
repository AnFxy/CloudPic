package com.fxyandtjh.voiceaccounting.tool;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;

import androidx.core.content.FileProvider;
import androidx.core.os.EnvironmentCompat;

import com.blankj.utilcode.util.UriUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HandlePhoto {

    public static Uri mCameraUri;

    public static String mCameraImagePath;
    private static final boolean isAndroidQ = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;


    @SuppressLint("Range")
    private static String getImagePath(Uri uri, String selection, Context context) {
        String path = null;
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);   //内容提供器
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));   //获取路径
            }
            cursor.close();
        }

        return path;
    }

    public static String handleImageOkKitKat(Uri uri, Context context) {
        String imagePath = "";
        if (DocumentsContract.isDocumentUri(context, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);        //数据表里指定的行
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, context);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null, context);
            }

        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null, context);
        } else {
            imagePath = UriUtils.uri2File(uri).getAbsolutePath();
        }
        return imagePath;
    }

    /**
     * 创建图片地址uri,用于保存拍照后的照片
     */
    public static Uri createImageUri(Context context) {
        if (isAndroidQ) {
            String status = Environment.getExternalStorageState();
            // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
            if (status.equals(Environment.MEDIA_MOUNTED)) {
                Uri exUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
                mCameraUri = exUri;
                return exUri;
            } else {
                Uri inUri = context.getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, new ContentValues());
                mCameraUri = inUri;
                return inUri;
            }
        } else {
            File photoFile = null;
            Uri photoUri = null;
            try {
                photoFile = createImageFile(context);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile != null) {
                mCameraImagePath = photoFile.getAbsolutePath();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    //适配Android 7.0文件权限，通过FileProvider创建一个content类型的Uri
                    photoUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", photoFile);
                } else {
                    photoUri = Uri.fromFile(photoFile);
                }
            }
            mCameraUri = photoUri;
            return photoUri;
        }
    }

    /**
     * 创建保存图片的文件
     */
    private static File createImageFile(Context context) throws IOException {
        String imageName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            storageDir.mkdir();
        }
        File tempFile = new File(storageDir, imageName);
        if (!Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(tempFile))) {
            return null;
        }
        return tempFile;
    }

    //根据图片路径创建bitmap
    public static Bitmap createBitmap(String filePath) {
        return TextUtils.isEmpty(filePath) ? null : BitmapFactory.decodeFile(filePath);
    }

    //判断bitmap是否为空
    private static boolean isNull(Bitmap bitmap) {
        return (bitmap == null || bitmap.getWidth() == 0 || bitmap.getHeight() == 0);
    }

    //创建指定大小的bitmap
    public static Bitmap createBitmapWithHW(Bitmap bitmap, int wight, int height) {
        if (isNull(bitmap))
            return null;
        return Bitmap.createScaledBitmap(bitmap, wight, height, true);
    }

    /**
     * 压缩图片到指定大小并转为Base64格式
     *
     * @param bitmapPath 图片路径
     * @param size       指定压缩的图片大小
     * @return
     */
    public static String compressBitmap2Base64(String bitmapPath, long size) {
        Bitmap bitmap = zoomImage(createBitmap(bitmapPath));
        return compressBitmap2Base64(bitmap, size);
    }

    /**
     * 压缩图片到指定大小并转为Base64格式
     *
     * @param bitmap 要压缩的bitmap
     * @param size   指定压缩的图片大小
     * @return
     */
    public static String compressBitmap2Base64(Bitmap bitmap, long size) {
        if (isNull(bitmap) || size <= 0L)
            return null;
        //压缩到指定大小--上传
        Bitmap tempBitmap = compressOfHW(bitmap);
        byte[] bytes;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        tempBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        int i = 90;
        boolean bool = true;
        while (((byteArrayOutputStream.toByteArray()).length / 1024) > size && bool == true) {
            byteArrayOutputStream.reset();
            tempBitmap.compress(Bitmap.CompressFormat.JPEG, i, byteArrayOutputStream);
            int j = i - 10;
            i = j;
            if (j <= 10) {
                bool = false;
                i = 10;
            }
        }
        bytes = byteArrayOutputStream.toByteArray();


        if (bitmap != null && !bitmap.isRecycled())
            bitmap.recycle();

        return byte2Base64(bytes);

    }

    //压缩图片到指定大小，并存储到文件中
    public static boolean compressAndSaveBitmap(String bitmapPath, long size, File file) {
        Bitmap bitmap = createBitmap(bitmapPath);

        boolean b = compressAndSaveBitmap(bitmap, size, file);
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return b;
    }

    //压缩并保存bitmap
    public static boolean compressAndSaveBitmap(Bitmap paramBitmap, long paramLong, File paramFile) {
        if (isNull(paramBitmap) || paramLong <= 0L)
            return false;
        //压缩到指定大小
        Bitmap tempBitmap = compressOfHW(paramBitmap);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        tempBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        if (byteArrayOutputStream.size() <= paramLong) {
            byteArrayOutputStream.toByteArray();
        } else {
            byteArrayOutputStream.reset();
            tempBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            if (byteArrayOutputStream.size() <= paramLong) {
                byteArrayOutputStream.toByteArray();
            } else {
                int i = 90;
                boolean bool = true;
                while (((byteArrayOutputStream.toByteArray()).length / 1024) > paramLong && bool == true) {
                    byteArrayOutputStream.reset();
                    tempBitmap.compress(Bitmap.CompressFormat.JPEG, i, byteArrayOutputStream);
                    int j = i - 10;
                    i = j;
                    if (j <= 10) {
                        bool = false;
                        i = 10;
                    }
                }
                byteArrayOutputStream.toByteArray();
            }
        }

        if (tempBitmap != null && !tempBitmap.isRecycled()) {
            tempBitmap.recycle();
        }

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(paramFile);
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
            fileOutputStream.flush();
            fileOutputStream.close();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("文件大小");
            stringBuilder.append(paramFile.length() / 1024L);
            return true;
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    /**
     * 压缩图片到指定大小
     *
     * @param bitmapPath 被压缩图片的路径
     * @param size       指定压缩到的大小
     * @return
     */
    public static Bitmap compressBitmap(String bitmapPath, long size) {
        Bitmap b = compressBitmap(createBitmap(bitmapPath), size);
        return b;
    }

    /**
     * 压缩图片到指定大小
     *
     * @param paramBitmap 被压缩bitmap对象
     * @param size
     * @return
     */
    public static Bitmap compressBitmap(Bitmap paramBitmap, long size) {
        if (isNull(paramBitmap) || size <= 0L)
            return null;
        //压缩到指定大小--上传
        Bitmap tempBitmap = compressOfHW(paramBitmap);
        byte[] bytes;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        tempBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        int i = 90;
        boolean bool = true;
        while (((byteArrayOutputStream.toByteArray()).length / 1024) > size && bool == true) {
            byteArrayOutputStream.reset();
            tempBitmap.compress(Bitmap.CompressFormat.JPEG, i, byteArrayOutputStream);
            int j = i - 10;
            i = j;
            if (j <= 10) {
                bool = false;
                i = 10;
            }
        }

        bytes = byteArrayOutputStream.toByteArray();


        if (paramBitmap != null && !paramBitmap.isRecycled())
            paramBitmap.recycle();

        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /***
     * 图片的缩放方法
     * @param bitmap ：源图片资源
     */
    public static Bitmap zoomImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int newWidth = bitmap.getWidth();
        int newHeight = bitmap.getHeight();
        //原图与目标图大小的倍数
        if (baos.toByteArray().length / 1024 <= 4112) {
            return bitmap;
        }
        float multiple = (baos.toByteArray().length / 1024) / 4112;
        if (multiple < 1) {
            multiple = 1;
        }
        double scale = Math.sqrt(multiple);
        if (scale <= 1)
            scale = 1;
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        int scaleWidth = (int) Math.floor((newWidth / scale));
        int scaleHeight = (int) Math.floor(newHeight / scale);

        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bm = Bitmap.createScaledBitmap(bitmap, scaleWidth, scaleHeight, true);
        return bm;
    }

    //压缩到需求宽高
    public static Bitmap compressOfHW(Bitmap paramBitmap) {
        //获取这个图片的宽和高
        float width = paramBitmap.getWidth();
        float height = paramBitmap.getHeight();

        float newHeight = height;
        float newWidth = width;

        if (newHeight >= 4096) {
            newHeight = 4000;
        }
        if (newHeight < 256) {
            newHeight = 300;
        }
        if (newWidth >= 4096) {
            newWidth = 4000;
        }
        if (newWidth < 256) {
            newWidth = 300;
        }

        //创建操作图片用的matrix对象
        Matrix matrix = new Matrix();

        //计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        //压缩Bitmap
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBitmap = Bitmap.createBitmap(paramBitmap, 0, 0, (int) width, (int) height, matrix, true);
        return newBitmap;
    }

    //bitmap转byte
    public static byte[] bitmap2Byte(Bitmap paramBitmap, Bitmap.CompressFormat paramCompressFormat) {
        if (paramBitmap == null)
            return null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        paramBitmap.compress(paramCompressFormat, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    //文件（图片）转换为base64
    public static String imageToBase64(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        InputStream is = null;
        byte[] data = null;
        String result = null;
        try {
            is = new FileInputStream(path);
            //创建一个字符流大小的数组。
            data = new byte[is.available()];
            //写入数组
            is.read(data);
            //用默认的编码格式进行编码
            result = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }

    //图片转换为base64
    public static String imageToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();

                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static String byte2Base64(byte[] bitmapBytes) {
        return new String(Base64.encode(bitmapBytes, Base64.NO_WRAP));
    }
}