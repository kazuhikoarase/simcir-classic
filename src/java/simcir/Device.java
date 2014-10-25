package simcir;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TextField;

import com.d_project.ui.DADComponent;
import com.d_project.ui.DADGroup;
import com.d_project.ui.DComponent;
import com.d_project.ui.DEvent;
import com.d_project.ui.DLabel;
import com.d_project.ui.event.DMouseEvent;
import com.d_project.ui.event.DMouseListener;
import com.d_project.xml.Element;

/**
 * Device
 * @author Kazuhiko Arase
 */
public abstract class Device extends DADComponent 
implements InputListener {

    private static String nameBase  = "dev";
    private static int    nameCount = 0;

    protected InputNode[]  in;
    protected OutputNode[] out;

    DeviceWindow deviceWindow;
    DLabel       label;

    String type;
        
    boolean focus;
    boolean active = true;

    protected Device(int inCount, int outCount) {

        setName(nameBase + nameCount++);
        setBackground(new Color(0xffcccccc) );

        in  = new InputNode[inCount];
        out = new OutputNode[outCount];

        for (int i = 0; i < in.length; i++) {
            add(in[i] = new InputNode(this) );
            in[i].setName("in" + i);
            in[i].addInputListener(this);
        }
        for (int i = 0; i < out.length; i++) {
            add(out[i] = new OutputNode(this) );
            out[i].setName("out" + i);
        }

        deviceWindow = new DeviceWindow(this);
        deviceWindow.add(this);
        deviceWindow.add(label = new DLabel() );

        label.enableEvents(DEvent.MOUSE_EVENT_MASK);
        label.addMouseListener(new DMouseListener() {
            public void mousePressed(DMouseEvent e) {
                if (e.getClickCount() == 2 && isOnCircuitBoard() ) {
                    Frame f = new LabelEditWindow();
                    f.pack();
                    f.setVisible(true);                        
                }
            }
            public void mouseReleased(DMouseEvent e) {
            }
        });
    }

    protected void setType(String type) {
        this.type = type;
    }
                
    private class LabelEditWindow extends Frame implements java.awt.event.ActionListener {
        
        TextField field;

        public LabelEditWindow() {
            super("");
            Panel panel = new Panel();
            Button ok = new Button("OK");
            Button cancel = new Button("CANCEL");
            panel.add(ok);
            panel.add(cancel);
            this.add("Center", field = new TextField(getLabel() ) );
            this.add("South", panel);
            ok.addActionListener(this);
            cancel.addActionListener(this);
        }

        public void actionPerformed(java.awt.event.ActionEvent e) {
            if (e.getActionCommand().equals("OK") ) {

                setLabel(field.getText() );

                DeviceWindow deviceWindow = getDeviceWindow();
                Rectangle r = deviceWindow.getBounds();
                Dimension ps = deviceWindow.getPreferredSize();
                deviceWindow.setBounds(
                    r.x + (r.width  - ps.width) / 2,
                    r.y + (r.height - ps.height) / 2,
                    ps.width, ps.height
                );
                label.invalidate();
                getCircuitBoard().validate();
                getCircuitBoard().repaint();
            }
            this.dispose();
        }
    }

    public abstract Device createClone();

    public InputNode getInputNode(String node) {
        for (int i = 0; i < in.length; i++) {
            if (in[i].getName().equals(node) ) return in[i];
        }
        return null;
    }

    public OutputNode getOutputNode(String node) {
        for (int i = 0; i < out.length; i++) {
            if (out[i].getName().equals(node) ) return out[i];
        }
        return null;
    }

    public InputNode[] getInputNodes() {
        return in;
    }

    public OutputNode[] getOutputNodes() {
        return out;
    }

    public CircuitBoard getCircuitBoard() {
        DComponent comp = getParent();
        while (comp != null && !(comp instanceof CircuitBoard) ) {
            comp = comp.getParent();
        }
        return (CircuitBoard)comp;
    }

    public String getType() {
        return type;
    }

    public Element createElement() {

        Element e = new Element("device", false);
        e.setAttribute("name", getName() );
        e.setAttribute("type", getType() );
        e.setAttribute("label", getLabel() );
        Point p = deviceWindow.getLocation();
        e.setAttribute("location", p.x + "," + p.y);

        for (int c = 0; c < in.length; c++) {
            Element ne = new Element("node");
            ne.setAttribute("name", in[c].getName() );
            ne.setAttribute("type", "input");
            OutputNode out = in[c].getOutput();
            if (out != null) {
                String con = out.getDevice().getName() + "." + out.getName();                
                ne.setAttribute("connect", con);
            }
            e.addElement(ne);
        }

        for (int c = 0; c < out.length; c++) {
            Element ne = new Element("node");
            ne.setAttribute("name", out[c].getName() );
            ne.setAttribute("type", "output");
            e.addElement(ne);
        }
        return e;
    }


    public void dadDrag() {
        if (getDADGroup() == null) {
            CircuitBoard cb = getCircuitBoard();
            cb.deselectAll();
            cb.select(this, true);
            deviceWindow.toFront();
            requestFocus();
        }
        if (!isActive() ) {
            // Create Clone Device.
            Device dev = createClone();
            getCircuitBoard().getToolbox().replaceDevice(this, dev);
        }
    }

    public void dadDrop() {
        if (isOnCircuitBoard() ) {
            setActive(true);
            relocateDevice();
        } else {
            dispose();
        }
    }
    public boolean isOnCircuitBoard() {
        int ts = getCircuitBoard().getToolboxWidth();
        Dimension size = getSize();
        Point     pos  = getLocation(getCircuitBoard() );
        return (pos.x + size.width / 2 > ts);
    }

    public void relocateDevice() {
        CircuitBoard cb = getCircuitBoard();
        if (cb != null) {
            Rectangle rect = cb.getBounds();
            Rectangle crect = deviceWindow.getBounds();
            crect.x = Math.max(cb.getToolboxWidth() + cb.getScrollbarWidth(), 
                Math.min(crect.x, rect.width - crect.width) ); 
            crect.y = Math.max(0, 
                Math.min(crect.y, rect.height - crect.height) ); 
            deviceWindow.setLocation(crect.x, crect.y);
        }
    }


    public void setActive(boolean active) {
        this.active = active;
        for (int i = 0; i < in.length; i++) {
            in[i].setActive(active);
        }
        for (int i = 0; i < out.length; i++) {
            out[i].setActive(active);
        }
    }
    
    public boolean isActive() {
        return active;
    }

    public void dispose() {
        
        for (int i = 0; i < in.length; i++) {
            in[i].disconnect();
        }
        for (int i = 0; i < out.length; i++) {
            int count = out[i].getInputCount();
            for (int j = 0; j < count; j++) {
                out[i].getInputAt(0).disconnect();
            }
        }

        DADGroup group = getDADGroup();
        if (group != null) group.remove(this);

        CircuitBoard cb = getCircuitBoard();
        cb.remove(deviceWindow);
        cb.repaint();
    }

    public DComponent getDADTarget() {
        return deviceWindow;
    }

    public DeviceWindow getDeviceWindow() {
        return deviceWindow;
    }

    public void setLabel(String name) {
        label.setLabel(name);
    }

    public String getLabel() {
            return label.getLabel();
    }

    public final void inputValueChanged(InputEvent e) {
        processInputEvent(e);
    }

    protected void processInputEvent(InputEvent e) {
    }

    public void doLayout() {
        Dimension size = getSize();

        for (int i = 0; i < in.length; i++) {
            int y = size.height * (i * 2 + 1) / (in.length * 2);
            in[i].setBounds(0, y - 4, 8, 8);
        }

        for (int i = 0; i < out.length; i++) {
            int y = size.height * (i * 2 + 1) / (out.length * 2);
            out[i].setBounds(size.width - 8, y - 4, 8, 8);
        }
    }

    public void paint(Graphics g) {

        Dimension size = getSize();

        g.setColor(getBackground() );
        g.fill3DRect(1 + 4, 1, size.width - 2 - 8, size.height - 2, true);
        
        g.setColor(getBackground() );
        g.draw3DRect(1 + 6, 3, size.width - 2 - 13, size.height - 7, false);
        
        g.setColor(getForeground() );
        g.drawRect(0 + 4, 0, size.width - 1 - 8, size.height - 1);

        super.paint(g);
    }
}

