package com.droidworker.rximageloader.core.gif;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.droidworker.rximageloader.cache.gif.GifFile;
import com.droidworker.rximageloader.core.LoaderCore;
import com.droidworker.rximageloader.core.Processor;
import com.droidworker.rximageloader.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author DroidWorkerLYF
 */
public class GifProcessor {
    private GifDecoder mDecoder;
    private String cachePath;
    private GifFile mGifFile;
    private int curFrame = -1;

    public GifProcessor(String gifName) {
        mDecoder = new GifDecoder();

        File file = new File(LoaderCore.getGlobalConfig().diskCachePath);
        cachePath = file.getParent() + File.separator + "Gif" + File.separator + Utils.hashKeyForDisk(gifName);
        mGifFile = new GifFile(cachePath);

        if (!mGifFile.exists()) {
            mGifFile.mkdirs(false);
        }
    }

    public void read(InputStream inputStream) {
        prepareDecoder(inputStream);
//        mGifFile.prepare();
        prepareCache();
    }

    private void prepareDecoder(InputStream inputStream) {
        if(inputStream == null){
            return ;
        }
        try {
            mDecoder.read(inputStream, inputStream.available());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void prepareCache(){
        mGifFile.prepare();
        if(!mGifFile.exists()){
            for(int i =0;i<mDecoder.getFrameCount();i++){
                mDecoder.advance();
                Bitmap bitmap = mDecoder.getNextFrame();
                final int frame = getCurrentFrame();
                File file = new File(cachePath + File.separator + "frame_" + frame + "_" + mDecoder.getDelay(frame));
                OutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.WEBP, 100, outputStream);
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public int getCurrentFrame(){
        return curFrame = (curFrame + 1) % getFrameCount();
    }

    public int getDelay() {
        return getDelay(curFrame);
    }

    public int getDelay(int frame) {
        final int delay = mGifFile.getFrameDelay(frame);
        if (delay != -1 && delay == mDecoder.getDelay(frame)) {
            return delay;
        }
        return mDecoder.getDelay(frame);
    }

    public int getFrameCount() {
        final int count = mGifFile.getFrameCount();
        if (count != 0 && count == mDecoder.getFrameCount()) {
            return count;
        }
        return mDecoder.getFrameCount();
    }

    public Bitmap getFrame() {
        return getFrame(getCurrentFrame());
    }

    public Bitmap getFrame(int frame) {
        String path = mGifFile.getFramePath(frame);
        mDecoder.advance();
        if (!TextUtils.isEmpty(path)) {
            return Processor.decodeSampledBitmapFromFile(path, 0, 0, Bitmap.Config.ARGB_8888);
        }
        return getNextFrame();
    }

    private Bitmap getNextFrame() {
        Bitmap bitmap = mDecoder.getNextFrame();
//        File file = new File(cachePath + File.separator + "frame_" + curFrame + "_" + mDecoder.getDelay(curFrame));
//        OutputStream outputStream = null;
//        try {
//            outputStream = new FileOutputStream(file);
//            bitmap.compress(Bitmap.CompressFormat.WEBP, 100, outputStream);
//            outputStream.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (outputStream != null) {
//                try {
//                    outputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
        return bitmap;
    }
}
