package com.droidworker.rximageloader.cache;

import com.droidworker.rximageloader.cache.disk.DiskICacheImpl;
import com.droidworker.rximageloader.cache.interfaces.ICache;
import com.droidworker.rximageloader.cache.interfaces.ICacheManager;
import com.droidworker.rximageloader.cache.memory.MemoryICacheImpl;
import com.droidworker.rximageloader.core.LoaderConfig;

/**
 * Implements of ICacheManager,this manager is in charge of create memory and disk cache,
 * get resource from cache, put resource into cache
 *
 * @author DroidWorkerLYF
 */
public class DroidCacheManager implements ICacheManager{
    private static DroidCacheManager INSTANCE;
    private ICache memCache;
    private ICache diskCache;

    private DroidCacheManager(LoaderConfig loaderConfig){
        init(loaderConfig);
    }

    public static DroidCacheManager getInstance(LoaderConfig loaderConfig){
        if(INSTANCE == null){
            synchronized (DroidCacheManager.class){
                if(INSTANCE == null){
                    INSTANCE = new DroidCacheManager(loaderConfig);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void init(LoaderConfig loaderConfig) {
        if(loaderConfig.memCacheEnabled){
            memCache = new MemoryICacheImpl(loaderConfig);
        }
        if(loaderConfig.diskCacheEnabled){
            diskCache = new DiskICacheImpl(loaderConfig);
        }
    }

    @Override
    public void clearAll() {
        clearMemCache();
        clearDiskCache();
    }

    @Override
    public void clearMemCache() {
        if(memCache != null){
            memCache.clearCache();
        }
    }

    @Override
    public void clearDiskCache() {
        if(diskCache != null){
            diskCache.clearCache();
        }
    }
}
