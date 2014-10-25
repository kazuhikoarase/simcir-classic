package com.d_project.ui;

import java.awt.AWTEvent;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import com.d_project.ui.event.DFocusEvent;
import com.d_project.ui.event.DMouseEvent;

/**
 * DPanel
 * @author Kazuhiko Arase
 */
public class DPanel extends Canvas {

    DComponent  baseComp;
    DComponent  dragTarget;
    DComponent  focusTarget;
    DComponent  currentTarget;

    DEventQueue queue;

    Image   image;
    int     imageWidth;
    int     imageHeight;

    public DPanel(DComponent baseComp) {
        this(baseComp, null);
    }

    public DPanel(DComponent baseComp, DEventQueue queue) {
        this.queue    = queue;
        this.baseComp = baseComp;
        baseComp.panel = this;
        enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
    }
    
    Object getTreeLock_() {
        return getTreeLock();
    }        

//    public Dimension preferredSize() {
    public Dimension getPreferredSize() {
        return baseComp.getPreferredSize();
    }

    public DEventQueue getQueue() {
        return (queue != null)? queue : DToolkit.getQueue();
    }

    public void addNotify() {
        super.addNotify();
        baseComp.addNotify();
    }

    public void validate() {
        super.validate();
        baseComp.validate();
    }

    public void removeNotify() {
        baseComp.removeNotify();
        super.removeNotify();
        image = null;
    }

    public void update(Graphics g) {
        Image image = getImage();
        if (image != null) {

//            Rectangle r = g.getClipRect();
            Rectangle r = g.getClipBounds();

            Graphics ig = image.getGraphics();
            ig.clipRect(r.x, r.y, r.width, r.height);
            baseComp.paint(ig);
            ig.dispose();
        }
        paint(g);
    }

    public void paint(Graphics g) {
        Image image = getImage();
        if (image != null) {
            g.drawImage(image, 0, 0, this);
        }
    }

    protected void processMouseEvent(MouseEvent e) {

        int x = e.getX();
        int y = e.getY();

        switch(e.getID() ) {
        case MouseEvent.MOUSE_PRESSED :
            mouseDownImpl(x, y, e.getClickCount() );
            break;
        case MouseEvent.MOUSE_RELEASED :
            mouseUpImpl(x, y);
            break;
        case MouseEvent.MOUSE_EXITED :
            mouseExitImpl(x, y);
            break;
        case MouseEvent.MOUSE_ENTERED :
            mouseEnterImpl(x, y);
            break;
        default : 
            break;
        }
    }

    protected void processMouseMotionEvent(MouseEvent e) {

        int x = e.getX();
        int y = e.getY();

        switch(e.getID() ) {
        case MouseEvent.MOUSE_DRAGGED :
            mouseDragImpl(x, y);
            break;
        case MouseEvent.MOUSE_MOVED :
            mouseMoveImpl(x, y);
            break;
        default : 
            break;
        }
    }
/*
    public boolean mouseDown(Event evt, int x, int y) {
        mouseDownImpl(x, y);
        return true;
    }

    public boolean mouseUp(Event evt, int x, int y) {
        mouseUpImpl(x, y);
        return true;
    }

    public boolean mouseDrag(Event evt, int x, int y) {
        mouseDragImpl(x, y);
        return true;
    }

    public boolean mouseMove(Event evt, int x, int y) {
        mouseMoveImpl(x, y);
        return true;
    }

    public boolean mouseEnter(Event evt, int x, int y) {
        mouseEnterImpl(x, y);
        return true;
    }

    public boolean mouseExit(Event evt, int x, int y) {
        mouseExitImpl(x, y);
        return true;
    }
*/

    /* ----------------------------------------------------------- */

    private Image getImage() {

//        Dimension s = size();
        Dimension s = getSize();
        boolean resized = (s.width != imageWidth || s.height != imageHeight);

        if (image == null || resized) {

            baseComp.setBounds(0, 0, s.width, s.height);
            if (!baseComp.isValid() ) baseComp.validate();

            imageWidth  = s.width;
            imageHeight = s.height;

            if (imageWidth > 0 && imageHeight > 0) {
                image = createImage(imageWidth, imageHeight);
                Graphics ig = image.getGraphics();
                baseComp.paint(ig);
                ig.dispose();
            } else {
                image = null;
            }
        }

        return image;
    }

    /* ----------------------------------------------------------- */

    private void mouseDownImpl(int x, int y, int clickCount) {
        DComponent target = getMouseEventTarget(x, y);
        if (target != null && target.isEventEnable(DEvent.MOUSE_EVENT_MASK) ) {
            DEvent e = new DMouseEvent(target, DMouseEvent.MOUSE_PRESSED, x, y, clickCount);            
            getQueue().postEvent(e);
            dragTarget = target;
        }

        setCurrentTarget(x, y, target);
    }

    private void mouseUpImpl(int x, int y) {
        DComponent target = this.dragTarget;
        if (target != null) {
            DEvent e = new DMouseEvent(target, DMouseEvent.MOUSE_RELEASED, x, y, 0);            
            getQueue().postEvent(e);
            this.dragTarget = null;
        }
        setCurrentTarget(x, y);
    }

    private void mouseDragImpl(int x, int y) {
        DComponent target = this.dragTarget;
        if (target != null && target.isEventEnable(DEvent.MOUSE_MOTION_EVENT_MASK) ) {
            DEvent e = new DMouseEvent(target, DMouseEvent.MOUSE_DRAGGED, x, y, 0);            
            getQueue().postEvent(e);
        }
        setCurrentTarget(x, y);
    }

    private void mouseEnterImpl(int x, int y) {
        setCurrentTarget(x, y);
    }

    private void mouseExitImpl(int x, int y) {
        setCurrentTarget(x, y);
    }

    private void mouseMoveImpl(int x, int y) {
        DComponent target = getMouseMotionEventTarget(x, y);
        if (target != null) {
            DEvent e = new DMouseEvent(target, DMouseEvent.MOUSE_MOVED, x, y, 0);            
            getQueue().postEvent(e);
        }
        setCurrentTarget(x, y);
    }

    /* ----------------------------------------------------------- */

    void setFocusTarget(DComponent target) {
        if (focusTarget != target) {
            if (focusTarget != null) {
                DEvent e = new DFocusEvent(focusTarget, DFocusEvent.FOCUS_LOST);            
                getQueue().postEvent(e);
            }
            focusTarget = target;
            if (focusTarget != null) {
                DEvent e = new DFocusEvent(focusTarget, DFocusEvent.FOCUS_GAINED);            
                getQueue().postEvent(e);
            }
        }
    }

    /* ----------------------------------------------------------- */

    private DComponent getMouseEventTarget(int x, int y) {
        DComponent comp = baseComp.getComponentAt(x, y);
        while (comp != null) {
            if (comp.isEventEnable(DEvent.MOUSE_EVENT_MASK) ) return comp;
            comp = comp.getParent();
        }
        return null;
    }

    private DComponent getMouseMotionEventTarget(int x, int y) {
        DComponent comp = baseComp.getComponentAt(x, y);
        while (comp != null) {
            if (comp.isEventEnable(DEvent.MOUSE_MOTION_EVENT_MASK) ) return comp;
            comp = comp.getParent();
        }
        return null;
    }

    private void setCurrentTarget(int x, int y) {
        setCurrentTarget(x, y, getMouseEventTarget(x, y) );
    }

    private synchronized void setCurrentTarget(int x, int y, DComponent target) {
        if (currentTarget != target) {
            if (currentTarget != null && currentTarget.isEventEnable(DEvent.MOUSE_EVENT_MASK) ) {
                DEvent e = new DMouseEvent(currentTarget, DMouseEvent.MOUSE_EXITED, x, y, 0);            
                getQueue().postEvent(e);
            }

            if (target != null && target.isEventEnable(DEvent.MOUSE_EVENT_MASK) ) {
                DEvent e = new DMouseEvent(target, DMouseEvent.MOUSE_ENTERED, x, y, 0);            
                getQueue().postEvent(e);
            }
            currentTarget = target;
        }
    }
}
