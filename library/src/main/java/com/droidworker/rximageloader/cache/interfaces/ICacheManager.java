package com.droidworker.rximageloader.cache.interfaces;

import com.droidworker.rximageloader.core.LoaderConfig;

/**
 * define the behaviour of a cache manager
 *
 * @author DroidWorkerLYF
 */
public interface ICacheManager {

    void init(LoaderConfig loaderConfig);

    void clearAll();

    void clearMemCache();

    void clearDiskCache();
}
