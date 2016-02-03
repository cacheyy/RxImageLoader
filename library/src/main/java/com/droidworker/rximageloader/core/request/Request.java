package com.droidworker.rximageloader.core.request;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;

import com.droidworker.rximageloader.utils.Utils;

import java.lang.ref.WeakReference;

import rx.Subscriber;
import rx.functions.Action1;

/**
 * @author DroidWorkerLYF
 */
public class Request<T extends Bitmap> extends Subscriber<T> {
    protected String mKey;
    protected Option mOption;
    protected WeakReference<View> mReference;
    protected Action1<Float> onProgress;
    /**
     * If this is true, then we will not cache the bitmap in memory
     */
    private boolean skipCacheInMem;
    /**
     * If this is true, the we will not cache the bitmap in disk
     */
    private boolean skipCacheInDisk;
    private int errorId;
    private int placeholderId;

    public void copy(Request<T> request) {

    }

    public Request load(String url) {
        this.mKey = url;
        return this;
    }

    public Request progress(Action1<Float> onProgress){
        this.onProgress = onProgress;
        return this;
    }

    public Request skipCacheInMem(boolean skip){
        this.skipCacheInMem = skip;
        return this;
    }

    public Request skipCacheInDisk(boolean skip){
        this.skipCacheInDisk = skip;
        return this;
    }

    public Request error(int resId){
        this.errorId = resId;
        return this;
    }

    public Request placeholder(int resId){
        this.placeholderId = resId;
        return this;
    }

    public Request into(View view) {
        if (mReference != null) {
            mReference.clear();
        } else {
            mReference = new WeakReference<>(view);
        }
        return this;
    }

    public Option getOption() {
        return mOption;
    }

    public String getKey() {
        return mKey;
    }

    public void clear() {
        mKey = null;
        mOption = null;
        mReference.clear();
        skipCacheInMem = false;
        skipCacheInDisk = false;
    }

    public void onProgress(float percent){
        if(onProgress != null){
            onProgress.call(percent);
        }
    }

    @Override
    public void onCompleted() {
        clear();
        unsubscribe();
    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onNext(T requestResult) {
        if(isUnsubscribed()){
            return ;
        }
        if(mReference==null || mReference.get() == null){
            return ;
        }
        View view = mReference.get();
        if(view instanceof ImageView){
            ((ImageView) view).setImageBitmap(requestResult);
        } else {
            if(Utils.hasJellyBean()){
                view.setBackground(new BitmapDrawable(view.getResources(), requestResult));
            } else {
                view.setBackgroundDrawable(new BitmapDrawable(view.getResources(), requestResult));
            }
        }
    }

    public class Option {
        public int reqWidth;
        public int reqHeight;
        public Bitmap.Config config;
    }
}
