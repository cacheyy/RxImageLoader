package com.droidworker.rximageloader.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.droidworker.rximageloader.cache.disk.DiskICacheImpl;
import com.droidworker.rximageloader.cache.interfaces.ICache;
import com.droidworker.rximageloader.cache.interfaces.ICacheManager;
import com.droidworker.rximageloader.cache.memory.MemoryICacheImpl;
import com.droidworker.rximageloader.core.LoaderConfig;
import com.droidworker.rximageloader.core.request.Request;
import com.droidworker.rximageloader.utils.Utils;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import rx.Observable;

/**
 * Implements of ICacheManager,this manager is in charge of create memory and disk cache,
 * get resource from cache, put resource into cache
 *
 * @author DroidWorkerLYF
 */
public class DroidCacheManager implements ICacheManager {
    private static final String TAG = "DroidCacheManager";
    private ICache memCache;
    private ICache diskCache;
    private Set<SoftReference<Bitmap>> mReusableBitmaps;

    public DroidCacheManager() {
    }

    @Override
    public void init(LoaderConfig loaderConfig) {
        if (loaderConfig.memCacheEnabled) {
            mReusableBitmaps = Collections.synchronizedSet(new HashSet<>());
            memCache = new MemoryICacheImpl(loaderConfig, mReusableBitmaps);
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
    public Bitmap getBitmapFromReusableSet(BitmapFactory.Options options) {
        Bitmap bitmap = null;

        if (mReusableBitmaps != null && !mReusableBitmaps.isEmpty()) {
            synchronized (this) {
                final Iterator<SoftReference<Bitmap>> iterator = mReusableBitmaps.iterator();
                Bitmap item;

                while (iterator.hasNext()) {
                    item = iterator.next().get();

                    if (null != item && item.isMutable()) {
                        if (canUseForInBitmap(item, options)) {
                            bitmap = item;
                            // 从reusable set中移除，避免再次被使用
                            iterator.remove();
                            Log.i(TAG, "Find reusable bitmap");
                            break;
                        }
                    } else {
                        iterator.remove();
                    }
                }
            }
        }
        return bitmap;
    }

    private boolean canUseForInBitmap(Bitmap bitmap, BitmapFactory.Options options) {
        if (!Utils.hasKitKat()) {
            // 早期版本是必须大小完全一致的
            // inSampleSize必须为1
            return bitmap.getWidth() == options.outWidth && bitmap.getHeight() == options.outHeight && options.inSampleSize == 1;
        }

        // android4.4开始如果新的bitmap小于复用的bitmap也是可以的
        int width = options.outWidth / options.inSampleSize;
        int height = options.outHeight / options.inSampleSize;
        int byteCount = width * height * getBytesPerPixel(bitmap.getConfig());
        return byteCount <= bitmap.getByteCount();
    }

    private int getBytesPerPixel(Bitmap.Config config) {
        if (config == Bitmap.Config.ARGB_8888) {
            return 4;
        } else if (config == Bitmap.Config.RGB_565) {
            return 2;
        } else if (config == Bitmap.Config.ARGB_4444) {
            return 2;
        } else if (config == Bitmap.Config.ALPHA_8) {
            return 1;
        }
        return 1;
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
