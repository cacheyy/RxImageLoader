package com.droidworker.rximageloader.core.request;

import android.graphics.Bitmap;
import android.widget.ImageView;

import rx.Subscription;

/**
 * @author DroidWorkerLYF
 */
public class Request {
    private String mKey;
    private Option mOption;

    public Request() {

    }

    public Request with() {
        return this;
    }

    public Request load(String url) {
        this.mKey = url;
        return this;
    }

    public Option getOption(){
        return mOption;
    }

    public String getKey(){
        return mKey;
    }

    public Subscription into(ImageView imageView) {
        return null;
    }

    public class Option {
        public int reqWidth;
        public int reqHeight;
        public Bitmap.Config config;
    }
}
