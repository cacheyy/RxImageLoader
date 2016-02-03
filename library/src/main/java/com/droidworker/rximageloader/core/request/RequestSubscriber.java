package com.droidworker.rximageloader.core.request;

import rx.Subscriber;

/**
 * A custom subscriber added ability to provide progress
 *
 * @author DroidWorkerLYF
 */
public class RequestSubscriber<T> extends Subscriber<T> {

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onNext(T t) {

    }

    /**
     * used for notify progress change
     *
     * @param percent current progress
     */
    public void onProgress(float percent) {

    }
}
