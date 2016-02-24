package com.droidworker.rximageloader.cache.interfaces;

/**
 * @author DroidWorkerLYF
 */
public interface IGifFile {

    boolean exists();

    void mkdirs(boolean force);

    void delete();

    void prepare();

    String getFramePath(int frame);

    int getFrameDelay(int frame);

    int getFrameCount();
}
