package com.sagereal.factorymode.singletest;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.sagereal.factorymode.R;
import com.sagereal.factorymode.databinding.ActivityMikeTestBinding;
import com.sagereal.factorymode.utils.EnumSingleTest;
import com.sagereal.factorymode.utils.ToastUtils;
import com.sagereal.factorymode.utils.PermissionRequestUtil;
import com.sagereal.factorymode.utils.SharePreferenceUtils;

import java.io.IOException;

public class MikeTestActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityMikeTestBinding binding;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String mikeAudioFilePath; // 录音文件存放位置
    private boolean plugHeadphones = false; // 耳机插拔状态
    private BroadcastReceiver headphonesReceiver;
    private boolean testOver = false; // 是否测试完毕
    private AudioManager audioManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mike_test);
        setOnClickListeners(binding.btnMikeRecord, binding.btnPass, binding.btnFail);

        mikeAudioFilePath = getExternalCacheDir().getAbsolutePath() + "/mikeTestRecording.mp4";
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // 注册耳机插拔状态变化的广播接收器
        registerHeadphonesReceiver();
    }

    public static void openActivity(Context context) {
        // 检查录音权限
        if (!PermissionRequestUtil.requestSinglePermission(context, Manifest.permission.RECORD_AUDIO)) {
            PermissionRequestUtil.showPermissionDialog(context);
        } else {
            context.startActivity(new Intent(context, MikeTestActivity.class));
        }
    }

    /**
     * 监听并更改耳机插拔状态变化的广播接收器
     */
    private void registerHeadphonesReceiver() {
        headphonesReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra("state")) {
                    checkHeadphones();
                }
            }
        };
        IntentFilter filter = new IntentFilter(AudioManager.ACTION_HEADSET_PLUG);
        registerReceiver(headphonesReceiver, filter);
    }

    /**
     * 检查耳机状态
     */
    private void checkHeadphones() {
        if (audioManager != null) {
            // 判断是否插入耳机并进行提示
            if (audioManager.isWiredHeadsetOn()) {
                ToastUtils.showToast(this, getString(R.string.speaker_test_headphones), Toast.LENGTH_SHORT);
                plugHeadphones = true;
                // 若插入了耳机且在测试中，则刷新该页面
                if (!binding.btnMikeRecord.isEnabled()) {
                    recreate();
                }
            } else if (!audioManager.isWiredHeadsetOn() && plugHeadphones) {
                ToastUtils.showToast(this, getString(R.string.speaker_test_no_headphones), Toast.LENGTH_SHORT);
                plugHeadphones = false;
            }
        }
    }

    /**
     * 开始录音
     */
    private void startRecording() {
        testOver = false;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding.tvMikeRecordTip.setVisibility(View.VISIBLE);
        binding.tvMikeRecordTip.setText(getString(R.string.recording));
        binding.btnMikeRecord.setText(getString(R.string.testing));
        binding.btnMikeRecord.setEnabled(false);

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(mikeAudioFilePath);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopRecording();
                }
            }, 5000); // 5秒后停止录音
        } catch (IOException e) {
            e.printStackTrace();
            // 添加日志输出或其他处理
        }
    }

    /**
     * 停止录音
     */
    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            // 停止录音后立即播放录音
            playRecording();
        }
    }

    /**
     * 播放录音
     */
    private void playRecording() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(mikeAudioFilePath);
            mediaPlayer.prepare();

            // 检查音量是否为零，如果是，设置音量为最大值的一半
            if (audioManager != null && audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 2,
                        AudioManager.FLAG_SHOW_UI);
            }

            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build());
            mediaPlayer.start();
            binding.tvMikeRecordTip.setText(R.string.record_playing);
            // 播放完成
            mediaPlayer.setOnCompletionListener(mp -> {
                mediaPlayer.release();
                mediaPlayer = null;
                binding.tvMikeRecordTip.setText(R.string.record_test_finish);
                binding.btnMikeRecord.setText(R.string.retest);
                binding.btnMikeRecord.setEnabled(true);
                testOver = true;
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销耳机插拔状态变化的广播接收器
        unregisterReceiver(headphonesReceiver);
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
        if (v.getId() == R.id.btn_mike_record) { // 点击录音
            checkHeadphones();
            if (plugHeadphones) {
                return;
            }
            startRecording();
            return;
        } else if (v.getId() == R.id.btn_pass) {
            // 未测试或测试中不能点击通过
            if (!testOver) {
                ToastUtils.showToast(this, getString(R.string.cannot_pass_fail), Toast.LENGTH_SHORT);
                return;
            } else {
                SharePreferenceUtils.saveData(v.getContext(), EnumSingleTest.POSITION_MIKE.getValue(), EnumSingleTest.TESTED_PASS.getValue());
            }
        } else if (v.getId() == R.id.btn_fail) { // 点击测试失败
            SharePreferenceUtils.saveData(v.getContext(), EnumSingleTest.POSITION_MIKE.getValue(), EnumSingleTest.TESTED_FAIL.getValue());
        }
        // 跳转至单项测试列表页面
        finish();
    }
}
