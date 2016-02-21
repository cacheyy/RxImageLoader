package com.droidworker.rximageloader.core.request;

import android.graphics.Bitmap;
import android.view.View;

import rx.Observable;

/**
 * @author DroidWorkerLYF
 */
public class GifRequest extends Request {

    @Override
    public void into(View view) {

    }

    @Override
    public Observable<Bitmap> observable(View view) {
        return null;
    }

    @Override
    public void onNext(Bitmap bitmap) {

    }
}
