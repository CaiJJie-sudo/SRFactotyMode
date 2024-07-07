package com.sagereal.factorymode.utils;

/**
 * 单项测试枚举类
 */
public enum EnumSingleTest {
    // 单项测试
    BATTERY_POSITION(0),
    VIBRATION_POSITION(1),
    MIKE_POSITION(2),
    HEADPHONES_POSITION(3),
    LCD_POSITION(4),
    SPEAKER_POSITION(5),
    RECEIVER_POSITION(6),
    CAMERA_POSITION(7),
    FLASH_POSITION(8),
    KEY_POSITION(9),
    SINGLE_TEST_NUM(10),

    // 单项测试状态
    UNTESTED(-1),
    TESTED_PASS(1),
    TESTED_FAIL(0);


    private final int value;

    EnumSingleTest(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}