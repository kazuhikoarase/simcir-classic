package com.d_project.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Vector;

import com.d_project.ui.event.DFocusEvent;
import com.d_project.ui.event.DMouseEvent;
import com.d_project.ui.event.DMouseListener;

/**
 * DComponent
 * @author Kazuhiko Arase
 */
public class DComponent implements DEventTarget {

    static final Object LOCK = new Object();

    Component comp;

    String name;

    long eventMask;

    int x;
    int y;
    int width;
    int height;

    DContainer parent;
    DPanel panel;

    Font  font;
    Color foreground;
    Color background;

    Vector mouseListener;

    boolean valid    = false;
    boolean notified = false;
    boolean visible  = true;

    public DComponent() {
    }

    protected Object getTreeLock() {
        try {
            return getPanel().getTreeLock_();        
        } catch(NullPointerException e) {
            return LOCK;
        }
    }

    public void setVisible(boolean visible) {
        if (this.visible != visible) {
            this.visible = visible;
            if (visible) validate();
            repaint();
        }
    }

    public void requestFocus() {
        DPanel panel = getPanel();
        if (panel != null) {
            panel.setFocusTarget(this);
        }
    }

    public boolean isShowing() {
        return visible && notified;
    }

    public boolean isVisible() {
        return visible;
    }

    public void toFront() {
        DContainer parent = getParent();
        if (parent != null && parent.indexOf(this) != 0) {
            parent.remove(this);
            parent.add(this, 0);
            repaint();
        }
    }

    public void toBack() {
        DContainer parent = getParent();
        if (parent != null && parent.indexOf(this) != parent.getComponentCount() - 1) {
            parent.remove(this);
            parent.add(this);
            repaint();
        }
    }

    public void invalidate() {
        synchronized(getTreeLock()) {
            valid = false;
            if (parent != null && parent.valid) {
                parent.invalidate();
            }
        }
    }

    public void doLayout() {
    }

    public void addNotify() {
        synchronized(getTreeLock()) {
            notified = true;
        }
    }

    public void removeNotify() {
        synchronized(getTreeLock()) {
            notified = false;
        }
    }

    public void validate() {
        synchronized(getTreeLock()) {
            if (!valid) {
                doLayout();
                valid = true;
            }
        }
    }

    public boolean isValid() {
        return valid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Image createImage(int width, int height) {
        return getPanel().createImage(width, height);
    }

    public FontMetrics getFontMetrics(Font f) {
        return getPanel().getFontMetrics(f);
    }

    public DComponent getComponentAt(Point p) {
        return getComponentAt(p.x, p.y);
    }

    public DComponent getComponentAt(int x, int y) {
        return (visible && contains(x, y) )? this : null;
    }

    public Point getLocation(DContainer base) {
        int x = this.x;
        int y = this.y;
        DContainer parent = getParent();
        while(parent != null && parent != base) {
            x += parent.x;
            y += parent.y;
            parent = parent.getParent();
        }
        return new Point(x, y);
    }

    public boolean contains(Point p) {
        return contains(p.x, p.y);
    }

    public boolean contains(int x, int y) {
        return (0 <= x && x < width && 0 <= y && y < height);
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public Font getFont() {
        if (font != null) return font;
        return (parent != null)? parent.getFont() : getPanel().getFont();
    }

    public void setBackground(Color background) {
        this.background = background;
    }

    public Color getBackground() {
        if (background != null) return background;
        return (parent != null)? parent.getBackground() : getPanel().getBackground();
    }

    protected final DEventQueue getQueue() {
        DPanel panel = getPanel();
        if (panel != null) return panel.getQueue();
System.out.println("Ooooooooooooooooops!!" + this);
        return null;
    }

    public void setForeground(Color foreground) {
        this.foreground = foreground;
    }

    public Color getForeground() {
        if (foreground != null) return foreground;
        return (parent != null)? parent.getForeground() : getPanel().getForeground();
    }

    public DContainer getParent() {
        return parent;
    }

    public Dimension getPreferredSize() {
        return getSize();
    }

    public Dimension getSize() {
        return new Dimension(width, height);
    }
    
    public void enableEvents(long newMask) {
        eventMask = eventMask | newMask; 
    }

    public void disableEvents(long newMask) {
        eventMask = eventMask & ~newMask; 
    }

    boolean isEventEnable(long mask) {
        return (eventMask & mask) != 0;
    }

    public final void dispatchEvent(DEvent e) {
        processEvent(e);
    } 

    protected void processEvent(DEvent e) {

        if (e instanceof DMouseEvent) {
            switch(e.getID() ) {
            case DMouseEvent.MOUSE_PRESSED :
            case DMouseEvent.MOUSE_RELEASED :
            case DMouseEvent.MOUSE_ENTERED :
            case DMouseEvent.MOUSE_EXITED :
                processMouseEvent((DMouseEvent)e);
                break;

            case DMouseEvent.MOUSE_DRAGGED :
            case DMouseEvent.MOUSE_MOVED :
                processMouseMotionEvent((DMouseEvent)e);
                break;
            default : 
                break;
            }
        } else if (e instanceof DFocusEvent) {
            processFocusEvent((DFocusEvent)e);
        }
    }

    public void addMouseListener(DMouseListener l) {
        if (mouseListener == null) mouseListener = new Vector();
        if (!mouseListener.contains(l) ) {
            mouseListener.addElement(l);
        }
    }

    public void removeMouseListener(DMouseListener l) {
        if (mouseListener != null && mouseListener.contains(l) ) {
            mouseListener.removeElement(l);
        }
    }

    protected void processMouseEvent(DMouseEvent e) {
        if (mouseListener != null) {
            int count = mouseListener.size();
            for (int i = 0; i < count; i++) {
                switch(e.getID() ) {
                case DMouseEvent.MOUSE_PRESSED  :
                    ((DMouseListener)mouseListener.elementAt(i) ).mousePressed(e);
                    break;
                case DMouseEvent.MOUSE_RELEASED :
                    ((DMouseListener)mouseListener.elementAt(i) ).mouseReleased(e);
                    break;
                default :
                    break;
                }
            }
        }
    }

    protected void processMouseMotionEvent(DMouseEvent e) {

    }

    protected void processFocusEvent(DFocusEvent e) {
    }

    public String toString() {
        return getClass().getName() + "{" + paramString() + "}";
    }

    public String paramString() {
        return x + "," + y + "," + width + "x" + height;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public Point getLocation() {
        return new Point(x, y);
    }

    public void setSize(Dimension size) {
            setSize(size.width, size.height);
    }

    public void setSize(int width, int height) {
        setBounds(x, y, width, height);
    }

    public void setLocation(Point p) {
            setLocation(p.x, p.y);
    }

    public void setLocation(int x, int y) {
        setBounds(x, y, width, height);
    }

    public void setBounds(int x, int y, int width, int height) {

        synchronized(getTreeLock()) {

            Rectangle rect = getBounds();

            boolean moved   = (this.x != x || this.y != y);
            boolean resized = (this.width != width || this.height != height);

            if (moved) {
                this.x = x;
                this.y = y;
            }

            if (resized) {
                this.width  = width;
                this.height = height;
                invalidate();
            }

            if (notified && (moved || resized) ) {

                validate();

                DContainer parent = getParent();

                if (parent != null) {
                    parent.repaint(rect.x, rect.y, rect.width, rect.height);
                }            
                repaint();
            }
        }
    }

    public void paint(Graphics g) {
    }

    public void repaint() {
        repaint(0, 0, width, height);
    }

    public void repaint(int x, int y, int width, int height) {
        DPanel panel = getPanel();
        if (panel != null) {
            Point p = getLocation(null);
            panel.repaint(p.x + x, p.y + y, width, height);
        }
    }

    DPanel getPanel() {
        if (panel != null) return panel;
        return (parent != null)? parent.getPanel() : null;
    } 
}

