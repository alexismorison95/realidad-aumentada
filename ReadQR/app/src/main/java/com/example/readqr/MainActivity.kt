package com.example.readqr

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.*
import com.google.ar.core.AugmentedImageDatabase
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.ViewRenderable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.menu1.*
import kotlinx.android.synthetic.main.menu1.view.*
import java.io.InputStream


class MainActivity : AppCompatActivity(), Scene.OnUpdateListener, View.OnClickListener {

    private lateinit var arFragment: CustomArFragment
    private lateinit var btn1 : Button
    private lateinit var btn2 : Button

    var addModel1 = true
    var menu1Index = -1

    var qrList = hashMapOf<Int, Bitmap>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        arFragment = ar_fragment as CustomArFragment

        arFragment.planeDiscoveryController.hide()
        arFragment.planeDiscoveryController.setInstructionView(null)

        arFragment.arSceneView.scene.addOnUpdateListener(this::onUpdate)
    }

    fun setupDatabase(config: Config, session: Session) {

        val qrCode1 = BitmapFactory.decodeResource(resources, R.drawable.qrcode1)
        val qrCode2 = BitmapFactory.decodeResource(resources, R.drawable.qrcode2)

        qrList[0] = qrCode1
        qrList[1] = qrCode2

        // Augmented Images Database
        //val aid = AugmentedImageDatabase(session)

        // Load DB from file
        val inputStream = this.assets.open("images_db.imgdb")
        val imageDatabase = AugmentedImageDatabase.deserialize(session, inputStream)

        //menu1Index = imageDatabase.addImage("MENU1", qrCode1)
        //menu2Index = aid.addImage("MENU2", qrCode2)



        config.augmentedImageDatabase = imageDatabase
    }

    override fun onUpdate(frameTime: FrameTime) {

        val frame = arFragment.arSceneView.arFrame

        val images: Collection<AugmentedImage> = frame!!.getUpdatedTrackables(AugmentedImage::class.java)

        for (image in images) {

            if (image.trackingState == TrackingState.TRACKING) {

                //if (image.index == 0 && addModel1) {
                if (addModel1) {

                    val anchor = image.createAnchor(image.centerPose)

                    val txt = qrList[image.index]?.let { Utils.scanQRImage(it) }

                    //val txt = qrList[image.index]?.let { scanQRImage(it) }

                    Toast.makeText(this, txt, Toast.LENGTH_SHORT).show()

                    createView(anchor, R.layout.menu1)

                    addModel1 = false
                }
            }
        }
    }

    private fun createView(anchor: Anchor, layout: Int) {

        ViewRenderable.builder()
            .setView(this, layout)
            .build()
            .thenAccept {

                addViewToScene(anchor, it)

                btn1 = it.view.btn_menu_1
                btn2 = it.view.btn_menu_2

                btn1.setOnClickListener(this::onClick)
                btn2.setOnClickListener(this::onClick)

            }
            .exceptionally {

                val builder = AlertDialog.Builder(this)
                builder.setMessage(it.message).setTitle("Error")

                val dialog = builder.create()
                dialog.show()

                return@exceptionally null
            }
    }

    private fun addViewToScene(anchor: Anchor, renderable: Renderable) {

        val anchorNode = AnchorNode(anchor)

        val node = Node()

        val pose = Pose.makeTranslation(0.0f, 0.0f, 0.12f)
        node.localPosition = Vector3(pose.tx(), pose.ty(), pose.tz())

        node.renderable = renderable
        node.setParent(anchorNode)
        node.localRotation = Quaternion(pose.qx(), 90f, -90f, pose.qw())
        node.renderable!!.isShadowCaster = false

        arFragment.arSceneView.scene.addChild(anchorNode)
    }

    override fun onClick(v: View) {

        var msj = ""

        when (v.id) {

            R.id.btn_menu_1 -> msj = "Click btn 1"
            R.id.btn_menu_2 -> msj = "Click btn 2"
        }

        Toast.makeText(this, msj, Toast.LENGTH_SHORT).show()
    }
}