package com.example.aryoulearning.augmented.model;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aryoulearning.R;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;

public class Letter extends Node implements Node.OnTapListener {
    private final String planetName;
    private final float planetScale;
//    private final float orbitDegreesPerSecond;
    private final ModelRenderable planetRenderable;
    private Node letterVisual;

    private final Context context;

    public Letter(
            Context context,
            String planetName,
            float planetScale,
            ModelRenderable planetRenderable) {
        this.context = context;
        this.planetName = planetName;
        this.planetScale = planetScale;
        this.planetRenderable = planetRenderable;
        setOnTapListener(this);
    }

    @Override
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    public void onActivate() {
        Toast.makeText(context, "on Activate method", Toast.LENGTH_SHORT).show();
        letterVisual = new Node();
        letterVisual.setParent(this);
        letterVisual.setRenderable(planetRenderable);

    }

    @Override
    public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {


        Toast.makeText(context, "on Tap listener", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpdate(FrameTime frameTime) {
        // Typically, getScene() will never return null because onUpdate() is only called when the node
        // is in the scene.
        // However, if onUpdate is called explicitly or if the node is removed from the scene on a
        // different thread during onUpdate, then getScene may be null.
        if (getScene() == null) {
            return;
        }
    }
}