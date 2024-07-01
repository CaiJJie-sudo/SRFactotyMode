package com.sagereal.factorymode;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.sagereal.factorymode.databinding.ActivityMainBinding;
import com.sagereal.factorymode.utils.DeviceBasicInfoUtil;
import com.sagereal.factorymode.utils.PermissionRequestUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private ActivityMainBinding binding;
    private DeviceBasicInfoUtil deviceBasicInfoUtil;
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
    }

    // 初始化首页的基本信息
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

    // 相机测试
    private void capture() {
        // 检查是否拥有相机权限
        if (!PermissionRequestUtil.onRequestSinglePermission(this,Manifest.permission.CAMERA)){
            PermissionRequestUtil.showPermissionDialog(this);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivity(intent);
        }
    }
    // 自动带号码跳转拨打电话页面测试
    private void call112(){
       // 检查是否拥有拨打电话的权限
       if (!PermissionRequestUtil.onRequestSinglePermission(this,Manifest.permission.CALL_PHONE)){
           PermissionRequestUtil.showPermissionDialog(this);
       } else {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DIAL);
            Uri uri = Uri.parse(getString(R.string.tel_112));
            intent.setData(uri);
            startActivity(intent);
        }
    }

}