package com.lysterr.Lysterr.util;

import android.widget.Toast;

/**
 * Created by zfoster on 6/28/14.
 */
public class UiUtil {
    /**
     * Display a toast message
     *
     * @param msg
     */
    public static void showToast(String msg)
    {
        Toast toast = Toast.makeText(Registry.sApp, msg, Toast.LENGTH_LONG);
        toast.show();
    }

    public static void showToast(int resId)
    {
        String msg = Registry.sApp.getString(resId);
        showToast(msg);
    }
}
