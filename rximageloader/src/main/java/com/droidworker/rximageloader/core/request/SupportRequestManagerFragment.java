package com.droidworker.rximageloader.core.request;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.droidworker.rximageloader.core.LoaderCore;

/**
 * SupportRequestManager is bind to an activity or a fragment and provide life cycle support
 * for request
 *
 * @author DroidWorkerLYF
 */
public class SupportRequestManagerFragment extends Fragment {
    private static final String TAG = "SRequestManagerFragment";
    private RequestManager mRequestManager;

    public SupportRequestManagerFragment(){
        if(mRequestManager == null){
            synchronized (this){
                if(mRequestManager == null){
                    mRequestManager = new RequestManager();
                }
            }
        }
    }

    public RequestManager getRequestManager(){
        return mRequestManager;
    }

    @Override
    public void onStop() {
        super.onStop();
        mRequestManager.unsubscribeAll();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRequestManager.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.e(TAG, "onLowMemory");

        mRequestManager.unsubscribeAll();
        LoaderCore.clearMemory();
    }
}
