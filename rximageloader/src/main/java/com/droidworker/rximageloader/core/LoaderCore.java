package com.droidworker.rximageloader.core;

import com.droidworker.rximageloader.cache.DroidCacheManager;
import com.droidworker.rximageloader.cache.interfaces.ICacheManager;

/**
 * This class is used to storage objects
 * @author DroidWorkerLYF
 */
public class LoaderCore {
    private static LoaderConfig mGlobalConfig;
    private static DroidCacheManager cacheManager = new DroidCacheManager();

    public static ICacheManager getCacheManager(){
        return cacheManager;
    }

    public static LoaderConfig getGlobalConfig(){
        return mGlobalConfig;
    }

    public static void clearCache() {
        cacheManager.clearAll();
    }

    public static void clearMemory() {
        cacheManager.clearMemCache();
    }

    public static void clearDisk() {
        cacheManager.clearDiskCache();
    }

    public static void setGlobalLoaderConfig(LoaderConfig loaderConfig) {
        if (mGlobalConfig != null) {
            throw new IllegalStateException("Global loader config has been set");
        }
        mGlobalConfig = loaderConfig;
        cacheManager.init(loaderConfig);
    }
}
