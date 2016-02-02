package com.droidworker.rximageloader.core.request;

import android.app.Fragment;
import android.os.Bundle;

/**
 * RequestManagerFragment is bind to an activity or a fragment and provide life cycle support for
 * request
 *
 * @author DroidWorkerLYF
 */
public class RequestManagerFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
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
