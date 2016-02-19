package com.droidworker.rximageloader.core.strategy;

/**
 * @author DroidWorkerLYF
 */
public class DiskCacheAllStrategy implements DiskCacheStrategy {

    @Override
    public boolean cacheOrigin() {
        return true;
    }

    @Override
    public boolean cacheRealSize() {
        return true;
    }

    @Override
    public boolean cacheTransform() {
        return true;
    }
}
