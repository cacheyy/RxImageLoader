package com.droidworker.rximageloader.core.strategy;

/**
 * @author DroidWorkerLYF
 */
public interface DiskCacheStrategy {

    boolean cacheOrigin();

    boolean cacheRealSize();

    boolean cacheTransform();
}
