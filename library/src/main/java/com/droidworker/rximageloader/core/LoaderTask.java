package com.droidworker.rximageloader.core;

import android.graphics.Bitmap;

import com.droidworker.rximageloader.cache.DroidCacheManager;
import com.droidworker.rximageloader.core.request.Request;

import rx.Observable;

/**
 * In charge of creating {@link Observable} to deal with the request
 *
 * @author DroidWorkerLYF
 */
public class LoaderTask {

    public static Observable<Bitmap> getFromMem(Request request) {
        return DroidCacheManager.getInstance().getFromMem(request);
    }

    public static Observable<Bitmap> getFormDisk(Request request) {
        return DroidCacheManager.getInstance().getFormDisk(request);
    }

    public static Observable<Bitmap> getBitmap(Request request) {
        return Observable.create(subscriber -> {

        });
    }
}
