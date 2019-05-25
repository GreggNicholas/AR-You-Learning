package com.example.aryoulearning.view.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aryoulearning.R;
import com.example.aryoulearning.audio.PronunciationUtil;
import com.example.aryoulearning.controller.NavListener;
import com.example.aryoulearning.model.Model;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class GameFragment extends Fragment {
    private NavListener listener;
    private List<Model> modelList;
    private ImageView imageView;
    private TextView checker;
    private String answer;
    private int counter;
    private int width;
    private int height;
    private int answersCorrect;
    private Set<String> rightAnswer = new HashSet<>();
    private Set<String> wrongAnswer = new HashSet<>();
    private Set<String> correctAnswerSet = new HashSet<>();
    private SharedPreferences sharedPreferences;
    private PronunciationUtil pronunciationUtil;
    private TextToSpeech textToSpeech;

    public static GameFragment newInstance(List<Model> modelList) {
        GameFragment fragment = new GameFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("model-list-key", (ArrayList<? extends Parcelable>) modelList);
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            modelList = getArguments().getParcelableArrayList("model-list-key");
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_game, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textToSpeech = pronunciationUtil.getTTS(view.getContext());
        checker = view.findViewById(R.id.checker);
        imageView = view.findViewById(R.id.imageView);
        setMaxWidthAndHeight();
        answer = modelList.get(0).getName();
        Picasso.get().load(modelList.get(0).getImage()).into(imageView);
        setWordsOnScreen(answer);
    }

    public void setWordsOnScreen(String word) {
        List<HashMap<String, Integer>> mapList = new ArrayList<>();

        for (int i = 0; i < word.length(); i++) {
            mapList.add(getCoordinates());

            while (checkCollision(mapList, mapList.get(i))) {
                mapList.remove(i);
                mapList.add(i, getCoordinates());
            }
        }

        for (int i = 0; i < word.length(); i++) {
            drawLetters(Character.toString(word.charAt(i)), mapList.get(i));
        }

    }

    public void drawLetters(String l, HashMap<String, Integer> map) {
        final TextView letter = new TextView(getContext());
        letter.setTextSize(80);
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
                    if (checker.getText().toString().equals(answer)) {
                        Toast.makeText(getContext(), "right", Toast.LENGTH_SHORT).show();
                        answersCorrect++;
                        rightAnswer.add(checker.getText().toString());
                        pronunciationUtil.textToSpeechAnnouncer(checker, textToSpeech);
                        loadNext();
                    } else {
                        Toast.makeText(getContext(), "wrong", Toast.LENGTH_SHORT).show();
                        wrongAnswer.add(checker.getText().toString());
                        sharedPreferences.edit().putString(checker.getText().toString(), answer).apply();
                        correctAnswerSet.add(answer);
                        pronunciationUtil.textToSpeechAnnouncer("wrong!", textToSpeech);
                        repeatTheSameWordUntilCorrectlySpelled(answer);
                    }
                }
            }
        });

        layout.addView(letter);
    }

    private void repeatTheSameWordUntilCorrectlySpelled(String mistakenWord) {
        setWordsOnScreen(mistakenWord);
        if (checker.getText().length() == answer.length()) {
            if (checker.getText().toString().equals(answer)) {
                pronunciationUtil.textToSpeechAnnouncer("Correct!", textToSpeech);
                loadNext();
            } else {
                pronunciationUtil.textToSpeechAnnouncer("try again!", textToSpeech);
                checker.setText("");
            }
        }
    }

    private HashMap<String, Integer> getCoordinates() {
        HashMap<String, Integer> map = new HashMap<>();
        Random r = new Random();

        int ranX = r.nextInt(width);
        int ranY = r.nextInt(height);

        while ((ranX > width / 2 - 200 && ranX < width / 2 + 200)) {
            ranX = r.nextInt(width);
        }
        while ((ranY > height / 2 - 240 && ranY < height / 2 + 240)) {
            ranY = r.nextInt(height);
        }

        map.put("x", ranX);
        map.put("y", ranY);

        return map;
    }

    private boolean checkCollision(List<HashMap<String, Integer>> mapList,
                                   HashMap<String, Integer> testMap) {

        for (int i = 0; i < mapList.size() - 1; i++) {
            if (mapList.get(i).get("x") > testMap.get("x") - 80 &&
                    mapList.get(i).get("x") < testMap.get("x") + 80) {
                return true;
            }
            if (mapList.get(i).get("y") > testMap.get("y") - 100 &&
                    mapList.get(i).get("y") < testMap.get("y") + 100) {
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

    private void loadNext() {
        counter++;
        if (counter < modelList.size()) {
            checker.setText("");
            answer = modelList.get(counter).getName();
            Picasso.get().load(modelList.get(counter).getImage()).into(imageView);
            setWordsOnScreen(answer);
        } else {
            Toast.makeText(getContext(), "DONE", Toast.LENGTH_SHORT).show();
            sharedPreferences.edit().putInt(ResultsFragment.ANSWERSCORRECT, answersCorrect).apply();
            sharedPreferences.edit().putStringSet(ResultsFragment.RIGHTANSWERS, rightAnswer).apply();
            sharedPreferences.edit().putStringSet(ResultsFragment.WRONGANSWER, wrongAnswer).apply();
            sharedPreferences.edit().putStringSet(ResultsFragment.CORRECT_ANSWER_FOR_USER, correctAnswerSet).apply();
            listener.moveToResultsFragment();
        }
    }

}
