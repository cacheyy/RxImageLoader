package com.droidworker.rximageloader.core.request;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.view.View;

import com.droidworker.rximageloader.cache.DroidCacheManager;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.functions.Action1;

/**
 * RequestManager is bind to an activity or a fragment and provide life cycle support
 * for request
 * @author DroidWorkerLYF
 */
public class RequestManager extends Fragment{
    private Map<View, Request<Bitmap>> requestMap = new HashMap<>();
    private Request<Bitmap> mCurRequest = new Request<>();

    public RequestManager load(String url){
        mCurRequest.clear();
        mCurRequest.load(url);
        return this;
    }

    public void into(View view, Action1<Float> onProgress){
        if(view == null){
            throw new IllegalArgumentException("can not load into a null object");
        }

        Request<Bitmap> request;
        //If there is already a request bind to this view, unsubscribe it first
        if(requestMap.containsKey(view)){
            request = requestMap.get(view);
            requestMap.remove(view);
            request.unsubscribe();
            request.clear();
            request.copy(mCurRequest);
        } else {
            request = new Request<>();
            request.copy(mCurRequest);
        }
        requestMap.put(view, request);

        Observable.concat(DroidCacheManager.getInstance().getFromMem(request),DroidCacheManager
                .getInstance().getFormDisk(request)).subscribe(request);
    }

    public void into(View view){
        into(view, null);
    }

}
