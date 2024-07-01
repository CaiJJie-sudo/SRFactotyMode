package com.sagereal.factorymode.utils;

public enum EnumData {
    BATTERY_POSITION(0),
    VIBRATION_POSITION(1),
    MIKE_POSITION(2),
    HEADPHONES_POSITION(3),
    LCD_POSITION(4),
    SPEAKER_POSITION(5),
    RECEIVER_POSITION(6),
    CAMERA_POSITION(7),
    FLASH_POSITION(8),
    KEY_POSITION(9);

    private final int value;

    EnumData(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}