package com.battery.library.data;


/*
 * created by ltf ，Date 21-10-18
 */

import android.os.BatteryManager;

public class BatteryInfo {

    public BatteryInfo(int status) {
        this.status = status;
    }

    public int status;
    public int chargePlugged;
    public int level;
    public int scale;
    public int batteryPercent;
    public int temperature;
    public int voltage;
    public int health;
    public String technology = "";
    public int lastChargeSpeed;
    public int lastChargePlugged;
    public int normalCharge;
    public int fastCharge;
    public int overCharge;
    public int disChargingLevel;
    public int chargingLevel;
    public long lastChargeDuration;
    public long remainingTime;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getChargePlugged() {
        return chargePlugged;
    }

    public void setChargePlugged(int chargePlugged) {
        this.chargePlugged = chargePlugged;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public int getBatteryPercent() {
        return batteryPercent;
    }

    public void setBatteryPercent(int batteryPercent) {
        this.batteryPercent = batteryPercent;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getVoltage() {
        return voltage;
    }

    public void setVoltage(int voltage) {
        this.voltage = voltage;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public int getLastChargeSpeed() {
        return lastChargeSpeed;
    }

    public void setLastChargeSpeed(int lastChargeSpeed) {
        this.lastChargeSpeed = lastChargeSpeed;
    }

    public int getLastChargePlugged() {
        return lastChargePlugged;
    }

    public void setLastChargePlugged(int lastChargePlugged) {
        this.lastChargePlugged = lastChargePlugged;
    }

    public int getNormalCharge() {
        return normalCharge;
    }

    public void setNormalCharge(int normalCharge) {
        this.normalCharge = normalCharge;
    }

    public int getFastCharge() {
        return fastCharge;
    }

    public void setFastCharge(int fastCharge) {
        this.fastCharge = fastCharge;
    }

    public int getOverCharge() {
        return overCharge;
    }

    public void setOverCharge(int overCharge) {
        this.overCharge = overCharge;
    }

    public int getDisChargingLevel() {
        return disChargingLevel;
    }

    public void setDisChargingLevel(int disChargingLevel) {
        this.disChargingLevel = disChargingLevel;
    }

    public int getChargingLevel() {
        return chargingLevel;
    }

    public void setChargingLevel(int chargingLevel) {
        this.chargingLevel = chargingLevel;
    }

    public long getLastChargeDuration() {
        return lastChargeDuration;
    }

    public void setLastChargeDuration(long lastChargeDuration) {
        this.lastChargeDuration = lastChargeDuration;
    }

    public long getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(long remainingTime) {
        this.remainingTime = remainingTime;
    }

    public boolean isCharging(){
        return status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
    }

    public boolean usbCharging() {
        return this.chargePlugged == BatteryManager.BATTERY_PLUGGED_USB;
    }

    public boolean getAcCharging() {
        return this.chargePlugged == BatteryManager.BATTERY_PLUGGED_AC;
    }

    public boolean wirelessCharging() {
        return this.chargePlugged == BatteryManager.BATTERY_PLUGGED_WIRELESS;
    }


}
