package com.droidworker.rximageloader.core;

import com.droidworker.rximageloader.cache.DroidCacheManager;
import com.droidworker.rximageloader.cache.interfaces.ICacheManager;
import com.droidworker.rximageloader.core.strategy.DiskCacheAllStrategy;
import com.droidworker.rximageloader.core.strategy.DiskCacheStrategy;

/**
 * This class is used to storage objects
 *
 * @author DroidWorkerLYF
 */
public class LoaderCore {
    /**
     * The global config
     */
    private static LoaderConfig mGlobalConfig;
    /**
     * Cache manager
     */
    private static DroidCacheManager cacheManager = new DroidCacheManager();

    public static ICacheManager getCacheManager() {
        return cacheManager;
    }

    public static LoaderConfig getGlobalConfig() {
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

    public static DiskCacheStrategy getDiskCacheStrategy(){
        if(mGlobalConfig == null){
            return new DiskCacheAllStrategy();
        }
        return mGlobalConfig.mDiskCacheStrategy;
    }
}
