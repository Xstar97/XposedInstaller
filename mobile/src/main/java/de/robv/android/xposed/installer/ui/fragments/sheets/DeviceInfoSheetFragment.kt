package de.robv.android.xposed.installer.ui.fragments.sheets

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.XposedApp
import de.robv.android.xposed.installer.core.util.FrameworkZips
import de.robv.android.xposed.installer.logic.adapters.info.TabInfoBaseAdapter
import de.robv.android.xposed.installer.logic.adapters.info.TabInfoModel
import de.robv.android.xposed.installer.ui.fragments.BaseViewFragment
import java.io.File
import kotlinx.android.synthetic.main.fragment_view.*

class DeviceInfoSheetFragment: BaseViewFragment()
{
    override fun onItemClick(infoItem: TabInfoModel) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val deviceAdapter by lazy { TabInfoBaseAdapter(activity!!, this) }
    private val deviceList = ArrayList<TabInfoModel>()
    private val SECTION_DEVICE = 2

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_view, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareListInfo()
    }

    private fun prepareListInfo(){
        fragment_view_recyclerView.adapter = deviceAdapter
        fragment_view_recyclerView.layoutManager = LinearLayoutManager(activity)
        deviceInfo()
    }
    private fun deviceInfo(){
        val androidSdk = getString(R.string.android_sdk, Build.VERSION.RELEASE, androidVersion, Build.VERSION.SDK_INT)
        val manufacturer = uiFramework
        val cpu = FrameworkZips.ARCH

        val verified_boot_deactivated = determineVerifiedBootState().first
        val verified_boot_explanation = determineVerifiedBootState().second

        deviceList.add(TabInfoModel(R.drawable.ic_android, androidSdk, ""))
        deviceList.add(TabInfoModel(R.drawable.ic_phone, manufacturer, ""))
        deviceList.add(TabInfoModel(R.drawable.ic_chip, cpu, ""))
        if (verified_boot_deactivated != "" || verified_boot_explanation != "") {
            deviceList.add(TabInfoModel(R.drawable.ic_verified, verified_boot_deactivated, verified_boot_explanation))
        }
        deviceAdapter.addItems(SECTION_DEVICE, deviceList)
    }

    private val androidVersion: String
        get() {
            return when (Build.VERSION.SDK_INT) {
                15 -> "Ice Cream Sandwich"
                16, 17, 18 -> "Jelly Bean"
                19 -> "KitKat"
                21, 22 -> "Lollipop"
                23 -> "Marshmallow"
                24, 25 -> "Nougat"
                26, 27 -> "Oreo"
                else -> "unknown"
            }
        }
    private val uiFramework: String
        get() {
            var manufacturer = Character.toUpperCase(Build.MANUFACTURER[0]) + Build.MANUFACTURER.substring(1)
            if (Build.BRAND != Build.MANUFACTURER) {
                manufacturer += " " + Character.toUpperCase(Build.BRAND[0]) + Build.BRAND.substring(1)
            }
            manufacturer += " " + Build.MODEL + " "
            if (manufacturer.contains("Samsung")) {
                manufacturer += if (File("/system/framework/twframework.jar").exists()) "(TouchWiz)" else "(AOSP-based ROM)"
            } else if (manufacturer.contains("Xioami")) {
                manufacturer += if (File("/system/framework/framework-miui-res.apk").exists()) "(MIUI)" else "(AOSP-based ROM)"
            }
            return manufacturer
        }

    @SuppressLint("PrivateApi")
    private fun determineVerifiedBootState(): Pair<String, String> {
        return try {
            val c = Class.forName("android.os.SystemProperties")
            val m = c.getDeclaredMethod("get", String::class.java, String::class.java)
            m.isAccessible = true

            val propSystemVerified = m.invoke(null, "partition.system.verified", "0") as String
            val propState = m.invoke(null, "ro.boot.verifiedbootstate", "") as String
            val fileDmVerityModule = File("/sys/module/dm_verity")

            val verified = propSystemVerified != "0"
            val detected = !propState.isEmpty() || fileDmVerityModule.exists()

            return when {
                verified -> {
                    //TODO add color support
                    Pair(getString(R.string.verified_boot_active), getString(R.string.verified_boot_explanation))
                    //tv.setTextColor(resources.getColor(R.color.warning))
                }
                detected -> {
                    Pair(getString(R.string.verified_boot_deactivated), "")
                }
                else -> Pair("", "")
            }
        } catch (e: Exception) {
            Log.e(XposedApp.TAG, "Could not detect Verified Boot state", e)
            Pair("", "")
        }
    }
}