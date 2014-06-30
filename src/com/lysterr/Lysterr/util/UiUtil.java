package com.lysterr.Lysterr.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

    /**
     * Explicitly hide the soft keyboard
     *
     * @param context  The activity on which the keyboard might be displayed
     */
    public static void hideKeyboard(Activity context)
    {
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusedView = context.getCurrentFocus();
        if (focusedView != null && imm != null)
        {
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
            return;
        }

        focusedView = context.getWindow().getDecorView();
        if (focusedView != null && imm != null)
        {
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
            return;
        }
    }

    /**
     * Explicitly show the keyboard
     *
     * @param context
     * @param view
     */
    public static void showKeyboard(Context context, View view)
    {
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null && imm != null)
            imm.showSoftInput(view, 0);
    }
}
