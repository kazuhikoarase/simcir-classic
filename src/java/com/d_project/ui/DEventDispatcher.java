package com.d_project.ui;

import java.awt.Point;

import com.d_project.ui.event.DMouseEvent;

/**
 * DEventDispatcher
 * @author Kazuhiko Arase
 */
class DEventDispatcher extends Thread {

    static int count = 0;

    DEventQueue queue;
    boolean alive = true;

    public DEventDispatcher(DEventQueue queue) {
        super("DEventDispatcher-" + count++);
        this.queue = queue;
    }

    boolean isValid(DEvent currEvent, DEvent nextEvent) {
        if (
            currEvent != null && 
            nextEvent != null && 
            currEvent instanceof DMouseEvent && 
            nextEvent instanceof DMouseEvent && 
            currEvent.getSource() == nextEvent.getSource() &&
            currEvent.getID()     == nextEvent.getID() 
        ) {
            int id = currEvent.getID();
            switch(id) {
            case DMouseEvent.MOUSE_DRAGGED :
            case DMouseEvent.MOUSE_MOVED :
                    return false;
            default :
                break;
            }
        }
        return true;
    }

    public void run() {

        while(alive) {

            DEvent e = queue.getNext();

            while (!isValid(e, queue.peek() ) ) {
                e = queue.getNext();
            }

            if (e instanceof DSystemEvent) {
                if (e.getID() == DSystemEvent.SYSTEM_STOP) {
                    break;
                }
            } else {
                Object source = e.getSource();
                if (source instanceof DEventTarget) {
                    try {
                        if (e instanceof DMouseEvent) {
                            Point p = ((DComponent)source).getLocation(null);
                            ((DMouseEvent)e).translate(-p.x, -p.y);
                        }
                        ((DEventTarget)source).dispatchEvent(e);
                    } catch(Exception exc) {
                        exc.printStackTrace();
                    }
                }
            }
        }

    }
}

