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
import com.sagereal.factorymode.utils.SharePreferenceUtil;

import java.io.IOException;

public class CameraTestActivity extends AppCompatActivity implements View.OnClickListener, SurfaceHolder.Callback {
    private ActivityCameraTestBinding mBinding;
    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private int mCameraStatus = Camera.CameraInfo.CAMERA_FACING_BACK; // 默认后置摄像头
    private boolean mRevertedCamera = false; // 是否切换过相机
    private long mLastClickTime = 0; // 记录上次点击时间戳
    private static final long CLICK_INTERVAL = 2000; // 限制的点击间隔，单位毫秒

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_camera_test);
        mSurfaceView = mBinding.surfaceViewCameraTest;
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        setOnClickListeners(mBinding.btnCameraRevert, mBinding.btnPass, mBinding.btnFail);
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
        mCameraStatus = (mCameraStatus == Camera.CameraInfo.CAMERA_FACING_BACK) ?
                Camera.CameraInfo.CAMERA_FACING_FRONT :
                Camera.CameraInfo.CAMERA_FACING_BACK;
        mBinding.btnCameraRevert.setText((mCameraStatus == Camera.CameraInfo.CAMERA_FACING_BACK) ? R.string.front_camera : R.string.rear_camera);

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
            mCamera = Camera.open(mCameraStatus);
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.setDisplayOrientation(90);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放摄像头资源
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
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
            if (System.currentTimeMillis() - mLastClickTime < CLICK_INTERVAL) {
                // 提示用户不能连续点击
                ToastUtils.showToast(this, getString(R.string.disable_quick_clicks), Toast.LENGTH_SHORT);
                return;
            }
            // 更新上次点击时间
            mLastClickTime = System.currentTimeMillis();
            // 反转相机
            revertCamera();
            mRevertedCamera = true;
        } else if (id == R.id.btn_pass) {
            // 还未反转过相机
            if (!mRevertedCamera) {
                ToastUtils.showToast(this, getString(R.string.camera_cannot_pass), Toast.LENGTH_SHORT);
            } else {
                SharePreferenceUtil.saveData(v.getContext(), EnumSingleTest.POSITION_CAMERA.getValue(), EnumSingleTest.TESTED_PASS.getValue());
                finish();
            }
        } else if (id == R.id.btn_fail) {
            SharePreferenceUtil.saveData(v.getContext(), EnumSingleTest.POSITION_CAMERA.getValue(), EnumSingleTest.TESTED_FAIL.getValue());
            finish();
        }
    }
}

