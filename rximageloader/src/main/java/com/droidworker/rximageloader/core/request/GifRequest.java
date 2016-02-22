package com.droidworker.rximageloader.core.request;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;

import com.droidworker.rximageloader.utils.Utils;

import rx.Observable;

/**
 * @author DroidWorkerLYF
 */
public class GifRequest extends Request {

    @Override
    public Observable<Bitmap> observable(View view) {
        return null;
    }

    @Override
    public void onNext(Bitmap bitmap) {
        if (isUnsubscribed() || checkNull()) {
            return;
        }
        View view = mReference.get();

        if (view instanceof ImageView) {
            ((ImageView) view).setImageBitmap(bitmap);
        } else {
            if (Utils.hasJellyBean()) {
                view.setBackground(new BitmapDrawable(view.getResources(), bitmap));
            } else {
                //noinspection deprecation
                view.setBackgroundDrawable(new BitmapDrawable(view.getResources(), bitmap));
            }
        }
    }
}
