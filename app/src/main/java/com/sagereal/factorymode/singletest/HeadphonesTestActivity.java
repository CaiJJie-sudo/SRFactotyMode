package com.sagereal.factorymode.singletest;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.sagereal.factorymode.R;
import com.sagereal.factorymode.databinding.ActivityHeadphonesTestBinding;
import com.sagereal.factorymode.utils.EnumSingleTest;
import com.sagereal.factorymode.utils.FunctionUtils;
import com.sagereal.factorymode.utils.PermissionRequestUtil;
import com.sagereal.factorymode.utils.SharePreferenceUtils;

import java.io.IOException;

public class HeadphonesTestActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityHeadphonesTestBinding binding;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String headphonesTestFilePath; // 录音文件存放位置
    private boolean plugHeadphones = false;// 耳机插拔状态

    private BroadcastReceiver headphonesReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_headphones_test);
        headphonesTestFilePath = getExternalCacheDir().getAbsolutePath() + "/headphonesTestRecording.mp4";
        binding.btnHeadphonesRecord.setOnClickListener(this);
        binding.btnPass.setOnClickListener(this);
        binding.btnFail.setOnClickListener(this);
        // 注册耳机插拔状态变化的广播接收器
        registerHeadphonesReceiver();
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_headphones_record) {
            startRecording();
            return;
        } else if (v.getId() == R.id.btn_pass){
            // 未测试或测试中不能点击通过
            if(binding.tvMikeRecordTip.getVisibility() == View.INVISIBLE ||
                    !binding.btnHeadphonesRecord.isEnabled()) {
                FunctionUtils.showToast(this, getString(R.string.cannot_pass_fail), Toast.LENGTH_SHORT);
                return;
            }else {
                SharePreferenceUtils.saveData(v.getContext(), EnumSingleTest.HEADPHONES_POSITION.getValue(), EnumSingleTest.TESTED_PASS.getValue());
            }
        } else if (v.getId() == R.id.btn_fail) {
            SharePreferenceUtils.saveData(v.getContext(), EnumSingleTest.HEADPHONES_POSITION.getValue(), EnumSingleTest.TESTED_FAIL.getValue());
        }
        // 跳转至单项测试列表页面
        onBackPressed();
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

    public static void openActivity(Context context) {
        // 检查录音权限
        if (!PermissionRequestUtil.requestSinglePermission(context, Manifest.permission.RECORD_AUDIO)) {
            PermissionRequestUtil.showPermissionDialog(context);
        } else {
            Intent intent = new Intent(context, HeadphonesTestActivity.class);
            context.startActivity(intent);
        }
    }

    /**
     * 检查是否插入耳机
     * @return
     */
    private void checkHeadphones() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // 判断是否插入耳机并进行提示
        if (!audioManager.isWiredHeadsetOn()) {
            FunctionUtils.showToast(this, getString(R.string.not_plugged_headphones), Toast.LENGTH_SHORT);
            plugHeadphones = false;
        } else if (audioManager.isWiredHeadsetOn() && plugHeadphones == false) {
            FunctionUtils.showToast(this, getString(R.string.plugged_headphones), Toast.LENGTH_SHORT);
            plugHeadphones = true;
        }
    }

    /**
     * 开始录音
     */
    private void startRecording() {
        checkHeadphones();
        if (!plugHeadphones) {
            return;
        }
        binding.tvMikeRecordTip.setVisibility(View.VISIBLE);
        binding.tvMikeRecordTip.setText(getString(R.string.recording));
        binding.btnHeadphonesRecord.setText(getString(R.string.testing));
        binding.btnHeadphonesRecord.setEnabled(false);

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(headphonesTestFilePath);

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
            mediaPlayer.setDataSource(headphonesTestFilePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            binding.tvMikeRecordTip.setText(R.string.record_playing);
            // 播放完成
            mediaPlayer.setOnCompletionListener(mp -> {
                mediaPlayer.release();
                mediaPlayer = null;
                binding.tvMikeRecordTip.setText(R.string.record_test_finish);
                binding.btnHeadphonesRecord.setText(R.string.retest);
                binding.btnHeadphonesRecord.setEnabled(true);
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
}
