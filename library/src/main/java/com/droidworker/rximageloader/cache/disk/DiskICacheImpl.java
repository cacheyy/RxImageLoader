package com.droidworker.rximageloader.cache.disk;

import android.graphics.Bitmap;
import android.util.Log;

import com.droidworker.rximageloader.cache.interfaces.ICache;
import com.droidworker.rximageloader.core.LoaderConfig;
import com.droidworker.rximageloader.core.Processor;
import com.droidworker.rximageloader.core.request.Request;
import com.droidworker.rximageloader.utils.Utils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import rx.Observable;
import rx.Subscriber;

/**
 * A LRU based disk cache which implements ICache interface
 *
 * @author DroidWorkerLYF
 */
public class DiskICacheImpl implements ICache {
    private static final String TAG = "DiskCacheImpl";
    private static final int DISK_CACHE_INDEX = 0;
    /**
     * LruCache used for disk cache
     */
    private DiskLruCache mDiskLruCache;
    private final Object mDiskCacheLockObject = new Object();
    private boolean mDiskCacheStarting = true;
    private LoaderConfig mLoaderConfig;

    public DiskICacheImpl(LoaderConfig loaderConfig) {
        mLoaderConfig = loaderConfig;
        initCache(loaderConfig);
    }

    @Override
    public void initCache(LoaderConfig loaderConfig) {
        synchronized (mDiskCacheLockObject) {
            if (mDiskLruCache == null || mDiskLruCache.isClosed()) {
                File diskCacheDir = new File(loaderConfig.diskCachePath);
                if (!diskCacheDir.exists()) {
                    if(diskCacheDir.mkdirs()){
                        if(loaderConfig.isDebug){
                            Log.w(TAG, "disk cache mkdirs fail");
                        }
                    }
                }
                if (getUsableSpace(diskCacheDir) > loaderConfig.diskCacheSize) {
                    try {
                        mDiskLruCache = DiskLruCache.open(diskCacheDir, 1, 1, loaderConfig.diskCacheSize);
                    } catch (IOException e) {
                        e.printStackTrace();
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
        synchronized (mDiskCacheLockObject) {
            if (mDiskLruCache != null) {
                final String path = Utils.hashKeyForDisk(key);
                OutputStream out = null;
                try {
                    DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
                    if (snapshot == null) {
                        final DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                        if (editor != null) {
                            out = editor.newOutputStream(DISK_CACHE_INDEX);
                            bitmap.compress(mLoaderConfig.mCompressFormat,
                                    mLoaderConfig.mCompressQuality, out);
                            editor.commit();
                            out.close();
                        }
                    } else {
                        snapshot.getInputStream(DISK_CACHE_INDEX).close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public Observable<Bitmap> getFromCache(Request request) {
        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {

                final String path = Utils.hashKeyForDisk(request.getKey());
                Bitmap bitmap;
                synchronized (mDiskCacheLockObject) {
                    while (mDiskCacheStarting) {
                        try {
                            mDiskCacheLockObject.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (mDiskLruCache != null) {
                        InputStream inputStream = null;
                        try {
                            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(path);
                            if (snapshot != null) {
                                inputStream = snapshot.getInputStream(DISK_CACHE_INDEX);
                                if (inputStream != null) {
                                    FileDescriptor fd = ((FileInputStream) inputStream).getFD();
                                    bitmap = Processor.decodeBitmapFromFileDescriptor(fd, request.getOption());
                                    subscriber.onNext(bitmap);
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            subscriber.onError(e);
                        } finally {
                            if (inputStream != null) {
                                try {
                                    inputStream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }
                }
                subscriber.onCompleted();
            }
        });
    }
}
