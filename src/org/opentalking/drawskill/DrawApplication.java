package org.opentalking.drawskill;

import java.util.HashMap;

import org.opentalking.drawskill.style.StylesFactory;

import android.app.Application;
import android.content.Context;

public class DrawApplication extends Application {
	private static Context context = null;

	public static final HashMap<Integer, StylesFactory.BrushType> StyleButtonMap = new HashMap<Integer, StylesFactory.BrushType>();

	@Override
	public void onCreate() {
		super.onCreate();
		if (null == context) {
			context = this.getApplicationContext();
		}
		if (StyleButtonMap.size() == 0) {
			StyleButtonMap.put(0,StylesFactory.BrushType.SKETCHY);
			StyleButtonMap.put(1,StylesFactory.BrushType.SHADED);
			StyleButtonMap.put(2,StylesFactory.BrushType.FUR);
			StyleButtonMap.put(3,StylesFactory.BrushType.WEB);
			StyleButtonMap.put(4,StylesFactory.BrushType.CIRCLES);
			StyleButtonMap.put(5,StylesFactory.BrushType.RIBBON);
			StyleButtonMap.put(6,StylesFactory.BrushType.SIMPLE);
		}
	}

	public static Context getAppContext() {
		return context;
	}
	
	public static float getDensity() {
		return getAppContext().getResources().getDisplayMetrics().density;
	}
}
