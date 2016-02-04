package com.droidworker.test.model;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.droidworker.test.model.bean.ImageBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * @author DroidWorkerLYF
 */
public class ImageManager {

    private Context mContext;
    private static ImageManager instance;

    private ImageManager(Context context) {
        mContext = context.getApplicationContext();
    }

    public static ImageManager getInstance(Context context) {
        if (instance == null) {
            synchronized (ImageManager.class) {
                if (instance == null) {
                    instance = new ImageManager(context);
                }
            }
        }
        return instance;
    }

    /**
     * 所有的图片信息
     */
    private List<ImageBean> allImages = new ArrayList<>();

    /**
     * 获取所有图片信息
     *
     * @return
     */
    public List<ImageBean> getAllImages() {
        return allImages;
    }

    /**
     * 清空所有图片信息
     */
    public void clearAllImages() {
        allImages.clear();
    }

    /**
     * 同步所有图片信息
     */
    private List<ImageBean> updateAllImages() {
        String[] projection = new String[] { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA // 图片绝对路径
        };
        Cursor cursor = mContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, "", null,
                "");
        if (cursor == null) { return null; }
        List<ImageBean> tempList = new ArrayList<>();
        cursor.moveToFirst();
        do {
            try {
                int fileIdColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                int pathColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                String path = cursor.getString(pathColumn);
                String id = cursor.getString(fileIdColumn);
                File file = new File(path);
                if (file.exists() && file.length() > 0 && file.canRead()) {
                    ImageBean imageInfo = new ImageBean();
                    imageInfo.id = Integer.parseInt(id);
                    imageInfo.path = path;
                    imageInfo.position = allImages.size();
                    imageInfo.folder = file.getParent();
                    tempList.add(imageInfo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (cursor.moveToNext());
        cursor.close();
        return tempList;
    }

    public Observable<List<ImageBean>> getImageList(){
        return Observable.create(new Observable.OnSubscribe<List<ImageBean>>() {
            @Override
            public void call(Subscriber<? super List<ImageBean>> subscriber) {
                clearAllImages();
                allImages.addAll(updateAllImages());
                subscriber.onNext(allImages);
            }
        });
    }
}
