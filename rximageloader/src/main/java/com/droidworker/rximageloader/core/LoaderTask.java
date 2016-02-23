package com.droidworker.rximageloader.core;

import android.graphics.Bitmap;

import com.droidworker.rximageloader.core.gif.GifProcessor;
import com.droidworker.rximageloader.core.request.Request;
import com.droidworker.rximageloader.utils.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import rx.Observable;
import rx.Subscriber;
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
    public static Observable<Bitmap> bitmapTask(Request request) {
        //noinspection unchecked
        return Observable.concat(memTask(request), diskTask(request),
                downloadToBitmapTask(request), localBitmapTask(request))
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
        return LoaderCore.getCacheManager().getFormDisk(request)
                .doOnNext(bitmap -> LoaderCore.getCacheManager().putInMem(request.getKey(), bitmap)
                );
    }

    /**
     * Download picture form the url, because I don't want Processor rely on Request, so
     * I write this method here
     *
     * @param request {@link Request}
     * @return decoded bitmap
     */
    public static Observable<Bitmap> downloadToBitmapTask(Request request) {
        if (!Utils.isUrl(request.getPath())) {
            return Observable.empty();
        }
        return downloadToFile(request).map(file -> {
            Bitmap bitmap = Processor.decodeSampledBitmapFromFile(file.getAbsolutePath(),
                request.getReqWidth(), request.getReqHeight(), request.getConfig());
            if (bitmap != null) {
                LoaderCore.getCacheManager().putInMem(request.getKey(), bitmap);
                if (LoaderCore.getDiskCacheStrategy().cacheRealSize()) {
                    LoaderCore.getCacheManager().putInDisk(request.getKey(), bitmap);
                }
            }
            return bitmap;
        });
    }

    public static Observable<File> downloadToFile(Request request){
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed()) {
                return;
            }
            String url = request.getPath();
            String targetPath = LoaderCore.getGlobalConfig().tempFilePath + File.separator + Utils
                    .hashKeyForDisk(request.getRawKey());
            HttpURLConnection urlConnection = null;
            File tempOut;
            BufferedOutputStream out = null;
            BufferedInputStream in = null;

            try {
                urlConnection = (HttpURLConnection) new URL(url).openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream(), IO_BUFFER_SIZE);

                tempOut = new File(targetPath);
                if (!tempOut.exists()) {
                    if (!tempOut.getParentFile().exists()) {
                        if (!tempOut.getParentFile().mkdirs()) {
                            return;
                        }
                    }
                    if (tempOut.createNewFile()) {
                        out = new BufferedOutputStream(new FileOutputStream(tempOut), IO_BUFFER_SIZE);

                        final int total = urlConnection.getContentLength();
                        int len;
                        int current = 0;
                        byte[] buffer = new byte[IO_BUFFER_SIZE];
                        while ((len = in.read(buffer)) != -1 && !subscriber.isUnsubscribed()) {
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
                    if (LoaderCore.getDiskCacheStrategy().cacheOrigin()) {
                        //noinspection ResultOfMethodCallIgnored
                        tempOut.renameTo(
                                new File(LoaderCore.getGlobalConfig().diskCachePath + File.separator
                                        + Utils.hashKeyForDisk(request.getRawKey())));
                        if (subscriber.isUnsubscribed()) {
                            return;
                        }
                        subscriber.onNext(tempOut);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                subscriber.onError(e);
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
        });
    }

    public static Observable<Bitmap> localBitmapTask(Request request){
        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                if(!subscriber.isUnsubscribed()){
                    Bitmap bitmap = Processor.decodeSampledBitmapFromFile(request.getPath(), request
                            .getReqWidth(), request.getReqHeight(), request.getConfig());
                    if(bitmap != null){
                        LoaderCore.getCacheManager().putInMem(request.getKey(), bitmap);
                        if (LoaderCore.getDiskCacheStrategy().cacheRealSize()) {
                            LoaderCore.getCacheManager().putInDisk(request.getKey(), bitmap);
                        }
                        subscriber.onNext(bitmap);
                    }
                }
            }
        });
    }

    public static Observable<Bitmap> gifTask(Request request) {
        
        return null;
    }

    public static Observable<Bitmap> localGifTask(Request request){
        return Observable.create(
                new Observable.OnSubscribe<Bitmap>() {
                    @Override
                    public void call(Subscriber<? super Bitmap> subscriber) {
                        GifProcessor gifProcessor = new GifProcessor(request.getRawKey());
                        InputStream inputStream = null;
                        try {
                            File file = new File(request.getPath());
                            if (file.exists()) {
                                inputStream = new FileInputStream(file);
                            } else if (request.getPath().contains("android_asset")) {
                                inputStream = request.getAttachedView().getContext().getAssets()
                                        .open(request.getRawKey());
                            }
                            gifProcessor.read(inputStream);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        while (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(gifProcessor.getFrame());
                            try {
                                Thread.sleep(gifProcessor.getDelay());
                            } catch (InterruptedException e) {

                            }
                        }
                    }
                }
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<File> netGifTask(Request request){
        return Observable.empty();
    }
}
