package com.v2soft.AndLib.ui.animation;

import android.view.View;
import android.view.animation.Animation;

/**
 * Few simple animations
 * @author vshcryabets@gmail.com
 *
 */
public class SimpleAnimationUtils {
    public static void showAndThenAnimate(View view, Animation animation) {
        view.setAnimation(animation);
        view.setVisibility(View.VISIBLE);
        animation.start();
    }
}
