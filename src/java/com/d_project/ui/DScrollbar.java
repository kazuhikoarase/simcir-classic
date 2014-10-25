package com.d_project.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Vector;

import com.d_project.ui.event.DAdjustmentEvent;
import com.d_project.ui.event.DAdjustmentListener;

/**
 * DScrollbar
 * @author Kazuhiko Arase
 */
public class DScrollbar extends DContainer {
    
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL   = 1;

    DSlider scrollbarImpl;
    Vector listener;

    public DScrollbar(int orientation) {
        this(orientation, 0, 10, 0, 100);
    }

    public DScrollbar(int orientation, int value, int visible, int min, int max) {

        setLayout(new DBorderLayout(0, 0) );
        add("Center", scrollbarImpl = new DScrollbarImpl(this, orientation, value, visible, min, max) );

        DButton button;

        switch(orientation) {
        case HORIZONTAL :
            add("East", button = new DArrowButton(DArrowButton.RIGHT) );
            button.setActionCommand("unitInc");
            button.addActionListener(scrollbarImpl);

            add("West", button = new DArrowButton(DArrowButton.LEFT) );
            button.setActionCommand("unitDec");
            button.addActionListener(scrollbarImpl);
            break;

        case VERTICAL :
            add("South", button = new DArrowButton(DArrowButton.DOWN) );
            button.setActionCommand("unitInc");
            button.addActionListener(scrollbarImpl);

            add("North", button = new DArrowButton(DArrowButton.UP) );
            button.setActionCommand("unitDec");
            button.addActionListener(scrollbarImpl);
            break;
        }

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

    public Dimension getPreferredSize() {
        return new Dimension(16, 16);
    }

    public void setValue(int value) {
        scrollbarImpl.setValue(value);
    }

    public void setMinimum(int min) {
        scrollbarImpl.setMinimum(min);
    }

    public void setMaximum(int max) {
        scrollbarImpl.setMaximum(max);
    }

    public void setVisibleAmount(int visible) {
        scrollbarImpl.setVisibleAmount(visible);
    }

    public void setValues(int value, int visible, int min, int max) {
        scrollbarImpl.setValues(value, visible, min, max);
    }

    public int getValue() {
        return scrollbarImpl.getValue();
    }

    public int getOrientation() {
        return scrollbarImpl.getOrientation();
    }

    public int getMinimum() {
        return scrollbarImpl.getMinimum();
    }

    public int getMaximum() {
        return scrollbarImpl.getMaximum();
    }

    public int getVisibleAmount() {
        return scrollbarImpl.getVisibleAmount();
    }
    
    public void setUnitIncrement(int unit) {
        scrollbarImpl.setUnitIncrement(unit);
    }

    public int getUnitIncrement() {
        return scrollbarImpl.getUnitIncrement();
    }

    public void setBlockIncrement(int block) {
        scrollbarImpl.setBlockIncrement(block);
    }

    public int getBlockIncrement() {
        return scrollbarImpl.getBlockIncrement();
    }
}

/**
 * DScrollbarImpl
 */
class DScrollbarImpl extends DSlider {

    DScrollbar bar;

    public DScrollbarImpl(DScrollbar bar, int orientation, int value, int visible, int min, int max) {
        super(orientation, value, visible, min, max);
        this.bar = bar;
    }

    protected void processAdjustmentEvent(DAdjustmentEvent e) {
        getQueue().postEvent(new DAdjustmentEvent(bar, e.getID(), e.getAdjustmentType(), e.getValue() ) );
    }

    public void drawSlider(Graphics g, int x, int y, int width, int height) {

        g.setColor(getBackground() );
        g.fillRect(x, y, width, height);

        g.setColor(getForeground() );
        g.drawRect(x, y, width - 1, height - 1);

        g.setColor(getBackground() );
        g.draw3DRect(x + 1, y + 1, width - 3, height - 3, true);

    }

    public void drawBlock(Graphics g, int x, int y, int width, int height) {
        g.setColor(getForeground() );
        g.fillRect(x, y, width, height);
    }

    public void drawBackground(Graphics g, int x, int y, int width, int height) {
        g.setColor(getBackground().darker() );
        g.fill3DRect(x, y, width, height, false);
    }
}

