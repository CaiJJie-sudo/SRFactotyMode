package com.sagereal.factorymode.singletest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.sagereal.factorymode.R;
import com.sagereal.factorymode.databinding.ActivityCameraTestBinding;
import com.sagereal.factorymode.utils.EnumSingleTest;
import com.sagereal.factorymode.utils.ToastUtils;
import com.sagereal.factorymode.utils.PermissionRequestUtil;
import com.sagereal.factorymode.utils.SharePreferenceUtils;

import java.io.IOException;

public class CameraTestActivity extends AppCompatActivity implements View.OnClickListener, SurfaceHolder.Callback {
    private ActivityCameraTestBinding binding;
    private Camera camera;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private int cameraStatus = Camera.CameraInfo.CAMERA_FACING_BACK; // 默认后置摄像头
    private boolean reverted = false; // 是否切换过相机
    private static final long CLICK_INTERVAL = 2000; // 限制的点击间隔，单位毫秒
    private long lastClickTime = 0; // 记录上次点击时间戳

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_camera_test);
        surfaceView = binding.vCameraTest;
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        setOnClickListeners(binding.btnCameraRevert, binding.btnPass, binding.btnFail);
    }

    /**
     * 打开相机测试活动
     */
    public static void openActivity(Context context) {
        // 检查相机权限
        if (!PermissionRequestUtil.requestSinglePermission(context, Manifest.permission.CAMERA)) {
            PermissionRequestUtil.showPermissionDialog(context);
        } else {
            context.startActivity(new Intent(context, CameraTestActivity.class));
        }
    }

    /**
     * 切换相机
     */
    private void revertCamera() {
        // 切换相机状态
        cameraStatus = (cameraStatus == Camera.CameraInfo.CAMERA_FACING_BACK) ?
                Camera.CameraInfo.CAMERA_FACING_FRONT :
                Camera.CameraInfo.CAMERA_FACING_BACK;
        binding.btnCameraRevert.setText((cameraStatus == Camera.CameraInfo.CAMERA_FACING_BACK) ? R.string.front_camera : R.string.rear_camera);

        // 释放旧摄像头资源
        releaseCamera();

        // 打开新摄像头
        openCamera();
    }

    /**
     * 打开摄像头
     */
    private void openCamera() {
        try {
            camera = Camera.open(cameraStatus);
            camera.setPreviewDisplay(surfaceHolder);
            camera.setDisplayOrientation(90);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放摄像头资源
     */
    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        openCamera();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        releaseCamera();
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
        int id = v.getId();
        // 点击反转相机按钮
        if (id == R.id.btn_camera_revert) {
            // 检查是否在限制时间内连续点击
            if (System.currentTimeMillis() - lastClickTime < CLICK_INTERVAL) {
                // 提示用户不能连续点击
                ToastUtils.showToast(this, getString(R.string.disable_quick_clicks), Toast.LENGTH_SHORT);
                return;
            }
            // 更新上次点击时间
            lastClickTime = System.currentTimeMillis();
            // 反转相机
            revertCamera();
            reverted = true;
        } else if (id == R.id.btn_pass) {
            // 还未反转过相机
            if (!reverted) {
                ToastUtils.showToast(this, getString(R.string.camera_cannot_pass), Toast.LENGTH_SHORT);
            } else {
                SharePreferenceUtils.saveData(v.getContext(), EnumSingleTest.CAMERA_POSITION.getValue(), EnumSingleTest.TESTED_PASS.getValue());
                onBackPressed();
            }
        } else if (id == R.id.btn_fail) {
            SharePreferenceUtils.saveData(v.getContext(), EnumSingleTest.CAMERA_POSITION.getValue(), EnumSingleTest.TESTED_FAIL.getValue());
            onBackPressed();
        }
    }
}

