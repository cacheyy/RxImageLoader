package com.droidworker.rximageloader.cache.disk;

import android.graphics.Bitmap;

import com.droidworker.rximageloader.cache.interfaces.ICache;
import com.droidworker.rximageloader.core.LoaderConfig;

import java.io.File;
import java.io.IOException;

import rx.Observable;

/**
 * A LRU based disk cache which implements ICache interface
 *
 * @author DroidWorkerLYF
 */
public class DiskICacheImpl implements ICache {
    private static final int DISK_CACHE_INDEX = 0;
    /**
     * LruCache used for disk cache
     */
    private DiskLruCache mDiskLruCache;
    private final Object mDiskCacheLockObject = new Object();
    private boolean mDiskCacheStarting = true;

    public DiskICacheImpl(LoaderConfig loaderConfig) {
        initCache(loaderConfig);
    }

    @Override
    public void initCache(LoaderConfig loaderConfig) {
        synchronized (mDiskCacheLockObject) {
            if (mDiskLruCache == null || mDiskLruCache.isClosed()) {
                File diskCacheDir = new File(loaderConfig.diskCachePath);
                if (diskCacheDir != null) {
                    if (!diskCacheDir.exists()) {
                        diskCacheDir.mkdirs();
                    }
                    if (getUsableSpace(diskCacheDir) > loaderConfig.diskCacheSize) {
                        try {
                            mDiskLruCache = DiskLruCache.open(diskCacheDir, 1, 1, loaderConfig.diskCacheSize);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            mDiskCacheStarting = false;
            mDiskCacheLockObject.notifyAll();
        }
    }

    /**
     * @param path the path of disk cache
     * @return the free space of the given path
     */
    private long getUsableSpace(File path) {
        return path.getUsableSpace();
    }

    @Override
    public void clearCache() {

    }

    @Override
    public void putInCache(String key, Bitmap bitmap) {

    }

    @Override
    public Observable<Bitmap> getFromCache(String key) {
        return null;
    }
}
