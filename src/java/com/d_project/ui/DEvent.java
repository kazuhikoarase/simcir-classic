package com.d_project.ui;

/**
 * DEvent
 * @author Kazuhiko Arase
 */
public class DEvent {

    public static final long MOUSE_EVENT_MASK        = 1 << 0;
    public static final long MOUSE_MOTION_EVENT_MASK = 1 << 1;

/*
    public static final long FOCUS_EVENT_MASK        = 1 << 2;
*/
    
    Object source;
    int id;

    public DEvent(Object source, int id) {
        this.source = source;
        this.id     = id;
    }

    public Object getSource() {
        return source;
    }

    public int getID() {
        return id;
    }
}
