package com.capstone.aryoulearning.animation;

import android.animation.ObjectAnimator;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

import com.capstone.aryoulearning.R;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.QuaternionEvaluator;
import com.google.ar.sceneform.math.Vector3;

public class Animations {

    public static class AR {

        public static ObjectAnimator createRotationAnimator() {
            // Node's setLocalRotation method accepts Quaternions as parameters.
            // First, set up orientations that will animate a circle.
            Quaternion orientation1 = Quaternion.axisAngle(new Vector3(0.0f, 1.0f, 0.0f), 0);
            Quaternion orientation2 = Quaternion.axisAngle(new Vector3(0.0f, 1.0f, 0.0f), 120);
            Quaternion orientation3 = Quaternion.axisAngle(new Vector3(0.0f, 1.0f, 0.0f), 240);
            Quaternion orientation4 = Quaternion.axisAngle(new Vector3(0.0f, 1.0f, 0.0f), 360);

            ObjectAnimator orbitAnimation = new ObjectAnimator();
            orbitAnimation.setObjectValues(orientation1, orientation2, orientation3, orientation4);

            // Next, give it the localRotation property.
            orbitAnimation.setPropertyName("localRotation");

            // Use Sceneform's QuaternionEvaluator.
            orbitAnimation.setEvaluator(new QuaternionEvaluator());

            //  Allow orbitAnimation to repeat forever
            orbitAnimation.setRepeatCount(ObjectAnimator.INFINITE);
            orbitAnimation.setRepeatMode(ObjectAnimator.RESTART);
            orbitAnimation.setInterpolator(new LinearInterpolator());
            orbitAnimation.setAutoCancel(true);

            return orbitAnimation;
        }
    }

    public static class Normal {
        public static ObjectAnimator setCardFadeInAnimator(CardView cv) {
            cv.setAlpha(0);
            cv.setVisibility(View.VISIBLE);
            ObjectAnimator fadeAnimation = ObjectAnimator.ofFloat(cv,"alpha",0f,1.0f);
            fadeAnimation.setDuration(1000);
            fadeAnimation.setStartDelay(500);
            return fadeAnimation;
        }

        public static ObjectAnimator setCardFadeOutAnimator(CardView cv) {
            ObjectAnimator fadeAnimation = ObjectAnimator.ofFloat(cv,"alpha",1.0f,0f);
            fadeAnimation.setDuration(1000);
            return fadeAnimation;
        }

        public static Animation getVibrator(View itemView){
            return AnimationUtils.loadAnimation(itemView.getContext(), R.anim.vibrate);
        }

    }

}
