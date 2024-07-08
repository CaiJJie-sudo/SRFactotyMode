package com.sagereal.factorymode.singletest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.sagereal.factorymode.R;
import com.sagereal.factorymode.databinding.ActivityVibrationTestBinding;
import com.sagereal.factorymode.utils.EnumSingleTest;
import com.sagereal.factorymode.utils.ToastUtils;
import com.sagereal.factorymode.utils.SharePreferenceUtils;

public class VibrationTestActivity extends AppCompatActivity implements View.OnClickListener {
    private Vibrator vibrator;
    private ActivityVibrationTestBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_vibration_test);
        binding.btnPass.setOnClickListener(this);
        binding.btnFail.setOnClickListener(this);
        // 初始化 Vibrator 对象
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }
    public static void openActivity(Context context) {
        context.startActivity(new Intent(context, VibrationTestActivity.class));
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
        if (!vibrator.hasVibrator()){
            ToastUtils.showToast(this, getString(R.string.not_support_vibration), Toast.LENGTH_SHORT);
        }
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


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_pass){
            if (!vibrator.hasVibrator()){
                ToastUtils.showToast(this, getString(R.string.not_support_vibration), Toast.LENGTH_SHORT);
                return;
            }else {
                // 保存数据
                SharePreferenceUtils.saveData(v.getContext(), EnumSingleTest.VIBRATION_POSITION.getValue(), EnumSingleTest.TESTED_PASS.getValue());
            }
        } else if (v.getId() == R.id.btn_fail) {
            SharePreferenceUtils.saveData(v.getContext(), EnumSingleTest.VIBRATION_POSITION.getValue(), EnumSingleTest.TESTED_FAIL.getValue());
        }
        // 跳转至单项测试列表页面
        onBackPressed();
    }
}
