package com.droidworker.rximageloader.cache.interfaces;

import android.graphics.Bitmap;

import com.droidworker.rximageloader.core.LoaderConfig;
import com.droidworker.rximageloader.core.request.Request;

import rx.Observable;

/**
 * define the behaviour of a cache manager
 *
 * @author DroidWorkerLYF
 */
public interface ICacheManager {

    void init(LoaderConfig loaderConfig);

    Observable<Bitmap> getFromMem(Request request);

    Observable<Bitmap> getFormDisk(Request request);

    void clearAll();

    void clearMemCache();

    void clearDiskCache();
}
