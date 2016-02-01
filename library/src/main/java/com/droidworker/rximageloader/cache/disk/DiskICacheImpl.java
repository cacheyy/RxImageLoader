package com.droidworker.rximageloader.cache.disk;

import android.graphics.Bitmap;

import com.droidworker.rximageloader.cache.interfaces.ICache;

import rx.Observable;

/**
 * @author DroidWorkerLYF
 */
public class DiskICacheImpl implements ICache{
    @Override
    public void initCache() {

    }

    @Override
    public void clearCache() {

    }

    @Override
    public void putInCache(Bitmap bitmap) {

    }

    @Override
    public Observable<Bitmap> getFromCache() {
        return null;
    }
}
