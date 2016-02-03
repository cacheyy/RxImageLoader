package com.droidworker.rximageloader.core.request;

import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.droidworker.rximageloader.core.request.Request;

import java.util.HashMap;
import java.util.Map;

/**
 * SupportRequestManager is bind to an activity or a fragment and provide life cycle support
 * for request
 *
 * @author DroidWorkerLYF
 */
public class SupportRequestManager extends Fragment {
    private Map<ImageView, Request> requestMap = new HashMap<>();
}
