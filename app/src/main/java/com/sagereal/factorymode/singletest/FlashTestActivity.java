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
import com.sagereal.factorymode.utils.EnumData;

public class FlashTestActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityFlashTestBinding binding;
    private CameraManager cameraManager;
    private String cameraId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_flash_test);
        binding.btnPass.setOnClickListener(this);
        binding.btnFail.setOnClickListener(this);
        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[1]; // 获取第前置摄像头的ID
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    public static void openActivity(Context context) {
        Intent intent = new Intent(context, FlashTestActivity.class);
        intent.getIntExtra(String.valueOf(EnumData.FLASH_POSITION), 0);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_pass){

        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        try {
            // 打开闪光灯
            cameraManager.setTorchMode(cameraId, true);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            // 关闭闪光灯
            cameraManager.setTorchMode(cameraId, false);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}
