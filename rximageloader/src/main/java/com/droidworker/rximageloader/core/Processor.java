package com.droidworker.rximageloader.core;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author DroidWorkerLYF
 */
public class Processor {
    private static final int IO_BUFFER_SIZE = 8 * 1024;

    /**
     * 从FileDescriptor decode图片
     * decode bitmap form file descriptor
     *
     * @param fileDescriptor fileDescriptor
     * @param reqWidth reqWidth
     * @param reqHeight reqHeight
     * @param config config
     * @return decoded bitmap
     */
    public static Bitmap decodeBitmapFromFileDescriptor(FileDescriptor fileDescriptor, int
            reqWidth, int reqHeight, Bitmap.Config config) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = config;

        return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, addInBitmapOptions
                (options));
    }

    /**
     * 设置inBitmap属性
     *
     * @param options options
     */
    private static BitmapFactory.Options addInBitmapOptions(BitmapFactory.Options options) {
        options.inMutable = true;

        Bitmap inBitmap = LoaderCore.getCacheManager().getBitmapFromReusableSet(options);
        if (inBitmap != null) {
            options.inBitmap = inBitmap;
        }
        return options;
    }

    /**
     * Calculate inSampleSize
     *
     * @param options options
     * @param reqWidth  request width
     * @param reqHeight request height
     * @return inSampleSize
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int inSampleSize = 1;
        final int height = options.outHeight;
        final int width = options.outWidth;

        if (reqHeight == 0 || reqWidth == 0) { return inSampleSize; }

        if (height > reqHeight || width > reqWidth) {

            int halfHeight = height;
            int halfWidth = width;

            while (halfHeight > reqHeight && halfWidth > reqWidth) {
                inSampleSize *= 2;
                halfHeight /= 2;
                halfWidth /= 2;
            }

            long totalPixels = width / inSampleSize * height / inSampleSize;
            final long totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels > totalReqPixelsCap) {
                inSampleSize *= 2;
                totalPixels /= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * decode bitmap form the given path
     * @return decode bitmap
     */
    public static Bitmap loadFromFilePath(String pathString, OutputStream outputStream, int
            reqWidth, int reqHeight, Bitmap.Config config,
                                          Bitmap.CompressFormat compressFormat, int compressQuality) {
        FileDescriptor fileDescriptor;
        Bitmap bitmap = null;
        try {
            fileDescriptor = new FileInputStream(pathString).getFD();
            bitmap = decodeBitmapFromFileDescriptor(fileDescriptor, reqWidth, reqHeight, config);
            if (bitmap != null) {
                bitmap.compress(compressFormat, compressQuality, outputStream);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * read raw resource and saved into the given output stream
     *
     * @param context      context
     * @param rawPath      the path of the bitmap
     * @param outputStream the output storage path
     * @return true if success
     */
    public static boolean loadFromRaw(Context context, String rawPath, OutputStream outputStream) {
        BufferedOutputStream out = null;
        BufferedInputStream in = null;

        try {
            in = new BufferedInputStream(context.getAssets().open(rawPath), IO_BUFFER_SIZE);
            out = new BufferedOutputStream(outputStream, IO_BUFFER_SIZE);

            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 从resources中decode图片
     * decode bitmap form resources
     *
     * @param res   resources
     * @param resId the resource's id
     * @return decoded bitmap
     */
    public static Bitmap decodeBitmapFromResource(Resources res, int resId, int
            reqWidth, int reqHeight, Bitmap.Config config) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inPreferredConfig = config;

        addInBitmapOptions(options);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * decode bitmap from the given filename
     *
     * @param filename filename of the bitmap
     * @return decoded bitmap
     */
    public static Bitmap decodeSampledBitmapFromFile(String filename, int
            reqWidth, int reqHeight, Bitmap.Config config) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        options.inPreferredConfig = config;

       addInBitmapOptions(options);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filename, options);
    }

}
