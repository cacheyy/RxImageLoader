package com.droidworker.rximageloader.core;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import com.droidworker.rximageloader.core.request.Request;
import com.droidworker.rximageloader.utils.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * In charge of creating {@link Observable} to deal with the request
 *
 * @author DroidWorkerLYF
 */
public class LoaderTask {
    private static final String TAG = LoaderTask.class.getSimpleName();
    private static final int IO_BUFFER_SIZE = 8 * 1024;

    /**
     * Create a new task to get bitmap
     *
     * @param request {@link Request}
     * @return a new task
     */
    public static Observable<Bitmap> newTask(Request request) {
        return Observable.concat(memTask(request), diskTask(request), getBitmap(request))
                .takeFirst(bitmap -> bitmap != null && !bitmap.isRecycled())
                .map(request.getTransformer())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * @param request {@link Request}
     * @return a task that get resource from memory cache
     */
    public static Observable<Bitmap> memTask(Request request) {
        return LoaderCore.getCacheManager().getFromMem(request);
    }

    /**
     * @param request {@link Request}
     * @return a task that get resource from disk cache
     */
    public static Observable<Bitmap> diskTask(Request request) {
        Log.i(TAG, "diskTask " + request.getKey());
        return LoaderCore.getCacheManager().getFormDisk(request)
                .doOnNext(bitmap -> LoaderCore.getCacheManager().putInMem(request.getKey(), bitmap)
                );
    }

    /**
     * @param request {@link Request}
     * @return a task that get resource from the original place
     */
    public static Observable<Bitmap> getBitmap(Request request) {
        return Observable.create(subscriber -> {
            Log.i(TAG, "get from origin " + request.getKey());
            if (subscriber.isUnsubscribed()) {
                return;
            }
            Bitmap bitmap = null;
            if (Utils.isUrl(request.getPath())) {
                bitmap = downloadUrlToStream(request, LoaderCore.getGlobalConfig());
            } else //noinspection StatementWithEmptyBody
                if (Utils.isGif(request.getPath())) {

            } else {
                //This is a local file
                bitmap = Processor.decodeSampledBitmapFromFile(request.getPath(), request
                        .getReqWidth(), request.getReqHeight(), request.getConfig());
            }
            if (bitmap != null) {
                LoaderCore.getCacheManager().putInMem(request.getKey(), bitmap);
                if (LoaderCore.getDiskCacheStrategy().cacheRealSize()) {
                    LoaderCore.getCacheManager().putInDisk(request.getKey(), bitmap);
                }
                View view = request.getAttachedView();
                subscriber.onNext(bitmap);
            }
            subscriber.onCompleted();
        });
    }

    /**
     * Download picture form the url, because I don't want Processor rely on Request, so
     * I write this method here
     *
     * @param request {@link Request}
     * @return decoded bitmap
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

            tempOut = new File(loaderConfig.tempFilePath + File.separator + Utils.hashKeyForDisk(request
                    .getRawKey()));
            if (!tempOut.exists()) {
                if (!tempOut.getParentFile().exists()) {
                    if (!tempOut.getParentFile().mkdirs()) {
                        return null;
                    }
                }
                if (tempOut.createNewFile()) {
                    out = new BufferedOutputStream(new FileOutputStream(tempOut), IO_BUFFER_SIZE);

                    final int total = urlConnection.getContentLength();
                    int len;
                    int current = 0;
                    byte[] buffer = new byte[IO_BUFFER_SIZE];
                    while ((len = in.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                        current += len;
                        if (total >= 0) {
                            request.onProgress(current * 1.0f / total);
                        }
                    }
                    out.flush();
                }
            }

            if (tempOut.exists()) {
                bitmap = Processor.decodeSampledBitmapFromFile(tempOut.getAbsolutePath(), request
                        .getReqWidth(), request.getReqHeight(), request.getConfig());
                if (LoaderCore.getDiskCacheStrategy().cacheOrigin()) {
                    //noinspection ResultOfMethodCallIgnored
                    tempOut.renameTo(
                            new File(loaderConfig.diskCachePath + File.separator
                                    + Utils.hashKeyForDisk(request.getRawKey())));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
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
