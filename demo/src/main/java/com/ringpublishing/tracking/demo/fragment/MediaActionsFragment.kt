package com.ringpublishing.tracking.demo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ringpublishing.tracking.data.ContentPageViewSource
import com.ringpublishing.tracking.demo.R
import com.ringpublishing.tracking.demo.controller.MediaActionsController
import com.ringpublishing.tracking.demo.databinding.FragmentMediaActionsBinding
import com.ringpublishing.tracking.demo.sample.SampleArticleBuilder

class MediaActionsFragment : Fragment(R.layout.fragment_media_actions) {

    private val mediaActionsController = MediaActionsController()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentMediaActionsBinding.inflate(inflater, container, false)

        mediaActionsController.prepare(ContentPageViewSource.DEFAULT, SampleArticleBuilder(resources).build(0))

        binding.mediaRunAudioScenario.setOnClickListener { mediaActionsController.runAudioScenario() }
        binding.mediaRunVideoScenario.setOnClickListener { mediaActionsController.runVideoScenario() }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mediaActionsController.viewDidAppear()
    }
}
