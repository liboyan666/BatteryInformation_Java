package com.battery.library.util;


/*
 * created by ltf ，Date 21-10-18
 */

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;

public class PermissionChecker {


    private static PermissionChecker instance = new PermissionChecker();

    private PermissionChecker() {
    }

    public static PermissionChecker getInstance() {
        return instance;
    }


    public boolean hasUsageAccessPermission( Context context) {
        boolean usageAccess;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            boolean var2;
            try {
                PackageManager packageManager = context.getPackageManager();
                ApplicationInfo info = packageManager.getApplicationInfo(context.getPackageName(), 0);
                AppOpsManager appOpsManager = (AppOpsManager)context.getSystemService(Context.APP_OPS_SERVICE);
                usageAccess = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, info.uid, info.packageName) == AppOpsManager.MODE_ALLOWED;
            } catch (Exception var5) {
                usageAccess = false;
            }

        } else {
            usageAccess = true;
        }

        return usageAccess;
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public void openUsageAccessSetting(Activity activity, int requestCode) {
        try {
            Intent intent = this.getUsageAccessSettingIntent();
            activity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {

        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public Intent getUsageAccessSettingIntent() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        return intent;
    }
}
