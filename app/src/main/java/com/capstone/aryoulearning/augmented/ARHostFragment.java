package com.capstone.aryoulearning.augmented;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.capstone.aryoulearning.R;
import com.capstone.aryoulearning.animation.Animations;
import com.capstone.aryoulearning.audio.PronunciationUtil;
import com.capstone.aryoulearning.controller.NavListener;
import com.capstone.aryoulearning.model.Model;
import com.capstone.aryoulearning.view.MainActivity;
import com.capstone.aryoulearning.view.fragment.ResultsFragment;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
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
    public static final String MODEL_LIST = "MODEL_LIST";
    private NavListener listener;

    private GestureDetector gestureDetector;
    private ArFragment arFragment;

    FrameLayout f;

    private boolean hasFinishedLoadingModels = false;
    private boolean hasFinishedLoadingLetters = false;
    private boolean hasPlacedGame = false;
    private Set<String> correctAnswerSet = new HashSet<>();
    private SharedPreferences prefs;

    private List<Model> categoryList = new ArrayList<>();

    private List<HashMap<String, CompletableFuture<ModelRenderable>>> futureModelMapList = new ArrayList<>();
    private HashMap<String, CompletableFuture<ModelRenderable>> futureLetterMap = new HashMap<>();

    private List<HashMap<String, ModelRenderable>> modelMapList = new ArrayList<>();
    private HashMap<String, ModelRenderable> letterMap = new HashMap<>();
    private String letters = "";
    private String currentWord = "";

    private int roundCounter = 0;
    private int roundLimit = 5;

    private CardView wordCardView;
    private LinearLayout wordContainer;

    private TextView wordValidator;
    private View wordValidatorLayout;
    private ImageView validatorImage;
    private ImageView validatorBackgroudImage;
    private TextView validatorWord;
    private TextView validatorWrongWord;
    private TextView validatorWrongPrompt;
    private Button validatorOkButton;

    private Set<Vector3> collisionSet = new HashSet<>();

    private Random r = new Random();

    private List<String> wrongAnswerList = new ArrayList<>();
    private TextToSpeech textToSpeech;
    private PronunciationUtil pronunciationUtil;

    private Anchor mainAnchor;
    private AnchorNode mainAnchorNode;
    private HitResult mainHit;

    private ObjectAnimator fadeIn;
    private ObjectAnimator fadeOut;

    private Node base;
    private ImageButton undo;

    private View exitMenu;
    private ImageButton exit;
    private Button exitYes;
    private Button exitNo;

    private LottieAnimationView tapAnimation;

    private MediaPlayer playBalloonPop;
    private boolean placedAnimation;

    public static ARHostFragment newInstance(List<Model> modelList) {
        ARHostFragment fragment = new ARHostFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(MODEL_LIST, (ArrayList<? extends Parcelable>) modelList);
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
        if (getArguments() != null) {
            categoryList = getArguments().getParcelableArrayList(MODEL_LIST);
            Collections.shuffle(categoryList);
        }

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        playBalloonPop = MediaPlayer.create(getContext(), R.raw.pop_effect);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        f = view.findViewById(R.id.frame_layout);

        wordCardView = view.findViewById(R.id.card_wordContainer);
        wordContainer = view.findViewById(R.id.word_container);

        wordValidatorLayout = getLayoutInflater().inflate(R.layout.validator_card,f,false);
        CardView wordValidatorCv = wordValidatorLayout.findViewById(R.id.word_validator_cv);
        wordValidator = wordValidatorLayout.findViewById(R.id.word_validator);

        validatorImage = wordValidatorLayout.findViewById(R.id.validator_imageView);
        validatorBackgroudImage = wordValidatorLayout.findViewById(R.id.correct_star_imageView);
        validatorWord = wordValidatorLayout.findViewById(R.id.validator_word);
        validatorWrongPrompt = wordValidatorLayout.findViewById(R.id.validator_incorrect_prompt);
        validatorWrongWord = wordValidatorLayout.findViewById(R.id.validator_wrong_word);
        validatorOkButton = wordValidatorLayout.findViewById(R.id.button_validator_ok);

//        wordValidatorCv.setVisibility(View.INVISIBLE);

        exitMenu = getLayoutInflater().inflate(R.layout.exit_menu_card,f,false);
        exit = view.findViewById(R.id.exit_imageButton);
        exitYes = exitMenu.findViewById(R.id.exit_button_yes);
        exitNo = exitMenu.findViewById(R.id.exit_button_no);

        exit.setOnClickListener(v -> f.addView(exitMenu));
        exitYes.setOnClickListener(v -> listener.moveToListFragment(MainActivity.getAnimalModelList(),
                MainActivity.getCategoryList(),
                MainActivity.getBackgroundList()));
        exitNo.setOnClickListener(v -> f.removeView(exitMenu));

        undo = view.findViewById(R.id.button_undo);
        undo.setOnClickListener(v -> recreateErasedLetter(eraseLastLetter(letters)));

        fadeIn = Animations.Normal.setCardFadeInAnimator(wordValidatorCv);
        fadeIn.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                f.addView(wordValidatorLayout);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

//                wordValidatorCv.setVisibility(View.VISIBLE);

                validatorOkButton.setOnClickListener(v -> {
                    fadeOut.setStartDelay(500);
                    fadeOut.start();
                });
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        fadeOut = Animations.Normal.setCardFadeOutAnimator(wordValidatorCv);
        fadeOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                f.removeView(wordValidatorLayout);

                if (roundCounter < roundLimit && roundCounter < modelMapList.size()) {
                    Handler handler = new Handler();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            createNextGame(modelMapList.get(roundCounter));
                        }
                    });
//                    createNextGame(modelMapList.get(roundCounter));
                } else {
                    moveToReplayFragment();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

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
                            if(!hasPlacedGame){
                            for(Plane plane : frame.getUpdatedTrackables(Plane.class)){
                                if(!placedAnimation && plane.getTrackingState() == TrackingState.TRACKING){
                                    placedAnimation = true;
                                    tapAnimation = getTapAnimationView();
                                    addTapAnimationToScreen(tapAnimation);
                                }
                            }
                            }

                        });
//         Lastly request CAMERA permission which is required by ARCore.
        requestCameraPermission(getActivity(), RC_PERMISSIONS);
    }


    private Node createGame(Map<String, ModelRenderable> modelMap) {

        base = new Node();

        Node sunVisual = new Node();
        sunVisual.setParent(base);

        ObjectAnimator rotate = Animations.AR.createRotationAnimator();
        rotate.setTarget(sunVisual);
        rotate.setDuration(7000);
        rotate.start();

        for (Map.Entry<String, ModelRenderable> e : modelMap.entrySet()) {
            sunVisual.setRenderable(e.getValue());
            sunVisual.setLocalPosition(new Vector3(base.getLocalPosition().x,//x
                    base.getLocalPosition().y,//y
                    base.getLocalPosition().z));
            sunVisual.setLookDirection(new Vector3(0, 0, 4));
            sunVisual.setLocalScale(new Vector3(1.0f, 1.0f, 1.0f));

//            String randomWord = e.getKey() + "abcdefghijklmnopqrstuvwxyz";
            collisionSet.clear();
            pronunciationUtil.textToSpeechAnnouncer(e.getKey(), textToSpeech);

            for (int i = 0; i < e.getKey().length(); i++) {
                createLetter(Character.toString(e.getKey().charAt(i)), e.getKey(), base, letterMap.get(Character.toString(e.getKey().charAt(i))));
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

            try {
                session.resume();

            } catch (CameraNotAvailableException e) {
                e.printStackTrace();
            }
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

        while (checkDoesLetterCollide(coordinates, parent.getLocalPosition())) {
            coordinates = getRandomCoordinates();
        }
        trNode.setLocalPosition(coordinates);

        trNode.setOnTapListener(new Node.OnTapListener() {
            @Override
            public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {

//                final MediaPlayer playBallonPop = MediaPlayer.create(getContext(), R.raw.balloon_pop);
                playBalloonPop.start();
                playBalloonPop.setOnCompletionListener(mp -> {
                    playBalloonPop.pause();
                });

                //Make the letter disappear
                base.getAnchor().detach();

                Log.d("motioneventxy", motionEvent.getX() + " " + motionEvent.getY());

                LottieAnimationView lav;

                if(letter.equals(Character.toString(word.charAt(letters.length())))) {
                    lav =getSparklingAnimationView();
                }else{
                    lav = getWarningAnimationView();
                }

                addAnimationViewOnTopOfLetter(lav,
                        Math.round(motionEvent.getX() - 7),
                        Math.round(motionEvent.getY() + 7));

                //Keep track of the letter selected
                letters += letter;

                if(wordCardView.getVisibility() == View.INVISIBLE){
                    wordCardView.setVisibility(View.VISIBLE);
                }

                if(undo.getVisibility() == View.INVISIBLE){
                    undo.setVisibility(View.VISIBLE);
                }

                //Add letter to container to show to the user.
                addLetterToWordContainer(letter);

                //Pronunciation of the word.
                textToSpeech.setSpeechRate(0.6f);
                pronunciationUtil.textToSpeechAnnouncer(letter, textToSpeech);

                //Compare concatenated letters to actual word
                //method was extracted into a handler because i suspected the heavy workload was causing my many AR errors

                if (letters.length() == word.length()) {
                    Handler handler = new Handler();
                    handler.post(() -> compareAnswer(letters, word));
                }
            }
        });
    }

    private void setValidatorCardView(boolean isCorrect){


        validatorWord.setText(currentWord);
        validatorWrongWord.setVisibility(View.INVISIBLE);
        validatorWrongPrompt.setVisibility(View.INVISIBLE);

        if(isCorrect){
            validatorBackgroudImage.setImageResource(R.drawable.star);
            Picasso.get().load(categoryList.get(roundCounter - 1).getImage()).into(validatorImage);
        }else{
            validatorBackgroudImage.setImageResource(R.drawable.error);
            Picasso.get().load(categoryList.get(roundCounter).getImage()).into(validatorImage);
            validatorWrongWord.setVisibility(View.VISIBLE);
            validatorWrongPrompt.setVisibility(View.VISIBLE);
        }

    }

    private void compareAnswer(String letters, String word) {
        String validator = "";
        boolean isCorrect;
        if (letters.equals(word)) {
            isCorrect = true;
            validator = "correct";

            //will run once when correct answer is entered. the method will instantiate, and add all from the current list
            categoryList.get(roundCounter).setWrongAnswerSet((ArrayList<String>) wrongAnswerList);

            //we will increment once the list is added to correct index
            roundCounter++;

            //this will remove all, seemed safer than clear, which nulls the object.
            wrongAnswerList.removeAll(wrongAnswerList);

            pronunciationUtil.textToSpeechAnnouncer(validator, textToSpeech);
        } else {
            isCorrect = false;
            validator = "try again!";
            validatorWrongWord.setText(letters);
            correctAnswerSet.add(word);
            //every wrong answer, until a correct answer will be added here
            wrongAnswerList.add(letters);
            categoryList.get(roundCounter).setCorrect(false);
            pronunciationUtil.textToSpeechAnnouncer("incorrect, please try again", textToSpeech);
        }

        wordValidator.setText(validator);
        setValidatorCardView(isCorrect);
        fadeIn.start();

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
                f.removeView(tapAnimation);
            }
        }
    }

    private boolean tryPlaceGame(MotionEvent tap, Frame frame) {
        if (tap != null && frame.getCamera().getTrackingState() == TrackingState.TRACKING) {

                mainHit = frame.hitTest(tap).get(0);

                Trackable trackable = mainHit.getTrackable();
                if (trackable instanceof Plane && ((Plane) trackable).isPoseInPolygon(mainHit.getHitPose())) {
                    // Create the Anchor.
                    if (trackable.getTrackingState() == TrackingState.TRACKING) {
                        mainAnchor = mainHit.createAnchor();
                    }
                    mainAnchorNode = new AnchorNode(mainAnchor);
                    mainAnchorNode.setParent(arFragment.getArSceneView().getScene());
                    Node gameSystem = createGame(modelMapList.get(0));
                    mainAnchorNode.addChild(gameSystem);
                    return true;
                }
        }
        return false;
    }

    private void createNextGame(Map<String, ModelRenderable> modelMap) {
        letters = "";
        undo.setVisibility(View.INVISIBLE);
        mainAnchorNode.getAnchor().detach();
        mainAnchor = null;
        mainAnchorNode = null;


            Trackable trackable = mainHit.getTrackable();
            if (trackable instanceof Plane && ((Plane) trackable).isPoseInPolygon(mainHit.getHitPose())) {
                // Create the Anchor.
                if (trackable.getTrackingState() == TrackingState.TRACKING) {
                    mainAnchor = mainHit.createAnchor();
                }
                mainAnchorNode = new AnchorNode(mainAnchor);
                mainAnchorNode.setParent(arFragment.getArSceneView().getScene());
                Node gameSystem = createGame(modelMap);
                mainAnchorNode.addChild(gameSystem);
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

    private boolean checkDoesLetterCollide(Vector3 newV3, Vector3 parentModel) {
        if (collisionSet.isEmpty()) {
            collisionSet.add(newV3);
            return false;
        }

        if ((newV3.x < parentModel.x + 2) && (newV3.x > parentModel.x - 2)
                && (newV3.y < parentModel.y + 2) && (newV3.y > parentModel.y - 2)
                && (newV3.z < parentModel.z + 2) && (newV3.z > parentModel.z - 10)) {
            return true;
        }

        for (Vector3 v : collisionSet) {
            //if the coordinates are within a range of any exisiting coordinates
            if (((newV3.x < v.x + 2 && newV3.x > v.x - 2)
                    && (newV3.y < v.y + 3 && newV3.y > v.y - 3))) {
                return true;
            } else {
                collisionSet.add(newV3);
                return false;
            }
        }
        return true;
    }

    private Vector3 getRandomCoordinates() {
        return new Vector3(getRandom(5, -5),//x
                getRandom(1, -2),//y
                getRandom(-2, -10));//z
    }

    //instantiates a lottie view
    private LottieAnimationView getSparklingAnimationView() {
        LottieAnimationView lav = new LottieAnimationView(getContext());
        lav.setVisibility(View.VISIBLE);
        lav.loop(false);
        lav.setAnimation("explosionA.json");
        lav.setScale(1);
        lav.setSpeed(.8f);
        return lav;
    }

    private LottieAnimationView getWarningAnimationView() {
        LottieAnimationView lav = new LottieAnimationView(getContext());
        lav.setVisibility(View.VISIBLE);
        lav.loop(false);
        lav.setAnimation("error.json");
        lav.setScale(1);
        lav.setSpeed(.8f);
        return lav;
    }

    private LottieAnimationView getTapAnimationView(){
        LottieAnimationView lav = new LottieAnimationView(getContext());
        lav.setVisibility(View.VISIBLE);
        lav.loop(true);
        lav.setAnimation("tap.json");
        lav.setScale(1);
        lav.setSpeed(.8f);
        return lav;
    }

    //adds a lottie view to the corresposnding x and y coordinates
    private void addAnimationViewOnTopOfLetter(LottieAnimationView lav, int x, int y) {
        lav.setX(x);
        lav.setY(y);
        f.addView(lav, 300, 300);
        lav.playAnimation();
        lav.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                f.removeView(lav);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    public void addTapAnimationToScreen(LottieAnimationView lavTap){
        int width = getActivity().getWindow().getDecorView().getWidth();
        int height = getActivity().getWindow().getDecorView().getHeight();
        lavTap.setX(width/2 - 50);
        lavTap.setY(height/2 - 50);
        f.addView(lavTap, 500, 500);
        lavTap.playAnimation();
    }

    private void addLetterToWordContainer(String letter) {
        Typeface ballonTF = ResourcesCompat.getFont(getActivity(), R.font.balloon);
        TextView t = new TextView(getActivity());
        t.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        t.setTypeface(ballonTF);
        t.setTextColor(getResources().getColor(R.color.colorWhite));
        t.setTextSize(100);
        t.setText(letter);
        t.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        wordContainer.addView(t);
    }

    public void moveToReplayFragment() {
        prefs.edit().putStringSet(ResultsFragment.CORRECT_ANSWER_FOR_USER, correctAnswerSet).apply();
        prefs.edit().putInt(ResultsFragment.TOTALSIZE, roundLimit).apply();

        //Checks to see which words have an empty wrongAnswerSet and changes the boolean pertaining to that Model to true.
        for (int i = 0; i < roundLimit; i++) {
            if (categoryList.get(i).getWrongAnswerSet() == null || categoryList.get(i).getWrongAnswerSet().size() == 0) {
                categoryList.get(i).setCorrect(true);
            }
        }
        listener.moveToReplayFragment(categoryList, true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        textToSpeech.shutdown();
        pronunciationUtil = null;
        playBalloonPop.reset();
        playBalloonPop.release();
    }

    public String eraseLastLetter(String spelledOutWord) {
        if (spelledOutWord.length() < 1) {
            undo.setVisibility(View.INVISIBLE);
            return spelledOutWord;
        } else {
            letters = letters.substring(0, spelledOutWord.length() - 1);
            wordContainer.removeViewAt(spelledOutWord.length() - 1);
            return spelledOutWord.substring(spelledOutWord.length() - 1);
        }
    }

    public void recreateErasedLetter(String letterToRecreate) {
        if (!letterToRecreate.equals("")) {
            createLetter(letterToRecreate, currentWord, base, letterMap.get(letterToRecreate));
        }
    }
}