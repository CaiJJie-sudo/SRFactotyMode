package com.sagereal.factorymode.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * 获取设备基本信息的工具类
 */
public class DeviceBasicInfoUtil {
    private static final double GB_TO_BYTES = 1024.0 * 1024.0 * 1024.0;
    private static final int[] ROM_MAP = new int[]{2, 4, 8, 16, 32, 64, 128, 256, 512};
    private final String deviceName;
    private final String deviceModel;
    private final String deviceVersion;
    private final String androidVersion;
    private final int ram;
    private final int rom;
    private final int batterySize;
    private final double screenSize;
    private final String screenResolution;

    public DeviceBasicInfoUtil(Context context) {
        this.deviceName = Build.DEVICE;
        this.deviceModel = Build.MODEL;
        this.deviceVersion = Build.DISPLAY;
        this.androidVersion = Build.VERSION.RELEASE;
        this.ram = setmDeviceRam(context);
        this.rom = setDeviceRom();
        this.batterySize = setBatterySize(context);
        this.screenSize = setScreenSize(context);
        this.screenResolution = setScreenResolution(context);
    }

    /**
     * 获取设备的RAM并向上取整返回整型GB
     */
    private int setmDeviceRam(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return (int) Math.ceil(memoryInfo.totalMem / GB_TO_BYTES);
    }

    /**
     * 获取设备的ROM并向上取整返回整型GB
     */
    private int setDeviceRom() {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        int totalRom = (int) Math.ceil(statFs.getTotalBytes() / GB_TO_BYTES);
        for (int i : ROM_MAP) {
            if (totalRom <= i) {
                totalRom = i;
                break;
            }
        }
        return totalRom;
    }

    /**
     * 获取设备的电池容量
     */
    private int setBatterySize(Context context) {
        Object powerProfile = null;
        double batterySize = 0;
        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";
        try {
            powerProfile = Class.forName(POWER_PROFILE_CLASS).getConstructor(Context.class).newInstance(context);
            batterySize = (double) Class.forName(POWER_PROFILE_CLASS).getMethod("getBatteryCapacity").invoke(powerProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (int)batterySize;
    }

    /**
     * 获取设备的屏幕分辨率
     */
    public static String setScreenResolution(Context context) {
        DisplayMetrics metrics = getMetrics(context);
        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;

        return widthPixels + " x " + heightPixels;

    }

    /**
     * 获取设备的屏幕尺寸
     */
    public static double setScreenSize(Context context) {
        DisplayMetrics metrics = getMetrics(context);
        float widthPixels = metrics.widthPixels;
        float heightPixels = metrics.heightPixels;
        float xdpi = metrics.xdpi;
        float ydpi = metrics.ydpi;

        // 获取屏幕物理尺寸（英寸 = 像素值 / 每英寸的距离中的像素）
        float widthInches = widthPixels / xdpi;
        float heightInches = heightPixels / ydpi;

        // 返回对角线尺寸（保留两位小数）
        double screenSize = Math.sqrt(Math.pow(widthInches, 2) + Math.pow(heightInches, 2));
        return Math.round(screenSize * 100.0) / 100.0;
    }

    private static DisplayMetrics getMetrics(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        // 获取 WindowManager 系统服务
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            Display display = windowManager.getDefaultDisplay();
            // 将屏幕的实际度量信息存储到 metrics 对象中
            display.getRealMetrics(metrics);
        }
        return metrics;
    }


    public String getDeviceName() {
        return deviceName;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public String getDeviceVersion() {
        return deviceVersion;
    }

    public String getAndroidVersion() {
        return androidVersion;
    }

    public int getRam() {
        return ram;
    }

    public int getRom() {
        return rom;
    }

    public int getBatterySize() {
        return batterySize;
    }

    public double getScreenSize() {
        return screenSize;
    }

    public String getScreenResolution() {
        return screenResolution;
    }
}
