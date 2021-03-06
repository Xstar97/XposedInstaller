package de.robv.android.xposed.installer.mobile.logic

import android.content.Context

import de.robv.android.xposed.installer.core.R
import de.robv.android.xposed.installer.core.logic.base.BaseXposedApp
import de.robv.android.xposed.installer.mobile.ui.base.XposedBaseActivity

object ThemeUtil
{
    private val THEMES = intArrayOf(R.style.Theme_XposedInstaller_Light, R.style.Theme_XposedInstaller_Dark, R.style.Theme_XposedInstaller_Dark_Black)

    private val selectTheme: Int
        get() {
            val myTheme = BaseXposedApp.getPreferences().getString("theme", "0")
            val theme = Integer.parseInt(myTheme!!)
            return if (theme >= 0 && theme < THEMES.size) theme else 0
        }

    fun setTheme(activity: XposedBaseActivity) {
        activity.mTheme = selectTheme
        activity.setTheme(THEMES[activity.mTheme])
    }

    fun reloadTheme(activity: XposedBaseActivity) {
        val theme = selectTheme
        if (theme != activity.mTheme)
            activity.recreate()
    }

    fun getThemeColor(context: Context, id: Int): Int {
        val theme = context.theme
        val a = theme.obtainStyledAttributes(intArrayOf(id))
        val result = a.getColor(0, 0)
        a.recycle()
        return result
    }
}
