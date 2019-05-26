package com.example.aryoulearning.view.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aryoulearning.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ResultsFragment extends Fragment {
    public static final String WRONGANSWER = "WRONGANSWER";
    public static final String ANSWERSCORRECT = "ANSWERSCORRECT";
    public static final String RIGHTANSWERS = "RIGHTANSWERS";
    private static final int REQUEST_CODE = 1;
    public static final String CORRECT_ANSWER_FOR_USER = "correct answer for user";
    private SharedPreferences sharedPreferences;
    private Set<String> rightAnswer = new HashSet<>();
    private HashMap<String, String> map = new HashMap<>();
    private Set<String> wrongAnswer = new HashSet();
    private Set<String> correctAnswersStringSet = new HashSet();
    private int correctAnswer;
    private RatingBar rainbowRatingBar;
    private String userRightAnswersString, userWrongAnswersString, correctAnswerForUserString;
    public static final String TAG = "ResultsFragment";
    private TextView userRightAnswerTextView, userWrongAnswerTextView, correctAnswerTextView;
    WebView congratsWebView;
    FloatingActionButton floatingActionButton;


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
        if(wrongAnswer != null) {
            for (String wrong : wrongAnswer) {
                map.put(wrong, sharedPreferences.getString(wrong, null));
            }
        }
        if(rightAnswer != null) {
            for (String right : rightAnswer) {
                rightAnswerBuilder.append(right).append(" ");
            }
        }

        if(wrongAnswer != null) {
            for (String wrongChoice : wrongAnswer) {
                wrongAnswerBuilder.append(wrongChoice).append(" ");
            }
        }
        if(correctAnswersStringSet != null) {
            for (String correctWay : correctAnswersStringSet) {
                correctAnswerBuilder.append(correctWay).append(" ");
            }
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
        fabClick();


    }

    public void fabClick(){
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
                    takeScreenshotAndShare(v);
                }else{
                    takeScreenshotAndShare(v);
                }
            }
        });
    }

    public void allowOnFileUriExposed(){
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){
            case REQUEST_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    break;
                }
                default:
                    break;
        }
<<<<<<< HEAD
    }

    public Bitmap takeScreenshotAndShare(View view) {
        allowOnFileUriExposed();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(true);
        saveBitmap(b);
        return b;
    }

=======
    }

    public Bitmap takeScreenshotAndShare(View view) {
        allowOnFileUriExposed();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(true);
        saveBitmap(b);
        return b;
    }

>>>>>>> 55e5ab189e264ef7a84339fe80523f2bff753e42
    private void shareIt(File imagePath) {
        Uri uri = Uri.fromFile(imagePath);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");

        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        try {
            startActivity(Intent.createChooser(intent, "Share Screenshot"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "No App Available", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveBitmap(Bitmap bitmap) {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            shareIt(imageFile);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void allAttemptsCorrectChecker() {
        if (userWrongAnswersString == null || wrongAnswer == null ||userWrongAnswersString.isEmpty() || wrongAnswer.isEmpty() || userWrongAnswerTextView.getText().equals(String.valueOf(0))) {
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
        floatingActionButton = view.findViewById(R.id.share_info);
        congratsWebView.setVisibility(View.INVISIBLE);
    }

    private void displayCorrectWordAttempts() {
        rainbowRatingBar.setRating(correctAnswer);

        if(rightAnswer != null && wrongAnswer != null) {
            rainbowRatingBar.setNumStars(rightAnswer.size() + wrongAnswer.size());
        }else{
            int r = 0;
            int w = 0;
            if(rightAnswer != null){
                r = rightAnswer.size();
            }
            if(wrongAnswer != null){
                w = wrongAnswer.size();
            }
            rainbowRatingBar.setNumStars(r + w);
        }

        rainbowRatingBar.setIsIndicator(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sharedPreferences.edit().clear().commit();
    }
}
