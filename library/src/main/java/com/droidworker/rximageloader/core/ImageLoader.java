package com.droidworker.rximageloader.core;

import android.content.Context;

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

    public void clearCache() {
        clearMemory();
        clearDisk();
    }

    public void clearMemory() {
    }

    public void clearDisk() {
    }

    public void setGlobalLoaderConfig(LoaderConfig loaderConfig) {
        this.mGlobalConfig = loaderConfig;
    }
}
