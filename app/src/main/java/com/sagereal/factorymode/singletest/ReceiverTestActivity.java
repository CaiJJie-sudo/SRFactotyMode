package com.sagereal.factorymode.singletest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.sagereal.factorymode.R;
import com.sagereal.factorymode.databinding.ActivityReceiverTestBinding;
import com.sagereal.factorymode.utils.EnumSingleTest;
import com.sagereal.factorymode.utils.ToastUtils;
import com.sagereal.factorymode.utils.SharePreferenceUtil;

import java.io.IOException;

public class ReceiverTestActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityReceiverTestBinding mBinding;
    private MediaPlayer mMediaPlayer;
    private BroadcastReceiver mHeadphonesBroadcastReceiver;
    private boolean mPlugHeadphones = false; // 耳机插拔状态
    private AudioManager mAudioManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_receiver_test);
        // 注册耳机插拔状态变化的广播接收器
        registerHeadphonesReceiver();
        mBinding.btnPass.setOnClickListener(this);
        mBinding.btnFail.setOnClickListener(this);

        // 获取AudioManager实例
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // 设置音量控制流
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
    }

    public static void openActivity(Context context) {
        context.startActivity(new Intent(context, ReceiverTestActivity.class));
    }

    /**
     * 播放音乐
     */
    private void playMusic() {
        releaseMediaPlayer();

        // 如果插入耳机，直接返回
        if (mPlugHeadphones) {
            return;
        }

        // 检查设备是否支持听筒
        if (!supportReceiver()) {
            ToastUtils.showToast(this, getString(R.string.not_support_receiver_test), Toast.LENGTH_SHORT);
            return;
        }

        if (mAudioManager != null) {
            // 设置音频路由为听筒
            mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            mAudioManager.setSpeakerphoneOn(false);

            // 创建 MediaPlayer
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build());

            try {
                // 设置MediaPlayer的数据源为应用程序的资源文件中的音乐文件
                mMediaPlayer.setDataSource(this, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.music));

                // 设置准备监听器，当MediaPlayer准备好时开始播放
                mMediaPlayer.setOnPreparedListener(MediaPlayer::start);

                // 异步准备MediaPlayer，这样不会阻塞主线程
                mMediaPlayer.prepareAsync();

                // 设置播放完成监听器，当MediaPlayer播放完成时恢复音频路由为默认值
                mMediaPlayer.setOnCompletionListener(mp -> {
                    mAudioManager.setMode(AudioManager.MODE_NORMAL);
                    mAudioManager.setSpeakerphoneOn(true);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 监听并更改耳机插拔状态变化的广播接收器
     */
    private void registerHeadphonesReceiver() {
        mHeadphonesBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra("state")) {
                    pluggedHeadphones();
                    if (!mPlugHeadphones) {
                        playMusic();
                    } else {
                        releaseMediaPlayer();
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter(AudioManager.ACTION_HEADSET_PLUG);
        registerReceiver(mHeadphonesBroadcastReceiver, filter);
    }

    /**
     * 检查耳机插入状态
     * @return true: 有耳机 false : 无耳机
     */
    private void pluggedHeadphones() {
        if (mAudioManager != null && !mAudioManager.isWiredHeadsetOn() && mPlugHeadphones) {
            ToastUtils.showToast(this, getString(R.string.speaker_test_no_headphones), Toast.LENGTH_SHORT);
            mPlugHeadphones = false;
        } else if (mAudioManager != null && mAudioManager.isWiredHeadsetOn()) {
            ToastUtils.showToast(this, getString(R.string.speaker_test_headphones), Toast.LENGTH_SHORT);
            mPlugHeadphones = true;
        }
    }

    /**
     * 当页面可见时播放音乐
     */
    @Override
    protected void onResume() {
        super.onResume();
        // 检查设备是否支持听筒
        if (!supportReceiver()) {
            return;
        }
        if (mMediaPlayer == null) {
            playMusic();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
        // 注销耳机插拔状态变化的广播接收器
        unregisterReceiver(mHeadphonesBroadcastReceiver);
    }

    /**
     * 释放MediaPlayer资源
     */
    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    /**
     * 检测设备是否支持听筒
     */
    private boolean supportReceiver() {
        if (mAudioManager != null) {
            // 获取所有输出音频设备的信息
            AudioDeviceInfo[] devices = mAudioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
            for (AudioDeviceInfo deviceInfo : devices) {
                // 检查设备类型是否为内置听筒
                if (deviceInfo.getType() == AudioDeviceInfo.TYPE_BUILTIN_EARPIECE) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_pass) {
            pluggedHeadphones();
            // 当耳机被插入，则点击PASS时进行提示
            if (mPlugHeadphones) {
                return;
            } else if (!supportReceiver()) {
                ToastUtils.showToast(this, getString(R.string.not_support_receiver_test), Toast.LENGTH_SHORT);
                return;
            } else {
                SharePreferenceUtil.saveData(v.getContext(), EnumSingleTest.POSITION_RECEIVER.getValue(), EnumSingleTest.TESTED_PASS.getValue());
            }
        } else if (v.getId() == R.id.btn_fail) {
            SharePreferenceUtil.saveData(v.getContext(), EnumSingleTest.POSITION_RECEIVER.getValue(), EnumSingleTest.TESTED_FAIL.getValue());
        }
        // 跳转至单项测试列表页面
        finish();
    }
}
