package com.droidworker.rximageloader.cache;

import com.droidworker.rximageloader.cache.interfaces.ICacheManager;

/**
 * Implements of ICacheManager,this manager in charge of initialize memory and disk cache,
 * get resource from cache, put resource into cache
 *
 * @author DroidWorkerLYF
 */
public class DroidCacheManager implements ICacheManager{
    private static DroidCacheManager INSTANCE;

    private DroidCacheManager(){

    }

    public static DroidCacheManager getInstance(){
        if(INSTANCE == null){
            synchronized (DroidCacheManager.class){
                if(INSTANCE == null){
                    INSTANCE = new DroidCacheManager();
                }
            }
        }
        return INSTANCE;
    }
}
