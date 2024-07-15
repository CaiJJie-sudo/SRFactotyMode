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
import com.sagereal.factorymode.utils.ToastUtils;
import com.sagereal.factorymode.utils.SharePreferenceUtil;

public class BatteryTestActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityBatteryTestBinding mBinding;
    private boolean mIsReceiverRegistered = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_battery_test);
        initBatteryInfo(); // 初始化时获取全部电池信息
        mBinding.btnPass.setOnClickListener(this);
        mBinding.btnFail.setOnClickListener(this);
    }

    /**
     * 在 Activity 进入前台显示时注册电池状态变化的广播接收器。
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (!mIsReceiverRegistered) {
            registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            mIsReceiverRegistered = true;
        }
    }

    /**
     * 在 Activity 进入后台或不可见状态时取消注册电池状态变化的广播接收器。
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mIsReceiverRegistered) {
            unregisterReceiver(batteryReceiver);
            mIsReceiverRegistered = false;
        }
    }

    /**
     * 监听电池充电状态变化的广播
     */
    private final BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                chargerChange();
            }
        }
    };

    /**
     * 打开电池测试活动。
     */
    public static void openActivity(Context context) {
        context.startActivity(new Intent(context, BatteryTestActivity.class));
    }

    /**
     * 获取全部电池信息并设置对应 textView 的内容。
     */
    private void initBatteryInfo() {
        chargerChange();
        mBinding.tvBatteryLevel.setText(String.valueOf(batteryLevel()) + getString(R.string.battery_level_unit));
        mBinding.tvBatteryTemperature.setText(String.valueOf(batteryTemperature()) + getString(R.string.temperature_unit));
    }

    /**
     * 插拔充电器时更新能骤变的充电状态和电压（电量和电池温度不随着充电器的插拔而改变）
     */
    private void chargerChange() {
        mBinding.tvBatteryChargeStatus.setText(batteryIsCharging() ? getString(R.string.is_charging) : getString(R.string.no_charging));
        mBinding.tvBatteryVoltage.setText(String.valueOf(batteryVoltage()) + getString(R.string.voltage_unit));
    }

    /**
     * 获取电池状态。
     * @return 电池是否处于充电状态（包括充满电）
     */
    private boolean batteryIsCharging() {
        Intent intent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
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
        Intent intent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
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
        Intent intent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
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
        Intent intent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (intent != null) {
            int batteryTemp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            return batteryTemp / 10.0f;
        }
        return 0;
    }

    /**
     * 处理按钮点击事件并跳转页面
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_pass) {
            if (!batteryIsCharging()) {
                ToastUtils.showToast(this, getString(R.string.battery_test_tip), Toast.LENGTH_SHORT);
            } else {
                SharePreferenceUtil.saveData(v.getContext(), EnumSingleTest.POSITION_BATTERY.getValue(), EnumSingleTest.TESTED_PASS.getValue());
                finish();
            }
        } else if (id == R.id.btn_fail) {
            SharePreferenceUtil.saveData(v.getContext(), EnumSingleTest.POSITION_BATTERY.getValue(), EnumSingleTest.TESTED_FAIL.getValue());
            finish();
        }
    }
}
