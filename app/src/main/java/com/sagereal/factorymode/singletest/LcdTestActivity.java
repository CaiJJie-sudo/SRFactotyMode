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
import com.sagereal.factorymode.utils.SharePreferenceUtil;

public class LcdTestActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityLcdTestBinding mBinding;
    private int mScreenColorIndex = 0;
    private int[] mScreenColors = {R.color.red, R.color.green, R.color.gray};
    private int mCurrentColor = mScreenColors[mScreenColorIndex];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_lcd_test);
        setOnClickListeners(mBinding.btnBegin, mBinding.lcdLayout, mBinding.btnPass, mBinding.btnFail);
    }
    public static void openActivity(Context context) {
        context.startActivity(new Intent(context, LcdTestActivity.class));
    }

    /**
     * 设置点击事件监听器
     */
    private void setOnClickListeners(View... views) {
        for (View view : views) {
            view.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        // 点击开始按钮，开始测试
        if (v.getId() == R.id.btn_begin) {
            mBinding.btnBegin.setVisibility(View.GONE);
            mBinding.lcdLayout.setBackgroundColor(getResources().getColor(mCurrentColor));
        } else if (v.getId() == R.id.lcd_layout && mBinding.btnBegin.getVisibility() == View.GONE) { // 点击屏幕lcd_layout组件范围，改变背景颜色
            if(mScreenColorIndex == mScreenColors.length - 1) {   // lcd 颜色变换完毕
                mBinding.lcdLayout.setBackgroundColor(getResources().getColor(R.color.white));
                mBinding.linearPassFail.setVisibility(View.VISIBLE);
            } else {
                mCurrentColor = mScreenColors[++mScreenColorIndex];
                mBinding.lcdLayout.setBackgroundColor(getResources().getColor(mCurrentColor));
            }
        } else if(v.getId() == R.id.btn_pass) { // 测试通过
            SharePreferenceUtil.saveData(v.getContext(), EnumSingleTest.POSITION_LCD.getValue(), EnumSingleTest.TESTED_PASS.getValue());
            // 跳转至单项测试列表页面
            finish();
        }else if(v.getId() == R.id.btn_fail){   // 测试失败
            SharePreferenceUtil.saveData(v.getContext(), EnumSingleTest.POSITION_LCD.getValue(), EnumSingleTest.TESTED_FAIL.getValue());
            finish();
        }
    }
}
