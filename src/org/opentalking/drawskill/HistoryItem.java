package org.opentalking.drawskill;

import android.graphics.Bitmap;


import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import org.opentalking.drawskill.style.StylesFactory;

/**
 * History element
 */
public final class HistoryItem {
    private SurfaceDiff surfaceDiff;
    private IntBuffer oldSurfaceBuffer;
    private Map<StylesFactory.BrushType, Object> oldBrushData = new HashMap<StylesFactory.BrushType, Object>();
    private HistoryItemState state = HistoryItemState.UNDO;

    public HistoryItem(IntBuffer oldSurface, Map<StylesFactory.BrushType, Object> oldBrushData) {
        surfaceDiff = SurfaceDiff.Create(oldSurface, MainActivity.getInstance().getSurface().getBitmap());

        if (null == surfaceDiff)
            this.oldSurfaceBuffer = (IntBuffer) oldSurface.rewind();

        this.oldBrushData = oldBrushData;
    }

    public void undo() {
        swap();
    }

    public void redo() {
        swap();
    }

    private void swap() {
        Bitmap bitmap = MainActivity.getInstance().getSurface().getBitmap();
        Map<StylesFactory.BrushType, Object> brushData = new HashMap<StylesFactory.BrushType, Object>();
        StylesFactory.saveState(brushData);

        if (null != surfaceDiff) {
            surfaceDiff.applyAndSwap(bitmap);
        } else {
            IntBuffer surf = IntBuffer.allocate(bitmap.getWidth() * bitmap.getHeight());
            bitmap.copyPixelsToBuffer(surf);

            bitmap.copyPixelsFromBuffer(oldSurfaceBuffer);
            oldSurfaceBuffer = (IntBuffer) surf.rewind();
        }

        StylesFactory.restoreState(oldBrushData);
        oldBrushData = brushData;

        MainActivity.getInstance().getSurface().invalidate();
    }

    public HistoryItemState getState() {
        return state;
    }

    public void setState(HistoryItemState state) {
        this.state = state;
    }

    public static enum HistoryItemState {
        UNDO,
        REDO
    }
}
