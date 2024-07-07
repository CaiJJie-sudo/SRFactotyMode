package com.sagereal.factorymode.singletest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.sagereal.factorymode.R;
import com.sagereal.factorymode.databinding.ActivityLcdTestBinding;
import com.sagereal.factorymode.utils.EnumSingleTest;
import com.sagereal.factorymode.utils.SharePreferenceUtils;

public class LcdTestActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityLcdTestBinding binding;
    private int screenColorIndex = 0;
    private int[] screenColors = {R.color.red, R.color.green, R.color.blue, R.color.gray};
    private int currentColor = screenColors[screenColorIndex];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lcd_test);
        binding.btnBegin.setOnClickListener(this);
        binding.lcdLayout.setOnClickListener(this);
        binding.btnPass.setOnClickListener(this);
        binding.btnFail.setOnClickListener(this);
    }

    public static void openActivity(Context context) {
        Intent intent = new Intent(context, LcdTestActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        // 点击开始按钮，开始测试
        if (v.getId() == R.id.btn_begin) {
            binding.btnBegin.setVisibility(View.GONE);
            binding.lcdLayout.setBackgroundColor(getResources().getColor(currentColor));
        } else if (v.getId() == R.id.lcd_layout && binding.btnBegin.getVisibility() == View.GONE) { // 点击屏幕lcd_layout组件范围，改变背景颜色
            if(screenColorIndex == screenColors.length - 1) {   // lcd 颜色变换完毕
                binding.lcdLayout.setBackgroundColor(getResources().getColor(R.color.white));
                binding.linearPassFail.setVisibility(View.VISIBLE);
            } else {
                currentColor = screenColors[++screenColorIndex];
                binding.lcdLayout.setBackgroundColor(getResources().getColor(currentColor));
            }
        } else if(v.getId() == R.id.btn_pass) { // 测试通过
            SharePreferenceUtils.saveData(v.getContext(), EnumSingleTest.LCD_POSITION.getValue(), EnumSingleTest.TESTED_PASS.getValue());
            // 跳转至单项测试列表页面
            onBackPressed();
        }else if(v.getId() == R.id.btn_fail){   // 测试失败
            SharePreferenceUtils.saveData(v.getContext(), EnumSingleTest.LCD_POSITION.getValue(), EnumSingleTest.TESTED_PASS.getValue());
            onBackPressed();
        }

    }

}
