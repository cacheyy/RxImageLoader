package com.droidworker.rximageloader.core.strategy;

/**
 * @author DroidWorkerLYF
 */
public class DiskCacheOriginStrategy implements DiskCacheStrategy {
    @Override
    public boolean cacheOrigin() {
        return true;
    }

    @Override
    public boolean cacheRealSize() {
        return false;
    }

    @Override
    public boolean cacheTransform() {
        return false;
    }
}
