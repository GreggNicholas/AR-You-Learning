package com.example.aryoulearning.augmented;

import android.Manifest;
import android.app.Activity;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.example.aryoulearning.R;
import com.example.aryoulearning.augmented.model.ModelLoader;
import com.example.aryoulearning.augmented.pointer.PointerDrawable;
import com.example.aryoulearning.model.Model;
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
import java.util.ArrayList;
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
    private String[] dog = {"dog", "OO", "DD", "DD"};
    private List<Model> categoryList = new ArrayList<>();
    private List<List<CompletableFuture<ModelRenderable>>> modelRenderables = new ArrayList<>();
    private List<List<ModelRenderable>> modelRenderableListPart2 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arfragment_host);
        modelLoader = new ModelLoader(new WeakReference<>(this));
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
//        categoryList = getIntent().getParcelableArrayListExtra(MainActivity.ARLIST);
        categoryList.add(new Model("dog", ""));

//        initializeAnimalGallery();
//        initializeLetterGallery();

        for(int i = 0; i < categoryList.size(); i++){
            List<CompletableFuture<ModelRenderable>> modelRenderableList = new ArrayList<>();

            modelRenderableList.add(ModelRenderable.builder().setSource(this, Uri.parse(categoryList.get(i).getName() + ".sfb")).build());
            for(int k = 0; k < categoryList.get(i).getName().length(); k++){
                modelRenderableList.add(ModelRenderable.builder().setSource(this, Uri.parse(categoryList.get(i).getName().charAt(k) + ".sfb")).build());
            }
            modelRenderables.add(modelRenderableList);
        }

        //dog.sfb, d.sfb, o.sfb, g.sfb


//        CompletableFuture<ModelRenderable> dogStage =
//                ModelRenderable.builder().setSource(this, Uri.parse("dog.sfb")).build();
//        CompletableFuture<ModelRenderable> oStage =
//                ModelRenderable.builder().setSource(this, Uri.parse("o.sfb")).build();
//        CompletableFuture<ModelRenderable> gStage =
//                ModelRenderable.builder().setSource(this, Uri.parse("d.sfb")).build();
//        CompletableFuture<ModelRenderable> dStage =
//                ModelRenderable.builder().setSource(this, Uri.parse("d.sfb")).build();

        for(int i = 0; i < modelRenderables.size(); i++){
            List<ModelRenderable> newList = new ArrayList<>();
            for(int k = 0; k < modelRenderables.get(i).size(); k++){
                final int num1 = i;
                final int num2 = k;
                CompletableFuture.allOf(modelRenderables.get(num1).get(num2))
                        .handle(
                                (notUsed, throwable) -> {
                                    // When you build a Renderable, Sceneform loads its resources in the background while
                                    // returning a CompletableFuture. Call handle(), thenAccept(), or check isDone()
                                    // before calling get().

                                    if (throwable != null) {
                                        return null;
                                    }

                                    try {
                                        newList.add(modelRenderables.get(num1).get(num2).get());

                                        // Everything finished loading successfully.
                                        hasFinishedLoading = true;

                                    } catch (InterruptedException | ExecutionException ex) {
                                    }
                                    return null;
                                });
            }
            modelRenderableListPart2.add(newList);
        }




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

    private Node createGame(List<ModelRenderable> modelRenderableList){

        Node base = new Node();

//        Node center = new Node();
//        center.setParent(base);
//        center.setLocalPosition(new Vector3(0.0f, 0.5f, 0.0f));

        Node sunVisual = new Node();
        sunVisual.setParent(base);
        sunVisual.setRenderable(modelRenderableList.get(0));
        sunVisual.setLocalScale(new Vector3(1.0f, 1.0f, 1.0f));


        for(int i = 1; i < modelRenderableList.size();i++){
            createLetter(sunVisual, 3.5f, modelRenderableList.get(i));
        }



        return base;
    }

    private Node createLetter(
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


        trNode.select();
        trNode.setOnTapListener(new Node.OnTapListener() {
            @Override
            public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {
                base.getAnchor().detach();
            }
        });


        trNode.setRenderable(renderable);
//        trNode.setLocalScale(new Vector3(.1f,.1f,.1f));
        trNode.setLocalPosition(new Vector3(auFromParent * .5f, 0.0f, 0.0f));

        return trNode;
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
//                ARHostFragment.this.addObject(Uri.parse("d.sfb"));
//            }
//        });
//        gallery.addView(letterD);
//
//        ImageView letterO = new ImageView(this);
//        letterO.setImageResource(R.drawable.o);
//        letterO.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ARHostFragment.this.addObject(Uri.parse("o.sfb"));
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
        for(int i = 0; i < modelRenderableListPart2.get(0).size(); i++){
            Log.d("TAG", modelRenderableListPart2.get(0).get(i).toString());
        }

        if (tap != null && frame.getCamera().getTrackingState() == TrackingState.TRACKING) {
            for (HitResult hit : frame.hitTest(tap)) {
                Trackable trackable = hit.getTrackable();
                if (trackable instanceof Plane && ((Plane) trackable).isPoseInPolygon(hit.getHitPose())) {
                    // Create the Anchor.
                    Anchor anchor = hit.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());
                    Node gameSystem = createGame(modelRenderableListPart2.get(0));
                    anchorNode.addChild(gameSystem);
                    return true;
                }
            }
        }

        return false;
    }
}
