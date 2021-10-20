package com.battery.library.util

import android.content.Context
import android.graphics.drawable.Drawable


/*
 * created by ltf ，Date 21-10-20
 */

object AppUtil {

    fun getApplicationName(context: Context, packageName: String): String =
        try {
            val packageManager = context.packageManager
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(applicationInfo) as String
        } catch (e: Exception) {
            ""
        }

    fun getAppIcon(context: Context, packageName: String): Drawable? =
        try {
            val packageManager = context.packageManager
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            applicationInfo.loadIcon(packageManager)
        } catch (e: Exception) {
            null
        }
}