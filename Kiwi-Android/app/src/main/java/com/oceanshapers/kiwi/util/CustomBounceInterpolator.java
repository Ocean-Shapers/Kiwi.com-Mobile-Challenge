package com.oceanshapers.kiwi.util;

public class CustomBounceInterpolator {
    public double mAmplitude = 1;
    public double mFrequency = 10;

    CustomBounceInterpolator(double amplitude, double frequency) {
        mAmplitude = amplitude;
        mFrequency = frequency;
    }

    public float getInterpolation(float time) {
        return (float) (-1 * Math.pow(Math.E, -time/ mAmplitude) *
                Math.cos(mFrequency * time) + 1);
    }
}
