package com.droidworker.rximageloader.cache;

import android.graphics.Bitmap;

import com.droidworker.rximageloader.cache.disk.DiskICacheImpl;
import com.droidworker.rximageloader.cache.interfaces.ICache;
import com.droidworker.rximageloader.cache.interfaces.ICacheManager;
import com.droidworker.rximageloader.cache.memory.MemoryICacheImpl;
import com.droidworker.rximageloader.core.LoaderConfig;
import com.droidworker.rximageloader.core.request.Request;

import rx.Observable;

/**
 * Implements of ICacheManager,this manager is in charge of create memory and disk cache,
 * get resource from cache, put resource into cache
 *
 * @author DroidWorkerLYF
 */
public class DroidCacheManager implements ICacheManager {
    private ICache memCache;
    private ICache diskCache;

    public DroidCacheManager() {
    }

    @Override
    public void init(LoaderConfig loaderConfig) {
        if (loaderConfig.memCacheEnabled) {
            memCache = new MemoryICacheImpl(loaderConfig);
        }
        if (loaderConfig.diskCacheEnabled) {
            diskCache = new DiskICacheImpl(loaderConfig);
        }
    }

    @Override
    public Observable<Bitmap> getFromMem(Request request) {
        if (memCache == null) {
            return Observable.empty();
        }
        return memCache.getFromCache(request);
    }

    @Override
    public Observable<Bitmap> getFormDisk(Request request) {
        if (diskCache == null) {
            return Observable.empty();
        }
        return diskCache.getFromCache(request);
    }

    @Override
    public void putInMem(String key, Bitmap bitmap) {
        memCache.putInCache(key, bitmap);
    }

    @Override
    public void putInDisk(String key, Bitmap bitmap) {
        diskCache.putInCache(key, bitmap);
    }

    @Override
    public void clearAll() {
        clearMemCache();
        clearDiskCache();
    }

    @Override
    public void clearMemCache() {
        if (memCache != null) {
            memCache.clearCache();
        }
    }

    @Override
    public void clearDiskCache() {
        if (diskCache != null) {
            diskCache.clearCache();
        }
    }
}
