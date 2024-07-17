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
import com.sagereal.factorymode.utils.ToastUtils;
import com.sagereal.factorymode.utils.SharePreferenceUtil;

public class KeysTestActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityKeysTestBinding mBinding;
    private BroadcastReceiver powerReceiver = new BroadcastReceiver() {
        /**
         * 处理屏幕关闭广播以隐藏电源按钮。
         * @param context 接收器正在运行的上下文。
         * @param intent 收到的Intent。
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                mBinding.btnKeyPower.setVisibility(View.INVISIBLE);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_keys_test);
        mBinding.btnPass.setOnClickListener(this);
        mBinding.btnFail.setOnClickListener(this);
    }

    public static void openActivity(Context context) {
        context.startActivity(new Intent(context, KeysTestActivity.class));
    }

    /**
     * 音量上和下物理按键的被按下时的处理。
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                mBinding.btnKeyVolumeUp.setVisibility(View.INVISIBLE);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                mBinding.btnKeyVolumeDown.setVisibility(View.INVISIBLE);
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
     */
    private boolean allKeysTested(){
        return mBinding.btnKeyVolumeUp.getVisibility() == View.INVISIBLE
                && mBinding.btnKeyVolumeDown.getVisibility() == View.INVISIBLE
                && mBinding.btnKeyPower.getVisibility() == View.INVISIBLE;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_pass) {
            if(!allKeysTested()){
                ToastUtils.showToast(this, getString(R.string.test_unfinished), Toast.LENGTH_SHORT);
                return;
            }else {
                SharePreferenceUtil.saveData(v.getContext(), EnumSingleTest.POSITION_KEY.getValue(), EnumSingleTest.TESTED_PASS.getValue());
            }
        } else if (v.getId() == R.id.btn_fail) {
            SharePreferenceUtil.saveData(v.getContext(), EnumSingleTest.POSITION_KEY.getValue(), EnumSingleTest.TESTED_FAIL.getValue());
        }
        // 跳转至单项测试列表页面
        finish();
    }
}
