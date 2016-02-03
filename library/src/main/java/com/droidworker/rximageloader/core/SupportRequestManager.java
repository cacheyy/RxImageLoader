package com.droidworker.rximageloader.core;

import android.support.v4.app.Fragment;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;

import rx.Subscription;

/**
 * SupportRequestManager is bind to an activity or a fragment and provide life cycle support
 * for request
 *
 * @author DroidWorkerLYF
 */
public class SupportRequestManager extends Fragment {
    private Map<ImageView, Subscription> requestMap = new HashMap<>();
}
