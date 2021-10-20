package com.battery.library.util;


/*
 * created by ltf ，Date 21-10-18
 */

import android.content.Context;
import android.os.BatteryManager;
import android.os.Build;

public class BatteryUtil {

    private static final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";
    private static BatteryUtil instance = new BatteryUtil();

    private BatteryUtil() {
    }

    public static BatteryUtil getInstance() {
        return instance;
    }

    public double getBatteryTotalCapacity(Context context) {
        double batteryCapacity = 0.0D;
        try {
            Object powerProfile = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class)
                    .newInstance(context);
            batteryCapacity = (double) Class.forName(POWER_PROFILE_CLASS)
                    .getMethod("getBatteryCapacity")
                    .invoke(powerProfile);

        } catch (Exception var8) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                BatteryManager batteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
                int chargeCounter = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
                int capacity = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                return chargeCounter != Integer.MIN_VALUE && capacity != Integer.MIN_VALUE ? (double) (chargeCounter / capacity) / 10.0D : 0.0D;
            }
        }

        return batteryCapacity;
    }
}
