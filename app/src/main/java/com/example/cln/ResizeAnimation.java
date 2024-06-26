package com.example.cln;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ResizeAnimation extends Animation {
    final int targetHeight;
    View view;
    int startHeight;

    public ResizeAnimation(View view, int targetHeight, int startHeight) {
        this.view = view;
        this.targetHeight = targetHeight;
        this.startHeight = startHeight;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        //to support decent animation, change new height as Nico S. recommended in comments
        //int newHeight = (int) (startHeight+(targetHeight - startHeight) * interpolatedTime);
//        view.getLayoutParams().height = (int) (startHeight + targetHeight * interpolatedTime);
        view.getLayoutParams().height = (int) (startHeight+(targetHeight - startHeight) * interpolatedTime);
        view.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

}