package com.example.aryoulearning.augmented.model;

import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.example.aryoulearning.augmented.ARFragment;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.rendering.ModelRenderable;

import java.lang.ref.WeakReference;

public class ModelLoader {
    private final WeakReference<ARFragment> owner;
    private static final String TAG = "modelLoader";

    public ModelLoader(WeakReference<ARFragment> owner) {
        this.owner = owner;
    }

    public void loadModel(Anchor anchor, Uri uri){
        if(owner.get() == null){
            Log.d(TAG, "Activity is null.  Cannot load model.");
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ModelRenderable.builder()
                    .setSource(owner.get(), uri)
                    .build()
                    .handle(((renderable, throwable) -> {
                        ARFragment activity = owner.get();
                        if(activity == null){
                            return null;
                        }else if(throwable != null){
                            activity.onException(throwable);
                        }else{
                            activity.addNodeToScene(anchor, renderable);
                        }
                        return null;
                    }));
        }

        return;
    }
}
