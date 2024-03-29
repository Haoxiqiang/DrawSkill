package org.opentalking.drawskill.style;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.opentalking.drawskill.DrawApplication;

public final class StylesFactory {
    public static final BrushType DEFAULT_BRUSH_TYPE = BrushType.SKETCHY;

    private static Map<BrushType, StyleBrush> usedBrushes = new HashMap<BrushType, StyleBrush>();
    private static BrushType currentStyle = DEFAULT_BRUSH_TYPE;

    public static StyleBrush getStyle(BrushType brushType) {
        if (!usedBrushes.containsKey(brushType)) {
            StyleBrush style;
            try {
                style = getStyleInstance(brushType);
            } catch (RuntimeException e) {
                brushType = DEFAULT_BRUSH_TYPE;
                style = getStyleInstance(brushType);
            }
            usedBrushes.put(brushType, style);
        }
        currentStyle = brushType;
        return usedBrushes.get(brushType);
    }

    public static Style getCurrentStyle() {
        return getStyle(currentStyle);
    }

    public static BrushType getCurrentBrushType() {
        return currentStyle;
    }

    public static void clearCache() {
        usedBrushes.clear();
    }

    private static StyleBrush getStyleInstance(BrushType id) {
        float density = DrawApplication.getDensity();
        switch (id) {
            case SKETCHY:
                return new SketchyStyle(density);
            case SHADED:
                return new ShadedStyle(density);
            case FUR:
                return new FurStyle(density);
            case WEB:
                return new WebStyle(density);
            case CIRCLES:
                return new CirclesStyle(density);
            case RIBBON:
                return new RibbonStyle();
            case SIMPLE:
                return new SimpleStyle();
            default:
                throw new RuntimeException("Invalid style ID");
        }
    }

    public static void saveState(Map<BrushType, Object> state) {
        Collection<StyleBrush> values = usedBrushes.values();
        for (Style style : values) {
            style.saveState(state);
        }
    }

    public static void restoreState(Map<BrushType, Object> state) {
        Set<BrushType> keySet = state.keySet();
        for (BrushType brushType : keySet) {
            Style style = getStyle(brushType);
            style.restoreState(state);
        }
    }

    public enum BrushType {
        SKETCHY,
        SHADED,
        CHROME,
        FUR,
        WEB,
        RIBBON,
        CIRCLES,
        SIMPLE
    }
}
