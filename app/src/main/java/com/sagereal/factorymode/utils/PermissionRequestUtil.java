package com.sagereal.factorymode.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.sagereal.factorymode.R;

public class PermissionRequestUtil {
    private static final int REQUEST_CODE = 1;

    // 处理全部的权限请求并返回结果
    public static void onRequestPermissionsResult(Context context, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean allPermissionsGranted = true;
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        allPermissionsGranted = false;
                        break;
                    }
                }
            }
        }
        if (!allPermissionsGranted) {
            // 使用静态方法调用ContextCompat.startActivity()
            showPermissionDialog(context);
        }
    }
    // 处理单个权限请求并返回结果
    public static boolean onRequestSinglePermission(Context context, String permissionName){
        boolean grantResult = false;
        if(ContextCompat.checkSelfPermission(context, permissionName) == PackageManager.PERMISSION_GRANTED){
            grantResult = true;
        }
        return grantResult;
    }
    // 显示权限申请对话框
    public static void showPermissionDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.permission_title);
        builder.setMessage(R.string.permission_message);
        // 跳转至设置页面
        builder.setPositiveButton(R.string.go_setting, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                intent.setData(uri);
                context.startActivity(intent);
            }
        });
        // 取消
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
