package com.droidworker.rximageloader.core;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import rx.functions.Func1;

/**
 * Change the given bitmap to byte[]
 *
 * @author DroidWorkerLYF
 */
public class ToByte implements Func1<Bitmap, byte[]> {
    private Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.JPEG;

    public ToByte() {

    }

    public ToByte(Bitmap.CompressFormat compressFormat) {
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
