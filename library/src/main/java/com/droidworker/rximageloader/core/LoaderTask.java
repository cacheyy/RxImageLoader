package com.droidworker.rximageloader.core;

import android.graphics.Bitmap;
import android.util.Log;

import com.droidworker.rximageloader.core.request.Request;
import com.droidworker.rximageloader.utils.Utils;

import rx.Observable;

/**
 * In charge of creating {@link Observable} to deal with the request
 *
 * @author DroidWorkerLYF
 */
public class LoaderTask {
    private static final String TAG = "LoaderTask";

    public static Observable<Bitmap> getFromMem(Request request) {
        return ImageLoader.getInstance().getCacheManager().getFromMem(request);
    }

    public static Observable<Bitmap> getFormDisk(Request request) {
        return ImageLoader.getInstance().getCacheManager().getFormDisk(request);
    }

    public static Observable<Bitmap> getBitmap(Request request) {
        return Observable.create(subscriber -> {
            if(Utils.isUrl(request.getPath())){

            } else if(Utils.isGif(request.getPath())){

            } else {
                //This is a local file
                Bitmap bitmap = Processor.decodeSampledBitmapFromFile(request.getPath(), request
                        .getOption());
                ImageLoader.getInstance().getCacheManager().putInMem(request.getKey(), bitmap);
                ImageLoader.getInstance().getCacheManager().putInDisk(request.getKey(), bitmap);
                subscriber.onNext(bitmap);
                Log.e(TAG, "" + bitmap.toString());
            }
            subscriber.onCompleted();
        });
    }
}
