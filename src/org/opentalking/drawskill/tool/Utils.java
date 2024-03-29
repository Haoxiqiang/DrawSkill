package org.opentalking.drawskill.tool;

import android.graphics.Color;

public class Utils {
	public static float[] color2HSV(int color) {
		float[] hsv = new float[3];

		int red = Color.red(color);
		int green = Color.green(color);
		int blue = Color.blue(color);
		Color.RGBToHSV(red, green, blue, hsv);

		return hsv;
	}
}
