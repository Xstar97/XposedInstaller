package de.robv.android.xposed.installer.core.util.chrome

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsClient
import android.support.customtabs.CustomTabsIntent
import android.support.customtabs.CustomTabsServiceConnection
import android.support.customtabs.CustomTabsSession

/**
 * This is a helper class to manage the connection to the Custom Tabs Service.
 */
class CustomTabActivityHelper : ServiceConnectionCallback {
    private var mCustomTabsSession: CustomTabsSession? = null
    private var mClient: CustomTabsClient? = null
    private var mConnection: CustomTabsServiceConnection? = null
    private var mConnectionCallback: ConnectionCallback? = null

    /**
     * Creates or retrieves an exiting CustomTabsSession.
     *
     * @return a CustomTabsSession.
     */
    val session: CustomTabsSession?
        get() {
            if (mClient == null) {
                mCustomTabsSession = null
            } else if (mCustomTabsSession == null) {
                mCustomTabsSession = mClient!!.newSession(null)
            }
            return mCustomTabsSession
        }

    /**
     * Unbinds the Activity from the Custom Tabs Service.
     *
     * @param activity the activity that is connected to the service.
     */
    fun unbindCustomTabsService(activity: Activity) {
        if (mConnection == null)
            return
        activity.unbindService(mConnection)
        mClient = null
        mCustomTabsSession = null
        mConnection = null
    }

    /**
     * Register a Callback to be called when connected or disconnected from the
     * Custom Tabs Service.
     *
     * @param connectionCallback
     */
    fun setConnectionCallback(connectionCallback: ConnectionCallback) {
        this.mConnectionCallback = connectionCallback
    }

    /**
     * Binds the Activity to the Custom Tabs Service.
     *
     * @param activity
     * the activity to be binded to the service.
     */
    fun bindCustomTabsService(activity: Activity) {
        if (mClient != null)
            return

        val packageName = CustomTabsHelper.getPackageNameToUse(activity) ?: return

        mConnection = ServiceConnection(this)
        CustomTabsClient.bindCustomTabsService(activity, packageName, mConnection)
    }

    /**
     * @return true if call to mayLaunchUrl was accepted.
     * @see {@link CustomTabsSession.mayLaunchUrl
     */
    fun mayLaunchUrl(uri: Uri, extras: Bundle,
                     otherLikelyBundles: List<Bundle>): Boolean {
        if (mClient == null)
            return false

        val session = session
        return session != null && session.mayLaunchUrl(uri, extras, otherLikelyBundles)
    }

    override fun onServiceConnected(client: CustomTabsClient) {
        mClient = client
        mClient!!.warmup(0L)
        if (mConnectionCallback != null)
            mConnectionCallback!!.onCustomTabsConnected()
    }

    override fun onServiceDisconnected() {
        mClient = null
        mCustomTabsSession = null
        if (mConnectionCallback != null)
            mConnectionCallback!!.onCustomTabsDisconnected()
    }

    /**
     * A Callback for when the service is connected or disconnected. Use those
     * callbacks to handle UI changes when the service is connected or
     * disconnected.
     */
    interface ConnectionCallback {
        /**
         * Called when the service is connected.
         */
        fun onCustomTabsConnected()

        /**
         * Called when the service is disconnected.
         */
        fun onCustomTabsDisconnected()
    }

    /**
     * To be used as a fallback to open the Uri when Custom Tabs is not
     * available.
     */
    interface CustomTabFallback {
        /**
         * @param activity The Activity that wants to open the Uri.
         * @param uri      The uri to be opened by the fallback.
         */
        fun openUri(activity: Activity, uri: Uri)
    }

    companion object {

        /**
         * Opens the URL on a Custom Tab if possible. Otherwise fallsback to opening
         * it on a WebView.
         *
         * @param activity         The host activity.
         * @param customTabsIntent a CustomTabsIntent to be used if Custom Tabs is available.
         * @param uri              the Uri to be opened.
         * @param fallback         a CustomTabFallback to be used if Custom Tabs is not
         * available.
         */
        fun openCustomTab(activity: Activity, customTabsIntent: CustomTabsIntent, uri: Uri, fallback: CustomTabFallback?) {
            val packageName = CustomTabsHelper.getPackageNameToUse(activity)

            // If we cant find a package name, it means theres no browser that
            // supports
            // Chrome Custom Tabs installed. So, we fallback to the webview
            if (packageName == null) {
                fallback?.openUri(activity, uri)
            } else {
                customTabsIntent.intent.setPackage(packageName)
                customTabsIntent.launchUrl(activity, uri)
            }
        }
    }

}