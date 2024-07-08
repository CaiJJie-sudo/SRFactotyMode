package com.sagereal.factorymode;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.sagereal.factorymode.databinding.ActivityReportBinding;
import com.sagereal.factorymode.utils.EnumSingleTest;

public class ReportActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityReportBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_report);
        binding.ibBack.setOnClickListener(this);
        getData();
    }

    /**
     * 填充数据
     */
    private void getData(){
        // 定义单项测试名称数组
        String[] singleTestName = {getString(R.string.battery_test), getString(R.string.vibration_test), getString(R.string.mike_test),
                getString(R.string.headphones_test), getString(R.string.lcd_test), getString(R.string.speaker_test), getString(R.string.receiver_test),
                getString(R.string.camera_test), getString(R.string.flash_test), getString(R.string.keys_test)};

        // 获取存储的 SharedPreferences
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getApplicationContext().getResources().getString(R.string.app_name), Context.MODE_PRIVATE);

        // 遍历测试结果并显示在界面上
        for (int i = 0; i < EnumSingleTest.SINGLE_TEST_NUM.getValue(); i++) {
            int value = sharedPreferences.getInt(getResources().getString(R.string.single_item_position) + i, EnumSingleTest.UNTESTED.getValue());

            // 根据结果值将测试名称显示到相应的文本视图中
            if (value == EnumSingleTest.TESTED_PASS.getValue()) {
                binding.tvPass.append(singleTestName[i] + "\n\n");
            } else if (value == EnumSingleTest.TESTED_FAIL.getValue()) {
                binding.tvFail.append(singleTestName[i] + "\n\n");
            } else if (value == EnumSingleTest.UNTESTED.getValue()) {
                binding.tvUntested.append(singleTestName[i] + "\n\n");
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.ib_back){
            finish();
        }
    }
}
