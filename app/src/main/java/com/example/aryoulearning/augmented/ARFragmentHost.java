package com.example.aryoulearning.augmented;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.aryoulearning.R;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;

public class ARFragmentHost extends AppCompatActivity {
    private ArFragment arFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arfragment);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);


    }

    public void onException(Throwable throwable) {
    }

    public void addNodeToScene(Anchor anchor, ModelRenderable renderable) {
    }
}
