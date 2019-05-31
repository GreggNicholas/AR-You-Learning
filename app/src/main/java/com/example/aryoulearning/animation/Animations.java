package com.example.aryoulearning.animation;

import android.animation.ObjectAnimator;
import android.view.animation.LinearInterpolator;

import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.QuaternionEvaluator;
import com.google.ar.sceneform.math.Vector3;

public class Animations {

    public static class AR {

        public static ObjectAnimator createAnimator() {
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

    }

}
