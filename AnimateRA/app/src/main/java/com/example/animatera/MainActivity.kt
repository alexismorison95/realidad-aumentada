package com.example.animatera

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.SkeletonNode
import com.google.ar.sceneform.animation.ModelAnimator
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var arFragment: ArFragment
    private lateinit var btnAnimar: Button

    private var modelAnimator: ModelAnimator? = null
    private var i = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        arFragment = arFragmentView as ArFragment
        
        arFragment.setOnTapArPlaneListener { hitResult, _, _ ->

            createModel(arFragment, hitResult.createAnchor())
        }
    }

    private fun createModel(arFragment: ArFragment, anchor: Anchor) {

        ModelRenderable.builder()
                .setSource(this, Uri.parse("skeleton.sfb"))
                .build()
                .thenAccept {

                    val anchorNode = AnchorNode(anchor)

                    val skeletonNode = SkeletonNode()

                    skeletonNode.setParent(anchorNode)
                    skeletonNode.renderable = it

                    arFragment.arSceneView.scene.addChild(anchorNode)

                    btnAnimar = btn_animar

                    val modelRenderable = it

                    btnAnimar.setOnClickListener {

                        animateModel(modelRenderable)
                    }
                }
    }

    private fun animateModel(modelRenderable: ModelRenderable) {

        if (modelAnimator != null && modelAnimator!!.isRunning) {

            modelAnimator!!.end()
        }

        val animationCount = modelRenderable.animationDataCount

        if (i == animationCount) {

            i = 0
        }

        val animationData = modelRenderable.getAnimationData(i)

        modelAnimator = ModelAnimator(animationData, modelRenderable)
        modelAnimator!!.start()

        i++
    }
}