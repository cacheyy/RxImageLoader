package com.droidworker.rximageloader.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.DisplayMetrics;

import java.io.File;

/**
 * The config of ImageLoader
 *
 * @author DroidWorkerLYF
 */
public class LoaderConfig {
    private static final int DEFAULT_DISK_CACHE_SIZE = 1024 * 1024 * 20;//KB
    private static final Bitmap.CompressFormat DEFAULT_COMPRESS_FORMAT = Bitmap.CompressFormat.WEBP;
    private static final int DEFAULT_COMPRESS_QUALITY = 100;
    private static final Bitmap.Config DEFAULT_BITMAP_CONFIG = Bitmap.Config.RGB_565;
    private static final float DEFAULT_MEM_CACHE_PERCENT = 0.25f;
    /** the size of memory cache */
    public final int memCacheSize;
    /** the size of disk cache */
    public final int diskCacheSize;
    /** the global CompressFormat to be used */
    public final Bitmap.CompressFormat mCompressFormat;
    /** the global Compress quality to be used */
    public final int mCompressQuality;
    /** use memory cache or not */
    public final boolean memCacheEnabled;
    /** use disk cache or not */
    public final boolean diskCacheEnabled;
    /** memory cache/total available memory */
    public final float memCacheSizePercent;
    /** the global bitmap config to be used */
    public final Bitmap.Config mConfig;
    /** the path of disk cache */
    public final String diskCachePath;
    /** screen's width and height */
    public final int screenWidth, screenHeight;
    /** the storage path of temporary files */
    public final String tempFilePath;
    public final boolean isDebug;

    private LoaderConfig(final Builder builder, Context context) {
        this.memCacheSizePercent = builder.memCacheSizePercent != 0.0f ? builder.memCacheSizePercent : DEFAULT_MEM_CACHE_PERCENT;
        this.memCacheSize = builder.memCacheSize != 0 ? builder.memCacheSize : Math.round(memCacheSizePercent
                * Runtime.getRuntime().maxMemory() / 1024);
        this.diskCacheSize = builder.diskCacheSize != 0 ? builder.diskCacheSize : DEFAULT_DISK_CACHE_SIZE;
        this.mCompressFormat = builder.mCompressFormat != null ? builder.mCompressFormat : DEFAULT_COMPRESS_FORMAT;
        this.mCompressQuality = builder.mCompressQuality != 0 ? builder.mCompressQuality : DEFAULT_COMPRESS_QUALITY;
        this.memCacheEnabled = builder.memCacheEnabled;
        this.diskCacheEnabled = builder.diskCacheEnabled;
        this.mConfig = builder.mConfig != null ? builder.mConfig : DEFAULT_BITMAP_CONFIG;

        final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !isExternalStorageRemovable() ? getExternalCacheDir(context).getPath() : context
                .getCacheDir().getPath();
        this.diskCachePath = builder.diskCachePath != null ? builder.diskCachePath : cachePath;
        final File tempFile = new File(this.diskCachePath).getParentFile();
        this.tempFilePath = tempFile.getAbsolutePath() + "/temp";
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        isDebug = builder.isDebug;
    }

    /**
     * create a default LoaderConfig
     *
     * @param context context
     * @return the global LoaderConfig
     */
    public static LoaderConfig createDefaultConfig(Context context) {
        return new Builder().build(context);
    }

    public static class Builder {
        private int memCacheSize;
        private int diskCacheSize;
        private Bitmap.CompressFormat mCompressFormat;
        private int mCompressQuality;
        private boolean memCacheEnabled = true;
        private boolean diskCacheEnabled = true;
        private float memCacheSizePercent;
        private Bitmap.Config mConfig;
        private String diskCachePath;
        private boolean isDebug = true;

        public Builder() {

        }

        /**
         * set size of memory cache
         *
         * @param size the size of memory cache
         * @return Builder
         */
        public Builder setMemCacheSize(int size) {
            this.memCacheSize = size;
            return this;
        }

        /**
         * set size of disk cache
         *
         * @param size the size of disk cache
         * @return Builder
         */
        public Builder setDiskCacheSize(int size) {
            this.diskCacheSize = size;
            return this;
        }

        /**
         * set Bitmap.CompressFormat as global config
         *
         * @param format Bitmap.CompressFormat
         * @return Builder
         */
        public Builder setCompressFormat(Bitmap.CompressFormat format) {
            this.mCompressFormat = format;
            return this;
        }

        /**
         * set compress quality as global config
         *
         * @param quality 0-100
         * @return Builder
         */
        public Builder setCompressQuality(int quality) {
            this.mCompressQuality = quality;
            return this;
        }

        /**
         * use memory cache or not
         *
         * @param enable true if use memory cache
         * @return Builder
         */
        public Builder setMemCacheEnabled(boolean enable) {
            this.memCacheEnabled = enable;
            return this;
        }

        /**
         * use disk cache or not
         *
         * @param enable true if use disk cache
         * @return Builder
         */
        public Builder setDiskCacheEnable(boolean enable) {
            this.diskCacheEnabled = enable;
            return this;
        }

        /**
         * set percent of memory cache of total available memory
         *
         * @param percent 0-1
         * @return Builder
         */
        public Builder setMemCacheSizePercent(float percent) {
            this.memCacheSizePercent = percent;
            return this;
        }

        /**
         * set Bitmap.Config as global config
         *
         * @param config Bitmap.config
         * @return Builder
         */
        public Builder setBitmapConfig(Bitmap.Config config) {
            this.mConfig = config;
            return this;
        }

        /**
         * set a path to disk cache
         *
         * @param diskCache the path of disk cache
         * @return Builder
         */
        public Builder setDiskCachePath(String diskCache) {
            this.diskCachePath = diskCache;
            return this;
        }

        public Builder setDebug(boolean debug){
            this.isDebug = debug;
            return this;
        }

        /**
         * @return create global loader config
         */
        public LoaderConfig build(Context context) {
            return new LoaderConfig(this, context);
        }
    }

    /**
     * @return true if external storage can be removed
     */
    public static boolean isExternalStorageRemovable() {
        return Environment.isExternalStorageRemovable();
    }

    /**
     * @param context context
     * @return external cache's file
     */
    public static File getExternalCacheDir(Context context) {
        return context.getExternalCacheDir();
    }
}
