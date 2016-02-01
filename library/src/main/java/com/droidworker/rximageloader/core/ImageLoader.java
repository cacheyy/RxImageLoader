package com.droidworker.rximageloader.core;

import android.content.Context;

import com.droidworker.rximageloader.core.request.RequestManager;

/**
 *
 * @author DroidWorkerLYF
 */
public class ImageLoader {
    private static ImageLoader INSTANCE;

    private ImageLoader(){

    }

    public void setMemoryCache(){

    }

    public void setDiskCache(){

    }

    public static ImageLoader getInstance(){
        if(INSTANCE == null){
            synchronized (ImageLoader.class){
                if(INSTANCE == null){
                    INSTANCE = new ImageLoader();
                }
            }
        }
        return INSTANCE;
    }

    public static RequestManager with(Context context){
        return null;
    }

    public void clearCache(){
        clearMemory();
        clearDisk();
    }

    public void clearMemory(){

    }

    public void clearDisk(){

    }

    public void setGloableLoaderConfig(){

    }
}
