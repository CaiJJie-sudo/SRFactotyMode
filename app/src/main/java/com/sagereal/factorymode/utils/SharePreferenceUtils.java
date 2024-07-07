package com.sagereal.factorymode.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.sagereal.factorymode.R;

/**
 * 管理单项测试数据保存工具类
 */
public class SharePreferenceUtils {
    public static void saveData(Context context, int position, int result){
        // 获取一个特定名称和模式的 SharedPreferences 对象，在应用程序中存储和管理数据, 文件只能被当前应用程序访问。
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // 将指定位置 (position) 的测试结果 (result) 存储到 SharedPreferences 中
        editor.putInt(context.getResources().getString(R.string.single_item_position) + position, result);
        // 提交修改
        editor.apply();
    }
}
