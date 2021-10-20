package com.battery.library.viewmodel;


/*
 * created by ltf ，Date 21-10-18
 */

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.battery.library.BatteryApp;
import com.battery.library.data.BatteryInfo;
import com.battery.library.data.BatteryState;
import com.battery.library.data.LastUsedApp;
import com.battery.library.util.SystemSettingUtil;
import com.battery.library.util.TaskFetcher;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import kotlin.jvm.internal.Intrinsics;


public class BatteryViewModel extends ViewModel {

    private final MutableLiveData batteryInfo = new MutableLiveData();
    private final MutableLiveData batteryState = new MutableLiveData();
    private final MutableLiveData consumeBatteryAppCount = new MutableLiveData();
    private final MutableLiveData lastUsedAppList = new MutableLiveData();
    private final MutableLiveData hapticFeedbackEnabled = new MutableLiveData();
    private final MutableLiveData soundEffectsEnabled = new MutableLiveData();
    private final MutableLiveData screenBrightness = new MutableLiveData();
    private final MutableLiveData screenOffTimeout = new MutableLiveData();


    @NotNull
    public final MutableLiveData getBatteryInfo() {
        return this.batteryInfo;
    }

    public final void setBatteryStateInfo(@NotNull BatteryInfo batteryInfo) {
        Intrinsics.checkNotNullParameter(batteryInfo, "batteryInfo");
        this.batteryInfo.setValue(batteryInfo);
    }

    @NotNull
    public final MutableLiveData getBatteryState() {
        return this.batteryState;
    }

    public final void setBatteryState(@NotNull BatteryState batteryState) {
        Intrinsics.checkNotNullParameter(batteryState, "batteryState");
        this.batteryState.setValue(batteryState);
    }

    @NotNull
    public final MutableLiveData getConsumeBatteryAppCount() {
        return this.consumeBatteryAppCount;
    }

    @NotNull
    public final MutableLiveData getLastUsedAppList() {
        return this.lastUsedAppList;
    }




    public void getConsumeBatteryApps() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(TaskFetcher.getInstance().getCanStopAppList(BatteryApp.getInstance().getApplication()).size());
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer count) throws Exception {
                if (count != null)
                    consumeBatteryAppCount.setValue(count);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
            }
        });

    }


    public void getLastUsedApps(long startTime, long endTime) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Observable.create(new ObservableOnSubscribe<List<LastUsedApp>>() {
                @Override
                public void subscribe(@NonNull ObservableEmitter<List<LastUsedApp>> emitter) throws Exception {
                    emitter.onNext(TaskFetcher.getInstance().getLastUsedAppList(BatteryApp.getInstance().getApplication(), startTime, endTime));
                    emitter.onComplete();
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<LastUsedApp>>() {
                @Override
                public void accept(List<LastUsedApp> list) throws Exception {
                    lastUsedAppList.setValue(list);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                }
            });
        }
    }


    @NotNull
    public final MutableLiveData isHapticFeedbackEnabled() {
        return this.hapticFeedbackEnabled;
    }

    public final void getHapticFeedbackEnabled() {
        boolean enable = SystemSettingUtil.getInstance().isHapticFeedbackEnabled((Context)BatteryApp.getInstance().getApplication());
        this.hapticFeedbackEnabled.setValue(enable);
    }

    public final boolean setHapticFeedbackEnabled(boolean enable) {
        int value = enable ? 1 : 0;
        return SystemSettingUtil.getInstance().setHapticFeedback((Context)BatteryApp.getInstance().getApplication(), value);
    }

    @NotNull
    public final MutableLiveData isSoundEffectsEnabled() {
        return this.soundEffectsEnabled;
    }

    public final void getSoundEffectsEnabled() {
        boolean enable = SystemSettingUtil.getInstance().isSoundEffectsEnabled((Context)BatteryApp.getInstance().getApplication());
        this.soundEffectsEnabled.setValue(enable);
    }

    public final boolean setSoundEffectsEnabled(boolean enable) {
        int value = enable ? 1 : 0;
        return SystemSettingUtil.getInstance().setSoundEffects((Context)BatteryApp.getInstance().getApplication(), value);
    }

    @NotNull
    public final MutableLiveData getBrightnessValue() {
        return this.screenBrightness;
    }

    public final void getBrightness() {
        int value = SystemSettingUtil.getInstance().getBrightness((Context)BatteryApp.getInstance().getApplication());
        this.screenBrightness.setValue(value);
    }

    public final boolean setBrightness(float brightness) {
        return SystemSettingUtil.getInstance().setBrightness((Context)BatteryApp.getInstance().getApplication(), brightness);
    }

    @NotNull
    public final MutableLiveData getScreenOffTimeoutValue() {
        return this.screenOffTimeout;
    }

    public final void getScreenOffTimeout() {
        int value = SystemSettingUtil.getInstance().getScreenOffTimeout((Context)BatteryApp.getInstance().getApplication());
        this.screenOffTimeout.setValue(value);
    }

    public final boolean setScreenOffTimeout(int timeout) {
        return SystemSettingUtil.getInstance().setScreenOffTimeout((Context)BatteryApp.getInstance().getApplication(), timeout);
    }

}
