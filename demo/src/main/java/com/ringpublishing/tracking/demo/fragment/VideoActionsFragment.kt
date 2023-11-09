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
import com.ringpublishing.tracking.demo.controller.VideoActionsController
import com.ringpublishing.tracking.demo.databinding.FragmentVideoActionsBinding
import com.ringpublishing.tracking.demo.sample.SampleArticleBuilder

class VideoActionsFragment : Fragment(R.layout.fragment_video_actions) {

    private val videoActionsController = VideoActionsController()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentVideoActionsBinding.inflate(inflater, container, false)

        videoActionsController.prepare(ContentPageViewSource.DEFAULT, SampleArticleBuilder(resources).build(0))

        attachClickListeners(binding)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        videoActionsController.viewDidAppear()
    }

    private fun attachClickListeners(binding: FragmentVideoActionsBinding) {
        with(binding)
        {
            with(videoActionsController)
            {
                actionStart.setOnClickListener { actionVideoStart() }
                actionPlayingStart.setOnClickListener { actionVideoPlayingStart() }
                actionPlayingAutoStart.setOnClickListener { actionVideoPlayingAutostart() }
                actionPaused.setOnClickListener { actionVideoPaused() }
                actionPlaying.setOnClickListener { actionVideoPlaying() }
                actionKeepPlaying.setOnClickListener { actionVideoKeepPlaying() }
                actionPlayingEnd.setOnClickListener { actionVideoPlayingEnd() }
            }
        }
    }
}
