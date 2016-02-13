package com.droidworker.test;

import android.app.Application;

import com.droidworker.rximageloader.core.ImageLoader;
import com.droidworker.rximageloader.core.LoaderConfig;

/**
 * @author DroidWorkerLYF
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        LoaderConfig loaderConfig = LoaderConfig.createDefaultConfig(getApplicationContext());
        ImageLoader.setGlobalLoaderConfig(loaderConfig);
    }

}
