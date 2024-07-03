package com.sagereal.factorymode.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    private static Toast toast;

    /**
     * 创建和显示轻量级的通知
     * @param context
     * @param message
     * @param duration
     */
    public static void showToast(Context context, String message, int duration) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, message, duration);
        toast.show();
    }
}
