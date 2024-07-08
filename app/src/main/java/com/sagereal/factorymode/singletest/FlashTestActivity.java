package com.sagereal.factorymode.singletest;

import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.sagereal.factorymode.R;
import com.sagereal.factorymode.databinding.ActivityFlashTestBinding;
import com.sagereal.factorymode.utils.EnumSingleTest;
import com.sagereal.factorymode.utils.SharePreferenceUtils;

public class FlashTestActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityFlashTestBinding binding;
    private CameraManager cameraManager;
    private String[] cameraId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_flash_test);
        binding.btnPass.setOnClickListener(this);
        binding.btnFail.setOnClickListener(this);
        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList(); // 获取所有摄像头的ID
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    public static void openActivity(Context context) {
        Intent intent = new Intent(context, FlashTestActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_pass){
            SharePreferenceUtils.saveData(v.getContext(), EnumSingleTest.FLASH_POSITION.getValue(), EnumSingleTest.TESTED_PASS.getValue());
        } else if (v.getId() == R.id.btn_fail) {
            SharePreferenceUtils.saveData(v.getContext(), EnumSingleTest.FLASH_POSITION.getValue(), EnumSingleTest.TESTED_FAIL.getValue());
        }
        // 跳转至单项测试列表页面
        onBackPressed();
    }
    @Override
    protected void onResume() {
        super.onResume();
        try {
            // 打开所有闪光灯
            for (String flashId : cameraId){
                cameraManager.setTorchMode(flashId, true);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            for (String flashId : cameraId){
                // 关闭闪光灯
                cameraManager.setTorchMode(flashId, false);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}
