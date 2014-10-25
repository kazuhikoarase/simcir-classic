package simcir;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

import com.d_project.ui.DComponent;
import com.d_project.ui.DContainer;

/**
 * DeviceWindow
 * @author Kazuhiko Arase
 */
final class DeviceWindow extends DContainer {

    int gap = 2;
    Device device;

    public DeviceWindow(Device device) {
        this.device = device;
    }

    public Device getDevice() {
        return device;
    }

    public void setBounds(int x, int y, int width, int height) {

        Point p = getLocation();
        p.translate(-x, -y);

        super.setBounds(x, y, width, height);

        if (isShowing() && (p.x != 0 || p.y != 0) ) {
            repaintAllWires(0, 0);
            repaintAllWires(p.x, p.y);
        }
    }

    private void repaintAllWires(int x, int y) {

        Dimension s;
        CircuitBoard cb = device.getCircuitBoard();

        InputNode[]  in  = getDevice().getInputNodes();
        for (int i = 0; i < in.length; i++) {
            OutputNode out = in[i].getOutput();
            if (out != null) {
                Point p1 = in[i].getLocation(cb);
                Point p2 = out.getLocation(cb);
                s = in[i].getSize();
                p1.translate(s.width / 2, s.height / 2);
                s = out.getSize();
                p2.translate(s.width / 2, s.height / 2);
                // rep
                p1.translate(x, y);
                p2.translate(x, y);
                cb.repaintWire(p1, p2);
                }
        }

        OutputNode[] out = getDevice().getOutputNodes();
        for (int i = 0; i < out.length; i++) {
            int count = out[i].getInputCount();
            Point p1 = out[i].getLocation(cb);
            s = out[i].getSize();
            p1.translate(s.width / 2, s.height / 2);

            for (int j = 0; j < count; j++) {
                Point p2 = out[i].getInputAt(j).getLocation(cb);
                s = out[i].getInputAt(j).getSize();
                p2.translate(s.width / 2, s.height / 2);

                // rep
                p1.translate(x, y);
                p2.translate(x, y);
                cb.repaintWire(p1, p2);
            }
        }

    }

    public boolean contains(int x, int y) {
        int count = getComponentCount();
        for (int i = 0; i < count; i++) {
            DComponent comp = getComponent(i);
            Point p = comp.getLocation();
            if (comp.contains(x - p.x, y - p.y) ) return true;
        }                
        return false;
    }

    public void doLayout() {
        int count = getComponentCount();
        Dimension s = getSize();
        int y = gap;
        for (int i = 0; i < count; i++) {
            DComponent comp = getComponent(i);
            Dimension  ps = comp.getPreferredSize();
            comp.setBounds(gap + (s.width - ps.width) / 2, y, ps.width, ps.height);
            y += ps.height;
        }                
    }

    public Dimension getPreferredSize() {
            int count = getComponentCount();
        int width  = 0;
        int height = 0;
        for (int i = 0; i < count; i++) {
            DComponent comp = getComponent(i);
            Dimension  ps = comp.getPreferredSize();
            width = Math.max(width, ps.width);
            height += ps.height;
        }                
        return new Dimension(width + gap * 2, height + gap * 2);
    }

    public void paint(Graphics g) {
        Dimension size = getSize();

        super.paint(g);
        if (device.getDADGroup() != null) {

            g.setColor(Color.black);
            g.drawRect(0, 0, size.width - 1, size.height - 1);
            int pt = 4;
            g.fillRect(0, 0, pt, pt);
            g.fillRect(size.width - pt, 0, pt, pt);
            g.fillRect(0, size.height - pt, pt, pt);
            g.fillRect(size.width - pt, size.height - pt, pt, pt);
        }
    }
}
