package com.sagereal.factorymode.utils;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import androidx.core.content.ContextCompat;

import com.sagereal.factorymode.R;


/**
 * 权限处理工具类
 */
public class PermissionRequestUtil {
    /**
     * 处理单个权限请求并返回结果
     * @param context
     * @param permissionName
     * @return
     */
    public static boolean requestSinglePermission(Context context, String permissionName){
        if(ContextCompat.checkSelfPermission(context, permissionName) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    /**
     * 显示权限申请对话框
     * @param context
     */
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
