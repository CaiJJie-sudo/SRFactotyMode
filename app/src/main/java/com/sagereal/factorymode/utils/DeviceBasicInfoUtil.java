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
    private String mDeviceName;
    private String mDeviceModel;
    private String mDeviceVersion;
    private String mAndroidVersion;
    private int mRam;
    private int mRom;
    private int mBatterySize;
    private double mScreenSize;
    private String mScreenResolution;
    private Context mContext;

    public DeviceBasicInfoUtil(Context context) {
        this.mContext = context;
    }

    private String setDeviceName(){
        this.mDeviceName = Build.DEVICE;
        return mDeviceName;
    }
    private String setDeviceModel(){
        this.mDeviceModel = Build.MODEL;
        return mDeviceModel;
    }
    private String setDeviceVersion(){
        this.mDeviceVersion = Build.DISPLAY;
        return mDeviceVersion;
    }
    private String setAndroidVersion(){
        this.mAndroidVersion = Build.VERSION.RELEASE;
        return mAndroidVersion;
    }
    /**
     * 获取设备的RAM并向上取整返回整型GB
     */
    private int setRam() {
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        this.mRam =  (int) Math.ceil(memoryInfo.totalMem / GB_TO_BYTES);
        return mRam;
    }

    /**
     * 获取设备的ROM并向上取整返回整型GB
     */
    private int setRom() {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        int totalRom = (int) Math.ceil(statFs.getTotalBytes() / GB_TO_BYTES);
        for (int i : ROM_MAP) {
            if (totalRom <= i) {
                totalRom = i;
                break;
            }
        }
        this.mRom = totalRom;
        return mRom;
    }

    /**
     * 获取设备的电池容量
     */
    private int setBatterySize() {
        Object powerProfile = null;
        double batterySize = 0;
        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";
        try {
            powerProfile = Class.forName(POWER_PROFILE_CLASS).getConstructor(Context.class).newInstance(mContext);
            batterySize = (double) Class.forName(POWER_PROFILE_CLASS).getMethod("getBatteryCapacity").invoke(powerProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.mBatterySize = (int)batterySize;
        return mBatterySize;
    }

    /**
     * 获取设备的屏幕分辨率
     */
    public String setScreenResolution() {
        DisplayMetrics metrics = getMetrics(mContext);
        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;

        this.mScreenResolution = widthPixels + " x " + heightPixels;
        return mScreenResolution;
    }

    /**
     * 获取设备的屏幕尺寸
     */
    public double setScreenSize() {
        DisplayMetrics metrics = getMetrics(mContext);
        float widthPixels = metrics.widthPixels;
        float heightPixels = metrics.heightPixels;
        float xdpi = metrics.xdpi;
        float ydpi = metrics.ydpi;

        // 获取屏幕物理尺寸（英寸 = 像素值 / 每英寸的距离中的像素）
        float widthInches = widthPixels / xdpi;
        float heightInches = heightPixels / ydpi;

        // 返回对角线尺寸（保留两位小数）
        double screenSize = Math.sqrt(Math.pow(widthInches, 2) + Math.pow(heightInches, 2));
        this.mScreenSize = Math.round(screenSize * 100.0) / 100.0;
        return mScreenSize;
    }

    private DisplayMetrics getMetrics(Context context) {
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
        return setDeviceName();
    }

    public String getDeviceModel() {
        return setDeviceModel();
    }

    public String getDeviceVersion() {
        return setDeviceVersion();
    }

    public String getAndroidVersion() {
        return setAndroidVersion();
    }

    public int getRam() {
        return setRam();
    }

    public int getRom() {
        return setRom();
    }

    public int getBatterySize() {
        return setBatterySize();
    }

    public double getScreenSize() {
        return setScreenSize();
    }

    public String getScreenResolution() {
        return setScreenResolution();
    }
}
