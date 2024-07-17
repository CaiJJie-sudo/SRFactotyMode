package com.sagereal.factorymode;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
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
import com.sagereal.factorymode.utils.ToastUtils;
import com.sagereal.factorymode.utils.PermissionRequestUtil;

import java.util.Arrays;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityMainBinding mBinding;
    private DeviceBasicInfoUtil mDeviceBasicInfoUtil;
    private boolean mDoubleBackToExit = false; // 双击返回键退出程序

    private static final int PERMISSIONS_REQUEST_CODE = 1; // 权限请求的代码
    private final String[] mPermissions = { // 需要请求的权限数组
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CALL_PHONE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mDeviceBasicInfoUtil = new DeviceBasicInfoUtil(this);

        setOnClickListeners(mBinding.btnCapture, mBinding.btnCall112, mBinding.btnSingleTest, mBinding.btnReport, mBinding.ibSwitchLanguage);

        initHomePage();
        //检查权限是否授权
        if (!checkPermissions()) {
            //请求权限
            ActivityCompat.requestPermissions(this, mPermissions, PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * 设置点击事件监听器
     */
    private void setOnClickListeners(View... views) {
        for (View view : views) {
            view.setOnClickListener(this);
        }
    }

    /**
     * 检查所有权限
     */
    private boolean checkPermissions() {
        for (String permission : mPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 处理权限请求的结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            // 遍历权限数组
            if (Arrays.stream(grantResults).anyMatch(result -> result != PackageManager.PERMISSION_GRANTED)) {
                PermissionRequestUtil.showPermissionDialog(this);
            }
        }
    }

    /**
     * 初始化首页的基本信息
     */
    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void initHomePage() {
        mBinding.tvDeviceName.setText(mDeviceBasicInfoUtil.getDeviceName());
        mBinding.tvDeviceModel.setText(mDeviceBasicInfoUtil.getDeviceModel());
        mBinding.tvDeviceVersion.setText(mDeviceBasicInfoUtil.getDeviceVersion());
        mBinding.tvAndroidVersion.setText(mDeviceBasicInfoUtil.getAndroidVersion());
        mBinding.tvRam.setText(mDeviceBasicInfoUtil.getRam() + " " + getText(R.string.g));
        mBinding.tvRom.setText(mDeviceBasicInfoUtil.getRom() + " " + getText(R.string.gb));
        mBinding.tvBatterySize.setText(mDeviceBasicInfoUtil.getBatterySize() + " " + getText(R.string.mAh));
        mBinding.tvScreenSize.setText(mDeviceBasicInfoUtil.getScreenSize() + " " + getText(R.string.inch));
        mBinding.tvScreenResolution.setText(mDeviceBasicInfoUtil.getScreenResolution() + " " + getText(R.string.pixel));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ib_switch_language) {
            switchLanguage();
        } else if (id == R.id.btn_capture) {
            capture();
        } else if (id == R.id.btn_call_112) {
            call112();
        } else if (id == R.id.btn_single_test) {
            startActivity(new Intent(this, SingleTestActivity.class));
        } else if (id == R.id.btn_report) {
            startActivity(new Intent(this, ReportActivity.class));
        }
    }


    /**
     * 相机测试
     */
    private void capture() {
        // 检查是否拥有相机权限
        if (PermissionRequestUtil.requestSinglePermission(this, Manifest.permission.CAMERA)) {
            startActivity(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));
        } else {
            PermissionRequestUtil.showPermissionDialog(this);
        }
    }

    /**
     * 自动带号码跳转拨打电话测试
     */
    private void call112() {
        // 检查是否拥有拨打电话的权限
        if (PermissionRequestUtil.requestSinglePermission(this, Manifest.permission.CALL_PHONE)) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(getString(R.string.tel_112)));
            startActivity(intent);
        } else {
            PermissionRequestUtil.showPermissionDialog(this);
        }
    }

    /**
     * 双击系统返回键退出程序
     */
    @Override
    public void onBackPressed() {
        if (mDoubleBackToExit) {
            super.onBackPressed();
        } else {
            mDoubleBackToExit = true;
            ToastUtils.showToast(this, getString(R.string.exit), Toast.LENGTH_SHORT);
            new Handler().postDelayed(() -> mDoubleBackToExit = false, 2000);
        }
    }

    /**
     * 改变应用语言
     */
    private void switchLanguage() {
        // 获取当前语言设置
        boolean isChinese = getResources().getConfiguration().locale.getLanguage().equals("zh");
        Configuration config = getResources().getConfiguration();
        // 切换语言
        config.locale = isChinese ? Locale.US : Locale.CHINESE;
        // 更新应用的 Configuration
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        recreate();
    }

}
