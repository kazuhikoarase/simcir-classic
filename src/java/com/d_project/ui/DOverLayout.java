package com.d_project.ui;

import java.awt.Dimension;

/**
 * DOverLayout
 * @author Kazuhiko Arase
 */
public class DOverLayout implements DLayoutManager {

    int vgap;
    int hgap;
    
    public DOverLayout(int hgap, int vgap) {
        this.hgap = hgap;
        this.vgap = vgap;
    }
    
    public void addLayoutComponent(String name, DComponent comp) {
    }
    
    public void removeLayoutComponent(DComponent comp) {
    }

    public Dimension preferredLayoutSize(DContainer cont) {
        int width  = 0;
        int height = 0;
        int count =  cont.getComponentCount();
        for (int i = 0; i < count; i++) {
            Dimension s = cont.getComponent(i).getPreferredSize();
            width  = Math.max(width,  s.width);
            height = Math.max(height, s.height);
            }
        return new Dimension(width + hgap * 2, height + vgap * 2);
    }

    public void layoutContainer(DContainer cont) {
        Dimension size = cont.getSize();
        int count = cont.getComponentCount();
        size.width  = Math.max(0, size.width  - hgap * 2);
        size.height = Math.max(0, size.height - vgap * 2);
        for (int i = 0; i < count; i++) {
            cont.getComponent(i).setBounds(hgap, vgap, size.width, size.height);
            }
    }
}
