package com.battery.library.util;


/*
 * created by ltf ，Date 21-10-18
 */

public interface IBatteryState {
    void onBatteryChanged();

    void onBatteryOkay();

    void onBatteryLow();

    void onPowerConnected();

    void onPowerDisconnected();
}
