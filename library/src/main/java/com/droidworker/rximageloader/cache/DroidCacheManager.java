package com.droidworker.rximageloader.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import com.droidworker.rximageloader.core.LoaderConfig;

/**
 * Implements of ICacheManager
 *
 * @author DroidWorkerLYF
 */
public class DroidCacheManager implements ICacheManager {

    @Override
    public void initMemCache() {

    }

    @Override
    public void initDiskCache() {

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
