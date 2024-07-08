package com.sagereal.factorymode;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.Manifest;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.sagereal.factorymode.databinding.ActivityMainBinding;
import com.sagereal.factorymode.utils.DeviceBasicInfoUtil;
import com.sagereal.factorymode.utils.FunctionUtils;
import com.sagereal.factorymode.utils.PermissionRequestUtil;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private ActivityMainBinding binding;
    private DeviceBasicInfoUtil deviceBasicInfoUtil;
    private boolean doubleBackToExit = false;   // 双击返回键退出程序
    //请求权限
    private static final int PERMISSIONS_REQUEST_CODE = 1;
    private final String[] mPermissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CALL_PHONE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        deviceBasicInfoUtil = new DeviceBasicInfoUtil(this);

        binding.btnCapture.setOnClickListener(this);
        binding.btnCall112.setOnClickListener(this);
        binding.btnSingleTest.setOnClickListener(this);
        binding.btnReport.setOnClickListener(this);

        initHomePage();
        //检查权限是否授权
        if (!checkPermissions()) {
            //请求权限
            ActivityCompat.requestPermissions(this, mPermissions, PERMISSIONS_REQUEST_CODE);
        }

    }
    // 检查所有权限
    private boolean checkPermissions() {
        for (String permission : mPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    // 处理权限请求的结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean allPermissionsGranted = true;
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            // 检查每一项权限是否都已授权
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
        }
        if(!allPermissionsGranted){
            PermissionRequestUtil.showPermissionDialog(this);
        }
    }

    /**
     * 初始化首页的基本信息
     */
    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    public void initHomePage(){
        binding.tvDeviceName.setText(deviceBasicInfoUtil.getDeviceName());
        binding.tvDeviceModel.setText(deviceBasicInfoUtil.getDeviceModel());
        binding.tvDeviceVersion.setText(deviceBasicInfoUtil.getDeviceVersion());
        binding.tvAndroidVersion.setText(deviceBasicInfoUtil.getAndroidVersion());
        binding.tvRam.setText(deviceBasicInfoUtil.getRam() + " " + getText(R.string.g));
        binding.tvRom.setText(deviceBasicInfoUtil.getRom() + " " + getText(R.string.gb));
        binding.tvBatterySize.setText(deviceBasicInfoUtil.getBatterySize() + " " + getText(R.string.mAh));
        binding.tvScreenSize.setText(deviceBasicInfoUtil.getScreenSize() + " " + getText(R.string.inch));
        binding.tvScreenResolution.setText(deviceBasicInfoUtil.getScreenResolution() + " " + getText(R.string.pixel));
    }
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_capture){
            capture();
        }
        if (v.getId() == R.id.btn_call_112) {
            call112();
        }
        if(v.getId() == R.id.btn_single_test){
            startActivity(new Intent(this, SingleTestActivity.class));
        }
        if(v.getId() == R.id.btn_report){
            startActivity(new Intent(this, ReportActivity.class));
        }
    }

    /**
     * Capture 测试
     */
    private void capture() {
        // 检查是否拥有相机权限
        if (!PermissionRequestUtil.requestSinglePermission(this,Manifest.permission.CAMERA)){
            PermissionRequestUtil.showPermissionDialog(this);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivity(intent);
        }
    }

    /**
     * 自动带号码跳转拨打电话测试
     */
    private void call112(){
       // 检查是否拥有拨打电话的权限
       if (!PermissionRequestUtil.requestSinglePermission(this,Manifest.permission.CALL_PHONE)){
           PermissionRequestUtil.showPermissionDialog(this);
       } else {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DIAL);
            Uri uri = Uri.parse(getString(R.string.tel_112));
            intent.setData(uri);
            startActivity(intent);
        }
    }

    /**
     * 双击系统返回键退出程序
     */
    @Override
    public void onBackPressed() {
        if (doubleBackToExit){
            super.onBackPressed();
            return;
        }
        doubleBackToExit = true;
        FunctionUtils.showToast(this, getString(R.string.exit), Toast.LENGTH_SHORT);
        // 在 2s 内用户再次点击返回键，则退出程序
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExit = false;
            }
        }, 2000);
    }


}