package com.droidworker.rximageloader.core.request;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.view.View;

import com.droidworker.rximageloader.cache.DroidCacheManager;

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
    private Map<View, Request<Bitmap>> requestMap = new HashMap<>();

    public Request<Bitmap> load(String url) {
        Request<Bitmap> request = new Request<>();
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
        if (requestMap.containsKey(view)) {
            request = requestMap.get(view);
            requestMap.remove(view);
            request.unsubscribe();
            request.clear();
        }
        requestMap.put(view, request);

        Observable.concat(DroidCacheManager.getInstance().getFromMem(request), DroidCacheManager
                .getInstance().getFormDisk(request)).subscribeOn(Schedulers.io()).observeOn
                (AndroidSchedulers.mainThread())
                .subscribe
                        (request);
    }

    @Override
    public void onPause() {
        super.onPause();

        for (Map.Entry<View, Request<Bitmap>> viewRequestEntry : requestMap.entrySet()) {
            viewRequestEntry.getValue().unsubscribe();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
