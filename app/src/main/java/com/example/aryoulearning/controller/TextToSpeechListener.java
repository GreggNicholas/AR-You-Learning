package com.example.aryoulearning.controller;

import android.content.Context;
import android.widget.TextView;

public interface TextToSpeechListener {
    void getSpeech(TextView textView, Context context);

    void getContextOfSpeech(Context context);
}
