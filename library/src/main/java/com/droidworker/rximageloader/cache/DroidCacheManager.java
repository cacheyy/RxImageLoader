package com.droidworker.rximageloader.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.LruCache;

import com.droidworker.rximageloader.cache.disk.DiskLruCache;
import com.droidworker.rximageloader.cache.interfaces.ICacheManager;
import com.droidworker.rximageloader.core.LoaderConfig;

/**
 * Implements of ICacheManager,this manager in charge of initialize memory and disk cache,
 * get resource from cache, put resource into cache
 *
 * @author DroidWorkerLYF
 */
public class DroidCacheManager implements ICacheManager {
    private static DroidCacheManager INSTANCE;
    /** 内存缓存 */
    private LruCache<String, BitmapDrawable> mMemoryCache;
    /** 磁盘缓存 */
    private DiskLruCache mDiskLruCache;

    private DroidCacheManager(){

    }

    public static DroidCacheManager getInstance(){
        if(INSTANCE == null){
            synchronized (INSTANCE){
                if(INSTANCE == null){
                    INSTANCE = new DroidCacheManager();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void initMemCache() {

    }

    @Override
    public void initDiskCache() {

    }

    @Override
    public void clearMemCache() {

    }

    @Override
    public void clearDiskCache() {

    }

    @Override
    public BitmapDrawable getBitmapFromMemory(String path) {
        return null;
    }

    @Override
    public void putBitmap2Mem(String path, BitmapDrawable drawable) {

    }

    @Override
    public Bitmap getBitmapFromDisk(String path, int reqWidth, int reqHeight, LoaderConfig option) {
        return null;
    }

    @Override
    public Bitmap getBitmapFromReusableSet(BitmapFactory.Options options) {
        return null;
    }

    @Override
    public Bitmap processBitmap(String path, int reqWidth, int reqHeight, LoaderConfig option) {
        return null;
    }
}
