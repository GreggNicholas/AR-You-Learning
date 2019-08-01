package com.capstone.aryoulearning.view.fragment;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.aryoulearning.R;
import com.capstone.aryoulearning.audio.PronunciationUtil;
import com.capstone.aryoulearning.controller.ResultsAdapter;
import com.capstone.aryoulearning.model.Model;
import com.capstone.aryoulearning.view.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;


public class ResultsFragment extends Fragment {
    public static final String TOTALSIZE = "TOTALSIZE";
    private static final int REQUEST_CODE = 1;
    public static final String CORRECT_ANSWER_FOR_USER = "correct answer for user";
    public static final String MODEL_LIST = "modelList";
    private SharedPreferences sharedPreferences;
    private HashSet correctAnswersStringSet = new HashSet();
    private int totalSize;
    private RatingBar rainbowRatingBar;
    private TextView categoryTextView;
    private List<Model> modelList;
    FloatingActionButton shareFAB;
    FloatingActionButton backFAB;
    private RecyclerView resultRV;
    private PronunciationUtil pronunciationUtil;
    private TextToSpeech textToSpeech;


    public static ResultsFragment newInstance(final List<Model> modelList) {
        ResultsFragment resultsFragment = new ResultsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(MODEL_LIST, (ArrayList<? extends Parcelable>) modelList);
        resultsFragment.setArguments(args);
        return resultsFragment;
    }

    public ResultsFragment() {}


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            modelList = getArguments().getParcelableArrayList(MODEL_LIST);
        }
        pronunciationUtil = new PronunciationUtil();
        textToSpeech = pronunciationUtil.getTTS(getContext());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        extractSharedPrefs();
    }


    public void extractSharedPrefs() {
        correctAnswersStringSet = (HashSet) sharedPreferences.getStringSet(CORRECT_ANSWER_FOR_USER, null);
        totalSize = sharedPreferences.getInt(TOTALSIZE, 0);
    }

    private void initializeViews(@NonNull final View view) {
        rainbowRatingBar = view.findViewById(R.id.rainbow_correctword_ratingbar);
        shareFAB = view.findViewById(R.id.share_info);
        backFAB = view.findViewById(R.id.back_btn);
        resultRV = view.findViewById(R.id.result_recyclerview);
        categoryTextView = view.findViewById(R.id.results_category);
    }

    @Override
    public View onCreateView(@Nonnull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_results, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        setViews();
    }

    public void setViews(){
        displayRatingBarAttempts();
        categoryTextView.setText(MainActivity.currentCategory);
        shareFAB.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.share_button_color)));
        backFABClick();
        shareFABClick();
        setResultRV();
    }

    private void setResultRV() {
        resultRV.setAdapter(new ResultsAdapter(modelList, pronunciationUtil, textToSpeech, totalSize));
        resultRV.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

    }

    public void shareFABClick() {
        shareFAB.setOnClickListener(v -> {
            v = Objects.requireNonNull(ResultsFragment.this.getActivity()).getWindow().getDecorView().getRootView();
            if (ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ResultsFragment.this.getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
                ResultsFragment.this.takeScreenshotAndShare(v);
            } else {
                ResultsFragment.this.takeScreenshotAndShare(v);
            }
        });


    }

    public void backFABClick(){
        backFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });
    }

    public void allowOnFileUriExposed() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
    }

    public void takeScreenshotAndShare(final View view) {
        allowOnFileUriExposed();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(true);
        saveBitmap(b);
    }

    private void shareIt(final File imagePath) {
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

    public void saveBitmap(final Bitmap bitmap) {
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

    private void displayRatingBarAttempts() {
        rainbowRatingBar.setNumStars(totalSize);
        rainbowRatingBar.setStepSize(1);
        rainbowRatingBar.setRating(totalSize - correctAnswersStringSet.size());
        rainbowRatingBar.setIsIndicator(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        textToSpeech.shutdown();
        pronunciationUtil = null;
    }
}
