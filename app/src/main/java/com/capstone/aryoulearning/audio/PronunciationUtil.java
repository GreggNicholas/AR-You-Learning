package com.capstone.aryoulearning.audio;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;

import java.util.Locale;

public final class PronunciationUtil {
    private TextToSpeech textToSpeech;


    public TextToSpeech getTTS(final Context context) {
        if (textToSpeech == null) {
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
        return textToSpeech;
    }


    public void textToSpeechAnnouncer(final TextView textView, final TextToSpeech textToSpeech) {
        String letter = textView.getText().toString().toLowerCase();
        int speakText = textToSpeech.speak(pronounceSingleLetter(letter),
                TextToSpeech.QUEUE_ADD, null);
        if (speakText == TextToSpeech.ERROR) {
            Log.e("TTS", "Error in converting Text to Speech!");
        }
    }

    public void textToSpeechAnnouncer(final String message, final TextToSpeech textToSpeech) {
        ;
        int speakText = textToSpeech.speak(message,
                TextToSpeech.QUEUE_ADD, null);
        if (speakText == TextToSpeech.ERROR) {
            Log.e("TTS", "Error in converting Text to Speech!");
        }
    }

    private static String pronounceSingleLetter(String letter) {
        switch (letter) {
            case "a":
                return "ayee";
            case "b":
                return "bee";
            case "c":
                return "cee";
            case "d":
                return "dee";
            case "e":
                return "e";
            case "f":
                return "ef";
            case "g":
                return "gee";
            case "h":
                return "aitch";
            case "i":
                return "i";
            case "j":
                return "jay";
            case "k":
                return "kay";
            case "l":
                return "el";
            case "m":
                return "em";
            case "n":
                return "en";
            case "o":
                return "o";
            case "p":
                return "pee";
            case "q":
                return "cue";
            case "r":
                return "ar";
            case "s":
                return "ess";
            case "t":
                return "tee";
            case "u":
                return "u";
            case "v":
                return "vee";
            case "w":
                return "double-u";
            case "x":
                return "ex";
            case "y":
                return "wy";
            case "z":
                return "zed";
        }
        return letter;

    }

}
