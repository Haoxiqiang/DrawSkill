package org.opentalking.drawskill;

import java.util.HashMap;

import org.opentalking.drawskill.style.StylesFactory;

import android.app.Application;
import android.content.Context;

public class DrawApplication extends Application {
	private static Context context = null;

//	public static final HashMap<StylesFactory.BrushType, Integer> StyleButtonMap = new HashMap<StylesFactory.BrushType, Integer>();

	@Override
	public void onCreate() {
		super.onCreate();
		if (null == context) {
			context = this.getApplicationContext();
		}
//		if (StyleButtonMap.size() == 0) {
//			StyleButtonMap.put(StylesFactory.BrushType.SKETCHY,R.id.brush_sketchy);
//			StyleButtonMap.put(StylesFactory.BrushType.SHADED,R.id.brush_shaded);
//			StyleButtonMap.put(StylesFactory.BrushType.FUR, R.id.brush_fur);
//			StyleButtonMap.put(StylesFactory.BrushType.WEB, R.id.brush_web);
//			StyleButtonMap.put(StylesFactory.BrushType.CIRCLES,R.id.brush_circles);
//			StyleButtonMap.put(StylesFactory.BrushType.RIBBON,R.id.brush_ribbon);
//			StyleButtonMap.put(StylesFactory.BrushType.SIMPLE,R.id.brush_simple);
//		}
	}

	public static Context getAppContext() {
		return context;
	}
	
	public static float getDensity() {
		return getAppContext().getResources().getDisplayMetrics().density;
	}
}
