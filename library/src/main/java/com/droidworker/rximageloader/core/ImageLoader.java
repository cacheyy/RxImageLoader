package com.droidworker.rximageloader.core;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;

import com.droidworker.rximageloader.cache.DroidCacheManager;
import com.droidworker.rximageloader.core.request.RequestManager;

/**
 * @author DroidWorkerLYF
 */
public class ImageLoader {
    private static ImageLoader INSTANCE;
    private LoaderConfig mGlobalConfig;

    private ImageLoader() {

    }

    /**
     * should only used for set properties,if you want to load resource,use with(Context context)
     *
     * @return a singleton of ImageLoader
     */
    public static ImageLoader getInstance() {
        if (INSTANCE == null) {
            synchronized (ImageLoader.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ImageLoader();
                }
            }
        }
        return INSTANCE;
    }

    public static RequestManager with(Context context) {
        if(INSTANCE.mGlobalConfig == null){
            throw new IllegalStateException("You should set a global loader config before use");
        }
        return null;
    }

    public static RequestManager with(Activity activity){
        return null;
    }

    public static RequestManager with(Fragment fragment){
        return null;
    }

    public void clearCache() {
        DroidCacheManager.getInstance(mGlobalConfig).clearAll();
    }

    public void clearMemory() {
        DroidCacheManager.getInstance(mGlobalConfig).clearMemCache();
    }

    public void clearDisk() {
        DroidCacheManager.getInstance(mGlobalConfig).clearDiskCache();
    }

    public void setGlobalLoaderConfig(LoaderConfig loaderConfig) {
        this.mGlobalConfig = loaderConfig;
        DroidCacheManager.getInstance(loaderConfig);
    }
}
