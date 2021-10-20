package com.battery.library.data;


/*
 * created by ltf ，Date 21-10-18
 */

import android.graphics.drawable.Drawable;


public class InstalledAppInfo {

    private String appName;
    private String packageName;
    private Drawable icon;

    public InstalledAppInfo(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

}
