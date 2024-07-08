package com.sagereal.factorymode.singletest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.sagereal.factorymode.R;
import com.sagereal.factorymode.databinding.ActivityBatteryTestBinding;
import com.sagereal.factorymode.utils.EnumSingleTest;
import com.sagereal.factorymode.utils.FunctionUtils;
import com.sagereal.factorymode.utils.SharePreferenceUtils;

public class BatteryTestActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityBatteryTestBinding binding;
    private boolean isReceiverRegistered = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_battery_test);
        initBatteryInfo(); // 初始化时获取全部电池信息
        binding.btnPass.setOnClickListener(this);
        binding.btnFail.setOnClickListener(this);
    }

    /**
     * 在 Activity 进入前台显示时注册电池状态变化的广播接收器。
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (!isReceiverRegistered) {
            IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            registerReceiver(batteryReceiver, intentFilter);
            isReceiverRegistered = true;
        }
    }

    /**
     * 在 Activity 进入后台或不可见状态时取消注册电池状态变化的广播接收器。
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (isReceiverRegistered) {
            unregisterReceiver(batteryReceiver);
            isReceiverRegistered = false;
        }
    }

    /**
     * 监听电池状态变化的广播，当电池状态发生变化时，刷新电池信息。
     */
    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
                getBatteryChargingStatus(); // 收到广播时更新电池状态信息
            }
        }
    };

    public static void openActivity(Context context) {
        Intent intent = new Intent(context, BatteryTestActivity.class);
        context.startActivity(intent);
    }

    /**
     * 获取全部电池信息并设置对应 textView 的内容。
     */
    private void initBatteryInfo() {
        getBatteryChargingStatus();
        binding.tvBatteryLevel.setText(String.valueOf(batteryLevel()) + getString(R.string.battery_level_unit));
        binding.tvBatteryVoltage.setText(String.valueOf(batteryVoltage()) + getString(R.string.voltage_unit));
        binding.tvBatteryTemperature.setText(String.valueOf(batteryTemperature()) + getString(R.string.temperature_unit));
    }
    /**
     * 只更新电池状态
     */
    private void getBatteryChargingStatus(){
        binding.tvBatteryChargeStatus.setText(batteryIsCharging() ? getString(R.string.is_charging) : getString(R.string.no_charging));
    }
    /**
     * 获取电池状态。
     * @return 电池是否处于充电状态（包括充满电）
     */
    private boolean batteryIsCharging() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent intent = registerReceiver(null, intentFilter);
        if (intent != null) {
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            return status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
        }
        return false;
    }
    /**
     * 获取当前电池电量。
     * @return %
     */
    private int batteryLevel() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent intent = registerReceiver(null, intentFilter);
        if (intent != null) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            return (int) (level / (float) scale * 100);
        }
        return 0;
    }

    /**
     * 获取电池电压。
     * @return mV
     */
    private int batteryVoltage() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent intent = registerReceiver(null, intentFilter);
        if (intent != null) {
            return intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        }
        return 0;
    }

    /**
     * 获取电池温度。
     * @return 摄氏度
     */
    private float batteryTemperature() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent intent = registerReceiver(null, intentFilter);
        if (intent != null) {
            int batteryTemp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            return batteryTemp / 10.0f;
        }
        return 0;
    }
    /**
     * 处理按钮点击事件并跳转页面
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_pass){
            if(!batteryIsCharging()){
                FunctionUtils.showToast(this, getString(R.string.battery_test_tip), Toast.LENGTH_SHORT);
                return;
            }else {
                SharePreferenceUtils.saveData(v.getContext(), EnumSingleTest.BATTERY_POSITION.getValue(), EnumSingleTest.TESTED_PASS.getValue());
            }
        } else if (v.getId() == R.id.btn_fail) {
            SharePreferenceUtils.saveData(v.getContext(), EnumSingleTest.BATTERY_POSITION.getValue(), EnumSingleTest.TESTED_FAIL.getValue());
        }
        // 跳转至单项测试列表页面
        onBackPressed();
    }
}
