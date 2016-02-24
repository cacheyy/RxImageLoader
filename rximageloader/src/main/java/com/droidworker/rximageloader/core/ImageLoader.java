package com.droidworker.rximageloader.core;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;

import com.droidworker.rximageloader.core.request.manager.RequestManager;
import com.droidworker.rximageloader.core.request.manager.RequestManagerCreator;

/**
 * @author DroidWorkerLYF
 */
public class ImageLoader {

    private ImageLoader() {
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

    public static RequestManager with(android.support.v4.app.FragmentActivity activity) {
        isReady();
        return RequestManagerCreator.get().get(activity);
    }

    public static RequestManager with(android.support.v4.app.Fragment fragment) {
        isReady();
        return RequestManagerCreator.get().get(fragment);
    }

    private static void isReady() {
        if (LoaderCore.getGlobalConfig() == null) {
            throw new IllegalStateException("You should set a global loader config before use");
        }
    }

    public static void clearCache() {
        LoaderCore.clearCache();
    }

    public static void clearMemory() {
        LoaderCore.clearMemory();
    }

    public static void clearDisk() {
        LoaderCore.clearDisk();
    }

    public static void setGlobalLoaderConfig(LoaderConfig loaderConfig) {
        LoaderCore.setGlobalLoaderConfig(loaderConfig);
    }
}
