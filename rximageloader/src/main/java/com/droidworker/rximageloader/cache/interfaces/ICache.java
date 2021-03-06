package com.droidworker.rximageloader.cache.interfaces;

import android.graphics.Bitmap;

import com.droidworker.rximageloader.core.LoaderConfig;
import com.droidworker.rximageloader.core.request.Request;

import rx.Observable;

/**
 * Define the behaviour of a cache
 *
 * @author DroidWorkerLYF
 */
public interface ICache {

    /**
     * initialize cache
     *
     * @param loaderConfig the loader config needed to config cache
     */
    void initCache(LoaderConfig loaderConfig);

    /**
     * clear cache
     */
    void clearCache();

    /**
     * put bitmap into cache
     *
     * @param key    url or path used as the key
     * @param bitmap the bitmap needed to put in cache
     */
    void putInCache(String key, Bitmap bitmap);

    /**
     * get bitmap form cache
     *
     * @param request request
     * @return an Observable that get bitmap form cache
     */
    Observable<Bitmap> getFromCache(Request request);

}
