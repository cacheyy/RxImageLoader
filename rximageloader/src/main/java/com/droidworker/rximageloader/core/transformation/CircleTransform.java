package com.droidworker.rximageloader.core.transformation;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import rx.functions.Func1;

/**
 * @author DroidWorkerLYF
 */
public class CircleTransform implements Func1<Bitmap, Bitmap> {
    private Bitmap.Config mConfig = Bitmap.Config.ARGB_4444;

    public CircleTransform(){

    }

    public CircleTransform(Bitmap.Config config) {
        this.mConfig = config;
    }

    @Override
    public Bitmap call(Bitmap bitmap) {
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        final int size = Math.min(width, height);
        final int leftOffset = (width - size) / 2;
        final int topOffset = (height - size) / 2;

        Bitmap output = Bitmap.createBitmap(size, size, mConfig);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(bitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        if (leftOffset != 0 || topOffset != 0) {
            Matrix matrix = new Matrix();
            matrix.setTranslate(-leftOffset, -topOffset);
            shader.setLocalMatrix(matrix);
        }
        paint.setShader(shader);
        paint.setAntiAlias(true);
        final float r = size / 2;
        canvas.drawCircle(r, r, r, paint);
        return output;
    }
}
