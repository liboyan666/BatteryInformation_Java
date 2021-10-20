package com.battery.library;


/*
 * created by ltf ，Date 21-10-18
 */

public class BatteryConstants {


    public static final long ONE_SECOND = 1000;
    public static final long ONE_MINUTE = 60 * ONE_SECOND;
    public static final long ONE_HOUR = 60 * ONE_MINUTE;
    public static final long ONE_DAY = 24 * ONE_HOUR;


    public static final int CHARGING_NORMAL = 0;
    public static final int CHARGING_FAST = 1;
    public static final int CHARGING_OVER = 2;
    public static final long FAST_THRESHOLD = 7500000;  //750万瓦
    public static final long OVERCHARGE_TIME = 5 * ONE_MINUTE;
}
