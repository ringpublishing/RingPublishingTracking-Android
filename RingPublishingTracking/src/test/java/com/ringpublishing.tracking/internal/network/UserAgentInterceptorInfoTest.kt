package com.ringpublishing.tracking.internal.network

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.ringpublishing.tracking.internal.device.DeviceInfo
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class UserAgentInterceptorInfoTest
{
	@MockK
	lateinit var context: Context

	@MockK
	lateinit var packageManager: PackageManager

	@MockK
	lateinit var packageInfo: PackageInfo

	@MockK
	lateinit var applicationInfo: ApplicationInfo

	@MockK
	internal lateinit var deviceInfo: DeviceInfo

	@Before
	fun before()
	{
		MockKAnnotations.init(this, relaxUnitFun = true)
	}

	@Test
	fun getHeader_WhenMobileDevice_ReturnHeaderWithMobile()
	{
		every { context.packageManager } returns packageManager
		every { context.packageName } returns "packagename"
		every { packageManager.getPackageInfo("packagename", any()) } returns packageInfo
		every { packageManager.getApplicationInfo("packagename", 0) } returns applicationInfo
		every { deviceInfo.isTablet() } returns false

		val userAgentInterceptorInfo = UserAgentInterceptorInfo(context, deviceInfo)
		val header = userAgentInterceptorInfo.getHeader()
		Assert.assertEquals("null Mobile RingPublishing App/null", header)
	}

	@Test
	fun getHeader_WhenTabletDevice_ReturnHeaderWithoutMobile()
	{
		every { context.packageManager } returns packageManager
		every { context.packageName } returns "packagename"
		every { packageManager.getPackageInfo("packagename", any()) } returns packageInfo
		every { packageManager.getApplicationInfo("packagename", 0) } returns applicationInfo
		every { deviceInfo.isTablet() } returns true

		val userAgentInterceptorInfo = UserAgentInterceptorInfo(context, deviceInfo)
		val header = userAgentInterceptorInfo.getHeader()
		Assert.assertEquals("null RingPublishing App/null", header)
	}
}
