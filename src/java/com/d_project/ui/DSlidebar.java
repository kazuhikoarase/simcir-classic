package com.d_project.ui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * DSlidebar
 * @author Kazuhiko Arase
 */
class DSlidebar {

    int x;
    int y;
    int width;
    int height;

    public Point getLocation() {
        return new Point(x, y);
    }

    public Dimension getSize() {
        return new Dimension(width, height);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    boolean contains(int x, int y) {
        x -= this.x;
        y -= this.y;
        return (0 <= x && x < width && 0 <= y && y < height);    
    }

    public void setBounds(int x, int y, int width, int height) {
        this.x        = x;
        this.y        = y;
        this.width  = width;
        this.height = height;
    }
}
