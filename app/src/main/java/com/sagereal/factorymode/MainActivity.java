package com.sagereal.factorymode;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.sagereal.factorymode.databinding.ActivityMainBinding;
import com.sagereal.factorymode.utils.DeviceBasicInfoUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private ActivityMainBinding binding;
    private DeviceBasicInfoUtil deviceInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        deviceInfo = new DeviceBasicInfoUtil(getApplicationContext());
        binding.btnCapture.setOnClickListener(this);
        binding.btnCall112.setOnClickListener(this);
        binding.btnSingleTest.setOnClickListener(this);
        binding.btnReport.setOnClickListener(this);
        initHomePage();
    }

    // 初始化首页的基本信息
    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    public void initHomePage(){
        binding.tvDeviceName.setText(deviceInfo.getDeviceName());
        binding.tvDeviceModel.setText(deviceInfo.getDeviceModel());
        binding.tvDeviceVersion.setText(deviceInfo.getDeviceVersion());
        binding.tvAndroidVersion.setText(deviceInfo.getAndroidVersion());
        binding.tvRam.setText(deviceInfo.getRam() + " " + getText(R.string.g));
        binding.tvRom.setText(deviceInfo.getRom() + " " + getText(R.string.gb));
        binding.tvBatterySize.setText(deviceInfo.getBatterySize() + " " + getText(R.string.mAh));
        binding.tvScreenSize.setText(deviceInfo.getScreenSize() + " " + getText(R.string.inch));
        binding.tvScreenResolution.setText(deviceInfo.getScreenResolution() + " " + getText(R.string.pixel));
    }
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_capture){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivity(intent);
        }
        if (v.getId() == R.id.btn_call_112) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DIAL);
            Uri uri = Uri.parse(String.valueOf(R.string.tel_112));
            intent.setData(uri);
            startActivity(intent);
        }
        if(v.getId() == R.id.btn_single_test){
            startActivity(new Intent(this, SingleTestActivity.class));
        }
        if(v.getId() == R.id.btn_report){
            startActivity(new Intent(this, ReportActivity.class));
        }
    }

    // 显示权限申请对话框
    public void showPermissionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.permission_title);
        builder.setMessage(R.string.permission_message);
        // 跳转至设置页面
        builder.setPositiveButton(R.string.go_setting, (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        });
        // 取消
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }
}