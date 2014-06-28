package com.lysterr.Lysterr.util;

/**
 * Created by zfoster on 6/28/14.
 */
public class DebugUtil {
    public static void debugException(Exception e) {
        UiUtil.showToast("DEBUG Exception: " + e.getMessage());
        e.printStackTrace();
    }
}
