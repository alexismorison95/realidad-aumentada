package com.example.playvideo

import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.ExternalTexture
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.PlaneRenderer
import com.google.ar.sceneform.ux.ArFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var texture: ExternalTexture
    private lateinit var mediaPlayer: MediaPlayer

    private lateinit var videoRenderable: ModelRenderable

    private lateinit var arFragment: ArFragment
    private lateinit var btnPlayPause: ImageButton

    private var width: Int = 0
    private var height: Int = 0
    private var height2: Float = 1.25f

    private var isPlay: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createModelVideo()

        setViews()

        setListeners()
    }

    private fun setViews() {

        arFragment = ar_fragment as ArFragment

        btnPlayPause = btn_play_pause

        btnPlayPause.setOnClickListener {

            if (!mediaPlayer.isPlaying) {

                mediaPlayer.start()

                btnPlayPause.setBackgroundResource(R.drawable.ic_baseline_pause_circle_outline_24)
            }
            else {

                mediaPlayer.pause()

                btnPlayPause.setBackgroundResource(R.drawable.ic_baseline_play_circle_outline_24)
            }
        }
    }

    private fun createModelVideo() {

        texture = ExternalTexture()

        mediaPlayer = MediaPlayer.create(this, R.raw.the_expanse)

        mediaPlayer.setSurface(texture.surface)
        mediaPlayer.isLooping = true

        ModelRenderable
                .builder()
                .setSource(this, R.raw.video_screen2)
                .build()
                .thenAccept {

                    it.material.setExternalTexture("videoTexture", texture)

                    it.material.setFloat4("keyColor", Color(0.01843f, 1f, 0.098f))

                    videoRenderable = it
                }
    }

    private fun setListeners() {

        arFragment.setOnTapArPlaneListener { hitResult, _, _ ->

            val anchorNode = AnchorNode(hitResult.createAnchor())

            /*if (!mediaPlayer.isPlaying) {

                mediaPlayer.start()

                texture.surfaceTexture.setOnFrameAvailableListener {

                    anchorNode.renderable = videoRenderable

                    texture.surfaceTexture.setOnFrameAvailableListener {  }
                }
            }
            else {

                anchorNode.renderable = videoRenderable
            }*/

            anchorNode.renderable = videoRenderable

            // Change scale of surface with video size
            width = mediaPlayer.videoWidth
            height = mediaPlayer.videoHeight

            anchorNode.worldScale = Vector3(height2 * (width / height), height2, 0.95f)

            arFragment.arSceneView.scene.addChild(anchorNode)

            btnPlayPause.visibility = View.VISIBLE


        }
    }
}