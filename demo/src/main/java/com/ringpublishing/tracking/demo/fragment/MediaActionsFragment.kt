package com.ringpublishing.tracking.demo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.ringpublishing.tracking.data.ContentPageViewSource
import com.ringpublishing.tracking.demo.R
import com.ringpublishing.tracking.demo.controller.MediaActionsController
import com.ringpublishing.tracking.demo.sample.SampleArticleBuilder

class MediaActionsFragment : Fragment(R.layout.fragment_media_actions) {

    private val mediaActionsController = MediaActionsController()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_media_actions, container, false)

        mediaActionsController.prepare(ContentPageViewSource.DEFAULT, SampleArticleBuilder(resources).build(0))

        view.findViewById<Button>(R.id.media_run_audio_scenario).setOnClickListener { mediaActionsController.runAudioScenario() }
        view.findViewById<Button>(R.id.media_run_video_scenario).setOnClickListener { mediaActionsController.runVideoScenario() }

        return view
    }

    override fun onResume() {
        super.onResume()
        mediaActionsController.viewDidAppear()
    }
}
