package com.capstone.aryoulearning.view.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.capstone.aryoulearning.R;
import com.capstone.aryoulearning.animation.Animations;
import com.capstone.aryoulearning.audio.PronunciationUtil;
import com.capstone.aryoulearning.controller.NavListener;
import com.capstone.aryoulearning.model.Model;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nonnull;

public class GameFragment extends Fragment {
    public static final String MODEL_LIST = "MODEL_LIST";
    private NavListener listener;
    private List<Model> modelList;
    private List<String> wrongAnswerList = new ArrayList<>();
    private ImageView imageView;
    private TextView checker;
    private String answer;
    private int counter;
    private int width;
    private int height;
    private Set<String> correctAnswerSet = new HashSet<>();
    private SharedPreferences sharedPreferences;
    private PronunciationUtil pronunciationUtil;
    private TextToSpeech textToSpeech;
    CardView cv;
    TextView cvTextView;
    ObjectAnimator fadeIn;
    ObjectAnimator fadeOut;

    public static GameFragment newInstance(final List<Model> modelList) {
        GameFragment fragment = new GameFragment();
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
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            modelList = getArguments().getParcelableArrayList(MODEL_LIST);
            Collections.shuffle(modelList);
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    public void initializeViews(@Nonnull final View view){
        checker = view.findViewById(R.id.checker);
        imageView = view.findViewById(R.id.imageView);
        cv = view.findViewById(R.id.static_card);
        cvTextView = view.findViewById(R.id.static_card_text);
    }

    public void animations(){
        fadeIn = Animations.Normal.setCardFadeInAnimator(cv);
        fadeIn.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                fadeOut.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        fadeOut = Animations.Normal.setCardFadeOutAnimator(cv);
        fadeOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                loadNext(counter);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textToSpeech = pronunciationUtil.getTTS(view.getContext());

        initializeViews(view);

        setMaxWidthAndHeight();
        answer = modelList.get(0).getName();
        Picasso.get().load(modelList.get(0).getImage()).into(imageView);

        animations();

        Handler handler = new Handler();
        handler.post(() -> setWordsOnScreen(answer));

    }

    public void setWordsOnScreen(String word) {
        List<HashMap<String, Integer>> mapList = new ArrayList<>();

        for (int i = 0; i < word.length(); i++) {
            HashMap<String,Integer> newCoordinates = getCoordinates();

            while (checkCollision(mapList, newCoordinates)) {
                newCoordinates = getCoordinates();
            }
            mapList.add(newCoordinates);
        }

        for (int i = 0; i < word.length(); i++) {
            drawLetters(Character.toString(word.charAt(i)), mapList.get(i));
        }
    }

    public void drawLetters(String l, HashMap<String, Integer> map) {
        final TextView letter = new TextView(getContext());
        letter.setTextSize(80);
        letter.setTypeface(ResourcesCompat.getFont(Objects.requireNonNull(getActivity()),R.font.balloon));
        letter.setText(l);
        letter.setTextColor(getResources().getColor(R.color.colorBlack));

        Log.d("pixels", width + " " + height);

        AbsoluteLayout layout = (AbsoluteLayout) Objects.requireNonNull(getActivity()).findViewById(R.id.game_layout);

        AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                map.get("x"), map.get("y"));

        letter.setLayoutParams(params);

        letter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker.append(letter.getText().toString());
                letter.setVisibility(View.INVISIBLE);
                pronunciationUtil.textToSpeechAnnouncer(letter, textToSpeech);

                if (checker.getText().length() == answer.length()) {
                    String validator;
                    if (checker.getText().toString().equals(answer)) {
                        //will run once when correct answer is entered. the method will instantiate, and add all from the current list
                        modelList.get(counter).setWrongAnswerSet((ArrayList<String>) wrongAnswerList);
                        counter++;

                        validator = "You are correct";

                        wrongAnswerList.removeAll(wrongAnswerList);
                        pronunciationUtil.textToSpeechAnnouncer(validator, textToSpeech);

                    } else {
                        validator = "You are incorrect. Please try again";
                        correctAnswerSet.add(answer);
                        pronunciationUtil.textToSpeechAnnouncer(validator, textToSpeech);
                        //every wrong answer, until a correct answer will be added here
                        wrongAnswerList.add(checker.getText().toString());
                        modelList.get(counter).setCorrect(false);
                    }
                    cvTextView.setText(validator);
                    fadeIn.start();

                }
            }
        });
        layout.addView(letter);
    }

    private HashMap<String, Integer> getCoordinates() {
        HashMap<String, Integer> map = new HashMap<>();
        Random r = new Random();

        int ranX = r.nextInt(width);
        int ranY = r.nextInt(height);

        while ( ((ranX > (width / 2) - 240 && ranX < (width / 2) + 240))
            && ((ranY > (height / 2) - 350 && ranY < (height / 2) + 350)) )  {
            ranX = r.nextInt(width);
        }
        map.put("x", ranX);
        map.put("y", ranY);

        return map;
    }

    private boolean checkCollision(List<HashMap<String, Integer>> mapList,
                                   HashMap<String, Integer> testMap) {

        if(mapList.isEmpty()){
            return false;
        }

        for (int i = 0; i < mapList.size(); i++) {

            if( (testMap.get("x") > mapList.get(i).get("x") - 300 &&
                    testMap.get("x") < mapList.get(i).get("x") + 300) &&

            (testMap.get("y") > mapList.get(i).get("y") - 300 &&
                    testMap.get("y") < mapList.get(i).get("y")+ 300) ){
                return true;
            }
        }
        return false;
    }

    private void setMaxWidthAndHeight() {
        WindowManager wm = (WindowManager) getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        Point dimens = new Point();
        wm.getDefaultDisplay().getSize(dimens);
        wm.getDefaultDisplay().getMetrics(displaymetrics);

        width = dimens.x - 100;
        height = dimens.y - 300;
    }

    private void loadNext(int counter) {

        int limit = 5;
        if (counter < modelList.size() && counter < limit) {
            checker.setText("");
            answer = modelList.get(counter).getName();
            pronunciationUtil.textToSpeechAnnouncer(answer, textToSpeech);
            Picasso.get().load(modelList.get(counter).getImage()).into(imageView);
            Handler handler = new Handler();
            handler.post(() -> setWordsOnScreen(answer));

        } else {
            sharedPreferences.edit().putStringSet(ResultsFragment.CORRECT_ANSWER_FOR_USER, correctAnswerSet).apply();
            sharedPreferences.edit().putInt(ResultsFragment.TOTALSIZE, limit).apply();

            for (int i = 0; i < limit; i++) {
                if (modelList.get(i).getWrongAnswerSet() == null || modelList.get(i).getWrongAnswerSet().size() == 0) {
                    modelList.get(i).setCorrect(true);
                }
            }
            listener.moveToReplayFragment(modelList,false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        textToSpeech.shutdown();
        pronunciationUtil = null;

    }
}
