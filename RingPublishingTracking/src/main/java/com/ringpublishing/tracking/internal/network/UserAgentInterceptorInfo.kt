package com.ringpublishing.tracking.internal.network

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager.NameNotFoundException
import com.ringpublishing.tracking.internal.device.DeviceInfo
import com.ringpublishing.tracking.internal.log.Logger

internal class UserAgentInterceptorInfo(private val context: Context, private val deviceInfo: DeviceInfo)
{

    private val userAgentNonTabletSuffix = "Mobile "
    private val userAgentDefaultAppName = "App"
    private val userAgentDefaultAppVersion = "0.0.0"

    fun getHeader(): String
    {
        return try
        {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val aInfo = context.packageManager.getApplicationInfo(context.packageName, 0)
            getHeader(getAppName(aInfo), pInfo.versionName)
        } catch (ex: NameNotFoundException)
        {
            Logger.debug("Package info for user agent not found $ex")
            getHeader(userAgentDefaultAppName, userAgentDefaultAppVersion)
        }
    }

    private fun getAppName(appInfo: ApplicationInfo): String?
    {
        with(appInfo)
        {
            return when
            {
                nonLocalizedLabel != null -> nonLocalizedLabel.toString()
                labelRes != 0 -> context.getString(labelRes)
                packageName != null -> packageName
                else -> "App"
            }
        }
    }

    private fun getMobilePrefix() = if (deviceInfo.isTablet()) "" else userAgentNonTabletSuffix

    private fun getHeader(appName: String?, versionName: String?): String
    {
        return "${System.getProperty("http.agent")} ${getMobilePrefix()}RingPublishing $appName/$versionName"
    }
}
