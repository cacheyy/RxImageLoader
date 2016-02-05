package com.droidworker.rximageloader.core.request;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import com.droidworker.rximageloader.core.ImageLoader;
import com.droidworker.rximageloader.core.LoaderTask;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * RequestManager is bind to an activity or a fragment and provide life cycle support
 * for request
 *
 * @author DroidWorkerLYF
 */
public class RequestManager extends Fragment {
    private static final String TAG = "RequestManager";
    private Map<View, Request<Bitmap>> requestMap = new HashMap<>();

    public Request<Bitmap> load(String url) {
        Request<Bitmap> request = new Request<>(ImageLoader.getInstance().getGlobalConfig());
        request.setNotifySubscriber(new Subscriber<Request>() {
            @Override
            public void onCompleted() {
                unsubscribe();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Request request) {
                Log.e(TAG, "request created");
                into(request);
            }
        });
        return request.load(url);
    }

    private void into(Request<Bitmap> request) {
        final View view = request.getAttachedView();
        if (view == null) {
            return;
        }
        Log.e(TAG, "request received");
        if (requestMap.containsKey(view)) {
            Request oldRequest = requestMap.get(view);
            requestMap.remove(view);
            oldRequest.unsubscribe();
            oldRequest.clear();
        }
        requestMap.put(view, request);

        Observable.concat(LoaderTask.getFromMem(request), LoaderTask.getFormDisk(request),
                LoaderTask.getBitmap(request))
                .takeFirst(bitmap -> {
                    if(bitmap == null || bitmap.isRecycled()){
                        return false;
                    }
                    return true;
                })
                .subscribeOn(Schedulers.io()).observeOn
                (AndroidSchedulers.mainThread())
                .subscribe
                        (request);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
        for (Map.Entry<View, Request<Bitmap>> viewRequestEntry : requestMap.entrySet()) {
            viewRequestEntry.getValue().unsubscribe();
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
