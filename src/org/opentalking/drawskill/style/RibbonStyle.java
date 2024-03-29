package org.opentalking.drawskill.style;

import android.graphics.Canvas;

import java.util.Map;

class RibbonStyle extends StyleBrush {
    private static final int LINE_NUM = 50;
    private Painter[] paintPool = new Painter[LINE_NUM];

    private float x;
    private float y;

    {
        paint.setAntiAlias(true);

        for (int i = 0; i < LINE_NUM; i++) {
            paintPool[i] = new Painter();
        }
    }

    @Override
    public void setOpacity(int opacity) {
        super.setOpacity((int) (opacity * 0.25f));
    }

    public void draw(Canvas c) {
        float startX;
        float startY;
        for (Painter painter : paintPool) {
            startX = painter.dx;
            startY = painter.dy;
            painter.ax = (painter.ax + (painter.dx - x) * painter.div) * painter.ease;
            painter.dx -= painter.ax;
            painter.ay = (painter.ay + (painter.dy - y) * painter.div) * painter.ease;
            painter.dy -= painter.ay;
            c.drawLine(startX, startY, painter.dx, painter.dy, paint);
        }
    }

    public void stroke(Canvas c, float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void strokeStart(float x, float y) {
        this.x = x;
        this.y = y;

        for (Painter painter : paintPool) {
            painter.dx = x;
            painter.dy = y;
        }
    }

    public void saveState(Map<StylesFactory.BrushType, Object> state) {
    }

    public void restoreState(Map<StylesFactory.BrushType, Object> state) {
    }

    private class Painter {
        float dx = 0;
        float dy = 0;
        float ax = 0;
        float ay = 0;
        float div = 0.1F;
        float ease = (float) (Math.random() * 0.2 + 0.6);
    }
}
