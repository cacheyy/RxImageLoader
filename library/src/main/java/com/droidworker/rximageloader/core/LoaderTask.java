package com.droidworker.rximageloader.core;

import android.graphics.Bitmap;
import android.util.Log;

import com.droidworker.rximageloader.core.request.Request;
import com.droidworker.rximageloader.utils.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import rx.Observable;

/**
 * In charge of creating {@link Observable} to deal with the request
 *
 * @author DroidWorkerLYF
 */
public class LoaderTask {
    private static final String TAG = "LoaderTask";
    private static final int IO_BUFFER_SIZE = 8 * 1024;

    public static Observable<Bitmap> getFromMem(Request request) {
        return LoaderCore.getCacheManager().getFromMem(request);
    }

    public static Observable<Bitmap> getFormDisk(Request request) {
        return LoaderCore.getCacheManager().getFormDisk(request);
    }

    public static Observable<Bitmap> getBitmap(Request request) {
        return Observable.create(subscriber -> {
            Log.e(TAG, "get from origin");
            if (subscriber.isUnsubscribed()) {
                return;
            }
            if (Utils.isUrl(request.getPath())) {
//                Bitmap bitmap = downloadUrlToStream(request, )
            } else if (Utils.isGif(request.getPath())) {

            } else {
                //This is a local file
                Bitmap bitmap = Processor.decodeSampledBitmapFromFile(request.getPath(), request
                        .getReqWidth(), request.getReqHeight(), request.getConfig());
                LoaderCore.getCacheManager().putInMem(request.getKey(), bitmap);
                LoaderCore.getCacheManager().putInDisk(request.getKey(), bitmap);
                subscriber.onNext(bitmap);
                Log.e(TAG, "" + bitmap.toString());
            }
            subscriber.onCompleted();
        });
    }

    /**
     * 从网络获取图片并写入到本地，然后decode图片
     *
     * @param request request
     * @return
     */
    public static Bitmap downloadUrlToStream(Request request,
                                             LoaderConfig loaderConfig) {
        HttpURLConnection urlConnection = null;
        File tempOut;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        Bitmap bitmap = null;

        try {
            final URL url = new URL(request.getPath());
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), IO_BUFFER_SIZE);

            tempOut = new File(loaderConfig.tempFilePath + "/" + Utils.hashKeyForDisk(request.getKey()));
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
                    current += len;
                    request.onProgress(current * 1.0f / total);
                }
                out.flush();
            }

            if (tempOut != null && tempOut.exists()) {
                bitmap = Processor.decodeSampledBitmapFromFile(tempOut.getAbsolutePath(), request
                        .getReqWidth(), request.getReqHeight(), request.getConfig());
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
}
