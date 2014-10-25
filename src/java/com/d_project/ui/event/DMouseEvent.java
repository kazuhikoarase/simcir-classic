package com.d_project.ui.event;

import java.awt.Point;

import com.d_project.ui.DComponent;
import com.d_project.ui.DEvent;

/**
 * DMouseEvent
 * @author Kazuhiko Arase
 */
public class DMouseEvent extends DEvent {

    public static final int MOUSE_RELEASED = 0;
    public static final int MOUSE_PRESSED  = 1;
    public static final int MOUSE_DRAGGED  = 2;
    public static final int MOUSE_MOVED    = 3;
    public static final int MOUSE_ENTERED  = 4;
    public static final int MOUSE_EXITED   = 5;

    int x;
    int y;
    int clickCount;

    public DMouseEvent(DComponent comp, int id, int x, int y, int clickCount) {
        super(comp, id);
        this.x = x;
        this.y = y;
        this.clickCount = clickCount;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getClickCount() {
        return clickCount;
    }

    public Point getPoint() {
        return new Point(x, y);
    }
    
    public void translate(int x, int y) {
        this.x += x;
        this.y += y;
    }

    public String toString() {
        return getClass().getName() + "{" + getIDName() + " on " + getSource() + "}";
    }

    String getIDName() {
        switch(getID() ) {
        case MOUSE_RELEASED : return "released";
        case MOUSE_PRESSED  : return "pressed";
        case MOUSE_DRAGGED  : return "dragged";
        case MOUSE_MOVED    : return "moved";
        case MOUSE_ENTERED  : return "entered";
        case MOUSE_EXITED   : return "exited";
        default : return "undefined";
        }
    }
}
