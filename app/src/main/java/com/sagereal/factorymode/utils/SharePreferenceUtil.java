package com.sagereal.factorymode.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 管理单项测试数据保存和读取
 */
public class SharePreferenceUtil {
    private static final String PREFS_NAME = "sr_factory_mode_shared_prefs"; // 定义 SharedPreferences 文件的名称
    private static final String PREFIX = "single_item_position_"; // 定义键值前缀

    /**
     * 保存测试数据
     */
    public static void saveData(Context context, int position, int result){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PREFIX + position, result); // 将测试结果 result 存储到由前缀和位置组成的键值中
        editor.apply();
    }

    /**
     * 读取测试数据
     */
    public static int getData(Context context, int position, int defaultValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(PREFIX + position, defaultValue);
    }
}



