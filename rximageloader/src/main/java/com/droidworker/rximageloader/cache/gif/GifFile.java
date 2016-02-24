package com.droidworker.rximageloader.cache.gif;

import android.util.SparseArray;

import com.droidworker.rximageloader.cache.interfaces.IGifFile;

import java.io.File;
import java.io.IOException;

/**
 * @author DroidWorkerLYF
 */
public class GifFile implements IGifFile {
    private File mFile;
    private SparseArray<GifFrame> bitmapArray;

    public GifFile(String path){
        mFile = new File(path);
    }

    @Override
    public boolean exists() {
        return mFile != null && mFile.exists() && mFile.listFiles().length != 0;
    }

    @Override
    public void mkdirs(boolean force) {
        if(force){
            delete();
        }
        if(!mFile.exists()){
            if(!mFile.mkdirs()){
                try {
                    throw new IOException("File mkdirs failed");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void delete() {
        if(exists()){
            //noinspection ResultOfMethodCallIgnored
            mFile.delete();
        }
        if(bitmapArray != null){
            bitmapArray.clear();
        }
    }

    @Override
    public void prepare(){
        if(bitmapArray != null){
            return ;
        }
        File[] list = mFile.listFiles();
        bitmapArray = new SparseArray<>(list.length);
        for(File file: list){
            String[] params = file.getName().split("_");
            if(params[0].equals("frame")){
                try{
                    int frame = Integer.parseInt(params[1]);
                    int delay = Integer.parseInt(params[2]);
                    GifFrame gifFrame = new GifFrame();
                    gifFrame.path = file.getAbsolutePath();
                    gifFrame.delay = delay;
                    bitmapArray.put(frame, gifFrame);
                } catch (NumberFormatException e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public String getFramePath(int frame) {
        if(bitmapArray == null || bitmapArray.indexOfKey(frame) < 0){
            return null;
        }
        return bitmapArray.get(frame).path;
    }

    @Override
    public int getFrameDelay(int frame) {
        if(bitmapArray == null || bitmapArray.indexOfKey(frame) < 0){
            return -1;
        }
        return bitmapArray.get(frame).delay;
    }

    @Override
    public int getFrameCount() {
        return bitmapArray != null ? bitmapArray.size() : 0;
    }

    private static class GifFrame{
        public int delay = -1;
        public String path;
    }
}
