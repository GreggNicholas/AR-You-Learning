package com.example.aryoulearning.augmented;

import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.aryoulearning.R;
import com.example.aryoulearning.augmented.model.ModelLoader;
import com.example.aryoulearning.augmented.pointer.PointerDrawable;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.lang.ref.WeakReference;
import java.util.List;

public class ARHostFragment extends AppCompatActivity {
    private ArFragment arFragment;
    private PointerDrawable pointer = new PointerDrawable();
    private boolean isTracking;
    private boolean isHitting;
    private ModelLoader modelLoader;
    private ModelRenderable catRenderable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arfragment_host);
        modelLoader = new ModelLoader(new WeakReference<>(this));
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        initializeAnimalGallery();
        initializeLetterGallery();
        getScene();


    }

    private void getScene() {
        if (arFragment != null) {
            arFragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
                arFragment.onUpdate(frameTime);
                onUpdate();
            });
        }
    }


    private void initializeAnimalGallery() {
        LinearLayout gallery = findViewById(R.id.gallery_layout_left);

        ImageView dog = new ImageView(this);
        dog.setImageResource(R.drawable.dog);
        dog.setContentDescription("Dog");
        dog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ARHostFragment.this.addObject(Uri.parse("dog.sfb"));
            }
        });
        gallery.addView(dog);

        ImageView cat = new ImageView(this);
        cat.setImageResource(R.drawable.cat);
        cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ARHostFragment.this.addObject(Uri.parse("cat.sfb"));
            }
        });
        gallery.addView(cat);

        ImageView cow = new ImageView(this);
        cow.setImageResource(R.drawable.cow);
        cow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ARHostFragment.this.addObject(Uri.parse("cow.sfb"));
            }
        });
        gallery.addView(cow);
    }

    private void initializeLetterGallery() {
        LinearLayout gallery = findViewById(R.id.gallery_layout_right);

        ImageView letterD = new ImageView(this);
        letterD.setImageResource(R.drawable.d);
        letterD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ARHostFragment.this.addObject(Uri.parse("DD.sfb"));
            }
        });
        gallery.addView(letterD);

        ImageView letterO = new ImageView(this);
        letterO.setImageResource(R.drawable.o);
        letterO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ARHostFragment.this.addObject(Uri.parse("OO.sfb"));

            }
        });
        gallery.addView(letterO);

        ImageView letterG = new ImageView(this);
        letterG.setImageResource(R.drawable.g);
        letterG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ARHostFragment.this.addObject(Uri.parse("GG.sfb"));
            }
        });
        gallery.addView(letterG);


    }

    public void addNodeToScene(Anchor anchor, ModelRenderable renderable) {

        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
        node.setRenderable(renderable);
        node.setParent(anchorNode);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        node.select();
        node.setOnTapListener(new Node.OnTapListener() {
            @Override
            public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {
                anchorNode.getAnchor().detach();
            }
        });
    }

    public void addLetterNodeToScene(Anchor anchor, ModelRenderable renderable) {

        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
        node.getScaleController().setMaxScale(0.009f);
        node.getScaleController().setMinScale(0.008f);
        node.setRenderable(renderable);
        node.setRenderable(renderable);
        node.setParent(anchorNode);

        arFragment.getArSceneView().getScene().addChild(anchorNode);
        node.select();
        node.setOnTapListener(new Node.OnTapListener() {
            @Override
            public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {
                anchorNode.getAnchor().detach();
            }
        });
    }


    private void addObject(Uri model) {
        Frame frame = arFragment.getArSceneView().getArFrame();
        Point pt = getScreenCenter();
        List<HitResult> hits;


        if (frame != null) {
            hits = frame.hitTest(pt.x, pt.y);
            for (HitResult hit : hits) {
                Trackable trackable = hit.getTrackable();
                if (trackable instanceof Plane && ((Plane) trackable).isPoseInPolygon(hit.getHitPose())) {
                    modelLoader.loadModel(hit.createAnchor(), model);

                    break;
                }
            }
        }
    }

    private void onUpdate() {
        boolean trackingChanged = updateTracking();
        View contentView = findViewById(android.R.id.content);
        if (trackingChanged) {
            if (isTracking) {
                contentView.getOverlay().add(pointer);
            } else {
                contentView.getOverlay().remove(pointer);
            }
            contentView.invalidate();
        }

        if (isTracking) {
            boolean hitTestChanged = updateHitTest();
            if (hitTestChanged) {
                pointer.setEnabled(isHitting);
                contentView.invalidate();
            }
        }
    }

    private boolean updateTracking() {
        Frame frame = arFragment.getArSceneView().getArFrame();
        boolean wasTracking = isTracking;
        isTracking = frame != null && frame.getCamera().getTrackingState() == TrackingState.TRACKING;

        return isTracking != wasTracking;
    }

    private boolean updateHitTest() {
        Frame frame = arFragment.getArSceneView().getArFrame();
        android.graphics.Point pt = getScreenCenter();
        List<HitResult> hits;

        boolean wasHitting = isHitting;
        isHitting = false;

        if (frame != null) {
            hits = frame.hitTest(pt.x, pt.y);

            for (HitResult hit : hits) {
                Trackable trackable = hit.getTrackable();
                if (trackable instanceof Plane && ((Plane) trackable).isPoseInPolygon(hit.getHitPose())) {
                    isHitting = true;
                    break;
                }
            }
        }

        return wasHitting != isHitting;
    }

    private Point getScreenCenter() {
        View vw = findViewById(android.R.id.content);
        return new android.graphics.Point(vw.getWidth() / 2, vw.getHeight() / 2);
    }

    public void onException(Throwable throwable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(throwable.getMessage())
                .setTitle("Codelab error!");

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
