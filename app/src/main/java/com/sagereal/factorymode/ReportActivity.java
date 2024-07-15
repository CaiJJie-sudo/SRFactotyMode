package com.sagereal.factorymode;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.sagereal.factorymode.databinding.ActivityReportBinding;
import com.sagereal.factorymode.utils.EnumSingleTest;
import com.sagereal.factorymode.utils.SharePreferenceUtil;

public class ReportActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityReportBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_report);
        mBinding.ibBack.setOnClickListener(this);
        getData();
    }

    /**
     * 填充数据
     */
    private void getData(){
        String[] singleTestName = {getString(R.string.battery_test), getString(R.string.vibration_test), getString(R.string.mike_test),
                getString(R.string.headphones_test), getString(R.string.lcd_test), getString(R.string.speaker_test), getString(R.string.receiver_test),
                getString(R.string.camera_test), getString(R.string.flash_test), getString(R.string.keys_test)};

        for (int i = 0; i < EnumSingleTest.SINGLE_TEST_NUM.getValue(); i++) {
            int value = SharePreferenceUtil.getData(this, i, EnumSingleTest.UNTESTED.getValue());

            if (value == EnumSingleTest.TESTED_PASS.getValue()) {
                mBinding.tvPass.append(singleTestName[i] + "\n\n");
            } else if (value == EnumSingleTest.TESTED_FAIL.getValue()) {
                mBinding.tvFail.append(singleTestName[i] + "\n\n");
            } else if (value == EnumSingleTest.UNTESTED.getValue()) {
                mBinding.tvUntested.append(singleTestName[i] + "\n\n");
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