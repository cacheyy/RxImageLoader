package com.droidworker.rximageloader.core.request.manager;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;

import com.droidworker.rximageloader.core.LoaderTask;
import com.droidworker.rximageloader.core.request.BitmapRequest;
import com.droidworker.rximageloader.core.request.GifRequest;
import com.droidworker.rximageloader.core.request.Request;
import com.droidworker.rximageloader.utils.Utils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * RequestManager is bind to an activity or a fragment and provide life cycle support
 * for request
 *
 * @author DroidWorkerLYF
 */
public class RequestManager {
    private static final String TAG = RequestManager.class.getSimpleName();
    private Map<View, Request> requestMap = new HashMap<>();
    private WeakReference<AbsListView> absWeakReference;
    private WeakReference<RecyclerView> recyclerWeakReference;
    private AbsListView.OnScrollListener absScrollListener;
    private RecyclerView.OnScrollListener recyclerScrollListener;
    private boolean flying;

    /**
     * Create a request and set the load path
     *
     * @param path the path of resource
     * @return a {@link Request}
     */
    public Request load(String path) {
        Request request;
        if (Utils.isGif(path)) {
            return loadGif(path).load(path);
        } else {
            return loadBitmap(path).load(path);
        }
    }

    public BitmapRequest loadBitmap(String path) {
        BitmapRequest request = new BitmapRequest();
        request.setNotifySubscriber(request1 -> into(request1));
        return request;
    }

    public GifRequest loadGif(String path) {
        GifRequest request = new GifRequest();
        request.setNotifySubscriber(request1 -> into(request1));
        return request;
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
        if (!flying) {
            if (request instanceof BitmapRequest) {
                LoaderTask.bitmapTask(request).subscribe(request);
            } else if (request instanceof GifRequest) {
                LoaderTask.gifTask(request).subscribe(request);
            }
        }
    }

    /**
     * Unsubscribe all the subscribers
     */
    public void unsubscribeAll() {
        for (Map.Entry<View, Request> viewRequestEntry : requestMap.entrySet()) {
            viewRequestEntry.getValue().unsubscribe();
        }
    }

    /**
     * Set OnScrollListener to a {@link AbsListView}, such as {@link android.widget.ListView}
     * and {@link android.widget.GridView}, loader will take care of loading when scrolling,
     * if you need to set your custom scroll listener, just use {@link RequestManager#resumeLoad()}
     * and {@link RequestManager#pauseLoad()}
     *
     * @param absListView the scroll view
     */
    public void setOnScrollListener(AbsListView absListView) {
        clearAbsWeakReference();
        if (absScrollListener == null) {
            absScrollListener = new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    flying = scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING
                            || scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;
                    if (!flying) {
                        resumeLoad();
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                }
            };
        }
        absListView.setOnScrollListener(absScrollListener);
        absWeakReference = new WeakReference<>(absListView);
    }

    private void clearAbsWeakReference() {
        if (absWeakReference != null) {
            if (absWeakReference.get() != null) {
                absWeakReference.get().setOnScrollListener(null);
            }
            absWeakReference.clear();
        }
    }

    /**
     * Add OnScrollListener to a {@link RecyclerView}, loader will take care of loading
     * when scrolling, and the listener will be removed when manager destroyed
     *
     * @param recyclerView the scroll view
     */
    public void addOnScrollListener(RecyclerView recyclerView) {
        clearRecyclerWeakReference();
        if (recyclerScrollListener == null) {
            recyclerScrollListener = new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    flying = newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING;
                    if (!flying) {
                        resumeLoad();
                    }
                }
            };
        }
        recyclerView.addOnScrollListener(recyclerScrollListener);
        recyclerWeakReference = new WeakReference<>(recyclerView);
    }

    private void clearRecyclerWeakReference() {
        if (recyclerWeakReference != null) {
            if (recyclerWeakReference.get() != null) {
                recyclerWeakReference.get().removeOnScrollListener(recyclerScrollListener);
            }
            recyclerWeakReference.clear();
        }
    }

    /**
     * Tell request manager to resume loading
     */
    public void resumeLoad() {
        flying = false;
        for (Map.Entry<View, Request> viewRequestEntry : requestMap.entrySet()) {
            Request request = viewRequestEntry.getValue();
            if (!request.isUnsubscribed()) {
                if (request instanceof BitmapRequest) {
                    LoaderTask.bitmapTask(request).subscribe(request);
                } else if (request instanceof GifRequest) {
                    LoaderTask.gifTask(request).subscribe(request);
                }
            }
        }
    }

    /**
     * Tell request manager to pause loading
     */
    public void pauseLoad() {
        flying = true;
    }

    public void onDestroy() {
        clearAbsWeakReference();
        clearRecyclerWeakReference();
        absScrollListener = null;
        recyclerScrollListener = null;
        unsubscribeAll();
        requestMap.clear();
    }
}
