package com.d_project.ui;

import java.awt.Point;

import com.d_project.ui.event.DMouseEvent;

/**
 * DADComponent
 * @author Kazuhiko Arase
 */
public abstract class DADComponent extends DContainer {
    
    DADGroup group;

    private Point dragPoint;

    public DADComponent() {
        enableEvents(DEvent.MOUSE_EVENT_MASK | DEvent.MOUSE_MOTION_EVENT_MASK);
    }

    public DADGroup getDADGroup() {
        return group;
    }

    public DComponent getDADTarget() {
        return this;        
    }
    
    public void dadDrag() {
    }
    
    public void dadDrop() {
    }

    public void dadMove(int x, int y) {
        DComponent comp = getDADTarget();
        if (comp != null) {
            Point p = comp.getLocation();
            comp.setLocation(p.x + x, p.y + y);
        }
    }

    protected void processMouseEvent(DMouseEvent e) {

        switch(e.getID() ) {

        case DMouseEvent.MOUSE_PRESSED :
            dragPoint = e.getPoint();            
            if (group == null) {
                dadDrag();
            } else {
                group.drag();
            }
            break;

        case DMouseEvent.MOUSE_RELEASED :
            dragPoint = null;
            if (group == null) {
                dadDrop();
            } else {
                group.drop();
            }
            break;

        default :
            break;
        }
    }

    protected void processMouseMotionEvent(DMouseEvent e) {

        switch(e.getID() ) {

        case DMouseEvent.MOUSE_DRAGGED :

            synchronized(getTreeLock()) {

                Point dragPoint = this.dragPoint;
                DComponent comp = getDADTarget();

                if (dragPoint != null && comp != null) {
                    Point currentPoint = e.getPoint();
                    int dx = currentPoint.x - dragPoint.x; 
                    int dy = currentPoint.y - dragPoint.y;

                    if (group == null) {
                        dadMove(dx, dy);
                    } else {
                        group.move(dx, dy);
                    }
                }
            }
            break;

        default :
            break;
        }
    }
}    
