package com.d_project.ui;

import java.util.Vector;

/**
 * DADGroup
 * @author Kazuhiko Arase
 */
public class DADGroup {

    Vector group = new Vector();

    public DADGroup() {
    }

    public synchronized void add(DADComponent comp) {
        if (comp.group == this) return;
        if (comp.group != null) comp.group.remove(comp);
        group.addElement(comp);
        comp.group = this;
    }

    public synchronized void remove(DADComponent comp) {
        if (comp.group == this) {
            group.removeElement(comp);
            comp.group = null;
        }
    }

    public boolean isEmpty() {
        return (group.size() == 0);
    }


    public synchronized boolean contains(DADComponent comp) {
        return (comp.group == this);
    }

    public synchronized void removeAll() {
        int count = group.size();
        for (int i = 0; i < count; i++) {
            remove((DADComponent)group.elementAt(0) );
        }
    }

    public void drag() {
        DADComponent[] comps = getComponents();
        for (int i = 0; i < comps.length; i++) {
            comps[i].dadDrag();
        }
    }

    public void drop() {
        DADComponent[] comps = getComponents();
        for (int i = 0; i < comps.length; i++) {
            comps[i].dadDrop();
        }
    }

    public void move(int dx, int dy) {
        DADComponent[] comps = getComponents();
        for (int i = 0; i < comps.length; i++) {
            comps[i].dadMove(dx, dy);
        }
    }

    public synchronized DADComponent[] getComponents() {
        DADComponent[] comps = new DADComponent[group.size()];
        group.copyInto(comps);
        return comps;
    }

}    
