/*
 *  Created by Daniel Całka on 7/23/24, 1:51 PM
 *  Copyright © 2024 Ringier Axel Springer Tech. All rights reserved.
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
import com.ringpublishing.tracking.demo.controller.PaidController
import com.ringpublishing.tracking.demo.databinding.FragmentPaidBinding
import com.ringpublishing.tracking.demo.sample.SampleArticleBuilder

class PaidFragment : Fragment(R.layout.fragment_paid) {

    private val paidController = PaidController()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentPaidBinding.inflate(inflater, container, false)
        paidController.prepare(ContentPageViewSource.DEFAULT, SampleArticleBuilder(resources).build(0))
        attachClickListeners(binding)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        paidController.viewDidAppear()
    }

    private fun attachClickListeners(binding: FragmentPaidBinding) {
        with(binding) {
            with(paidController) {
                showOffer.setOnClickListener { showOffer() }
                showOfferTeaser.setOnClickListener { showOfferTeaser() }
                clickButton.setOnClickListener { clickButton() }
                purchase.setOnClickListener { purchase() }
                showMetricLimit.setOnClickListener { showMetricLimit() }
                likelihoodScoring.setOnClickListener { showLikelihoodScoring() }
                fakeUserReplaced.setOnClickListener { replaceFakeUser() }
            }
        }
    }
}
