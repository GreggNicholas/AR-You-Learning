package com.example.aryoulearning.augmented;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.aryoulearning.R;
import com.example.aryoulearning.audio.PronunciationUtil;
import com.example.aryoulearning.controller.NavListener;
import com.example.aryoulearning.model.Model;
import com.example.aryoulearning.view.fragment.ResultsFragment;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ARHostFragment extends Fragment {

    private static final int RC_PERMISSIONS = 0x123;
    public static final String MODEL_LIST_KEY = "model-list-key";
    private NavListener listener;

    private GestureDetector gestureDetector;
    private ArFragment arFragment;

    private boolean hasFinishedLoadingModels = false;
    private boolean hasFinishedLoadingLetters = false;
    private boolean hasPlacedGame = false;
    private Set<String> rightAnswer = new HashSet<>();
    private Set<String> wrongAnswer = new HashSet<>();
    private Set<String> correctAnswerSet = new HashSet<>();
    private int answersCorrect;
    private SharedPreferences prefs;
    private boolean isRepeat = false;

    private List<Model> categoryList = new ArrayList<>();

    private List<HashMap<String, CompletableFuture<ModelRenderable>>> futureModelMapList = new ArrayList<>();
    private HashMap<String, CompletableFuture<ModelRenderable>> futureLetterMap = new HashMap<>();

    private List<HashMap<String, ModelRenderable>> modelMapList = new ArrayList<>();
    private HashMap<String, ModelRenderable> letterMap = new HashMap<>();
    private String letters = "";
    private String currentWord = "";

    private int roundCounter = 0;
    private int roundLimit = 5;

    private LinearLayout wordContainer;

    private Set<Vector3> collisionSet = new HashSet<>();

    Random r = new Random();

    private TextToSpeech textToSpeech;
    private PronunciationUtil pronunciationUtil;

    Anchor mainAnchor;
    AnchorNode mainAnchorNode;
    Frame mainFrame;
    List<HitResult> mainHits;

    public static ARHostFragment newInstance(List<Model> modelList) {
        ARHostFragment fragment = new ARHostFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(MODEL_LIST_KEY, (ArrayList<? extends Parcelable>) modelList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NavListener) {
            listener = (NavListener) context;
        }

        pronunciationUtil = new PronunciationUtil();
        textToSpeech = pronunciationUtil.getTTS(getActivity());
//        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_arfragment_host, container, false);
        arFragment = (ArFragment) getChildFragmentManager().findFragmentById(R.id.ux_fragment);
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            categoryList = getArguments().getParcelableArrayList(MODEL_LIST_KEY);
        }
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        wordContainer = view.findViewById(R.id.word_container);

        setListMapsOfFutureModels(categoryList);
        setMapOfFutureLetters(futureModelMapList);

        setModelRenderables(futureModelMapList);
        setLetterRenderables(futureLetterMap);

        gestureDetector =
                new GestureDetector(
                        getActivity(),
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
//         Lastly request CAMERA permission which is required by ARCore.
        requestCameraPermission(getActivity(), RC_PERMISSIONS);
    }


    private Node createGame(Map<String, ModelRenderable> modelMap) {

        Node base = new Node();

        Node sunVisual = new Node();
        sunVisual.setParent(base);

        for (Map.Entry<String, ModelRenderable> e : modelMap.entrySet()) {
            sunVisual.setRenderable(e.getValue());
            sunVisual.setLookDirection(new Vector3(0, 0, 4));
            sunVisual.setLocalScale(new Vector3(1.0f, 1.0f, 1.0f));

            String randomWord = e.getKey() + "abcdefghijklmnopqrstuvwxyz";

            for (int i = 0; i < randomWord.length(); i++) {
                createLetter(Character.toString(randomWord.charAt(i)), e.getKey(), base, letterMap.get(Character.toString(randomWord.charAt(i))));
            }
            currentWord = e.getKey();
        }
        return base;
    }

    private void createLetter(String letter, String word,
                                           Node parent,
                                           ModelRenderable renderable) {

        Session session = arFragment.getArSceneView().getSession();
        float[] pos = {0,//x
                0,//y
                0};//z
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

        trNode.setRenderable(renderable);
//        trNode.setLocalScale(new Vector3(.1f,.1f,.1f));
        Vector3 coordinates = getRandomCoordinates();

        while (checkDoesLetterCollide(coordinates)) {
            coordinates = getRandomCoordinates();
        }

        trNode.setLocalPosition(coordinates);

        trNode.setOnTapListener(new Node.OnTapListener() {
            @Override
            public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {
                final MediaPlayer playBallonPop = MediaPlayer.create(getContext(), R.raw.balloon_pop);
                playBallonPop.start();
                playBallonPop.setOnCompletionListener(mp -> {
                    playBallonPop.stop();
                    playBallonPop.reset();
                    playBallonPop.release();
                });
                //Make the letter disappear
                base.getAnchor().detach();

                //Keep track of the letter selected
                letters += letter;

                //Add letter to container to show to the user.
                addLetterToWordContainer(letter);

                //Pronunciation of the word.
                textToSpeech.setSpeechRate(0.6f);
                pronunciationUtil.textToSpeechAnnouncer(letter, textToSpeech);

//                CountDownTimer timer = new CountDownTimer(500, 1) {
//                    @Override
//                    public void onTick(long millisUntilFinished) {
//                    }
//
//                    @Override
//                    public void onFinish() {
//                        pronunciationUtil.textToSpeechAnnouncer(letter, textToSpeech);
//                    }
//                }.start();

                //Compare concatenated letters to actual word
                if (letters.length() == word.length()) {
                    correctAnswerSet.add(word);

                    if(letters.equals(word)){
                        pronunciationUtil.textToSpeechAnnouncer(word, textToSpeech);
                        rightAnswer.add(letters);
                        roundCounter++;
                        isRepeat = false;
                    }else{
                        pronunciationUtil.textToSpeechAnnouncer("Wrong. Please Try Again", textToSpeech);
                        wrongAnswer.add(letters);
                        correctAnswerSet.add(word);
                        isRepeat = true;
                    }
                    letters = "";

                    if(roundCounter < roundLimit && roundCounter < modelMapList.size() && !isRepeat){
                        createNextGame(modelMapList.get(roundCounter));
                    }
                    else if(isRepeat){
                        createNextGame(modelMapList.get(roundCounter));
                    }else{
                        moveToResultsFragment();
                    }
                }

            }

        });

        Log.d("TAG", "" + roundCounter);
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
            mainFrame = frame;
            mainHits= frame.hitTest(tap);

            for (HitResult hit : frame.hitTest(tap)) {
                Trackable trackable = hit.getTrackable();
                if (trackable instanceof Plane && ((Plane) trackable).isPoseInPolygon(hit.getHitPose())) {
                    // Create the Anchor.
                        mainAnchor = hit.createAnchor();
                        mainAnchorNode = new AnchorNode(mainAnchor);
                        mainAnchorNode.setParent(arFragment.getArSceneView().getScene());
                        Node gameSystem = createGame(modelMapList.get(0));
                        mainAnchorNode.addChild(gameSystem);
                        return true;
                }
            }
        }

        return false;
    }

    private void createNextGame(Map<String,ModelRenderable> modelMap) {
        mainAnchorNode.getAnchor().detach();
        mainAnchor = null;
        mainAnchorNode = null;

        for (HitResult hit : mainHits) {
            Trackable trackable = hit.getTrackable();
            if (trackable instanceof Plane && ((Plane) trackable).isPoseInPolygon(hit.getHitPose())) {
                // Create the Anchor.
                mainAnchor = hit.createAnchor();
                mainAnchorNode = new AnchorNode(mainAnchor);
                mainAnchorNode.setParent(arFragment.getArSceneView().getScene());
                Node gameSystem = createGame(modelMap);
                mainAnchorNode.addChild(gameSystem);
            }
        }

        wordContainer.removeAllViews();

    }

    private void setListMapsOfFutureModels(List<Model> modelList) {

        for (int i = 0; i < modelList.size(); i++) {
            HashMap<String, CompletableFuture<ModelRenderable>> futureMap = new HashMap();
            futureMap.put(modelList.get(i).getName(),
                    ModelRenderable.builder().
                            setSource(getActivity(), Uri.parse(categoryList.get(i).getName() + ".sfb")).build());
            futureModelMapList.add(futureMap);
        }
    }

    private void setMapOfFutureLetters(List<HashMap<String, CompletableFuture<ModelRenderable>>> futureMapList) {
        for (int i = 0; i < futureMapList.size(); i++) {
            String modelName = futureMapList.get(i).keySet().toString();
            for (int j = 0; j < modelName.length(); j++) {
                futureLetterMap.put(Character.toString(modelName.charAt(j)), ModelRenderable.builder().
                        setSource(getActivity(), Uri.parse(modelName.charAt(j) + ".sfb")).build());
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

    private int getRandom(int max, int min) {
        return r.nextInt((max - min)) + min;
    }

    private boolean checkDoesLetterCollide(Vector3 newV3) {
        if (collisionSet.isEmpty()) {
            collisionSet.add(newV3);
            return false;
        }
        for (Vector3 v : collisionSet) {
            if ((newV3.x < v.x + 2 && newV3.x > v.x - 2)
                    && (newV3.y < v.y + 3 && newV3.y > v.y - 3)) {
                return true;
            } else {
                collisionSet.add(newV3);
                return false;
            }
        }
        return false;
    }

    private Vector3 getRandomCoordinates() {
        return new Vector3(getRandom(5, -5),//x
                getRandom(1, -4),//y
                getRandom(-7, -10));//z
    }

    private void addLetterToWordContainer(String letter) {
        Typeface ballonTF = ResourcesCompat.getFont(getActivity(), R.font.balloon);
        TextView t = new TextView(getActivity());
        t.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        t.setTypeface(ballonTF);
        t.setTextColor(getResources().getColor(R.color.colorWhite));
        t.setTextSize(80);
        t.setText(letter);
        t.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        wordContainer.addView(t);
    }

    public void moveToResultsFragment() {
        prefs.edit().putStringSet(ResultsFragment.RIGHTANSWERS, rightAnswer).apply();
        prefs.edit().putStringSet(ResultsFragment.WRONGANSWER, wrongAnswer).apply();
        prefs.edit().putStringSet(ResultsFragment.CORRECT_ANSWER_FOR_USER, correctAnswerSet).apply();
        prefs.edit().putInt(ResultsFragment.TOTALSIZE, categoryList.size()).apply();
        listener.moveToResultsFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        textToSpeech.shutdown();
        pronunciationUtil = null;
    }
}
