package com.droidworker.rximageloader.core.request;

import android.app.Fragment;
import android.util.Log;
import android.view.View;

import com.droidworker.rximageloader.core.LoaderCore;
import com.droidworker.rximageloader.core.LoaderTask;

import java.util.HashMap;
import java.util.Map;

/**
 * RequestManager is bind to an activity or a fragment and provide life cycle support
 * for request
 *
 * @author DroidWorkerLYF
 */
public class RequestManager extends Fragment {
    private static final String TAG = "RequestManager";
    private Map<View, Request> requestMap = new HashMap<>();

    /**
     * Create a request and set the load path
     *
     * @param path the path of resource
     * @return a {@link Request}
     */
    public Request load(String path) {
        Request request = new Request();
        request.setNotifySubscriber(request1 -> into(request1));
        return request.load(path);
    }

    /**
     * Start a request
     *
     * @param request a configured request
     */
    private void into(Request request) {
        final View view = request.getAttachedView();
        if (view == null) {
            return;
        }
        if (requestMap.containsKey(view)) {
            Request oldRequest = requestMap.get(view);
            requestMap.remove(view);
            oldRequest.unsubscribe();
            oldRequest.clear();
        }
        requestMap.put(view, request);
        LoaderTask.newTask(request).subscribe(request);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.e(TAG, "onTrimMemory");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.e(TAG, "onLowMemory");

        unsubscribeAll();
        LoaderCore.clearMemory();
    }

    /**
     * Unsubscribe all the subscribers
     */
    private void unsubscribeAll() {
        for (Map.Entry<View, Request> viewRequestEntry : requestMap.entrySet()) {
            viewRequestEntry.getValue().unsubscribe();
        }
    }
}
