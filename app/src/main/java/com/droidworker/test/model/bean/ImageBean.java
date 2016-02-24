package com.droidworker.test.model.bean;

/**
 * @author DroidWorkerLYF
 */
public class ImageBean {
    /**
     * the index of image
     */
    public int id = 0;
    /**
     * the path of image
     */
    public String path = "";
    public int position = 0;
    /**
     * the folder of image
     */
    public String folder = "";

    public ImageBean(){

    }

    public ImageBean(String path){
        this.path = path;
    }
}
