package com.example.augmentedimages02

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.*
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), Scene.OnUpdateListener {

    private lateinit var arFragment: CustomArFragment

    var shouldAddModel = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        arFragment = ar_fragment as CustomArFragment
        arFragment.arSceneView.scene.addOnUpdateListener(this)
    }

    fun setupDatabase(config: Config, session: Session) {

        val duckBitmap = BitmapFactory.decodeResource(resources, R.drawable.pato2)

        // Augmented Images Database
        val aid = AugmentedImageDatabase(session)
        aid.addImage("duck", duckBitmap)

        config.augmentedImageDatabase = aid
    }

    override fun onUpdate(frameTime: FrameTime?) {

        val frame = arFragment.arSceneView.arFrame

        val images: Collection<AugmentedImage> = frame!!.getUpdatedTrackables(AugmentedImage::class.java)

        for (image in images) {

            if (image.trackingState == TrackingState.TRACKING) {

                if (image.name == "duck" && shouldAddModel) {

                    val anchor = image.createAnchor(image.centerPose)

                    createModel(anchor)

                    shouldAddModel = false
                }
            }
        }
    }

    private fun createModel(anchor: Anchor) {

        ModelRenderable.builder()
            .setSource(this, Uri.parse("duck.sfb"))
            .build()
            .thenAccept { addModelToScene(anchor, it, "Duck") }
            .exceptionally {

                val builder = AlertDialog.Builder(this)

                builder.setMessage(it.message).setTitle("Error")

                val dialog = builder.create()
                dialog.show()

                return@exceptionally null
            }
    }

    private fun addModelToScene(anchor: Anchor, modelRenderable: ModelRenderable, name: String) {

        val anchorNode = AnchorNode(anchor)
        val transformableNode = TransformableNode(arFragment.transformationSystem)



        transformableNode.scaleController.maxScale = 0.3f
        transformableNode.scaleController.minScale = 0.1f

        transformableNode.setParent(anchorNode)
        transformableNode.renderable = modelRenderable

        transformableNode.setOnTapListener { _, _ ->

            Toast.makeText(this, "Click on model $name", Toast.LENGTH_SHORT).show()
        }

        arFragment.arSceneView.scene.addChild(anchorNode)
        transformableNode.select()
    }
}