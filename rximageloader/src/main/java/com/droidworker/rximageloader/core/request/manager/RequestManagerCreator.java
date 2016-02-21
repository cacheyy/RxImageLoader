package com.droidworker.rximageloader.core.request.manager;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.droidworker.rximageloader.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Create a RequestManager or return an exist one
 *
 * @author DroidWorkerLYF
 */
public class RequestManagerCreator {
    private static final String FRAGMENT_TAG = RequestManagerCreator.class.getSimpleName();
    private static final RequestManagerCreator INSTANCE = new RequestManagerCreator();
    private Map<FragmentManager, RequestManagerFragment> requestManagerMap = new
            HashMap<>();
    private Map<android.support.v4.app.FragmentManager, SupportRequestManagerFragment> supportRequestManagerMap
            = new HashMap<>();
    private RequestManager applicationRequestManager;

    private RequestManagerCreator() {

    }

    /**
     * @return the single instance of {@link RequestManagerCreator}
     */
    public static RequestManagerCreator get() {
        return INSTANCE;
    }

    public RequestManager get(Activity activity) {
        FragmentManager fm = activity.getFragmentManager();
        if (!requestManagerMap.containsKey(fm)) {
            return getRequestManager(fm);
        }
        return requestManagerMap.get(fm).getRequestManager();
    }

    public RequestManager get(Fragment fragment) {
        FragmentManager fm;
        if (Utils.hasJellyBeanMR1()) {
            fm = fragment.getChildFragmentManager();
        } else {
            fm = fragment.getFragmentManager();
        }
        if (!requestManagerMap.containsKey(fm)) {
            return getRequestManager(fm);
        }
        return requestManagerMap.get(fm).getRequestManager();
    }

    public RequestManager get(Context context) {
        if(context instanceof FragmentActivity){
            return get((FragmentActivity)context);
        } else if(context instanceof Activity){
            return get((Activity) context);
        }
        return getApplicationRequestManager();
    }

    public RequestManager getApplicationRequestManager(){
        applicationRequestManager.unsubscribeAll();
        applicationRequestManager.onDestroy();
        return applicationRequestManager;
    }

    private RequestManager getRequestManager(FragmentManager fm) {
        RequestManagerFragment fragment = new RequestManagerFragment();
        requestManagerMap.put(fm, fragment);
        fm.beginTransaction().add(fragment, FRAGMENT_TAG).commitAllowingStateLoss();
        return fragment.getRequestManager();
    }

    public RequestManager get(android.support.v4.app.FragmentActivity activity) {
        android.support.v4.app.FragmentManager fm = activity.getSupportFragmentManager();
        if (!supportRequestManagerMap.containsKey(fm)) {
            return getSupportRequestManager(fm);
        }
        return supportRequestManagerMap.get(fm).getRequestManager();
    }

    public RequestManager get(android.support.v4.app.Fragment fragment) {
//        android.support.v4.app.FragmentManager fm = fragment.getChildFragmentManager();
//
//        if (!supportRequestManagerMap.containsKey(fm)) {
//            return getSupportRequestManager(fm);
//        }
        return get(fragment.getActivity());
    }

    private RequestManager getSupportRequestManager(android.support.v4.app.FragmentManager fm) {
        SupportRequestManagerFragment fragment = new SupportRequestManagerFragment();
        supportRequestManagerMap.put(fm, fragment);
        fm.beginTransaction().add(fragment, FRAGMENT_TAG).commitAllowingStateLoss();
        return fragment.getRequestManager();
    }

}
