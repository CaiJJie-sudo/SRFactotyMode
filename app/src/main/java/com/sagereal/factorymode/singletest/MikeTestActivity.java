package com.sagereal.factorymode.singletest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.sagereal.factorymode.R;
import com.sagereal.factorymode.databinding.ActivityMikeTestBinding;
import com.sagereal.factorymode.utils.EnumData;
import com.sagereal.factorymode.utils.PermissionRequestUtil;

import java.io.IOException;

public class MikeTestActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityMikeTestBinding binding;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String mikeAudioFilePath; // 录音文件存放位置
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mike_test);
        binding.btnMikeRecord.setOnClickListener(this);
        mikeAudioFilePath = getExternalCacheDir().getAbsolutePath() + "/mikeTestRecording.mp4";
    }
    public static void openActivity(Context context) {
        // 检查录音权限
        if (!PermissionRequestUtil.onRequestSinglePermission(context, Manifest.permission.RECORD_AUDIO)) {
            PermissionRequestUtil.showPermissionDialog(context);
        }else {
            Intent intent = new Intent(context, MikeTestActivity.class);
            intent.getIntExtra(String.valueOf(EnumData.MIKE_POSITION), 0);
            context.startActivity(intent);
        }
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_mike_record) {
            startRecording();
        }
    }
    private void startRecording() {
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
                    stopRecordingAndPlay();
                }
            }, 5000); // 5秒后停止录音并播放
        } catch (IOException e) {
            e.printStackTrace();
            // 添加日志输出或其他处理
        }
    }

    private void stopRecordingAndPlay() {
        try {
            mediaPlayer.setDataSource(mikeAudioFilePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.stop();
                    mp.release();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.tvMikeRecordTip.setText(R.string.record_test_finish);
                            binding.btnMikeRecord.setText(R.string.retest);
                            binding.btnMikeRecord.setEnabled(true);
                        }
                    });
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("cai", "失败");
            // 添加日志输出或其他处理
            Toast.makeText(this, "播放录音失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
    /**
     * 在活动停止时，释放已经分配的录音和播放资源
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
