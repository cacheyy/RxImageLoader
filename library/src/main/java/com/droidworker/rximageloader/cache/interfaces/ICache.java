package com.droidworker.rximageloader.cache.interfaces;

import android.graphics.Bitmap;

import rx.Observable;

/**
 * define the behaviour of a cache
 *
 * @author DroidWorkerLYF
 */
public interface ICache {

    /**
     * initialize cache
     */
    void initCache();

    /**
     * clear cache
     */
    void clearCache();

    void putInCache(Bitmap bitmap);

    Observable<Bitmap> getFromCache();

}
