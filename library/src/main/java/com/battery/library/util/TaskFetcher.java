package com.battery.library.util;


/*
 * created by ltf ，Date 21-10-19
 */

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;


import com.battery.library.BatteryApp;
import com.battery.library.data.InstalledAppInfo;
import com.battery.library.data.LastUsedApp;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import kotlin.collections.CollectionsKt;

public class TaskFetcher {
    private static TaskFetcher instance = new TaskFetcher();

    private TaskFetcher() {
    }

    public static TaskFetcher getInstance() {
        return instance;
    }

    private static final ArrayList WHITE_LIST_PACKAGE = CollectionsKt.arrayListOf(new String[]{"android", "com.google.android.gsf", "com.google.android.gsf.login", "com.android.systemui", "com.google.android.packageinstaller", "com.android.settings", "com.google.android.gms"});
    private static final ArrayList WHITE_LIST_KEYS = CollectionsKt.arrayListOf(new String[]{"input", "time", "clock", "provider", "system", "launcher", "package"});

    public List<InstalledAppInfo> getCanStopAppList(Context context) {
        List<InstalledAppInfo> list = new ArrayList<InstalledAppInfo>();
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(0);
        for (PackageInfo packageInfo : packageInfoList) {
            String packageName = packageInfo.packageName;
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            boolean systemApp = (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0;
            boolean appStopped = (applicationInfo.flags & ApplicationInfo.FLAG_STOPPED) > 0;
            boolean appPersistent = (applicationInfo.flags & ApplicationInfo.FLAG_PERSISTENT) > 0;
            if (systemApp || appStopped || appPersistent) {
                continue;
            }
            if (WHITE_LIST_PACKAGE.contains(packageName)
                    || BatteryApp.getInstance().getApplication().getPackageName().equals(packageName)
            ) {
                continue;
            }
            InstalledAppInfo installedAppInfo = new InstalledAppInfo(packageName);
            installedAppInfo.setAppName(applicationInfo.loadLabel(packageManager).toString());
            installedAppInfo.setIcon(applicationInfo.loadIcon(packageManager));
            list.add(installedAppInfo);
        }
        return list;
    }


    public final List<LastUsedApp> getLastUsedAppList(@NotNull final Context context, final long startTime, final long endTime) {
        ArrayList<LastUsedApp> list = new ArrayList<LastUsedApp>();
        ArrayList<LastUsedApp> newList = new ArrayList<LastUsedApp>();
        if (!PermissionChecker.getInstance().hasUsageAccessPermission(context)) {
            return list;
        }
        try {
            //此处AppCompatActivity.USAGE_STATS_SERVICE提示最低版本要求是Android5.1, 但经测试5.0上也可用
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                UsageStatsManager statsManager =
                        (UsageStatsManager) context.getSystemService(AppCompatActivity.USAGE_STATS_SERVICE);

                Collection queryUsageStats = (Collection) statsManager
                        .queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
                if (!queryUsageStats.isEmpty()) {

                    Iterator iterator = queryUsageStats.iterator();
                    while (iterator.hasNext()) {
                        UsageStats usageStats = (UsageStats) iterator.next();
                        long appTotalTimeInForeground = usageStats.getTotalTimeInForeground();
                        if (appTotalTimeInForeground == 0L) {
                            continue;
                        }
                        int appLaunchCount;
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                //经测试9.0及以上版本才有getAppLaunchCount()方法
                                appLaunchCount = (int) Class.forName("android.app.usage.UsageStats")
                                        .getMethod("getAppLaunchCount")
                                        .invoke(usageStats);
                            } else {
                                appLaunchCount = usageStats.getClass().getDeclaredField("mLaunchCount").getInt(usageStats);
                            }
                        } catch (Exception e) {
                            appLaunchCount = 0;
                        }
                        if (appLaunchCount == 0) {
                            continue;
                        }
                        long appLastTimeUsed = usageStats.getLastTimeUsed();
                        String packageName = usageStats.getPackageName();
                        LastUsedApp lastUsedApp = new LastUsedApp(packageName);
                        if (!list.contains(lastUsedApp)) {
                            lastUsedApp.setName(AppUtil.INSTANCE.getApplicationName(context, packageName));
                            lastUsedApp.setIcon(AppUtil.INSTANCE.getAppIcon(context, packageName));
                            lastUsedApp.setLaunchCount(appLaunchCount);
                            lastUsedApp.setTotalTimeInForeground(appTotalTimeInForeground);
                            lastUsedApp.setLastTimeUsed(appLastTimeUsed);
                            list.add(lastUsedApp);
                        } else {
                            int index = list.indexOf(lastUsedApp);
                            LastUsedApp usedApp = list.get(index);
                            usedApp.setLaunchCount(usedApp.getLaunchCount() + appLaunchCount);
                            usedApp.setTotalTimeInForeground(usedApp.getTotalTimeInForeground() + appTotalTimeInForeground);
                            if (usedApp.getLastTimeUsed() < appLastTimeUsed) {
                                usedApp.setLastTimeUsed(appLastTimeUsed);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {

        }

        for (LastUsedApp lastUsedApp : list) {
            if (!lastUsedApp.getName().isEmpty() && lastUsedApp.getPackageName() != context.getPackageName()) {
                newList.add(lastUsedApp);
            }
        }

        return newList;
    }
}
