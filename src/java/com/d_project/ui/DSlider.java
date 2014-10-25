package com.d_project.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Vector;

import com.d_project.ui.event.DActionEvent;
import com.d_project.ui.event.DActionListener;
import com.d_project.ui.event.DAdjustmentEvent;
import com.d_project.ui.event.DAdjustmentListener;
import com.d_project.ui.event.DMouseEvent;

/**
 * DSlider
 * @author Kazuhiko Arase
 */
public class DSlider extends DComponent implements DActionListener {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL   = 1;

    DSlidebar slidebar = new DSlidebar();

    int orientation;
    int value;
    int visible;
    int min;
    int max;

    int unitIncrement  = 1;
    int blockIncrement = 10;

    boolean trackMode;
    boolean blockUp;
    boolean blockDown;

    Point lastPoint;
    Point dragPoint;

    Vector listener;

    public DSlider(int orientation) {
        this(orientation, 0, 10, 0, 100);
    }

    public DSlider(int orientation, int value, int visible, int min, int max) {
        this.min     = min;
        this.max     = max;
        this.value   = value;
        this.visible = visible;
        this.orientation = orientation;

        enableEvents(DEvent.MOUSE_EVENT_MASK | DEvent.MOUSE_MOTION_EVENT_MASK);
    }

    public void processEvent(DEvent e) {
        if (e instanceof DAdjustmentEvent) {
            processAdjustmentEvent((DAdjustmentEvent)e);
        } else {
            super.processEvent(e);
        }
    }

    public void addAdjustmentListener(DAdjustmentListener l) {
        if (listener == null) listener = new Vector();
        if (!listener.contains(l) ) {
            listener.addElement(l);
        }
    }

    public void removeAdjustmentListener(DAdjustmentListener l) {
        if (listener != null && listener.contains(l) ) {
            listener.removeElement(l);
        }
    }

    protected void processAdjustmentEvent(DAdjustmentEvent e) {
        if (listener != null) {
            int count = listener.size();
            for (int i = 0; i < count; i++) {
                ((DAdjustmentListener)listener.elementAt(i)).adjustmentValueChanged(e);
            }
        }
    }

    protected void processMouseEvent(DMouseEvent e) {

        switch(e.getID() ) {

        case DMouseEvent.MOUSE_PRESSED :
            Point pos = e.getPoint();
            trackMode = slidebar.contains(pos.x, pos.y);

            if (trackMode) {
                lastPoint = slidebar.getLocation();
                dragPoint = new Point(pos.x - lastPoint.x, pos.y - lastPoint.y);
            } else {
                Rectangle sliderRect = slidebar.getBounds();
                int xMin = sliderRect.x;
                int yMin = sliderRect.y;
                int xMax = sliderRect.x + sliderRect.width;
                int yMax = sliderRect.y + sliderRect.height;

                blockUp   = (xMax < pos.x || yMax < pos.y);
                blockDown = (pos.x < xMin || pos.y < yMin);
                
                if (blockUp)   blockIncrement(true);
                if (blockDown) blockIncrement(false);
            }
            break;

        case DMouseEvent.MOUSE_RELEASED :
            if (trackMode) {
                if (isTrackEnable(e.getPoint() )  ) {
//                    processEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, getActionCommand() ) );
                } else {
                    track(lastPoint.x, lastPoint.y);
                }

            } else {
                blockUp   = false;
                blockDown = false;
                repaint();
            }
            break;

        default :
            break;
        }

        super.processMouseEvent(e);
    }

    public Dimension getPreferredSize() {
        return new Dimension(16, 16);
    }

    public void setValue(int value) {
        setValues(value, visible, min, max);
    }

    public void setMinimum(int min) {
        setValues(value, visible, min, max);
    }
    public void setMaximum(int max) {
        setValues(value, visible, min, max);
    }
    public void setVisibleAmount(int visible) {
        setValues(value, visible, min, max);
    }

    public int getValue() {
        return value;
    }

    public int getOrientation() {
        return orientation;
    }

    public int getMinimum() {
        return min;
    }

    public int getMaximum() {
        return max;
    }

    public int getVisibleAmount() {
        return visible;
    }
    
    public void setUnitIncrement(int unitIncrement) {
        this.unitIncrement = unitIncrement;
    }

    public int getUnitIncrement() {
        return unitIncrement;
    }

    public void setBlockIncrement(int blockIncrement) {
        this.blockIncrement = blockIncrement;
    }

    public int getBlockIncrement() {
        return blockIncrement;
    }

    public synchronized void setValues(int value, int visible, int min, int max) {

        boolean changed = false;

        changed |= (this.value != value);
        this.value = value;

        changed |= (this.min != min);
        this.min = min;

        changed |= (this.max != max);
        this.max = max;

        changed |= (this.visible != visible);
        this.visible = visible;

        if (changed) {
            calcAdjustmentRect();
            repaint();
        }
    }

    private void calcAdjustmentRect() {

        int diff = max - min;
        Dimension size = getSize();

        int x = 0;
        int y = 0;
        int width  = size.width;
        int height = size.height;

        if (diff > 0) {
            switch(orientation) {

            case HORIZONTAL :
                width = Math.min(size.width, Math.max(8, visible * size.width / (diff + visible) ) );
                x     = Math.max(0, (size.width - width) * (value - min) / diff);
                break;

            case VERTICAL :
                height = Math.min(size.height, Math.max(8, visible * size.height / (diff + visible) ) );
                y      = Math.max(0, (size.height - height) * (value - min) / diff);
                break;

            default :
                break;
            }
        }

        slidebar.setBounds(x, y, width, height);        
    }

    public void doLayout() {
        calcAdjustmentRect();
    }

    protected void drawSlider(Graphics g, int x, int y, int width, int height) {

        g.setColor(getForeground() );
        g.fillRect(x, y, width, height);

        g.setColor(getBackground() );

        int len;

        switch(orientation) {
        case HORIZONTAL :
            len = width / 2;
            g.fill3DRect(x, y, len, height, true);
            g.fill3DRect(x + width - len, y, len, height, true);
            break;
        case VERTICAL :
            len = height / 2;
            g.fill3DRect(x, y, width, len, true);
            g.fill3DRect(x, y + height - len, width, len, true);
            break;
        }
    }

    public void drawBlock(Graphics g, int x, int y, int width, int height) {
        g.setColor(Color.white);
        g.fillRect(x, y, width, height);

    }

    public void drawBackground(Graphics g, int x, int y, int width, int height) {
        g.setColor(getBackground() );
        g.fillRect(x, y, width, height);
        int dw = 3;
        int gap = 0;

        switch(orientation) {
        case HORIZONTAL :
            gap =  getAdjustmentRect().width / 2;
            g.draw3DRect(x + gap, y + (height - dw) / 2, width - gap * 2 - 1, dw - 1, false);
            break;
        case VERTICAL :
            gap =  getAdjustmentRect().height / 2;
            g.draw3DRect(x + (width - dw) / 2, y + gap, dw - 1, height - gap * 2 - 1, false);
            break;
        }
    }

    public Rectangle getAdjustmentRect() {
        return slidebar.getBounds();
    }

    public void paint(Graphics g) {

        Rectangle sliderRect = slidebar.getBounds();
        Dimension size = getSize();

        drawBackground(g, 0, 0, size.width, size.height);

        drawSlider(g, sliderRect.x, sliderRect.y, sliderRect.width, sliderRect.height);

        if (blockUp) {
            switch(orientation) {
            case HORIZONTAL :
                int x = sliderRect.x + sliderRect.width;
                drawBlock(g, x, 0, size.width - x, size.height);
                break;
            case VERTICAL :
                int y = sliderRect.y + sliderRect.height;
                drawBlock(g, 0, y, size.width, size.height - y);
                break;
            } 
        }

        if (blockDown) {
            switch(orientation) {
            case HORIZONTAL :
                drawBlock(g, 0, 0, sliderRect.x, size.height);
                break;
            case VERTICAL :
                drawBlock(g, 0, 0, size.width, sliderRect.y);
                break;
            } 
        }
    }

    public void actionPerformed(DActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("unitInc") ) {
            unitIncrement(true);
        } else if (command.equals("unitDec") ) {
            unitIncrement(false);
        } else if (command.equals("blockInc") ) {
            blockIncrement(true);
        } else if (command.equals("blockDec") ) {
            blockIncrement(false);
        }
    }

    public void blockIncrement(boolean inc) {

        int value = this.value;

        if (inc) {
            value += blockIncrement;
        } else {
            value -= blockIncrement;
        }

        value = Math.min(max, Math.max(min, value) );
        boolean changed = (this.value != value);

        if (changed) {
            setValue(value);        

            getQueue().postEvent(new DAdjustmentEvent(
                this, 
                DAdjustmentEvent.ADJUSTMENT_VALUE_CHANGED,
                inc? DAdjustmentEvent.BLOCK_INCREMENT : DAdjustmentEvent.BLOCK_DECREMENT, 
                value)
            );
        }
    }

    public void unitIncrement(boolean inc) {

        int value = this.value;

        if (inc) {
            value += unitIncrement;
        } else {
            value -= unitIncrement;
        }

        value = Math.min(max, Math.max(min, value) );
        boolean changed = (this.value != value);

        if (changed) {
            setValue(value);        

            getQueue().postEvent(new DAdjustmentEvent(
                this, 
                DAdjustmentEvent.ADJUSTMENT_VALUE_CHANGED,
                inc? DAdjustmentEvent.UNIT_INCREMENT : DAdjustmentEvent.UNIT_DECREMENT, 
                value)
            );
        }
    }

    private void track(int x, int y) {

        Dimension sliderSize = slidebar.getSize();
        Dimension size = getSize();
        int value = 0;

        switch(orientation) {
        case HORIZONTAL :
            if (size.width != sliderSize.width) {
                value = (max - min) * x / (size.width  - sliderSize.width) + min;
            }
            break;

        case VERTICAL :
            if (size.height != sliderSize.height) {
                value = (max - min) * y / (size.height - sliderSize.height) + min;
            }
            break;
        default :
            break;
        }

        value = Math.min(max, Math.max(min, value) );
        boolean changed = (this.value != value);

        if (changed) {
            setValue(value);        

            getQueue().postEvent(new DAdjustmentEvent(
                this, 
                DAdjustmentEvent.ADJUSTMENT_VALUE_CHANGED,
                DAdjustmentEvent.TRACK, 
                value)
            );
        }
    }

    public boolean isTrackEnable(Point pos) {
//        return contains(pos);
        return true;
    }

    protected void processMouseMotionEvent(DMouseEvent e) {

        switch(e.getID() ) {

        case DMouseEvent.MOUSE_DRAGGED :
            if (trackMode) {
                if (isTrackEnable(e.getPoint() )  ) {
                    track(e.getX() - dragPoint.x, e.getY() - dragPoint.y);
                } else {
                    track(lastPoint.x, lastPoint.y);
                }
            }
            break;

        default : 
            break;
        }
    }

}    

