package com.droidworker.rximageloader.cache.interfaces;

import android.graphics.Bitmap;

import com.droidworker.rximageloader.core.LoaderConfig;
import com.droidworker.rximageloader.core.request.Request;

import rx.Observable;

/**
 * Define the behaviour of a cache manager
 *
 * @author DroidWorkerLYF
 */
public interface ICacheManager {

    /**
     * Initialize the cache manager according to the given {@link LoaderConfig}
     *
     * @param loaderConfig
     */
    void init(LoaderConfig loaderConfig);

    /**
     * @param request the {@link Request} which contains relative path and config
     * @return an Observable that contains a task which can get bitmap from memory cache
     */
    Observable<Bitmap> getFromMem(Request request);

    /**
     * @param request the {@link Request} which contains relative path and config
     * @return an Observable that contains a task which can get bitmap from disk cache
     */
    Observable<Bitmap> getFormDisk(Request request);

    void putInMem(String key, Bitmap bitmap);

    void putInDisk(String key, Bitmap bitmap);

    /**
     * Clear both memory and disk cache
     */
    void clearAll();

    /**
     * Clear memory cache
     */
    void clearMemCache();

    /**
     * Clear disk cache
     */
    void clearDiskCache();
}
