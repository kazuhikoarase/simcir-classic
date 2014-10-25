package com.d_project.ui;

import java.awt.Dimension;

/**
 * DBorderLayout
 * @author Kazuhiko Arase
 */
class DBorderLayout implements DLayoutManager {

    int hgap;
    int vgap;

    DComponent center;
    DComponent north;
    DComponent south;
    DComponent east;
    DComponent west;

    public DBorderLayout() {
        this(0, 0);
    }

    public DBorderLayout(int hgap, int vgap) {
        this.hgap = hgap;
        this.vgap = vgap;
    }

    public void addLayoutComponent(String name, DComponent comp) {

        name = (name != null)? name : "Center";

        if (name.equals("South") ) {
            south = comp;
        } else if (name.equals("North") ) {
            north = comp;
        } else if (name.equals("East") ) {
            east  = comp;
        } else if (name.equals("West") ) {
            west  = comp;
        } else if (name.equals("Center") ) {
            center = comp;
        } else {
            center = comp;
        }
    }

    public void removeLayoutComponent(DComponent comp) {
        if (comp == south) {
            south = null;
        } else if (comp == north) {
            north = null;
        } else if (comp == east) {
            east  = null;
        } else if (comp == west) {
            west  = comp;
        } else if (comp == center) {
            center = null;
        }
    }

    public Dimension preferredLayoutSize(DContainer cont) {
        return new Dimension(0, 0);
    }


    public Dimension minimumLayoutSize(DContainer cont) {
        return new Dimension(0, 0);
    }

    public void layoutContainer(DContainer cont) {

//        Insets insets  = cont.getInsets();
        Dimension size = cont.getSize();

        int barWidth  = size.height;
        int barHeight = size.width;

        int x = hgap;
        int y = vgap;
        int width  = Math.max(0, size.width  - hgap * 2);
        int height = Math.max(0, size.height - vgap * 2);
        
        barWidth  = Math.min(barWidth,  width  / 2);
        barHeight = Math.min(barHeight, height / 2);

        if (north != null && north.isVisible() ) {
            north.setBounds(x, y, width, barHeight);
            y      += barHeight;
            height -= barHeight;
        }

        if (south != null && south.isVisible() ) {
            south.setBounds(x, y + height - barHeight, width, barHeight);
            height -= barHeight;
        }

        if (west != null && west.isVisible() ) {
            west.setBounds(x, y, barWidth, height);
            x     += barWidth;
            width -= barWidth;
        }

        if (east != null && east.isVisible() ) {
            east.setBounds(x + width - barWidth, y, barWidth, height);
            width -= barWidth;
        }

        if (center != null && center.isVisible() ) {
            center.setBounds(x, y, width,height);
        }

    }
}
