package simcir;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

import com.d_project.ui.DComponent;
import com.d_project.ui.DEvent;
import com.d_project.ui.event.DMouseEvent;

/**
 * Node
 * @author Kazuhiko Arase
 */
abstract class Node extends DComponent {

    static final Object NODE_LOCK = new Object();

    Device  device;
    double  value  = Double.NaN;
    boolean active = true;
    boolean enter  = false;

    public Node(Device device) {
        this.device = device;
        enableEvents(DEvent.MOUSE_EVENT_MASK | DEvent.MOUSE_MOTION_EVENT_MASK);
        setSize(8, 8);
    }

    public Device getDevice() {
        return device;
    }

    public void setActive(boolean active) {
        this.active = active;
        if (active) {
            enableEvents(DEvent.MOUSE_EVENT_MASK | DEvent.MOUSE_MOTION_EVENT_MASK);
        } else {
            disableEvents(DEvent.MOUSE_EVENT_MASK | DEvent.MOUSE_MOTION_EVENT_MASK);
        }
    }

    public boolean isActive() {
        return active;
    }

    CircuitBoard getCircuitBoard() {
        return device.getCircuitBoard();
    }

    public void repaintLine(Node n) {

        CircuitBoard cb = getCircuitBoard();

        if (cb != null) {
            Dimension s;

            Point p1 = getLocation(cb);
            s = getSize();
            p1.translate(s.width / 2, s.height / 2);

            Point p2 = n.getLocation(cb);
            s = n.getSize();
            p2.translate(s.width / 2, s.height / 2);

            cb.repaintWire(p1, p2);
        }
    }

    protected void processMouseEvent(DMouseEvent e) {
        switch(e.getID() ) {

        case DMouseEvent.MOUSE_ENTERED :
            enter = true;
            repaint();
            break;

        case DMouseEvent.MOUSE_EXITED :
            enter = false;
            repaint();
            break;

        case DMouseEvent.MOUSE_PRESSED :

            if (this instanceof InputNode) {
                ((InputNode)this).disconnect();
            }
            getCircuitBoard().setDragNode(this, null);
            break;

        case DMouseEvent.MOUSE_RELEASED :
            CircuitBoard cb = getCircuitBoard();
            Point p   = e.getPoint();
            Point pos = getLocation(cb);
            pos.x += p.x;
            pos.y += p.y;
            DComponent comp = cb.getComponentAt(pos);
            if (this instanceof OutputNode && comp instanceof InputNode) {
                if ( ((InputNode)comp).isActive() ) {
                    ((OutputNode)this).connect((InputNode)comp);
                }
            } else if (comp instanceof OutputNode && this instanceof InputNode) {
                if ( ((OutputNode)comp).isActive() ) {
                    ((OutputNode)comp).connect((InputNode)this);
                }
            }
            getCircuitBoard().setDragNode(null, null);
            break;

        default :
            break;
        }
    }

    protected void processMouseMotionEvent(DMouseEvent e) {

        switch(e.getID() ) {

        case DMouseEvent.MOUSE_DRAGGED :
            CircuitBoard cb = getCircuitBoard();
            Point p   = e.getPoint();
            Point pos = getLocation(cb);
            pos.x += p.x;
            pos.y += p.y;
            cb.setDragNode(this, pos);
            break;

        default :
            break;
        }
    }

    public double getValue() {
        return value;
    }

    public static Object getNodeLock() {
        return NODE_LOCK;
    }

    public void paint(Graphics g) {
        super.paint(g);
        Dimension size = getSize();
        g.setColor(getBackground() );
        g.fill3DRect(1, 1, size.width - 2, size.height - 2, true);

//        g.setColor(getForeground() );
//        g.drawRect(0, 0, size.width - 1, size.height - 1);
        g.setColor(enter? Color.yellow : getForeground() );
        g.drawRect(0, 0, size.width - 1, size.height - 1);
    }
}
