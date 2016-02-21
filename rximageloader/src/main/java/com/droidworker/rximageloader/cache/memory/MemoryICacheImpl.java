package com.droidworker.rximageloader.cache.memory;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;

import com.droidworker.rximageloader.cache.interfaces.ICache;
import com.droidworker.rximageloader.core.LoaderConfig;
import com.droidworker.rximageloader.core.request.Request;
import com.droidworker.rximageloader.utils.Utils;

import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.Set;

import rx.Observable;
import rx.Subscriber;

/**
 * A LRU based memory cache which implements ICache interface
 *
 * @author DroidWorkerLYF
 */
public class MemoryICacheImpl implements ICache {
    private static final String TAG = MemoryICacheImpl.class.getSimpleName();
    /**
     * LruCache used for memory cache
     */
    private LruCache<String, Bitmap> mMemoryCache;
    /**
     * A set of bitmaps support inBitmap
     */
    private Set<SoftReference<Bitmap>> mReusableBitmaps;

    /**
     * Constructor of memory cache
     *
     * @param loaderConfig the global config which contains the memory cache size
     */
    public MemoryICacheImpl(LoaderConfig loaderConfig, Set<SoftReference<Bitmap>> reusableBitmaps) {
        mReusableBitmaps = reusableBitmaps;
        initCache(loaderConfig);
    }

    @Override
    public void initCache(LoaderConfig loaderConfig) {
        mMemoryCache = new LruCache<String, Bitmap>(loaderConfig.memCacheSize) {

            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                //Only support android 4.0 and above, so the BitmapFactory.Options inBitmap is
                // all available. we don't need recycle the bitmap here, instead, we put them in a
                // set, and reuse it when needed
                mReusableBitmaps.add(new SoftReference<>(oldValue));
            }

            @Override
            protected int sizeOf(String key, Bitmap value) {
                final int bitmapSize = getBitmapSize(value) / 1024;
                return bitmapSize == 0 ? 1 : bitmapSize;
            }
        };
    }

    /**
     * @param value a bitmap
     * @return size of bitmap
     */
    public int getBitmapSize(Bitmap value) {

        if (Utils.hasKitKat()) {
            try {
                return value.getAllocationByteCount();
            } catch (NullPointerException e) {
                // Do nothing.
            }
        }

        return value.getHeight() * value.getRowBytes();
    }

    @Override
    public void clearCache() {
        if (mMemoryCache != null) {
            mMemoryCache.evictAll();
        }
        clearReusableBitmaps();
    }

    /**
     * Clear the reusable bitmaps' set and recycle bitmaps
     */
    private void clearReusableBitmaps() {
        if (mReusableBitmaps != null && !mReusableBitmaps.isEmpty()) {
            synchronized (MemoryICacheImpl.class) {
                final Iterator<SoftReference<Bitmap>> iterator = mReusableBitmaps.iterator();
                Bitmap item;

                while (iterator.hasNext()) {
                    iterator.next().get().recycle();
                }
                mReusableBitmaps.clear();
            }
        }
    }

    @Override
    public void putInCache(String key, Bitmap bitmap) {
        if (TextUtils.isEmpty(key) || bitmap == null) {
            return;
        }
        if (mMemoryCache == null) {
            return;
        }
        Bitmap pre = mMemoryCache.get(key);
        if(pre == null || pre != bitmap){
            mMemoryCache.put(key, bitmap);
        }
    }

    @Override
    public Observable<Bitmap> getFromCache(Request request) {
        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                if(subscriber.isUnsubscribed()){
                    return;
                }
                Log.i(TAG, "search memory");
                subscriber.onNext(mMemoryCache.get(request.getKey()));
                subscriber.onCompleted();
            }
        });
    }
}
