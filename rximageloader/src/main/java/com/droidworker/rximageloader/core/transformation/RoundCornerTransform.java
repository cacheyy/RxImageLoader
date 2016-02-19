package com.droidworker.rximageloader.core.transformation;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

import rx.functions.Func1;

/**
 * @author DroidWorkerLYF
 */
public class RoundCornerTransform extends Transform implements Func1<Bitmap, Bitmap> {
    private float rx, ry;

    public RoundCornerTransform(float rx, float ry) {
        this(rx, ry, Bitmap.Config.ARGB_8888);
    }

    public RoundCornerTransform(float rx, float ry, Bitmap.Config config) {
        super(config);
        this.rx = rx;
        this.ry = ry;
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
        canvas.drawRoundRect(new RectF(0, 0, size, size), rx, ry, paint);
        return output;
    }
}
