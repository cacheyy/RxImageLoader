package com.droidworker.rximageloader.core.request;

import android.app.Fragment;
import android.util.Log;

import com.droidworker.rximageloader.core.LoaderCore;

/**
 * @author DroidWorkerLYF
 */
public class RequestManagerFragment extends Fragment {
    private static final String TAG = "RequestManagerFragment";
    private RequestManager mRequestManager;

    public RequestManagerFragment(){
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
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.e(TAG, "onTrimMemory");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.e(TAG, "onLowMemory");

        mRequestManager.unsubscribeAll();
        LoaderCore.clearMemory();
    }
}
