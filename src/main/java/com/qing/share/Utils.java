package com.qing.share;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.format.DateFormat;

import com.qing.log.MLog;
import com.qing.utils.StringUtil;

import junit.framework.Assert;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by zwq on 2015/11/04 14:09.<br/><br/>
 */
public class Utils {

    private static final String TAG = Utils.class.getName();

    public static void compressBitmap(final Bitmap bitmap, int quality) {
        if (bitmap == null)
            return;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, os);
            MLog.d(TAG, "thumb size:" + os.toByteArray().length);
        } catch (Exception e) {
            e.printStackTrace();
            MLog.d(TAG, "put thumb failed");
        } finally {
            try {
                if (os != null) {
                    os.close();
                    os = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 微信构造标识
     * @param type
     * @return
     */
    public static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    public static byte[] bitmapToByteArray(final Bitmap bitmap, final boolean needRecycle) {
        if (bitmap == null || bitmap.isRecycled())
            return null;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bitmap.recycle();
        }
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static final int MAX_DECODE_PICTURE_SIZE = 1920 * 1440;
    public static Bitmap extractThumbNail(final String path, final int height, final int width, final boolean crop) {
        Assert.assertTrue(path != null && !path.equals("") && height > 0 && width > 0);

        BitmapFactory.Options options = new BitmapFactory.Options();

        try {
            options.inJustDecodeBounds = true;
            Bitmap tmp = BitmapFactory.decodeFile(path, options);
            if (tmp != null) {
                tmp.recycle();
                tmp = null;
            }

            MLog.d(TAG, "extractThumbNail: round=" + width + "x" + height + ", crop=" + crop);
            final double beY = options.outHeight * 1.0 / height;
            final double beX = options.outWidth * 1.0 / width;
            MLog.d(TAG, "extractThumbNail: extract beX = " + beX + ", beY = " + beY);
            options.inSampleSize = (int) (crop ? (beY > beX ? beX : beY) : (beY < beX ? beX : beY));
            if (options.inSampleSize <= 1) {
                options.inSampleSize = 1;
            }

            // NOTE: out of memory error
            while (options.outHeight * options.outWidth / options.inSampleSize > MAX_DECODE_PICTURE_SIZE) {
                options.inSampleSize++;
            }

            int newHeight = height;
            int newWidth = width;
            if (crop) {
                if (beY > beX) {
                    newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
                } else {
                    newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
                }
            } else {
                if (beY < beX) {
                    newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
                } else {
                    newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
                }
            }

            options.inJustDecodeBounds = false;

            MLog.i(TAG, "bitmap required size=" + newWidth + "x" + newHeight + ", orig=" + options.outWidth + "x" + options.outHeight + ", sample=" + options.inSampleSize);
            Bitmap bm = BitmapFactory.decodeFile(path, options);
            if (bm == null) {
                MLog.e(TAG, "bitmap decode failed");
                return null;
            }

            MLog.i(TAG, "bitmap decoded size=" + bm.getWidth() + "x" + bm.getHeight());
            final Bitmap scale = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
            if (scale != null) {
                bm.recycle();
                bm = scale;
            }

            if (crop) {
                final Bitmap cropped = Bitmap.createBitmap(bm, (bm.getWidth() - width) >> 1, (bm.getHeight() - height) >> 1, width, height);
                if (cropped == null) {
                    return bm;
                }

                bm.recycle();
                bm = cropped;
                MLog.i(TAG, "bitmap croped size=" + bm.getWidth() + "x" + bm.getHeight());
            }
            return bm;

        } catch (final OutOfMemoryError e) {
            MLog.e(TAG, "decode bitmap failed: " + e.getMessage());
            options = null;
        }
        return null;
    }

    private static String filePath = null;

    public static boolean takePhoto(final Activity activity, final String dir, final String filename, final int cmd) {
        filePath = dir + filename;

        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final File cameraDir = new File(dir);
        if (!cameraDir.exists()) {
            return false;
        }

        final File file = new File(filePath);
        final Uri outputFileUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        try {
            activity.startActivityForResult(intent, cmd);

        } catch (final ActivityNotFoundException e) {
            return false;
        }
        return true;
    }

    public static String getResultPhotoPath(Context context, final Intent intent, final String dir) {
        if (filePath != null && new File(filePath).exists()) {
            return filePath;
        }

        return resolvePhotoFromIntent(context, intent, dir);
    }

    public static String resolvePhotoFromIntent(final Context ctx, final Intent data, final String dir) {
        if (ctx == null || data == null || dir == null) {
            MLog.e(TAG, "resolvePhotoFromIntent fail, invalid argument");
            return null;
        }

        String filePath = null;

        final Uri uri = Uri.parse(data.toURI());
        Cursor cu = ctx.getContentResolver().query(uri, null, null, null, null);
        if (cu != null && cu.getCount() > 0) {
            try {
                cu.moveToFirst();
                final int pathIndex = cu.getColumnIndex(MediaStore.MediaColumns.DATA);
                MLog.e(TAG, "orition: " + cu.getString(cu.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION)));
                filePath = cu.getString(pathIndex);
                MLog.d(TAG, "photo from resolver, path:" + filePath);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (data.getData() != null) {
            filePath = data.getData().getPath();
            if (!(new File(filePath)).exists()) {
                filePath = null;
            }
            MLog.d(TAG, "photo file from data, path:" + filePath);

        } else if (data.getAction() != null && data.getAction().equals("inline-data")) {

            try {
                final String fileName = StringUtil.getMD5(DateFormat.format("yyyy-MM-dd-HH-mm-ss", System.currentTimeMillis()).toString());//.getBytes()) + Util.PHOTO_DEFAULT_EXT;
                filePath = dir + fileName;

                final Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                final File file = new File(filePath);
                if (!file.exists()) {
                    file.createNewFile();
                }

                BufferedOutputStream out;
                out = new BufferedOutputStream(new FileOutputStream(file));
                final int cQuality = 100;
                bitmap.compress(Bitmap.CompressFormat.PNG, cQuality, out);
                out.close();
                MLog.d(TAG, "photo image from data, path:" + filePath);

            } catch (final Exception e) {
                e.printStackTrace();
            }

        } else {
            if (cu != null) {
                cu.close();
                cu = null;
            }
            MLog.e(TAG, "resolve photo from intent failed");
            return null;
        }
        if (cu != null) {
            cu.close();
            cu = null;
        }
        return filePath;
    }

}
