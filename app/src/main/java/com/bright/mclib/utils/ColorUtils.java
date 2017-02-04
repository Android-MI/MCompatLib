package com.bright.mclib.utils;

import android.graphics.Color;

import java.util.Random;

public class ColorUtils {

    public static int randomColor() {
        Random random = new Random();
        int red = random.nextInt(150) + 50;
        int green = random.nextInt(150) + 50;
        int blue = random.nextInt(150) + 50;
        return Color.rgb(red, green, blue);
    }
}
