package com.droidworker.rximageloader.core.transformation;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Change the given bitmap to byte[]
 *
 * @author DroidWorkerLYF
 */
public class ByteTransform extends Transform<Bitmap, byte[]> {
    private Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.JPEG;

    public ByteTransform() {

    }

    public ByteTransform(Bitmap.CompressFormat compressFormat) {
        this.mCompressFormat = compressFormat;
    }

    @Override
    public byte[] call(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(mCompressFormat, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        try {
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }
}
