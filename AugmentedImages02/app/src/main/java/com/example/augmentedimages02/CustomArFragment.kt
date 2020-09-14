package com.example.augmentedimages02

import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment

class CustomArFragment: ArFragment() {

    override fun getSessionConfiguration(session: Session?): Config {

        val config = Config(session)

        config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
        config.focusMode = Config.FocusMode.AUTO

        session?.configure(config)

        this.arSceneView.setupSession(session)

        session?.let { (activity as MainActivity).setupDatabase(config, it) }

        return config
    }
}