package com.example.playvideo

import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment

class CustomArFragment: ArFragment() {

    private lateinit var sessionAr: Session

    override fun getSessionConfiguration(session: Session?): Config {

        sessionAr = session!!

        val config = Config(sessionAr)

        config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL

        sessionAr.configure(config)

        this.arSceneView.setupSession(sessionAr)

        return config
    }
}