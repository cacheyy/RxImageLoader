package com.droidworker.rximageloader.core;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.droidworker.rximageloader.core.request.Request;
import com.droidworker.rximageloader.utils.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author DroidWorkerLYF
 */
public class Processor {
    private static final int IO_BUFFER_SIZE = 8 * 1024;

    /**
     * 从FileDescriptor decode图片
     * decode bitmap form file descriptor
     *
     * @param fileDescriptor
     * @param option
     * @return decode bitmap
     */
    public static Bitmap decodeBitmapFromFileDescriptor(FileDescriptor fileDescriptor, Request.Option
            option) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
        options.inSampleSize = calculateInSampleSize(options, option.reqWidth, option.reqHeight);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = option.config;

//        addInBitmapOptions(options, cache);
        return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
    }

    /**
     * 设置inBitmap属性
     *
     * @param options
     */
    private static void addInBitmapOptions(BitmapFactory.Options options) {
        options.inMutable = true;

//        if (cache != null) {
//            Bitmap inBitmap = cache.getBitmapFromReusableSet(options);
//
//            if (inBitmap != null) {
//                options.inBitmap = inBitmap;
//            }
//        }
    }

    /**
     * Calculate inSampleSize
     *
     * @param options
     * @param reqWidth  request width
     * @param reqHeight request height
     * @return inSampleSize
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

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
     * 从网络获取图片并写入到本地，然后decode图片
     *
     * @param urlString    图片地址
     * @param outputStream 输出流
     * @param option       加载选项
     * @return
     */
    public static Bitmap downloadUrlToStream(String urlString, OutputStream outputStream, Request.Option option,
                                             LoaderConfig loaderConfig) {
        HttpURLConnection urlConnection = null;
        File tempOut;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        Bitmap bitmap = null;

        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), IO_BUFFER_SIZE);

            tempOut = new File(loaderConfig.tempFilePath + "/" + Utils.hashKeyForDisk(urlString));
            if (!tempOut.exists()) {
                if (!tempOut.getParentFile().exists()) {
                    tempOut.getParentFile().mkdirs();
                }
                tempOut.createNewFile();
                out = new BufferedOutputStream(new FileOutputStream(tempOut), IO_BUFFER_SIZE);

                final int total = in.available();
                int len;
                int current = 0;
                byte[] buffer = new byte[IO_BUFFER_SIZE];
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
//                    if (listener != null) {
//                        current += len;
//                        listener.onProgress(current, total);
//                    }
                }
                out.flush();
            }

            if (tempOut != null && tempOut.exists()) {
                bitmap = loadFromFilePath(tempOut.getAbsolutePath(), outputStream, option, loaderConfig);
            }
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bitmap;
    }

    /**
     * decode bitmap form the given path
     *
     * @param pathString
     * @param outputStream
     * @param option
     * @param loaderConfig
     * @return decode bitmap
     */
    public static Bitmap loadFromFilePath(String pathString, OutputStream outputStream, Request.Option
            option,
                                          LoaderConfig loaderConfig) {
        FileDescriptor fileDescriptor;
        Bitmap bitmap = null;
        try {
            fileDescriptor = new FileInputStream(pathString).getFD();
            bitmap = decodeBitmapFromFileDescriptor(fileDescriptor, option);
            if (bitmap != null) {
                bitmap.compress(loaderConfig.mCompressFormat, loaderConfig.mCompressQuality, outputStream);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
            }
        }
        return false;
    }

    /**
     * 从resources中decode图片
     * decode bitmap form resources
     *
     * @param res    resources
     * @param resId  the resource's id
     * @param option option of this request
     * @return decoded bitmap
     */
    public static Bitmap decodeBitmapFromResource(Resources res, int resId, Request.Option option) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, option.reqWidth, option.reqHeight);
        options.inPreferredConfig = option.config;

//        addInBitmapOptions(options, cache);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * decode bitmap from the given filename
     *
     * @param filename filename of the bitmap
     * @param option   option of this request
     * @return decoded bitmap
     */
    static Bitmap decodeSampledBitmapFromFile(String filename, Request.Option option) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options);

        options.inSampleSize = calculateInSampleSize(options, option.reqWidth, option
                .reqHeight);
        options.inPreferredConfig = option.config;

//        addInBitmapOptions(options, cache);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filename, options);
    }
}
