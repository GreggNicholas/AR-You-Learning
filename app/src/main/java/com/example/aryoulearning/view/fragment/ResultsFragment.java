package com.example.aryoulearning.view.fragment;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
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
    public static final String CORRECT_ANSWER_FOR_USER = "correct answer for user";
    private SharedPreferences sharedPreferences;
    private Set<String> rightAnswer = new HashSet<>();
    private HashMap<String, String> map = new HashMap<>();
    private Set<String> wrongAnswer, correctAnswersStringSet;
    private int correctAnswer;
    private RatingBar rainbowRatingBar;
    private String userRightAnswersString, userWrongAnswersString, correctAnswerForUserString;
    public static final String TAG = "ResultsFragment";
    private TextView userRightAnswerTextView, userWrongAnswerTextView, correctAnswerTextView;
    WebView congratsWebView;

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
        correctAnswersStringSet = sharedPreferences.getStringSet(CORRECT_ANSWER_FOR_USER, null);
        final StringBuilder rightAnswerBuilder = new StringBuilder();
        final StringBuilder wrongAnswerBuilder = new StringBuilder();
        final StringBuilder correctAnswerBuilder = new StringBuilder();
        for (String wrong : wrongAnswer) {
            map.put(wrong, sharedPreferences.getString(wrong, null));
        }
        for (String right : rightAnswer) {
            rightAnswerBuilder.append(right).append(" ");
        }
        for (String wrongChoice : wrongAnswer) {
            wrongAnswerBuilder.append(wrongChoice).append(" ");
        }
        for (String correctWay : correctAnswersStringSet) {
            correctAnswerBuilder.append(correctWay).append(" ");
        }
        userRightAnswersString = " " + getString(R.string.rightanswers) + " " + rightAnswerBuilder.toString();
        userWrongAnswersString = " " + getString(R.string.wronganswers) + " " + wrongAnswerBuilder.toString();
        correctAnswerForUserString = " " + getString(R.string.correctanswers) + " " + correctAnswerBuilder.toString();


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
        displayCorrectWordAttempts();
        userRightAnswerTextView.setText(userRightAnswersString);
        userWrongAnswerTextView.setText(userWrongAnswersString);
        correctAnswerTextView.setText(correctAnswerForUserString);
        allAttemptsCorrectChecker();
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void allAttemptsCorrectChecker() {
        if (userWrongAnswersString.isEmpty() || wrongAnswer.isEmpty() || userWrongAnswerTextView.getText().equals(String.valueOf(0))) {
            userWrongAnswerTextView.setVisibility(View.INVISIBLE);
            correctAnswerTextView.setVisibility(View.INVISIBLE);
            congratsWebView.setVisibility(View.VISIBLE);
            String congratsImage = getString(R.string.congratsgif);
            congratsWebView.loadUrl(congratsImage);
        }
    }

    private void findViewByIds(@NonNull View view) {
        userRightAnswerTextView = view.findViewById(R.id.result_fragment_user_right_answer_tv);
        userWrongAnswerTextView = view.findViewById(R.id.result_fragment_user_wrong_answer_tv);
        correctAnswerTextView = view.findViewById(R.id.result_fragment_correct_answer_tv);
        rainbowRatingBar = view.findViewById(R.id.rainbow_correctword_ratingbar);
        congratsWebView = view.findViewById(R.id.congrats_webview);
        congratsWebView.setVisibility(View.INVISIBLE);
    }

    private void displayCorrectWordAttempts() {
        rainbowRatingBar.setRating(correctAnswer);
        rainbowRatingBar.setIsIndicator(true);
    }
}
