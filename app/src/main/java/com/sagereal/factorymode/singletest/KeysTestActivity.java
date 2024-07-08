package com.sagereal.factorymode.singletest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.sagereal.factorymode.R;
import com.sagereal.factorymode.databinding.ActivityKeysTestBinding;
import com.sagereal.factorymode.utils.EnumSingleTest;
import com.sagereal.factorymode.utils.FunctionUtils;
import com.sagereal.factorymode.utils.SharePreferenceUtils;

/**
 * 测试音量上、音量下和电源物理按键
 */
public class KeysTestActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityKeysTestBinding binding;
    private BroadcastReceiver powerReceiver = new BroadcastReceiver() {
        /**
         * 处理屏幕关闭广播以隐藏电源按钮。
         * @param context 接收器正在运行的上下文。
         * @param intent 收到的Intent。
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                binding.btnKeyPower.setVisibility(View.INVISIBLE);
            }
        }
    };

    /**
     * 当活动开始时调用。初始化UI组件并注册点击监听器。
     * @param savedInstanceState 如果活动重新初始化（例如在之前被关闭后），Bundle包含最近提供的数据。
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_keys_test);
        binding.btnPass.setOnClickListener(this);
        binding.btnFail.setOnClickListener(this);
    }

    public static void openActivity(Context context) {
        Intent intent = new Intent(context, KeysTestActivity.class);
        context.startActivity(intent);
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_pass) {
            if(!allKeysTested()){
                FunctionUtils.showToast(this, getString(R.string.key_quit), Toast.LENGTH_SHORT);
                return;
            }else {
                SharePreferenceUtils.saveData(v.getContext(), EnumSingleTest.KEY_POSITION.getValue(), EnumSingleTest.TESTED_PASS.getValue());
            }
        } else if (v.getId() == R.id.btn_fail) {
            SharePreferenceUtils.saveData(v.getContext(), EnumSingleTest.KEY_POSITION.getValue(), EnumSingleTest.TESTED_FAIL.getValue());
        }
        // 跳转至单项测试列表页面
        onBackPressed();
    }

    /**
     * 音量上和下物理按键的被按下时的处理。
     * @param keyCode 按下的按钮的键码。
     * @param event 描述按键事件的KeyEvent对象。
     * @return 如果事件被处理则返回true，否则返回false。
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                binding.btnKeyVolumeUp.setVisibility(View.INVISIBLE);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                binding.btnKeyVolumeDown.setVisibility(View.INVISIBLE);
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 在活动恢复时注册电源接收器以处理屏幕关闭事件。
     */
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(powerReceiver, intentFilter);
    }

    /**
     * 三个物理按键是否都已测试
     * @return
     */
    private boolean allKeysTested(){
        return binding.btnKeyVolumeUp.getVisibility() == View.INVISIBLE
                && binding.btnKeyVolumeDown.getVisibility() == View.INVISIBLE
                && binding.btnKeyPower.getVisibility() == View.INVISIBLE;
    }
}
