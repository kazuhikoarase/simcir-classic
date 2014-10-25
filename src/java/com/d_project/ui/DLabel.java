package com.d_project.ui;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;

/**
 * DLabel
 * @author Kazuhiko Arase
 */
public class DLabel extends DComponent {

    String label;

    int hgap = 2;
    int vgap = 2;

    public DLabel() {
        this(null);
    }

    public DLabel(String label) {
        this.label = label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
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
        if (label != null) {
            Dimension s = getSize();
            g.setColor(getForeground() );
            FontMetrics fm = getFontMetrics(getFont() );
            int x = (s.width - fm.stringWidth(label) ) / 2;
            int y = (s.height + fm.getMaxAscent() ) / 2;
            g.drawString(label, x, y);
        }
    }
}    
