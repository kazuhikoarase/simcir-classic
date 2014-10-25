package com.d_project.ui;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Vector;

import com.d_project.ui.event.DActionEvent;
import com.d_project.ui.event.DActionListener;
import com.d_project.ui.event.DMouseEvent;

/**
 * DButton
 * @author Kazuhiko Arase
 */
public class DButton extends DComponent {

    boolean selectable;
    boolean selected;
    boolean pressed;

    String label;
    String command;

    int hgap = 4;
    int vgap = 4;

    Vector listener;

    public DButton() {
        this(null, false);
    }

    public DButton(String label, boolean selectable) {
        this.label = label;
        this.selectable = selectable;
        enableEvents(DEvent.MOUSE_EVENT_MASK);
    }

/*
    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }
*/
    public void setActionCommand(String command) {
        this.command = command;
    }

    public String getActionCommand() {
        return command;
    }

    public boolean isPressed() {
        return pressed;
    }

    public boolean isSelected() {
        return selected;
    }

    public void processEvent(DEvent e) {
        if (e instanceof DActionEvent) {
            processActionEvent((DActionEvent)e);
        } else {
            super.processEvent(e);
        }
    }

    public void addActionListener(DActionListener l) {
        if (listener == null) listener = new Vector();
        if (!listener.contains(l) ) {
            listener.addElement(l);
        }
    }

    public void removeActionListener(DActionListener l) {
        if (listener != null && listener.contains(l) ) {
            listener.removeElement(l);
        }
    }

    protected void processActionEvent(DActionEvent e) {
        if (listener != null) {
            int count = listener.size();
            for (int i = 0; i < count; i++) {
                ((DActionListener)listener.elementAt(i)).actionPerformed(e);
            }
        }
    }

    public Dimension getPreferredSize() {
        if (label != null) {
            FontMetrics fm = getFontMetrics(getFont() );
            int w = fm.stringWidth(label) + hgap * 2;
            int h = fm.getMaxAscent() + fm.getMaxDescent() + vgap * 2;
            return new Dimension(w, h);
        }
        return super.getPreferredSize();
    }

    public void paint(Graphics g) {

        Dimension s = getSize();

        if (selectable && (pressed || selected) ) {
            g.setColor(getBackground().darker() );
        } else {
            g.setColor(getBackground() );
        }
        g.fillRect(0, 0, s.width, s.height);

        if (label != null) {
            g.setColor(getForeground() );
            FontMetrics fm = getFontMetrics(getFont() );
            int x = (s.width - fm.stringWidth(label) ) / 2;
            int y = (s.height + fm.getMaxAscent() ) / 2;
            g.drawString(label, x, y);
        }

        g.setColor(getForeground() );
        g.drawRect(0, 0, s.width - 1, s.height - 1);
        g.setColor(getBackground() );
        g.draw3DRect(1, 1, s.width - 3, s.height - 3, !(pressed || selected) );
    }

    protected void processMouseEvent(DMouseEvent e) {

        switch(e.getID() ) {

        case DMouseEvent.MOUSE_PRESSED :
            pressed = true;
            repaint();
            break;

        case DMouseEvent.MOUSE_RELEASED :
            pressed = false;
            if (contains(e.getPoint() ) ) {
                if (selectable) selected = !selected;
                getQueue().postEvent(new DActionEvent(this, DActionEvent.ACTION_PERFORMED, (command != null)? command : label) );
            }
            repaint();
            break;

        default :
            break;
        }

        super.processMouseEvent(e);
    }
}    
