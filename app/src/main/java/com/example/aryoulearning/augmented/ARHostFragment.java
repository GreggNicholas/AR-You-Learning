package com.example.aryoulearning.augmented;

import android.Manifest;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.example.aryoulearning.R;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ARHostFragment extends AppCompatActivity {
    private static final int RC_PERMISSIONS = 0x123;

    private GestureDetector gestureDetector;
    private ArFragment arFragment;

    private boolean hasFinishedLoadingModels = false;
    private boolean hasFinishedLoadingLetters = false;
    private boolean hasPlacedGame = false;

    private List<Model> categoryList = new ArrayList<>();

    private List<HashMap<String, CompletableFuture<ModelRenderable>>> futureModelMapList = new ArrayList<>();
    private HashMap<String, CompletableFuture<ModelRenderable>> futureLetterMap = new HashMap<>();

    private List<HashMap<String, ModelRenderable>> modelMapList = new ArrayList<>();
    private HashMap<String, ModelRenderable> letterMap = new HashMap<>();
    private String letters = "";
    private String word = "";

    Random r = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arfragment_host);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

//        categoryList = getIntent().getParcelableArrayListExtra(MainActivity.ARLIST);
        categoryList.add(new Model("dog", ""));

        setListMapsOfFutureModels(categoryList);
        setMapOfFutureLetters(futureModelMapList);

        setModelRenderables(futureModelMapList);
        setLetterRenderables(futureLetterMap);

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
    }


    private Node createGame(Map<String, ModelRenderable> modelMap) {


        Node base = new Node();

        Node sunVisual = new Node();
        sunVisual.setParent(base);


        for (Map.Entry<String, ModelRenderable> e : modelMap.entrySet()) {
            sunVisual.setRenderable(e.getValue());
            sunVisual.setLookDirection(new Vector3(0, 0, 4));
            sunVisual.setLocalScale(new Vector3(1.0f, 1.0f, 1.0f));


            for (int i = 0; i < e.getKey().length(); i++) {
                createLetter(Character.toString(e.getKey().charAt(i)), e.getKey(), base, letterMap.get(Character.toString(e.getKey().charAt(i))));
            }
        }

        return base;
    }

    private TransformableNode createLetter(String letter, String word,
            Node parent,
            ModelRenderable renderable) {


        Session session = arFragment.getArSceneView().getSession();
        float[] pos = {parent.getLocalPosition().x,
                parent.getLocalPosition().y,
                parent.getLocalPosition().z};

        float[] rotation = {0, 0, 0, 0};


        Anchor anchor = null;
        if (session != null) {

            anchor = session.createAnchor(new Pose(pos, rotation));

        }

        AnchorNode base = new AnchorNode(anchor);
        arFragment.getArSceneView().getScene().addChild(base);
        base.setParent(parent);
        TransformableNode trNode = new TransformableNode(arFragment.getTransformationSystem());
        // Create the planet and position it relative to the sun.
        trNode.setParent(base);

        trNode.setOnTapListener(new Node.OnTapListener() {
            @Override

            public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {
                base.getAnchor().detach();
                letters = letters + letter;
                checkLetters(letters, word);
            }
        });


        trNode.setRenderable(renderable);
//        trNode.setLocalScale(new Vector3(.1f,.1f,.1f));

        trNode.setLocalPosition(new Vector3(getRandom(2.5f, -1.5f), getRandom(.5f, -.5f), getRandom(-3, -5)));
        return trNode;
    }

    private void checkLetters(String letters, String word) {
        if(letters.equals(word)){
            Log.d("TAG", letters + " is equal to " + word);
        }else{
            Log.d("TAG", letters + "is not equal to" + word);
        }

    }

    public void addNodeToScene(Anchor anchor, ModelRenderable renderable) {

        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
        node.setRenderable(renderable);
        node.setParent(anchorNode);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        node.select();
        node.setOnTapListener((hitTestResult, motionEvent) -> anchorNode.getAnchor().detach());
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
        node.setOnTapListener((hitTestResult, motionEvent) -> anchorNode.getAnchor().detach());
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
                activity, new String[]{Manifest.permission.CAMERA}, requestCode);
    }

    private void onSingleTap(MotionEvent tap) {
        if (!hasFinishedLoadingModels || !hasFinishedLoadingLetters) {
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
                    Node gameSystem = createGame(modelMapList.get(0));
                    anchorNode.addChild(gameSystem);
                    return true;
                }
            }
        }

        return false;
    }

    private void setListMapsOfFutureModels(List<Model> modelList) {

        for (int i = 0; i < modelList.size(); i++) {
            HashMap<String, CompletableFuture<ModelRenderable>> futureMap = new HashMap();
            futureMap.put(modelList.get(i).getName(),
                    ModelRenderable.builder().
                            setSource(this, Uri.parse(categoryList.get(i).getName() + ".sfb")).build());
            futureModelMapList.add(futureMap);
        }
    }

    private void setMapOfFutureLetters(List<HashMap<String, CompletableFuture<ModelRenderable>>> futureMapList) {
        for (int i = 0; i < futureMapList.size(); i++) {
            String modelName = futureMapList.get(i).keySet().toString();
            for (int j = 0; j < modelName.length(); j++) {
                futureLetterMap.put(Character.toString(modelName.charAt(j)), ModelRenderable.builder().
                        setSource(this, Uri.parse(modelName.charAt(j) + ".sfb")).build());
            }
        }
    }

    private void setModelRenderables(List<HashMap<String, CompletableFuture<ModelRenderable>>> futureModelMapList) {

        for (int i = 0; i < futureModelMapList.size(); i++) {

            for (Map.Entry<String, CompletableFuture<ModelRenderable>> e : futureModelMapList.get(i).entrySet()) {

                HashMap<String, ModelRenderable> modelMap = new HashMap<>();

                CompletableFuture.allOf(e.getValue())
                        .handle(
                                (notUsed, throwable) -> {
                                    // When you build a Renderable, Sceneform loads its resources in the background while
                                    // returning a CompletableFuture. Call handle(), thenAccept(), or check isDone()
                                    // before calling get().
                                    if (throwable != null) {
                                        return null;
                                    }
                                    try {
                                        modelMap.put(e.getKey(), e.getValue().get());
                                    } catch (InterruptedException | ExecutionException ex) {
                                    }
                                    return null;
                                });
                modelMapList.add(modelMap);
            }
        }
        hasFinishedLoadingModels = true;
    }

    private void setLetterRenderables(HashMap<String, CompletableFuture<ModelRenderable>> futureLetterMap) {

        for (Map.Entry<String, CompletableFuture<ModelRenderable>> e : futureLetterMap.entrySet()) {

            CompletableFuture.allOf(e.getValue())
                    .handle(
                            (notUsed, throwable) -> {
                                // When you build a Renderable, Sceneform loads its resources in the background while
                                // returning a CompletableFuture. Call handle(), thenAccept(), or check isDone()
                                // before calling get().
                                if (throwable != null) {
                                    return null;
                                }
                                try {
                                    letterMap.put(e.getKey(), e.getValue().get());
                                } catch (InterruptedException | ExecutionException ex) {
                                }
                                return null;
                            });
        }
        hasFinishedLoadingLetters = true;
    }


    private float getRandom(float max, float min) {
        return r.nextFloat() * (max - min) + min;
    }


}
