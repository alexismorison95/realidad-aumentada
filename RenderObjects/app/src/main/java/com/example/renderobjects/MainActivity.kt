package com.example.renderobjects

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ShapeFactory
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var arFragment: ArFragment
    private lateinit var btnCube: Button
    private lateinit var btnSphere: Button
    private lateinit var btnCylinder: Button

    private enum class ShapeType {
        CUBE,
        SPHERE,
        CYLINDER
    }

    private var shapeType: ShapeType = ShapeType.CUBE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setViews()

        setListeners()
    }

    private fun setViews() {

        arFragment = ar_fragment as ArFragment
        btnCube = btn_cube
        btnSphere = btn_sphere
        btnCylinder = btn_cylinder
    }

    private fun setListeners() {

        arFragment.setOnTapArPlaneListener { hitResult, _, _ ->

            when (shapeType) {

                ShapeType.CUBE -> placeCube(hitResult.createAnchor())

                ShapeType.SPHERE -> placeSphere(hitResult.createAnchor())

                else -> placeCylinder(hitResult.createAnchor())
            }
        }

        btnCube.setOnClickListener { shapeType = ShapeType.CUBE }
        btnSphere.setOnClickListener { shapeType = ShapeType.SPHERE }
        btnCylinder.setOnClickListener { shapeType = ShapeType.CYLINDER }
    }

    private fun placeCylinder(anchor: Anchor) {

        MaterialFactory
            .makeOpaqueWithColor(this, Color(android.graphics.Color.BLUE))
            .thenAccept {

                val modelRenderable = ShapeFactory.makeCylinder(0.1f, 0.2f, Vector3(0f, 0.2f, 0f), it)

                placeModel(anchor, modelRenderable)
            }
    }

    private fun placeSphere(anchor: Anchor) {

        MaterialFactory
            .makeOpaqueWithColor(this, Color(android.graphics.Color.BLUE))
            .thenAccept {

                val modelRenderable = ShapeFactory.makeSphere(0.1f, Vector3(0f, 1f, 0f), it)

                placeModel(anchor, modelRenderable)
            }
    }

    private fun placeCube(anchor: Anchor) {

        MaterialFactory
            .makeOpaqueWithColor(this, Color(android.graphics.Color.BLUE))
            .thenAccept {

                val modelRenderable = ShapeFactory.makeCube(Vector3(0.1f, 0.1f, 0.1f), Vector3(0f, 0.1f, 0f), it)

                placeModel(anchor, modelRenderable)
            }
    }

    private fun placeModel(anchor: Anchor, modelRenderable: ModelRenderable) {

        val anchorNode = AnchorNode(anchor)
        val transformableNode = TransformableNode(arFragment.transformationSystem)

        transformableNode.setParent(anchorNode)
        transformableNode.renderable = modelRenderable

        arFragment.arSceneView.scene.addChild(anchorNode)
        transformableNode.select()
    }


}