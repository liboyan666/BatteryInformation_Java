package com.battery.library;


/*
 * created by ltf ，Date 21-10-18
 */

import android.app.Application;
import android.content.Context;
import android.os.BatteryManager;
import android.os.Build;

import androidx.lifecycle.ViewModelProvider;

import com.battery.library.viewmodel.BatteryViewModel;

public class BatteryApp {

    private Application application;

    private BatteryViewModel batteryViewModel;

    private BatteryManager batteryManager;
    private static BatteryApp instance = new BatteryApp();

    private BatteryApp() {
    }

    public static BatteryApp getInstance() {
        return instance;
    }

    public void init(Application application) {
        this.application = application;
        batteryViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(BatteryViewModel.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            batteryManager = (BatteryManager) application.getSystemService(Context.BATTERY_SERVICE);
        }
    }

    public Application getApplication() {
        return application;
    }

    public BatteryViewModel getBatteryViewModel() {
        return batteryViewModel;
    }

    public BatteryManager getBatteryManager() {
        return batteryManager;
    }
}
