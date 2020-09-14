package com.example.a01_first_app_kotlin

import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var arFragment: ArFragment

    private lateinit var btnDuck: ImageButton
    private lateinit var btnCube: ImageButton
    private lateinit var btnFox: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setViews()
    }

    private fun setViews() {

        arFragment = arFragmentView as ArFragment

        btnCube = btn_cube
        btnDuck = btn_duck
        btnFox = btn_fox

        btnCube.setOnClickListener { onClick(btnCube) }
        btnDuck.setOnClickListener { onClick(btnDuck) }
        btnFox.setOnClickListener { onClick(btnFox) }
    }

    override fun onClick(v: View?) {

        var model = ""

        when(v?.id) {
            R.id.btn_duck -> {
                model = "duck"
            }
            R.id.btn_cube -> {
                model = "animated_cube"
            }
            R.id.btn_fox -> {
                model = "fox"
            }
        }

        Toast.makeText(this, "Tocar donde desee colocar el modelo 3D", Toast.LENGTH_SHORT).show()

        arFragment.setOnTapArPlaneListener { hitResult, _, _ ->

            val anchor = hitResult.createAnchor()

            placeModel(anchor, model)
        }
    }

    private fun placeModel(anchor: Anchor, model: String) {

        ModelRenderable.builder()
            .setSource(this, Uri.parse("${model}.sfb"))
            .build()
            .thenAccept {

                // Hacer cosas antes de agregar modelo
                addModelToScene(anchor, it, model)
            }
            .exceptionally {

                val builder = AlertDialog.Builder(this)

                builder.setMessage(it.message).setTitle("Error")

                val dialog = builder.create()
                dialog.show()

                return@exceptionally null
            }
    }

    private fun addModelToScene(anchor: Anchor, modelRenderable: ModelRenderable, model: String) {

        val anchorNode = AnchorNode(anchor)
        val transformableNode = TransformableNode(arFragment.transformationSystem)

        transformableNode.scaleController.maxScale = 0.5f
        transformableNode.scaleController.minScale = 0.1f

        transformableNode.setParent(anchorNode)
        transformableNode.renderable = modelRenderable

        transformableNode.setOnTapListener { _, _ ->

            Toast.makeText(this, "Click on model $model", Toast.LENGTH_SHORT).show()
        }

        arFragment.arSceneView.scene.addChild(anchorNode)
        transformableNode.select()
    }


}