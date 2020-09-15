package com.example.readqr

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.*
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.Result


class MainActivity : AppCompatActivity(), Scene.OnUpdateListener {

    private lateinit var arFragment: CustomArFragment
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
        //val qrCode2 = BitmapFactory.decodeResource(resources, R.drawable.qrcode2)

        // Augmented Images Database
        val aid = AugmentedImageDatabase(session)

        menu1Index = aid.addImage("MENU1", qrCode1)
        //menu2Index = aid.addImage("MENU2", qrCode2)

        qrList[menu1Index] = qrCode1

        config.augmentedImageDatabase = aid
    }

    override fun onUpdate(frameTime: FrameTime) {

        val frame = arFragment.arSceneView.arFrame

        val images: Collection<AugmentedImage> = frame!!.getUpdatedTrackables(AugmentedImage::class.java)

        for (image in images) {

            if (image.trackingState == TrackingState.TRACKING) {

                if (image.index == menu1Index && addModel1) {

                    val anchor = image.createAnchor(image.centerPose)

                    val txt = qrList[image.index]?.let { scanQRImage(it) }

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
            .thenAccept { addViewToScene(anchor, it) }
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
        //anchorNode.renderable = renderable

        val node = Node()

        val pose = Pose.makeTranslation(0.0f, 0.0f, 0.12f)
        node.localPosition = Vector3(pose.tx(), pose.ty(), pose.tz())

        node.renderable = renderable
        node.setParent(anchorNode)
        node.localRotation = Quaternion(pose.qx(), 90f, -90f, pose.qw())
        node.renderable!!.isShadowCaster = false

        arFragment.arSceneView.scene.addChild(anchorNode)
    }

    fun scanQRImage(bMap: Bitmap): String? {
        var contents: String? = null
        val intArray = IntArray(bMap.width * bMap.height)
        //copy pixel data from the Bitmap into the 'intArray' array
        bMap.getPixels(intArray, 0, bMap.width, 0, 0, bMap.width, bMap.height)
        val source: LuminanceSource = RGBLuminanceSource(bMap.width, bMap.height, intArray)
        val bitmap = BinaryBitmap(HybridBinarizer(source))
        val reader: Reader = MultiFormatReader()
        try {
            val result = reader.decode(bitmap)
            contents = result.text
        } catch (e: Exception) {
            Log.e("QrTest", "Error decoding barcode", e)
        }
        return contents
    }
}