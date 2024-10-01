/*
 * Created by Daniel Całka on 10/01/24, 12:12 PM
 * Copyright © 2024 Ringier Axel Springer Tech. All rights reserved.
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
import com.ringpublishing.tracking.demo.controller.AudioActionsController
import com.ringpublishing.tracking.demo.databinding.FragmentAudioActionsBinding
import com.ringpublishing.tracking.demo.sample.SampleArticleBuilder

class AudioActionsFragment : Fragment(R.layout.fragment_audio_actions) {

    private val audioActionsController = AudioActionsController()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentAudioActionsBinding.inflate(inflater, container, false)

        audioActionsController.prepare(ContentPageViewSource.DEFAULT, SampleArticleBuilder(resources).build(0))

        attachClickListeners(binding)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        audioActionsController.viewDidAppear()
    }

    private fun attachClickListeners(binding: FragmentAudioActionsBinding) {
        with(binding)
        {
            with(audioActionsController)
            {
                actionStart.setOnClickListener { actionAudioStart() }
                actionPlayingStart.setOnClickListener { actionAudioPlayingStart() }
                actionPlayingAutoStart.setOnClickListener { actionAudioPlayingAutostart() }
                actionPaused.setOnClickListener { actionAudioPaused() }
                actionPlaying.setOnClickListener { actionAudioPlaying() }
                actionKeepPlaying.setOnClickListener { actionAudioKeepPlaying() }
                actionPlayingEnd.setOnClickListener { actionAudioPlayingEnd() }
            }
        }
    }
}
