package com.battery.library.util;


/*
 * created by ltf ，Date 21-10-18
 */

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


import com.battery.library.receiver.BatteryBroadcastReceiver;

import org.jetbrains.annotations.Nullable;

public class BatteryListenerHelper {
    private BatteryBroadcastReceiver receiver;
    private IBatteryState mIBatteryState;
    private Context context;

    public BatteryListenerHelper(Context context) {
        this.context = context;
    }

    public final void register(@Nullable IBatteryState IBatteryState) {
        this.mIBatteryState = IBatteryState;
        if (this.receiver == null) {
            this.receiver = new BatteryBroadcastReceiver(this.mIBatteryState);
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(Intent.ACTION_BATTERY_LOW);
        intentFilter.addAction(Intent.ACTION_BATTERY_OKAY);
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        this.context.registerReceiver(receiver, intentFilter);
    }

    public final void unregister() {
        if (receiver != null) {
            this.context.unregisterReceiver(receiver);
        }

        this.mIBatteryState = null;
    }

}
