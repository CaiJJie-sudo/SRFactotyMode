package com.sagereal.factorymode.utils;

/**
 * 单项测试枚举类
 */
public enum EnumSingleTest {
    // 单项测试
    POSITION_BATTERY(0),
    POSITION_VIBRATION(1),
    POSITION_MIKE(2),
    POSITION_HEADPHONES(3),
    POSITION_LCD(4),
    POSITION_SPEAKER(5),
    POSITION_RECEIVER(6),
    POSITION_CAMERA(7),
    POSITION_FLASH(8),
    POSITION_KEY(9),
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