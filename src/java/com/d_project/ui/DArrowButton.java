package com.d_project.ui;

import java.awt.Dimension;
import java.awt.Graphics;

/**
 * DArrowButton
 * @author Kazuhiko Arase
 */
public class DArrowButton extends DButton {
    
    public static final int RIGHT  = 0;
    public static final int LEFT   = 1;
    public static final int UP     = 2;
    public static final int DOWN   = 3;
/*        
    int[] x;
    int[] y;
*/
    int direction;

    public DArrowButton(int direction) {
        this.direction = direction;
    }

    public int getDirection() {
        return direction;
    }

/*
    public void setDirection(int direction) {
        this.direction = direction;
    }
*/
    public Dimension getPreferredSize() {
        return new Dimension(16, 16);
    }


    private static void rot(int[] x, int[] y, int npoints, int times) {
        for (int i = 0; i < npoints; i++) {
            for (int j = 0; j < times; j++) {
                int xx = x[i];
                x[i] = -y[i];
                y[i] = xx;
            }
        }
    }


    public void paint(Graphics g) {

        Dimension s = getSize();

        if (selectable && (pressed || selected) ) {
            g.setColor(getBackground().darker() );
        } else {
            g.setColor(getBackground() );
        }
        g.fillRect(0, 0, s.width, s.height);


        int[] x = new int[]{4, -4, -4};
        int[] y = new int[]{0,  5, -5};

        switch(direction) {
        case RIGHT : 
            break;
        case DOWN :
            rot(x, y, 3, 1);
            break;
        case LEFT : 
            rot(x, y, 3, 2);
            break;
        case UP :
            rot(x, y, 3, 3);
            break;
        }

        Dimension size = getSize();

        int[] xx = new int[3];
        int[] yy = new int[3];

        for (int i = 0; i < 3; i++) {
            xx[i] = (x[i] + 10) * size.width  / 20;
            yy[i] = (y[i] + 10) * size.height / 20;
        }

        g.setColor(getForeground() );
        g.fillPolygon(xx, yy, 3);

        g.setColor(getForeground() );
        g.drawRect(0, 0, s.width - 1, s.height - 1);
        g.setColor(getBackground() );
        g.draw3DRect(1, 1, s.width - 3, s.height - 3, !(pressed || selected) );
    }

}    
