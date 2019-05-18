package com.example.aryoulearning.audio;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

public final class PronunciationUtil {
    private TextToSpeech textToSpeech;

    public void initializeTextToSpeech(Context context) {
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int language = textToSpeech.setLanguage(Locale.US);
                    if (language == TextToSpeech.LANG_MISSING_DATA
                            || language == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        Log.e("TTS", "Initialization failed");
                    }
                }
            }
        });
    }

    public void textToSpeechAnnouncer(final TextView textView) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int speakText = textToSpeech.speak(String.valueOf(textView.getText()),
                        TextToSpeech.QUEUE_FLUSH, null);
                if (speakText == TextToSpeech.ERROR) {
                    Log.e("TTS", "Error in converting Text to Speech!");
                }
            }
        });
    }

    private static String pronounceSingleLetter(char letter) {
        switch (letter) {
            case 'a':
                return "a";
            case 'b':
                return "bee";
            case 'c':
                return "cee";
            case 'd':
                return "dee";
            case 'e':
                return "e";
            case 'f':
                return "ef";
            case 'g':
                return "gee";
            case 'h':
                return "aitch";
            case 'i':
                return "i";
            case 'j':
                return "jay";
            case 'k':
                return "kay";
            case 'l':
                return "el";
            case 'm':
                return "em";
            case 'n':
                return "en";
            case 'o':
                return "o";
            case 'p':
                return "pee";
            case 'q':
                return "cue";
            case 'r':
                return "ar";
            case 's':
                return "ess";
            case 't':
                return "tee";
            case 'u':
                return "u";
            case 'v':
                return "vee";
            case 'w':
                return "double-u";
            case 'x':
                return "ex";
            case 'y':
                return "wy";
            case 'z':
                return "zed";
        }
        return "";

    }

}
