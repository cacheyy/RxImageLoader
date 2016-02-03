package com.droidworker.rximageloader.core.request;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;

import com.droidworker.rximageloader.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Create a RequestManager or return an exist one
 *
 * @author DroidWorkerLYF
 */
public class RequestManagerCreator {
    private static final String FRAGMENT_TAG = "com.droidworker.rximageloader.request";
    private static final RequestManagerCreator INSTANCE = new RequestManagerCreator();
    private Map<FragmentManager, RequestManager> requestManagerMap = new
            HashMap<>();
    private Map<android.support.v4.app.FragmentManager, SupportRequestManager> supportRequestManagerMap
            = new HashMap<>();

    private RequestManagerCreator() {

    }

    public static RequestManagerCreator get() {
        return INSTANCE;
    }

    public RequestManager get(Activity activity) {
        FragmentManager fm = activity.getFragmentManager();
        if (!requestManagerMap.containsKey(fm)) {
            return getRequestManager(fm);
        }
        return requestManagerMap.get(fm);
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
        return requestManagerMap.get(fm);
    }

    public RequestManager get(Context context) {
        return get((Activity) context);
    }

    private RequestManager getRequestManager(FragmentManager fm) {
        RequestManager fragment = new RequestManager();
        requestManagerMap.put(fm, fragment);
        fm.beginTransaction().add(fragment, FRAGMENT_TAG).commit();
        return fragment;
    }

    public SupportRequestManager get(android.support.v4.app.FragmentActivity activity) {
        android.support.v4.app.FragmentManager fm = activity.getSupportFragmentManager();
        if (!supportRequestManagerMap.containsKey(fm)) {
            return getSupportRequestManager(fm);
        }
        return supportRequestManagerMap.get(fm);
    }

    public SupportRequestManager get(android.support.v4.app.Fragment fragment) {
        android.support.v4.app.FragmentManager fm = fragment.getChildFragmentManager();
        if (!supportRequestManagerMap.containsKey(fm)) {
            return getSupportRequestManager(fm);
        }
        return supportRequestManagerMap.get(fm);
    }

    private SupportRequestManager getSupportRequestManager(android.support.v4.app.FragmentManager fm) {
        SupportRequestManager fragment = new SupportRequestManager();
        supportRequestManagerMap.put(fm, fragment);
        fm.beginTransaction().add(fragment, FRAGMENT_TAG).commit();
        return fragment;
    }

}
