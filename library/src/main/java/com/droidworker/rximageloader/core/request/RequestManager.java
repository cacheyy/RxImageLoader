package com.droidworker.rximageloader.core.request;

import android.app.FragmentManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author DroidWorkerLYF
 */
public class RequestManager {
    private static final RequestManager INSTANCE = new RequestManager();
    private Map<FragmentManager, RequestManagerFragment> requestManagerFragmentMap = new
            HashMap<>();

    private RequestManager(){

    }

    public RequestManager getInstance(){
        return INSTANCE;
    }

//    public RequestManager get(Context context) {
//        if (context == null) {
//            throw new IllegalArgumentException("Context can not be null");
//        } else if (Utils.isOnMainThread() && !(context instanceof Application)) {
//            if (context instanceof Activity) {
//                return get(context);
//            } else if (context instanceof ContextWrapper) {
//                return get(((ContextWrapper) context).getBaseContext());
//            }
//        }
//
//        return INSTANCE;
//    }
}
