package com.example.aryoulearning.view.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.aryoulearning.R;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class ResultsFragment extends Fragment {
    public static final String WRONGANSWER = "WRONGANSWER";
    public static final String ANSWERSCORRECT = "ANSWERSCORRECT";
    public static final String RIGHTANSWERS = "RIGHTANSWERS";
    private SharedPreferences sharedPreferences;
    private Set<String> rightAnswer = new HashSet<>();
    private HashMap<String, String> map = new HashMap<>();
    private Set<String> wrongAnswer;
    private int correctAnswer;
    private RatingBar rainbowRatingBar;
    private String userRightAnswers;
    public static final String TAG = "ResultsFragment";
    private TextView userRightAnswerTextView, userWrongAnswerTextView, correctAnswerTextView;


    public static ResultsFragment newInstance() {
        return new ResultsFragment();
    }

    public ResultsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        extractSharedPrefs();
    }

    public void extractSharedPrefs() {
        rightAnswer = sharedPreferences.getStringSet(RIGHTANSWERS, null);
        wrongAnswer = sharedPreferences.getStringSet(WRONGANSWER, null);
        correctAnswer = sharedPreferences.getInt(ANSWERSCORRECT, 0);
        final StringBuilder builder = new StringBuilder();
        for (String wrong : wrongAnswer) {
            map.put(wrong, sharedPreferences.getString(wrong, null));
        }
        for (String right: rightAnswer) {
            builder.append(right + " ");
        }
        userRightAnswers = " Your Right Answer: " + builder.toString();
        Log.d(TAG, "userRightAnswers : " + userRightAnswers);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_results, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViewByIds(view);
        displayCorrectWordAttempts(view);
        userRightAnswerTextView.setText(userRightAnswers);
    }

    private void findViewByIds(@NonNull View view) {
        userRightAnswerTextView = view.findViewById(R.id.result_fragment_user_right_answer_tv);
        userWrongAnswerTextView = view.findViewById(R.id.result_fragment_user_wrong_answer_tv);
        correctAnswerTextView = view.findViewById(R.id.result_fragment_correct_answer_tv);
        rainbowRatingBar = view.findViewById(R.id.rainbow_correctword_ratingbar);
    }

    private void displayCorrectWordAttempts(View view) {
        rainbowRatingBar.setIsIndicator(true);
        rainbowRatingBar.setRating(correctAnswer);
    }
}
