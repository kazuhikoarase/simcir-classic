package com.d_project.ui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * DRegion
 * @author Kazuhiko Arase
 */
class DRegion {

    int xMin;
    int xMax;
    int yMin;
    int yMax;

    public DRegion() {
        this(0, 0, 0, 0);
    }

    public DRegion(Rectangle rect) {
        this(rect.x, rect.y, rect.width, rect.height);
    }

    public DRegion(int x, int y, int width, int height) {
        xMin = x;
        yMin = y;
        xMax = x + width;
        yMax = y + height;
    }

    public DRegion union(Rectangle rect) {
        return union(new DRegion(rect) );
    }

    public DRegion union(DRegion reg) {
        int xMin = Math.max(this.xMin, reg.xMin);
        int xMax = Math.min(this.xMax, reg.xMax);
        int yMin = Math.max(this.yMin, reg.yMin);
        int yMax = Math.min(this.yMax, reg.yMax);
        return (xMin < xMax && yMin < yMax)? 
            new DRegion(xMin, xMax - xMin, yMin, yMax - yMin) : null;
    }

    public void translate(int x, int y) {
        xMin += x;
        xMax += x;
        yMin += y;
        yMax += y;
    }

    public Rectangle getBounds() {
        return new Rectangle(xMin, yMin, xMax - xMin, yMax - yMin);
    }

    public void expand(Point p) {
        expand(p.x, p.y);
    }

    private void expand(int x, int y) {
        expand(x, y, x + 1, y + 1);
    }

    public void expand(Rectangle rect) {
        expand(rect.x, rect.y, rect.width, rect.height);
    }

    public void expand(Point p, Dimension s) {
        expand(p.x, p.y, s.width, s.height);
    }

    public void expand(int x, int y, int width, int height) {
        expandImpl(x, y);
        expandImpl(x + width, y + height);
    }

    private void expandImpl(int x, int y) {
        if (x < xMin) xMin = x;
        if (x > xMax) xMax = x;
        if (y < yMin) yMin = y;
        if (y > yMax) yMax = y;
    }
}
