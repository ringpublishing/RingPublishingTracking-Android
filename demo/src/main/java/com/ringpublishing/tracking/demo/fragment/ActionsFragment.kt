/*
 *  Created by Grzegorz Małopolski on 9/22/21, 1:03 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.demo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ringpublishing.tracking.data.ContentPageViewSource
import com.ringpublishing.tracking.demo.R
import com.ringpublishing.tracking.demo.sample.SampleArticleBuilder
import com.ringpublishing.tracking.demo.controller.ActionsController
import com.ringpublishing.tracking.demo.databinding.FragmentActionsBinding
import com.ringpublishing.tracking.demo.extension.startArticleActivity

class ActionsFragment : Fragment(R.layout.fragment_actions)
{

	private val actionsController = ActionsController()

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
	{
		val binding = FragmentActionsBinding.inflate(inflater, container, false)

		actionsController.prepare(ContentPageViewSource.DEFAULT, SampleArticleBuilder(resources).build(0))

		attachClickListeners(binding)

		return binding.root
	}

	override fun onResume()
	{
		super.onResume()
		actionsController.viewDidAppear()
	}

	private fun attachClickListeners(binding: FragmentActionsBinding)
	{
		with(binding)
		{
			with(actionsController)
			{
				actionLogin.setOnClickListener { actionLogin(it) }
				actionLogout.setOnClickListener { actionLogout(it) }

				actionReportUserActionString.setOnClickListener { actionReportUserActionString(it) }
				actionReportUserActionMap.setOnClickListener { actionReportUserActionMap(it) }

				actionOpenDetailFromPush.setOnClickListener {
					actionOpenDetailFromPush()
					activity?.startArticleActivity(0, actionsController.pageViewSource)
				}
				actionOpenDetailFromSocial.setOnClickListener {
					actionOpenDetailFromSocial()
					activity?.startArticleActivity(0, actionsController.pageViewSource)
				}

				actionReportGenericEvent.setOnClickListener { actionReportGenericEvent() }

				actionEnableDebugMode.setOnClickListener { actionEnableDebugMode() }
				actionDisableDebugMode.setOnClickListener { actionDisableDebugMode() }

				actionEnableOptOutMode.setOnClickListener { actionEnableOptOutMode() }
				actionDisableOptOutMode.setOnClickListener { actionDisableOptOutMode() }

				actionReportAureusImpression.setOnClickListener { actionReportAureusImpression() }
			}
		}
	}
}
