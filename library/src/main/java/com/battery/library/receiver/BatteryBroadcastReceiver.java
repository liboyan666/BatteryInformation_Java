package com.battery.library.receiver;


/*
 * created by ltf ，Date 21-10-18
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Build;


import com.battery.library.BatteryApp;
import com.battery.library.BatteryConstants;
import com.battery.library.data.BatteryInfo;
import com.battery.library.data.BatteryState;
import com.battery.library.util.BatteryStatsImpl;
import com.battery.library.util.IBatteryState;
import com.battery.library.viewmodel.BatteryViewModel;

import kotlin.jvm.internal.Intrinsics;

public class BatteryBroadcastReceiver extends BroadcastReceiver {


    private BatteryInfo batteryInfo;
    private BatteryViewModel viewModel;
    private long chargingTime;
    private boolean disCharging;
    private int lastChargingWattage;
    private boolean isSaveCharging;
    private long firstChargingTime;
    private int chargingLevel;
    private int lastChargeSpeed;
    private int lastChargePlugged;
    private boolean isFull;
    private long chargeTimeFull;
    private int normalCharge;
    private int fastCharge;
    private int overCharge;
    private com.battery.library.util.IBatteryState IBatteryState;

    public BatteryBroadcastReceiver(IBatteryState IBatteryState) {
        this.IBatteryState = IBatteryState;
        this.viewModel = BatteryApp.getInstance().getBatteryViewModel();
        this.isSaveCharging = true;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_BATTERY_CHANGED:
                viewModel.setBatteryState(BatteryState.BATTERY_CHANGED);
                IBatteryState.onBatteryChanged();
                getBatteryData(intent);
                break;
            case Intent.ACTION_BATTERY_OKAY:
                viewModel.setBatteryState(BatteryState.BATTERY_OKAY);
                IBatteryState.onBatteryOkay();
                break;
            case Intent.ACTION_BATTERY_LOW:
                viewModel.setBatteryState(BatteryState.BATTERY_LOW);
                IBatteryState.onBatteryLow();

                break;
            case Intent.ACTION_POWER_CONNECTED:
                viewModel.setBatteryState(BatteryState.POWER_CONNECTED);
                IBatteryState.onPowerConnected();

                break;
            case Intent.ACTION_POWER_DISCONNECTED:
                viewModel.setBatteryState(BatteryState.POWER_DISCONNECTED);
                IBatteryState.onPowerDisconnected();

                break;
            default:
                break;
        }
    }

    private void getBatteryData(Intent intent) {
        if (intent != null) {
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            int chargePlugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int batteryPercent = level * 100 / scale;
            int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / 10;
            int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
            int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
            String technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
            int intProperty;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                BatteryManager batteryManager = BatteryApp.getInstance().getBatteryManager();
                intProperty = batteryManager != null ? batteryManager.getIntProperty(3) * voltage : 0;
            } else {
                intProperty = 0;
            }
            BatteryStatsImpl.getInstance().setBatteryState(status, chargePlugged, level, BatteryApp.getInstance().getApplication());
            long remainingTime = BatteryStatsImpl.getInstance().computeBatteryTimeRemaining();
            if (batteryInfo == null) {
                batteryInfo = new BatteryInfo(status);
            }

            batteryInfo.setStatus(status);
            batteryInfo.setChargePlugged(chargePlugged);
            batteryInfo.setLevel(level);
            batteryInfo.setScale(scale);
            batteryInfo.setBatteryPercent(batteryPercent);
            batteryInfo.setTemperature(temperature);
            batteryInfo.setVoltage(voltage);
            batteryInfo.setHealth(health);
            batteryInfo.setTechnology(technology);
            batteryInfo.setRemainingTime(remainingTime);
            viewModel.setBatteryStateInfo(batteryInfo);


            switch (status) {
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    this.chargingTime = System.currentTimeMillis();
                    this.disCharging = false;
                    if (intProperty > 0 && this.lastChargingWattage < intProperty) {
                        this.lastChargingWattage = intProperty;
                    }

                    if (this.isSaveCharging) {
                        this.firstChargingTime = this.chargingTime;
                        this.chargingLevel = level;
                        this.isSaveCharging = false;
                        this.lastChargePlugged = chargePlugged;
                    }

                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    this.batteryStatusDischarging(level, scale);

                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    this.disCharging = false;
                    if (!this.isFull) {
                        this.chargeTimeFull = System.currentTimeMillis();
                        this.lastChargePlugged = chargePlugged;
                        this.isFull = true;
                    }
                    break;
                default:
                    break;
            }
        }
    }


    private final void batteryStatusDischarging(int level, int scale) {
        long currentTimeMillis = System.currentTimeMillis();
        if (!disCharging) {
            long duration = currentTimeMillis - this.firstChargingTime;

            if (this.firstChargingTime != 0L) {
                if (batteryInfo != null) {
                    batteryInfo.setLastChargeDuration(duration);
                    viewModel.setBatteryStateInfo(batteryInfo);
                }
            }

            if (level == scale && this.chargeTimeFull != 0L && currentTimeMillis - this.chargeTimeFull > BatteryConstants.OVERCHARGE_TIME) {
                this.overCharge++;
                this.lastChargeSpeed = BatteryConstants.CHARGING_OVER;
                this.lastChargingWattage = 0;
                this.chargeTimeFull = 0L;
                this.isSaveCharging = true;
                this.isFull = false;
                this.disCharging = true;
            }

            this.lastChargeSpeed = this.getChargingSpeed(this.lastChargingWattage);
            if (batteryInfo != null) {
                batteryInfo.setLastChargeSpeed(this.lastChargeSpeed);
                batteryInfo.setDisChargingLevel(level);
                batteryInfo.setChargingLevel(this.chargingLevel);
                batteryInfo.setLastChargePlugged(this.lastChargePlugged);
                batteryInfo.setNormalCharge(this.normalCharge);
                batteryInfo.setFastCharge(this.fastCharge);
                batteryInfo.setOverCharge(this.overCharge);
                Intrinsics.checkNotNull(batteryInfo);
                viewModel.setBatteryStateInfo(batteryInfo);
            }

            this.lastChargingWattage = 0;
            this.chargeTimeFull = 0L;
            this.isSaveCharging = true;
            this.isFull = false;
            this.disCharging = true;
        }
    }

    private final int getChargingSpeed(int lastChargingWattage) {
        if (lastChargingWattage < 7500000) {
            normalCharge++;
            return BatteryConstants.CHARGING_NORMAL;
        } else {
            fastCharge++;
            return BatteryConstants.CHARGING_FAST;
        }
    }
}
