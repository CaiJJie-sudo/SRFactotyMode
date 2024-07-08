package com.sagereal.factorymode.utils;

import android.content.Context;
import android.widget.Toast;

public class FunctionUtils {
    private static Toast lastToast;

    /**
     * 显示 Toast，避免连续显示多个 Toast
     * @param message
     * @param duration
     */
    public static void showToast(Context context, String message, int duration) {
        // 如果上一个Toast存在，则取消掉
        if (lastToast != null) {
            lastToast.cancel();
        }
        lastToast = Toast.makeText(context, message, duration);
        lastToast.show();
    }
}
