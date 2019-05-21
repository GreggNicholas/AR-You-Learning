package com.example.aryoulearning.augmented;

import android.Manifest;
import android.app.Activity;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.example.aryoulearning.R;
import com.example.aryoulearning.augmented.model.ModelLoader;
import com.example.aryoulearning.augmented.pointer.PointerDrawable;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ARHostFragment extends AppCompatActivity {
    private static final int RC_PERMISSIONS = 0x123;
//    private boolean installRequested;
    private GestureDetector gestureDetector;
    private ArFragment arFragment;
    private PointerDrawable pointer = new PointerDrawable();
    private boolean isTracking;
    private boolean isHitting;
    private ModelLoader modelLoader;
    private ModelRenderable dRenderable;
    private ModelRenderable oRenderable;
    private ModelRenderable gRenderable;
    private ModelRenderable dogRenderable;
    private boolean hasFinishedLoading = false;
    private boolean hasPlacedGame = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arfragment_host);
        modelLoader = new ModelLoader(new WeakReference<>(this));
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
//        initializeAnimalGallery();
//        initializeLetterGallery();

        CompletableFuture<ModelRenderable> dogStage =
                ModelRenderable.builder().setSource(this, Uri.parse("dog.sfb")).build();
        CompletableFuture<ModelRenderable> oStage =
                ModelRenderable.builder().setSource(this, Uri.parse("OO.sfb")).build();
        CompletableFuture<ModelRenderable> gStage =
                ModelRenderable.builder().setSource(this, Uri.parse("DD.sfb")).build();
        CompletableFuture<ModelRenderable> dStage =
                ModelRenderable.builder().setSource(this, Uri.parse("DD.sfb")).build();

        CompletableFuture.allOf(
                oStage,
                dogStage,
                gStage,
                dStage)
                .handle(
                        (notUsed, throwable) -> {
                            // When you build a Renderable, Sceneform loads its resources in the background while
                            // returning a CompletableFuture. Call handle(), thenAccept(), or check isDone()
                            // before calling get().

                            if (throwable != null) {
                                return null;
                            }

                            try {
                                dRenderable = dStage.get();
                                gRenderable = gStage.get();
                                dogRenderable = dogStage.get();
                                oRenderable = oStage.get();

                                // Everything finished loading successfully.
                                hasFinishedLoading = true;

                            } catch (InterruptedException | ExecutionException ex) {
                            }

                            return null;
                        });

        gestureDetector =
                new GestureDetector(
                        this,
                        new GestureDetector.SimpleOnGestureListener() {
                            @Override
                            public boolean onSingleTapUp(MotionEvent e) {
                                onSingleTap(e);
                                return true;
                            }

                            @Override
                            public boolean onDown(MotionEvent e) {
                                return true;
                            }
                        });

        arFragment.getArSceneView()
                .getScene()
                .setOnTouchListener(
                        (HitTestResult hitTestResult, MotionEvent event) -> {
                            // If the solar system hasn't been placed yet, detect a tap and then check to see if
                            // the tap occurred on an ARCore plane to place the solar system.
                            if (!hasPlacedGame) {
                                return gestureDetector.onTouchEvent(event);
                            }

                            // Otherwise return false so that the touch event can propagate to the scene.
                            return false;
                        });

        arFragment.getArSceneView()
                .getScene()
                .addOnUpdateListener(
                        frameTime -> {
                            Frame frame = arFragment.getArSceneView().getArFrame();
                            if (frame == null) {
                                return;
                            }

                            if (frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
                                return;
                            }

                        });

        // Lastly request CAMERA permission which is required by ARCore.
        requestCameraPermission(this, RC_PERMISSIONS);

//        getScene();



    }

    private void getScene() {
        if (arFragment != null) {
            arFragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
                arFragment.onUpdate(frameTime);
                onUpdate();
            });
        }
    }

//    private void initializeModels(){
//
//        ARHostFragment.this.addObject(Uri.parse("dog.sfb"));
//        ARHostFragment.this.addObject(Uri.parse("cat.sfb"));
//        ARHostFragment.this.addObject(Uri.parse("cow.sfb"));
//
//    }

    private Node createGame(){

        Node base = new Node();

        Node center = new Node();
        center.setParent(base);
        center.setLocalPosition(new Vector3(0.0f, 0.5f, 0.0f));

        Node sunVisual = new Node();
        sunVisual.setParent(center);
        sunVisual.setRenderable(dogRenderable);
        sunVisual.setLocalScale(new Vector3(1.0f, 1.0f, 1.0f));

        createLetter("D", center, 1.2f, dRenderable);

        createLetter("O", center, 3.5f, oRenderable);

        createLetter("G", center, -1.0f, gRenderable);

        return base;
    }

    private Node createLetter(
            String letter,
            Node parent,
            float auFromParent,
            ModelRenderable renderable) {

        Session session = arFragment.getArSceneView().getSession();
        float[] pos = {0.0f, 0.0f, 0.0f};
        float[] rotation = {0,0,0,0};
        Anchor anchor =  session.createAnchor(new Pose(pos, rotation));

        AnchorNode base = new AnchorNode(anchor);
        arFragment.getArSceneView().getScene().addChild(base);
        base.setParent(parent);
        TransformableNode trNode = new TransformableNode(arFragment.getTransformationSystem());
        // Create the planet and position it relative to the sun.
        trNode.setParent(base);

        Node tNode = new Node();


//        trNode.select();
        tNode.setOnTapListener(new Node.OnTapListener() {
            @Override
            public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {
                base.getAnchor().detach();
            }
        });


        tNode.setRenderable(renderable);
        tNode.setLocalScale(new Vector3(.1f,.1f,.1f));
        tNode.setLocalPosition(new Vector3(auFromParent * .5f, 0.0f, 0.0f));
        tNode.setParent(trNode);
        return tNode;
    }

//
//    private void initializeAnimalGallery() {
//        LinearLayout gallery = findViewById(R.id.gallery_layout_left);
//
//        ImageView dog = new ImageView(this);
//        dog.setImageResource(R.drawable.dog);
//        dog.setContentDescription("Dog");
//        dog.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ARHostFragment.this.addObject(Uri.parse("dog.sfb"));
//            }
//        });
//        gallery.addView(dog);
//
//        ImageView cat = new ImageView(this);
//        cat.setImageResource(R.drawable.cat);
//        cat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ARHostFragment.this.addObject(Uri.parse("cat.sfb"));
//            }
//        });
//        gallery.addView(cat);
//
//        ImageView cow = new ImageView(this);
//        cow.setImageResource(R.drawable.cow);
//        cow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ARHostFragment.this.addObject(Uri.parse("cow.sfb"));
//            }
//        });
//        gallery.addView(cow);
//    }
//
//    private void initializeLetterGallery() {
//        LinearLayout gallery = findViewById(R.id.gallery_layout_right);
//
//        ImageView letterD = new ImageView(this);
//        letterD.setImageResource(R.drawable.d);
//        letterD.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ARHostFragment.this.addObject(Uri.parse("DD.sfb"));
//            }
//        });
//        gallery.addView(letterD);
//
//        ImageView letterO = new ImageView(this);
//        letterO.setImageResource(R.drawable.o);
//        letterO.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ARHostFragment.this.addObject(Uri.parse("OO.sfb"));
//
//            }
//        });
//        gallery.addView(letterO);
//
//        ImageView letterG = new ImageView(this);
//        letterG.setImageResource(R.drawable.g);
//        letterG.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ARHostFragment.this.addObject(Uri.parse("GG.sfb"));
//            }
//        });
//        gallery.addView(letterG);
//
//
//    }

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


//    private void addObject(Uri model) {
//        Frame frame = arFragment.getArSceneView().getArFrame();
//        Point pt = getScreenCenter();
//        List<HitResult> hits;
//
//
//        if (frame != null) {
//            hits = frame.hitTest(pt.x, pt.y);
//            for (HitResult hit : hits) {
//                Trackable trackable = hit.getTrackable();
//                if (trackable instanceof Plane && ((Plane) trackable).isPoseInPolygon(hit.getHitPose())) {
//                    modelLoader.loadModel(hit.createAnchor(), model);
//
//                    break;
//                }
//            }
//        }
//    }

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

    public static void requestCameraPermission(Activity activity, int requestCode) {
        ActivityCompat.requestPermissions(
                activity, new String[] {Manifest.permission.CAMERA}, requestCode);
    }

    private void onSingleTap(MotionEvent tap) {
        if (!hasFinishedLoading) {
            // We can't do anything yet.
            return;
        }

        Frame frame = arFragment.getArSceneView().getArFrame();
        if (frame != null) {
            if (!hasPlacedGame && tryPlaceGame(tap, frame)) {
                hasPlacedGame = true;
            }
        }
    }

    private boolean tryPlaceGame(MotionEvent tap, Frame frame) {
        if (tap != null && frame.getCamera().getTrackingState() == TrackingState.TRACKING) {
            for (HitResult hit : frame.hitTest(tap)) {
                Trackable trackable = hit.getTrackable();
                if (trackable instanceof Plane && ((Plane) trackable).isPoseInPolygon(hit.getHitPose())) {
                    // Create the Anchor.
                    Anchor anchor = hit.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());
                    Node gameSystem = createGame();
                    anchorNode.addChild(gameSystem);
                    return true;
                }
            }
        }

        return false;
    }
}
