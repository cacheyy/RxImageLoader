package com.droidworker.rximageloader.core.transformation;


import android.graphics.Bitmap;

import com.droidworker.rximageloader.core.key;

import rx.functions.Func1;

/**
 * @author DroidWorkerLYF
 */
public abstract class Transform implements Func1<Bitmap, Bitmap>, key {
    protected Bitmap.Config mConfig = Bitmap.Config.ARGB_8888;

    public Transform() {

    }

    public Transform(Bitmap.Config config) {
        this.mConfig = config;
    }

    @Override
    public String getKey() {
        return getClass().getSimpleName();
    }
}
