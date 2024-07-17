package com.sagereal.factorymode.singletest;

import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.View;
<<<<<<< HEAD
import android.widget.Toast;
=======
>>>>>>> 9f3c1ad69fd50ae07b351f4d3601d2965f149b22

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.sagereal.factorymode.R;
import com.sagereal.factorymode.databinding.ActivityFlashTestBinding;
import com.sagereal.factorymode.utils.EnumSingleTest;
import com.sagereal.factorymode.utils.SharePreferenceUtil;
<<<<<<< HEAD
import com.sagereal.factorymode.utils.ToastUtils;
=======
>>>>>>> 9f3c1ad69fd50ae07b351f4d3601d2965f149b22

public class FlashTestActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityFlashTestBinding mBinding;
    private CameraManager mCameraManager;
    private String[] mCameraId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_flash_test);
        mBinding.btnPass.setOnClickListener(this);
        mBinding.btnFail.setOnClickListener(this);
        mCameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            mCameraId = mCameraManager.getCameraIdList(); // 获取所有摄像头的ID
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public static void openActivity(Context context) {
        context.startActivity(new Intent(context, FlashTestActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
<<<<<<< HEAD
        if(mCameraId.length == 1){
            ToastUtils.showToast(this, getString(R.string.single_flash_tip), Toast.LENGTH_SHORT);
        }else{
            ToastUtils.showToast(this, getString(R.string.dual_flash_tip), Toast.LENGTH_SHORT);
        }
=======
>>>>>>> 9f3c1ad69fd50ae07b351f4d3601d2965f149b22
        try {
            // 打开所有闪光灯
            for (String flashId : mCameraId){
                mCameraManager.setTorchMode(flashId, true);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            for (String flashId : mCameraId){
                // 关闭闪光灯
                mCameraManager.setTorchMode(flashId, false);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_pass){
            SharePreferenceUtil.saveData(v.getContext(), EnumSingleTest.POSITION_FLASH.getValue(), EnumSingleTest.TESTED_PASS.getValue());
        } else if (v.getId() == R.id.btn_fail) {
            SharePreferenceUtil.saveData(v.getContext(), EnumSingleTest.POSITION_FLASH.getValue(), EnumSingleTest.TESTED_FAIL.getValue());
        }
        // 跳转至单项测试列表页面
        finish();
    }
}
