package com.battery.library.util;

import android.content.Context;
import android.os.BatteryManager;
import android.os.SystemClock;
import android.util.Log;

import java.util.Random;

public class BatteryStatsImpl {

    // The part of a step duration that is the actual time.
    private static final long STEP_LEVEL_TIME_MASK = 0x000000ffffffffffL; //1099511627775L
    private static final long STEP_LEVEL_INITIAL_MODE_SHIFT = 48;
    private static final long STEP_LEVEL_MODIFIED_MODE_SHIFT = 56;
    private static final long STEP_LEVEL_LEVEL_SHIFT = 40;
    // Step duration mode: power save is on.
    public static final int STEP_LEVEL_MODE_POWER_SAVE = 0x04;
    // Step duration mode: the screen is on, off, dozed, etc; value is
    // Display.STATE_* - 1.
    public static final int STEP_LEVEL_MODE_SCREEN_STATE = 0x03;
    // This should probably be exposed in the API, though it's not critical
    private static final int BATTERY_PLUGGED_NONE = 0;

    private static final int MAX_LEVEL_STEPS = 200;
    private static final int STATE_UNKNOWN = 0;
    private final long[] mDischargeStepDurations = new long[MAX_LEVEL_STEPS];
    private final long[] mChargeStepDurations = new long[MAX_LEVEL_STEPS];
    private static final String TAG = "BatteryStatsImpl";
    private boolean mLowPowerModeEnabled;
    private boolean mOnBattery;
    private int mNumDischargeStepDurations;
    private int mCurrentBatteryLevel;
//    private int mInitStepMode = 0;
//    private int mModStepMode = 0;
    private int mLastDischargeStepLevel;
    private int mMinDischargeStepLevel;
    private long mLastDischargeStepTime;
    private int mCurStepMode = 0;
    private int mLastChargeStepLevel;
    private int mMaxChargeStepLevel;
    private int mNumChargeStepDurations;
    private long mLastChargeStepTime;
    private int mScreenState = STATE_UNKNOWN;
    private int mDischargeCurrentLevel;
    private byte batteryStatus;
    private int mHighDischargeAmountSinceCharge;
    private int mDischargeUnplugLevel;

    private final long random = ((long) (new Random().nextInt(60000) + 1200000));
    private static final long f7013a = 1200000;
    private boolean f7032t;

    private static volatile BatteryStatsImpl sInstance;

    private BatteryStatsImpl() {
        mDischargeUnplugLevel = 0;
        mDischargeCurrentLevel = 0;
        mCurrentBatteryLevel = 0;
        initDischarge();
    }

    public static BatteryStatsImpl getInstance() {
        if (sInstance == null) {
            synchronized (BatteryStatsImpl.class) {
                if (sInstance == null) {
                    sInstance = new BatteryStatsImpl();
                }
            }
        }
        return sInstance;
    }

    private void initDischarge() {
        mHighDischargeAmountSinceCharge = 0;
        mLastDischargeStepTime = -1;
        mNumDischargeStepDurations = 0;
        mLastChargeStepTime = -1;
        mNumChargeStepDurations = 0;
    }

    public long computeBatteryTimeRemaining() {
//        if (!mOnBattery) {
//            return -1;
//        }
//        if (mNumDischargeStepDurations < 1) {
//            return -1;
//        }
        long msPerLevel = computeTimePerLevel(mDischargeStepDurations, mNumDischargeStepDurations);
        if (msPerLevel <= 0) {
//            return -1;
            msPerLevel = random;
            if (!this.f7032t) {
                long[] array = mDischargeStepDurations;
                array[0] = random;
                mNumDischargeStepDurations++;
                System.arraycopy(array, 0, array, 1, array.length - 1);
                mDischargeStepDurations[0] = random;
                mNumDischargeStepDurations++;
                this.f7032t = true;
            }
        }
//        return (msPerLevel * mCurrentBatteryLevel) * 1000;
        return msPerLevel * mCurrentBatteryLevel;
    }

    public long computeChargeTimeRemaining() {
        if (mOnBattery) {
            return -1;
        }
        if (mNumChargeStepDurations < 1) {
            return -1;
        }
        long msPerLevel = computeTimePerLevel(mChargeStepDurations, mNumChargeStepDurations);
        if (msPerLevel <= 0) {
            return -1;
        }
        return msPerLevel * (100 - mCurrentBatteryLevel) * 1000;
    }

    private long computeTimePerLevel(long[] steps, int numSteps) {
        if (numSteps <= 0) {
            return 0;
        }
        long total = 0;
        for (int i = 0; i < numSteps; i++) {
            total += steps[i] & STEP_LEVEL_TIME_MASK;
        }
        return total / numSteps;
    }

    private int addLevelSteps(long[] steps, int stepCount, long lastStepTime,
                              int numStepLevels, long modeBits, long elapsedRealtime) {
        if (lastStepTime >= 0 && numStepLevels > 0) {
            long duration = elapsedRealtime - lastStepTime;
            for (int i = 0; i < numStepLevels; i++) {
                System.arraycopy(steps, 0, steps, 1, steps.length - 1);
                long thisDuration = duration / (numStepLevels - i);
                duration -= thisDuration;
                if (thisDuration > STEP_LEVEL_TIME_MASK) {
                    thisDuration = STEP_LEVEL_TIME_MASK;
                }
                long j6 = f7013a;
                if (thisDuration > j6) {
                    thisDuration = j6;
                } else if (thisDuration <= j6 / 2 || mCurrentBatteryLevel <= 10) {
                    if (thisDuration <= j6 / 2) {
                        thisDuration = (j6 + (thisDuration / 2)) / 2;
                    }
                } else {
                    thisDuration = (j6 + thisDuration) / 2;
                }
                steps[0] = thisDuration | modeBits;
            }
            stepCount += numStepLevels;
            if (stepCount > steps.length) {
                stepCount = steps.length;
            }
        }
        return stepCount;
    }

    public void setBatteryState(int status, int plugType, int level, Context context) {
        synchronized (this) {
            final boolean onBattery = plugType == BATTERY_PLUGGED_NONE;
            final long uptime = SystemClock.uptimeMillis();
            final long elapsedRealtime = SystemClock.elapsedRealtime();
            int oldStatus = batteryStatus;
//            PowerManager powerManager = (PowerManager) context
//                    .getSystemService(Context.POWER_SERVICE);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                if (powerManager.isPowerSaveMode()) {
//                    noteLowPowerMode(true);
//                    mLowPowerModeEnabled = false;
//                }
//            }
//            WindowManager windowManager = (WindowManager) context
//                    .getSystemService(Context.WINDOW_SERVICE);
//            int state = 0;
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT_WATCH) {
//                state = windowManager.getDefaultDisplay().getState();
//            }
//            noteScreenStateLocked(state);
            if (onBattery) {
                mDischargeCurrentLevel = level;
            }
            mCurrentBatteryLevel = level;
            if (onBattery != mOnBattery) {
                batteryStatus = (byte) status;
                setOnBatteryLocked(elapsedRealtime, uptime, onBattery, oldStatus, level);
            } else {
                if (batteryStatus != status) {
                    batteryStatus = (byte) status;
                }
//                long modeBits = (((long) mInitStepMode) << STEP_LEVEL_INITIAL_MODE_SHIFT)
//                        | (((long) mModStepMode) << STEP_LEVEL_MODIFIED_MODE_SHIFT)
//                        | (((long) (level & 0xff)) << STEP_LEVEL_LEVEL_SHIFT);
                long modeBits = (((long) (level & 0xff)) << STEP_LEVEL_LEVEL_SHIFT);
                if (onBattery) {
                    if (mLastDischargeStepLevel != level && mMinDischargeStepLevel > level) {
                        mNumDischargeStepDurations = addLevelSteps(mDischargeStepDurations,
                                mNumDischargeStepDurations, mLastDischargeStepTime,
                                mLastDischargeStepLevel - level, modeBits, elapsedRealtime);
                        mLastDischargeStepLevel = level;
                        mMinDischargeStepLevel = level;
                        mLastDischargeStepTime = elapsedRealtime;
//                        mInitStepMode = mCurStepMode;
//                        mModStepMode = 0;
                    }
                } else {
                    if (mLastChargeStepLevel != level && mMaxChargeStepLevel < level) {
                        mNumChargeStepDurations = addLevelSteps(mChargeStepDurations,
                                mNumChargeStepDurations, mLastChargeStepTime,
                                level - mLastChargeStepLevel, modeBits, elapsedRealtime);
                        mLastChargeStepLevel = level;
                        mMaxChargeStepLevel = level;
                        mLastChargeStepTime = elapsedRealtime;
//                        mInitStepMode = mCurStepMode;
//                        mModStepMode = 0;
                    }
                }
            }
        }
    }

    private void noteLowPowerMode(boolean enabled) {
        if (mLowPowerModeEnabled != enabled) {
            int stepState = enabled ? STEP_LEVEL_MODE_POWER_SAVE : 0;
//            mModStepMode |= (mCurStepMode & STEP_LEVEL_MODE_POWER_SAVE) ^ stepState;
            mCurStepMode = (mCurStepMode & ~STEP_LEVEL_MODE_POWER_SAVE) | stepState;
            mLowPowerModeEnabled = enabled;
        }
    }

    private void noteScreenStateLocked(int state) {
        if (mScreenState != state) {
            mScreenState = state;
            if (state != STATE_UNKNOWN) {
                int stepState = state - 1;
                if (stepState < 4) {
//                    mModStepMode |= (mCurStepMode & STEP_LEVEL_MODE_SCREEN_STATE) ^ stepState;
                    mCurStepMode = (mCurStepMode & ~STEP_LEVEL_MODE_SCREEN_STATE) | stepState;
                } else {
                    Log.i(TAG, "Unexpected screen state: " + state);
                }
            }
        }
    }

    private void setOnBatteryLocked(final long mSecRealtime, final long mSecUptime,
                                    final boolean onBattery, final int oldStatus, final int level) {
        if (onBattery) {
            if ((oldStatus == BatteryManager.BATTERY_STATUS_FULL
//                    || level >= 90
//                    || (mDischargeCurrentLevel < 20 && level >= 80)
                    || (getHighDischargeAmountSinceCharge() >= 200))) {
//                mNumDischargeStepDurations = 0;
                initDischarge();
            }
            mOnBattery = onBattery;
//            mInitStepMode = mCurStepMode;
//            mModStepMode = 0;
            mDischargeUnplugLevel = level;
            mDischargeCurrentLevel = level;
            mLastDischargeStepLevel = level;
            mMinDischargeStepLevel = level;
            mLastDischargeStepTime = mSecRealtime;
        } else {
            if (level < mDischargeUnplugLevel) {
                mHighDischargeAmountSinceCharge += mDischargeUnplugLevel - level;
            }
            mOnBattery = onBattery;
            mDischargeCurrentLevel = level;
            mNumChargeStepDurations = 0;
            mLastChargeStepLevel = level;
            mMaxChargeStepLevel = level;
            mLastChargeStepTime = mSecRealtime;
//            mInitStepMode = mCurStepMode;
//            mModStepMode = 0;
        }
    }

    private int getHighDischargeAmountSinceCharge() {
        synchronized (this) {
            int val = mHighDischargeAmountSinceCharge;
            if (mOnBattery && mDischargeCurrentLevel < mDischargeUnplugLevel) {
                val += mDischargeUnplugLevel - mDischargeCurrentLevel;
            }
            return val;
        }
    }
}