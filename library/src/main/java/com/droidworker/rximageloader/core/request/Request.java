package com.droidworker.rximageloader.core.request;

import android.widget.ImageView;

import rx.Subscription;

/**
 * @author DroidWorkerLYF
 */
public class Request {
    private String url;

    public Request() {

    }

    public Request with() {
        return this;
    }

    public Request load(String url) {
        this.url = url;
        return this;
    }

    public Subscription into(ImageView imageView) {
        return null;
    }
}
