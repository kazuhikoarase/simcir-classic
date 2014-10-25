package com.d_project.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Vector;

/**
 * DContainer
 * @author Kazuhiko Arase
 */
public class DContainer extends DComponent {

    Vector comps = new Vector();
    DLayoutManager layout;

    public DContainer() {
    }

    public void addNotify() {
        synchronized(getTreeLock()) {
            super.addNotify();
            int count = getComponentCount();
            for (int i = 0; i < count; i++) {
                getComponent(i).addNotify();
            }
        }
    }

    public void removeNotify() {
        synchronized(getTreeLock()) {
            int count = getComponentCount();
            for (int i = 0; i < count; i++) {
                getComponent(i).removeNotify();
            }
            super.removeNotify();
        }
    }

    public void validate() {
        synchronized(getTreeLock() ) {
            if (!valid) {
                doLayout();
                validateTree();            
                valid = true;
            }
        }
    }

    private void validateTree() {
        int count = getComponentCount();
        for (int i = 0; i < count; i++) {
            DComponent comp = getComponent(i);
            if (!comp.valid) comp.validate();
        }
    }

    public void setLayout(DLayoutManager layout) {
        this.layout = layout;
    }

    public void doLayout() {
        if (layout != null) layout.layoutContainer(this);
    }
    
    public Dimension getPreferredSize() {
        return (layout != null)? layout.preferredLayoutSize(this) : super.getPreferredSize();
    }

    public DComponent getComponentAt(int x, int y) {
        synchronized(getTreeLock()) {
            if (visible && contains(x, y) ) {
                int count = getComponentCount();
                for (int i = 0; i < count; i++) {
                    DComponent comp = getComponent(i);
                    DComponent c = comp.getComponentAt(x - comp.x, y - comp.y);
                    if (c != null) return c;
                }
                return this;
            } else {
                return null;
            }
        }
    }

    public void add(DComponent comp) {
        addImpl(null, comp, getComponentCount() );
    }

    public void add(DComponent comp, int i) {
        addImpl(null, comp, i);
    }

    public void add(String name, DComponent comp) {
        addImpl(name, comp, getComponentCount() );
    }

    private void addImpl(String name, DComponent comp, int i) {
        synchronized(getTreeLock()) {
            if (comp.parent != null) comp.parent.remove(this);
            comps.insertElementAt(comp, i);
            if (layout != null) layout.addLayoutComponent(name, comp);
            comp.parent = this;
            if (notified) comp.addNotify();
            invalidate();
        }
    }

    public void remove(DComponent comp) {
        synchronized(getTreeLock()) {
            if (comps.contains(comp) ) {
                if (notified) comp.removeNotify();
                comps.removeElement(comp);
                if (layout != null) layout.removeLayoutComponent(comp);
                comp.parent = null;
                invalidate();
            }
        }
    }

    public int indexOf(DComponent comp) {
        return comps.indexOf(comp);
    }

    public int getComponentCount() {
        return comps.size();
    }

    public DComponent getComponent(int index) {
        return (DComponent)comps.elementAt(index);
    }

    public void paint(Graphics g) {
        synchronized(getTreeLock()) {
//            Rectangle clipRect = g.getClipRect();
            Rectangle clipRect = g.getClipBounds();
            DRegion region = new DRegion(
                (clipRect != null)? clipRect : 
                new Rectangle(0, 0, width, height)
            );

            int count = getComponentCount();

            for (int i = count - 1; i >= 0; i--) {

                DComponent comp = getComponent(i);
                Rectangle cr = comp.getBounds();
                boolean rectEnable = (region.union(cr) != null);

                if (rectEnable && comp.isVisible() ) {
                    Graphics gg = g.create(comp.x, comp.y, comp.width, comp.height);
                    comp.paint(gg);
                    gg.dispose();
                }
            }

        }
    }
}
