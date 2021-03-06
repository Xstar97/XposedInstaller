package de.robv.android.xposed.installer.core.logic.base.fragments

import android.app.Activity
import android.content.Context
import androidx.core.content.ContextCompat
import de.robv.android.xposed.installer.core.R
import de.robv.android.xposed.installer.core.logic.Util
import de.robv.android.xposed.installer.core.logic.models.InfoModel
import de.robv.android.xposed.installer.core.util.NavUtil
import eu.chainfire.libsuperuser.Application.toast
import org.jetbrains.anko.toast

open class BaseSupport
{
    companion object {

        const val supportModulesLabel = 0
        const val supportFrameworkLabel = 1
        const val supportFaqLabel = 2
        const val supportDonateLabel = 3
    }
    open fun getSupportList(context: Context): ArrayList<InfoModel>{
        val list = ArrayList<InfoModel>()
        list.add(InfoModel(supportModulesLabel, ContextCompat.getDrawable(context, R.drawable.ic_info)!!, context.getString(R.string.support_modules_label), context.getString(R.string.support_modules_description, context.getString(R.string.module_support))))
        list.add(InfoModel(supportFrameworkLabel, ContextCompat.getDrawable(context, R.drawable.ic_help)!!, context.getString(R.string.support_framework_label), ""))
        list.add(InfoModel(supportFaqLabel, ContextCompat.getDrawable(context, R.drawable.ic_nav_logs)!!, context.getString(R.string.support_faq_label), ""))
        list.add(InfoModel(supportDonateLabel, ContextCompat.getDrawable(context, R.drawable.ic_donate)!!, context.getString(R.string.support_donate_label), context.getString(R.string.support_donate_description)))
        return list
    }
    //actions
    open fun showSupportPage(context: Context){
        setNavUtil(context, context.getString(R.string.about_support))
    }
    open fun showIssuesPage(context: Context){
        setNavUtil(context, context.getString(R.string.support_faq_url))
    }
    open fun showDonationPage(context: Context){
        setNavUtil(context, context.getString(R.string.support_donate_url))
    }

    private fun setNavUtil(context: Context,string: String){
        if (!Util().isDeviceTV(context))
            NavUtil.startURL(context as Activity, string)
        else
            context.toast("no browser...")
    }
}