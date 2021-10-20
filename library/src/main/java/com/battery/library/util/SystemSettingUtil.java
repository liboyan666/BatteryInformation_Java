package com.battery.library.util;


/*
 * created by ltf ，Date 21-10-18
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;

import org.jetbrains.annotations.NotNull;

public class SystemSettingUtil {
    private static SystemSettingUtil instance = new SystemSettingUtil();

    private SystemSettingUtil() {
    }

    public static SystemSettingUtil getInstance() {
        return instance;
    }

    public int getScreenOffTimeout(@NotNull Context context) {
        try {
            return Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public boolean setScreenOffTimeout(@NotNull Context context, int timeout) {
        return !this.canWrite(context) ? false : Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, timeout);
    }

    public boolean isHapticFeedbackEnabled(@NotNull Context context) {
        try {
            return Settings.System.getInt(context.getContentResolver(), Settings.System.HAPTIC_FEEDBACK_ENABLED) == 1;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean setHapticFeedback(@NotNull Context context, int enable) {
        return !this.canWrite(context) ? false : Settings.System.putInt(context.getContentResolver(), Settings.System.HAPTIC_FEEDBACK_ENABLED, enable);
    }

    public boolean isSoundEffectsEnabled(@NotNull Context context) {
        try {
            return Settings.System.getInt(context.getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED) == 1;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean setSoundEffects(@NotNull Context context, int enable) {
        return !this.canWrite(context) ? false : Settings.System.putInt(context.getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, enable);
    }

    public int getBrightness(@NotNull Context context){
        try {
            return Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public boolean setBrightness(@NotNull Context context, int brightness) {
        return !this.canWrite(context) ? false : Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
    }

    public boolean setBrightness(@NotNull Context context, float brightness) {
        return this.setBrightness(context, (int) (brightness * (float) 255));
    }

    public boolean isBrightnessModeAuto(@NotNull Context context) {
        boolean isBright;
        try {
            isBright = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Settings.SettingNotFoundException var4) {
            isBright = false;
        }

        return isBright;
    }

    public boolean setBrightnessMode(@NotNull Context context, int mode) {
        return !this.canWrite(context) ? false : Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, mode);
    }

    public boolean canWrite(@NotNull Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? Settings.System.canWrite(context) : true;
    }

    @RequiresApi(Build.VERSION_CODES.M)
    public final void openManageWriteSettings(@NotNull Activity activity, @NotNull String packageName, int requestCode) {
        activity.startActivityForResult(this.getManageWriteSettingsIntent(packageName), requestCode);
    }

    @RequiresApi(Build.VERSION_CODES.M)
    public final Intent getManageWriteSettingsIntent(@NotNull String packageName) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + packageName));
        return intent;
    }

}
