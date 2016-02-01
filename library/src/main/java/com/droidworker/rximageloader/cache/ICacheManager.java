package com.droidworker.rximageloader.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import com.droidworker.rximageloader.core.LoaderConfig;

/**
 * Interface of cache manager
 *
 * @author DroidWorkerLYF
 */
public interface ICacheManager {

    /**
     * initialize the memory cache to be used
     */
    void initMemCache();

    /**
     * initialize the disk cache to be used
     */
    void initDiskCache();

    /**
     * get bitmap according to the path in memory
     *
     * @param path the path of request resource
     * @return a BitmapDrawable
     */
    BitmapDrawable getBitmapFromMemory(String path);

    /**
     * put resource in memory cache
     *
     * @param path     the path of request resource, uesd as a key
     * @param drawable the resource needed to put in
     */
    void putBitmap2Mem(String path, BitmapDrawable drawable);

    Bitmap getBitmapFromDisk(String path, int reqWidth, int reqHeight, LoaderConfig option);

    Bitmap getBitmapFromReusableSet(BitmapFactory.Options options);


    Bitmap processBitmap(String path, int reqWidth, int reqHeight, LoaderConfig option);

}
