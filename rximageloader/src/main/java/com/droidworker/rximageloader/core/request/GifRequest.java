package com.droidworker.rximageloader.core.request;

import android.graphics.drawable.AnimationDrawable;
import android.view.View;

import rx.Observable;

/**
 * @author DroidWorkerLYF
 */
public class GifRequest extends Request<AnimationDrawable> {

    @Override
    public void into(View view) {

    }

    @Override
    public Observable<AnimationDrawable> observable(View view) {
        return null;
    }

    @Override
    public void onNext(AnimationDrawable animationDrawable) {

    }
}
