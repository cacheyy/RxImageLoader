package com.droidworker.rximageloader.core;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;

import com.droidworker.rximageloader.cache.DroidCacheManager;
import com.droidworker.rximageloader.core.request.RequestManager;
import com.droidworker.rximageloader.core.request.RequestManagerCreator;
import com.droidworker.rximageloader.core.request.SupportRequestManager;

/**
 * @author DroidWorkerLYF
 */
public class ImageLoader {
    private static ImageLoader INSTANCE;
    private LoaderConfig mGlobalConfig;

    private ImageLoader() {
        throw new IllegalStateException("No instance");
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
        isReady();
        return RequestManagerCreator.get().get(context);
    }

    public static RequestManager with(Activity activity) {
        isReady();
        return RequestManagerCreator.get().get(activity);
    }

    public static RequestManager with(Fragment fragment) {
        isReady();
        return RequestManagerCreator.get().get(fragment);
    }

    public static SupportRequestManager with(android.support.v4.app.FragmentActivity activity) {
        isReady();
        return RequestManagerCreator.get().get(activity);
    }

    public static SupportRequestManager with(android.support.v4.app.Fragment fragment) {
        isReady();
        return RequestManagerCreator.get().get(fragment);
    }

    private static void isReady() {
        if (INSTANCE.mGlobalConfig == null) {
            throw new IllegalStateException("You should set a global loader config before use");
        }
    }

    public void clearCache() {
        DroidCacheManager.getInstance().clearAll();
    }

    public void clearMemory() {
        DroidCacheManager.getInstance().clearMemCache();
    }

    public void clearDisk() {
        DroidCacheManager.getInstance().clearDiskCache();
    }

    public void setGlobalLoaderConfig(LoaderConfig loaderConfig) {
        if (mGlobalConfig != null) {
            throw new IllegalStateException("Global loader config has been set");
        }
        this.mGlobalConfig = loaderConfig;
        DroidCacheManager.getInstance().init(loaderConfig);
    }
}
