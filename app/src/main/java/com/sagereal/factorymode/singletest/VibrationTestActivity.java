package com.sagereal.factorymode.singletest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sagereal.factorymode.R;
import com.sagereal.factorymode.utils.EnumData;

public class VibrationTestActivity extends AppCompatActivity {
    private Vibrator vibrator;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vibration_test);
        // 初始化 Vibrator 对象
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }
    public static void openActivity(Context context) {
        Intent intent = new Intent(context, VibrationTestActivity.class);
        intent.getIntExtra(String.valueOf(EnumData.VIBRATION_POSITION), 0);
        context.startActivity(intent);
    }

    /**
     * 让设备进行振动
     */
    private void deviceVibraion(Vibrator vibrator){
        // 检查是否支持振动
        if (vibrator.hasVibrator()) {
            // 震动一次，传入震动时长（毫秒）
            vibrator.vibrate(3000);
            // 震动模式，传入震动模式数组和重复次数（-1表示不重复）
            long[] pattern = {1000, 1000}; // 震动1000ms，停止1000ms
            vibrator.vibrate(pattern, 0);
        }
    }
    /**
     * 在 Activity 进入前台显示时,进行振动
     */
    @Override
    protected void onResume() {
        super.onResume();
        deviceVibraion(vibrator);
    }
    /**
     *  Activity 进入后台或不可见状态时，取消设备振动
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (vibrator != null) {
            vibrator.cancel();
        }
    }
}
