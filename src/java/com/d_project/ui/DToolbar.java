package com.d_project.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.util.Vector;

import com.d_project.ui.event.DActionEvent;
import com.d_project.ui.event.DActionListener;
import com.d_project.ui.event.DMouseEvent;

/**
 * DToolbar
 * @author Kazuhiko Arase
 */
public class DToolbar extends DComponent {

    DToolbarItem[] item;

    Vector listener;

    int pressedIndex = -1;
    int enteredIndex = -1;

    Image image;

    public DToolbar(String[] command) {

        item = new DToolbarItem[command.length];
        for (int i = 0; i < item.length; i++) {
            item[i] = new DToolbarItem(command[i]);
        }

        enableEvents(DEvent.MOUSE_EVENT_MASK | DEvent.MOUSE_MOTION_EVENT_MASK);
    }

    public int indexOf(String command) {
        for (int i = 0; i < item.length; i++) {
            if (item[i].command.equals(command) ) return i;
        }
        return -1;
    }

    public void setImage(Image image) {
        this.image = image;
        repaint();
    }

    public void processEvent(DEvent e) {
        if (e instanceof DActionEvent) {
            processActionEvent((DActionEvent)e);
        } else {
            super.processEvent(e);
        }
    }

    public void setEnable(boolean enable, String command) {
        int index = indexOf(command);
        if (index != -1) setEnable(enable, index);
    }

    public void setEnable(boolean enable, int index) {
        if (item[index].enable != enable) {
            item[index].enable = enable;
            repaint();
        }
    }

    public boolean isEnable(int index) {
        return item[index].enable;
    }

    public int getItemCount() {
        return item.length;
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
//        return super.getPreferredSize();
        return new Dimension(32, 32);
    }


    protected void processMouseMotionEvent(DMouseEvent e) {
        switch(e.getID() ) {
        case DMouseEvent.MOUSE_MOVED :
            enteredIndex = getIndex(e.getPoint() );
            repaint();
            break;
        default :
            break;
        }
    }

    protected void processMouseEvent(DMouseEvent e) {

        switch(e.getID() ) {
        case DMouseEvent.MOUSE_EXITED :
            enteredIndex = -1;
            repaint();
            break;

        case DMouseEvent.MOUSE_ENTERED :
            enteredIndex = getIndex(e.getPoint() );
            repaint();
            break;

        case DMouseEvent.MOUSE_PRESSED :
//            pressed = true;
            pressedIndex = getIndex(e.getPoint() );
            repaint();
            break;

        case DMouseEvent.MOUSE_RELEASED :
            pressedIndex = -1;
            int index = getIndex(e.getPoint() );
            if (index >= 0 && isEnable(index) ) {
//                if (selectable) selected = !selected;
                getQueue().postEvent(
                    new DActionEvent(
                        this, 
                        DActionEvent.ACTION_PERFORMED, 
                        item[index].command
                    )
                );
            }
            repaint();
            break;

        default :
            break;
        }

        super.processMouseEvent(e);
    }

    public void paint(Graphics g) {

        Dimension s = getSize();
        g.setColor(getBackground() );
        g.fillRect(0, 0, s.width, s.height);
        
        int count = getItemCount();

        int iw = (image == null)? 0 : image.getWidth(null) / count;
        int ih = (image == null)? 0 : image.getHeight(null);

        for (int i = 0; i < count; i++) {

            int x = s.width * i / count;
            int w = s.width * (i + 1) / count - x;

            Graphics gg = g.create(x, 0, w, s.height);

            if (image != null) {
                Graphics ig = gg.create( (w - iw) / 2, (s.height - ih) / 2, iw, ih);
                ig.drawImage(image, -iw * i, 0, null);
                ig.dispose();
            }

            if (isEnable(i) ) {
                if (enteredIndex == i) {
                    gg.setColor(getBackground() );
                    gg.draw3DRect(0, 0, w - 1, s.height - 1, (pressedIndex != i) );
                } 
            } else {
                int dh = s.height - 1;
                gg.setColor(getBackground() );
                for (int xd = -dh; xd < w; xd += 2) {
                    gg.drawLine(xd, 0, xd + dh, dh);
                }
            }
            gg.dispose();
        }
    }

    private int getIndex(Point p) {
        Dimension s = getSize();
        int count = getItemCount();
        int index = p.x * count / s.width;
        return (0 <= index && index < count &&        
            0 <= p.y && p.y < s.height)? index : -1;
    }
}    

class DToolbarItem {

    String command;
    boolean enable;

    public DToolbarItem(String command) {
        this.command = command;
        enable = true;
    }
}
