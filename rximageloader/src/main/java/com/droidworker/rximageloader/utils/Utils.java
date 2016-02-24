package com.droidworker.rximageloader.utils;

import android.os.Build;
import android.os.Looper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author DroidWorkerLYF
 */
public class Utils {
    /**
     * Hash the given key
     *
     * @param key the key needed to hash
     * @return hashed key
     */
    public static String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private static String bytesToHexString(byte[] bytes) {
        // http://stackoverflow.com/questions/332079
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xFF & aByte);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean hasJellyBeanMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    public static boolean isOnMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    /**
     * Check if this path is an url
     *
     * @param path path of request
     * @return true if it is an url
     */
    public static boolean isUrl(String path) {
        return path.startsWith("http://") || path.startsWith("https://");
    }

    /**
     * Check if this path is an Gif
     *
     * @param path path of request
     * @return true if it is a gif
     */
    public static boolean isGif(String path) {
        return path.substring(path.lastIndexOf(".") + 1, path.length()).toLowerCase().equals("gif");
    }
}
