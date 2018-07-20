package de.robv.android.xposed.installer.ui.activities.containers

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.util.Log
import de.robv.android.xposed.installer.XposedApp
import de.robv.android.xposed.installer.logic.NavigationPosition
import de.robv.android.xposed.installer.logic.createFragment

class ViewActivity: FragmentActivity()
{
    companion object {
        val TAG: String = ViewActivity::class.java.simpleName
        const val INTENT_ACTIVITY_KEY = "initActivity"
        fun newInstance() = ViewActivity()
    }

    override fun onCreate(savedInstanceBundle: Bundle?) {
        super.onCreate(savedInstanceBundle)

        if (savedInstanceBundle == null) {
            setActivityFragment()
        }
    }
    private fun setActivityFragment(){
        val nav = NavigationPosition.values()
        val frag = nav[getFragPos()].createFragment()
        supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, frag).commit()
    }
    private fun getFragPos(): Int{
        val intent = this.intent.extras!!.get(INTENT_ACTIVITY_KEY).toString().toInt()
        Log.d(XposedApp.TAG, "$INTENT_ACTIVITY_KEY: $intent")
        return intent
    }
}