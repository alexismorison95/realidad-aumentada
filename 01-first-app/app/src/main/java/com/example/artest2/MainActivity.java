package com.example.artest2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.Config;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class MainActivity extends AppCompatActivity {

    private ArFragment arFragment;
    private ImageButton btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);

        btnSettings = findViewById(R.id.btn_settings);

        btnSettings.setOnClickListener(view -> {
            Toast.makeText(this, "Click btn Settings", Toast.LENGTH_SHORT).show();
        });

        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {

            Anchor anchor = hitResult.createAnchor();

            // Renderizar modelos 3D

            /*ModelRenderable.builder()
                    .setSource(this, Uri.parse("duck.sfb"))
                    .build()
                    .thenAccept(modelRenderable -> addModelToScene(anchor, modelRenderable))
                    .exceptionally(throwable -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);

                        builder.setMessage(throwable.getMessage())
                                .show();

                        return null;
                    });*/

            // Renderizar una vista (controls.xml)

            ViewRenderable.builder()
                    .setView(this, R.layout.controls)
                    .build()
                    .thenAccept(viewRenderable -> {

                        addControlsToScene(anchor, viewRenderable);

                        viewRenderable.getView().findViewById(R.id.btn1).setOnClickListener(view -> {
                            Toast.makeText(this, "Click btn 1", Toast.LENGTH_SHORT).show();
                        });

                        viewRenderable.getView().findViewById(R.id.btn2).setOnClickListener(view -> {
                            Toast.makeText(this, "Click btn 2", Toast.LENGTH_SHORT).show();
                        });

                    })
                    .exceptionally(throwable -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);

                        builder.setMessage(throwable.getMessage())
                                .show();

                        return null;
                    });
        });


    }

    private void addModelToScene(Anchor anchor, ModelRenderable modelRenderable) {

        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());

        transformableNode.getScaleController().setMaxScale(0.5f);
        transformableNode.getScaleController().setMinScale(0.1f);

        transformableNode.setParent(anchorNode);
        transformableNode.setRenderable(modelRenderable);

        transformableNode.setOnTapListener((hitTestResult, motionEvent) -> {
            Toast.makeText(this, "Click", Toast.LENGTH_SHORT).show();
        });

        arFragment.getArSceneView().getScene().addChild(anchorNode);

        transformableNode.select();
    }

    private void addControlsToScene(Anchor anchor, Renderable renderable) {
        
        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());

        transformableNode.setRenderable(renderable);
        transformableNode.setParent(anchorNode);

        arFragment.getArSceneView().getScene().addChild(anchorNode);
    }
}