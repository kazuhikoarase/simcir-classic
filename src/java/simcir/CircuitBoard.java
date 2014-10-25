package simcir;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import com.d_project.ui.DADComponent;
import com.d_project.ui.DADGroup;
import com.d_project.ui.DComponent;
import com.d_project.ui.DContainer;
import com.d_project.ui.DEvent;
import com.d_project.ui.DScrollbar;
import com.d_project.ui.DToolbar;
import com.d_project.ui.event.DAdjustmentEvent;
import com.d_project.ui.event.DAdjustmentListener;
import com.d_project.ui.event.DMouseEvent;
import com.d_project.xml.Element;

/**
 * CircuitBoard
 * @author Kazuhiko Arase
 */
public class CircuitBoard extends DContainer implements DAdjustmentListener {

    DeviceFactory factory = new DeviceFactory();

    private DADGroup dadGroup;

    Selector selector;

    int tboxWidth = 100;
    int sbarWidth = 20;

    Toolbox    toolbox;
    DScrollbar scrollbar;
    DToolbar   toolbar;


    Point currPoint;
    Node  dragNode;

    Point selPos1;
    Point selPos2;

    CircuitDocument copyBuffer;
        
    public CircuitBoard() {

        enableEvents(
            DEvent.MOUSE_EVENT_MASK | 
            DEvent.MOUSE_MOTION_EVENT_MASK
        );

        setBackground(Color.white);

        dadGroup  = new DADGroup();
        scrollbar = new DScrollbar(DScrollbar.VERTICAL);
        selector  = new Selector();

        add(scrollbar);
        add(selector);

        scrollbar.setBackground(Color.lightGray);
        scrollbar.addAdjustmentListener(this);


        toolbox = new Toolbox(this);
        toolbox.addDevice(new LED() );
        toolbox.addDevice(new DirectCurrentSource(5) );
        toolbox.addDevice(new Switch(Switch.PUSH_ON)  );
        toolbox.addDevice(new Switch(Switch.PUSH_OFF) );
        toolbox.addDevice(new Switch(Switch.TOGGLE) );
        toolbox.addDevice(new LogicGate(LogicGate.NAND) );
        toolbox.addDevice(new LogicGate(LogicGate.AND)  );
        toolbox.addDevice(new LogicGate(LogicGate.NOR)  );
        toolbox.addDevice(new LogicGate(LogicGate.OR)   );
        toolbox.addDevice(new LogicGate(LogicGate.ENOR) );
        toolbox.addDevice(new LogicGate(LogicGate.EOR)  );
        toolbox.addDevice(new LogicGate(LogicGate.NOT)  );
    }

    protected void processMouseEvent(DMouseEvent e) {

        switch(e.getID() ) {

        case DMouseEvent.MOUSE_PRESSED :
            requestFocus();
            selector.toFront();
            selPos1 = e.getPoint();
            selPos2 = null;
            updateSelector();
            updateDADGroup();
            break;

        case DMouseEvent.MOUSE_RELEASED :
            selPos1 = null;
            selPos2 = null;
            updateSelector();
            break;
        }
    }

    protected void processMouseMotionEvent(DMouseEvent e) {
        switch(e.getID() ) {
        case DMouseEvent.MOUSE_DRAGGED :
            selPos2 = e.getPoint();
            updateSelector();
            updateDADGroup();
            break;
        }
    }

    void updateDADGroup() {
        Rectangle r = selector.getBounds();
        Device[] device = getActiveDevices();
        for (int i = 0; i < device.length; i++) {
            Rectangle rect = device[i].getBounds();
            Point p = device[i].getDeviceWindow().getLocation();

            rect.x = p.x + rect.x + rect.width  / 2;
            rect.y = p.y + rect.y + rect.height / 2;
            if (r.x <= rect.x && rect.x < r.x + r.width  &&
                r.y <= rect.y && rect.y < r.y + r.height
            ) {
                select(device[i], true);
            } else {
                select(device[i], false);
            }
        }
    }

    public void selectAll() {
        Device[] device = getActiveDevices();
        for (int i = 0; i < device.length; i++) {
            select(device[i], true);
        }
    }

    public void deselectAll() {
        Device[] device = getActiveDevices();
        for (int i = 0; i < device.length; i++) {
            select(device[i], false);
        }
    }

    public void select(Device dev, boolean select) {
        if (select) {
                if (!dadGroup.contains(dev) ) {
                dadGroup.add(dev);
                dev.getDeviceWindow().repaint();
                updateToolbar();
            }
        } else {
                if (dadGroup.contains(dev) ) {
                dadGroup.remove(dev);
                dev.getDeviceWindow().repaint();
                updateToolbar();
            }
        }
    }


    public void addNotify() {
        super.addNotify();
        updateToolbar();
    }

    private void updateToolbar() {
        if (toolbar != null) {
            toolbar.setEnable(!dadGroup.isEmpty(), "Copy");
            toolbar.setEnable(!dadGroup.isEmpty(), "Cut");
            toolbar.setEnable(copyBuffer != null, "Paste");
        }
    }

    private void updateSelector() {
        Point p1 = selPos1;
        Point p2 = selPos2;
        if (p1 != null && p2 != null) {
            int x = Math.min(p1.x, p2.x);
            int y = Math.min(p1.y, p2.y);
            int w = Math.abs(p1.x - p2.x);
            int h = Math.abs(p1.y - p2.y);
            selector.setBounds(x, y, w, h);
        } else {
            selector.setSize(0, 0);
        }
    }

    public void cut() {
        copy();

        DADComponent[] device = dadGroup.getComponents();
        for (int i = 0; i < device.length; i++) {
            ((Device)device[i]).dispose();
        }
        updateToolbar();
    }

    public void clear() {
        Device[] device = getActiveDevices();
        for (int i = 0; i < device.length; i++) {
            device[i].dispose();
        }
        repaint();
    }
    
    private void rename() {
        Device[] device = getActiveDevices();
        for (int i = 0; i < device.length; i++) {
            device[i].setName("dev" + i);
        }
    }

    public void write(OutputStream os) throws IOException {
        rename();
    
        Device[] device = getActiveDevices();
        // create elements
        Element top = new Element("circuit", false);
        for (int i = 0; i < device.length; i++) {
            top.addElement(device[i].createElement() );
        }

        CircuitDocument doc = new CircuitDocument(top);
        doc.write(os);
    }

    public void paste() {
        CircuitDocument copyBuffer = this.copyBuffer;
        if (copyBuffer != null) {
            paste(copyBuffer, true);
        }
    }

    public void copy() {
        rename();
    
        DADComponent[] device = dadGroup.getComponents();

        for (int i = 0; i < device.length; i++) {
            device[i].setName("copy-dev" + i);
        }

        Element top = new Element("circuit", false);
        for (int i = 0; i < device.length; i++) {
            top.addElement( ( (Device)device[i]).createElement() );
        }

        copyBuffer = new CircuitDocument(top);
        updateToolbar();
    }

    private Device[] getActiveDevices() {
        Vector activeDevices = new Vector();
        int count = getComponentCount();
        for (int i = 0; i < count; i++) {
            DComponent comp = getComponent(i);
            if (comp instanceof DeviceWindow) {
                Device dev = ((DeviceWindow)comp).getDevice();
                if (dev.isActive() ) activeDevices.addElement(dev);
            }
        }
        Device[] devs = new Device[activeDevices.size()];
        activeDevices.copyInto(devs);
        return devs;
    }

    public void paste(CircuitDocument doc, boolean select) {

        rename();

        if (select) deselectAll();

        Element   top    = doc.getElement();
        Hashtable device = new Hashtable();

        int count = top.getElementCount();

        for (int i = 0; i < count; i++) {
            Device dev = factory.createDevice(top.getElementAt(i) );
            device.put(dev.getName(), dev);
            DeviceWindow devWin = dev.getDeviceWindow();
            add(devWin, 0);
            devWin.setSize(devWin.getPreferredSize() );
            if (select) select(dev, true);
        }

        for (int i = 0; i < count; i++) {
            Element eDev = top.getElementAt(i);
            int cc = eDev.getElementCount();
            for (int c = 0; c < cc; c++) {
                try {
                    Element eNode = eDev.getElementAt(c);
                    if (eNode.getName().equals("node") && eNode.getAttribute("type").equals("input") ) {
                        StringTokenizer st = new StringTokenizer(eNode.getAttribute("connect"), ".");
                        Device     dev1 = (Device)device.get(eDev.getAttribute("name") );
                        InputNode  in   = dev1.getInputNode(eNode.getAttribute("name") );
                        Device     dev2 = (Device)device.get(st.nextToken() );
                        OutputNode out  = dev2.getOutputNode(st.nextToken() );                    
                        out.connect(in);
                    }
                } catch(NullPointerException e) {
                    // ignore.
                }
            }
        }
//        if (select) dadGroup.move(8, 8);

        repaint();
    }

    int getToolboxWidth() {
        return tboxWidth;
    }

    int getScrollbarWidth() {
        return sbarWidth;
    }

    public void adjustmentValueChanged(DAdjustmentEvent e) {
        doLayout();
    }

    public void doLayout() {

        Dimension s = getSize();

        int count = toolbox.getDeviceCount();

        int aveHeight = 0;
        for (int i = 0; i < count; i++) {
            DComponent comp = toolbox.getDeviceAt(i).getDeviceWindow();
            aveHeight = Math.max(aveHeight, comp.getPreferredSize().height);
        }

        aveHeight += 8; // add gap.
        int visibleCount = Math.max(1, s.height / aveHeight);
        int max   = Math.max(0, count - visibleCount);
        int value = Math.min(scrollbar.getValue(), max);
        scrollbar.setValues(value, visibleCount, 0, max);
        scrollbar.setBounds(tboxWidth, 0, sbarWidth, s.height);

        for (int i = 0; i < count; i++) {
            Device dev = toolbox.getDeviceAt(i);
            DComponent comp = dev.getDeviceWindow();
            Dimension compSize = comp.getPreferredSize();
            int compX = (tboxWidth - compSize.width) / 2;
            int y1 = (i - value) * s.height / visibleCount;
            int y2 = (i - value + 1) * s.height / visibleCount;
            int compY = y1 + (y2 - y1 - compSize.height) / 2;

            comp.setBounds(compX, compY, compSize.width, compSize.height);
        }

        if (s.width > 0 && s.height > 0) {
            Device[] device = getActiveDevices();
            for (int i = 0; i < device.length; i++) {
                device[i].relocateDevice();
            }
        }
    }

    public Toolbox getToolbox() {
        return toolbox;
    }

    public void setDragNode(Node dragNode, Point currPoint) {

        DComponent c;
        Point      p;

        c = this.dragNode;
        p = this.currPoint;

        this.dragNode  = dragNode;
        this.currPoint = currPoint;

        if (c != null && p != null) {
            Point     loc = c.getLocation(this);
            Dimension s   = c.getSize();
            loc.translate(s.width / 2, s.height / 2);
            repaintWire(loc, p);
        } 

        c = this.dragNode;
        p = this.currPoint;

        if (c != null && p != null) {
            Point     loc = c.getLocation(this);
            Dimension s   = c.getSize();
            loc.translate(s.width / 2, s.height / 2);
            repaintWire(loc, p);
        } 
    }
    
    public void repaintWire(Point p1, Point p2) {
/*
        int w = p1.x - p2.x;
        int h = p1.y - p2.y;
        int len = (int)Math.sqrt(w * w + h * h);
            repaintWire(p1.x, p1.y, p2.x, p2.y, len / 32);
*/            
        repaintWire(p1.x, p1.y, p2.x, p2.y, 1);
    }

    private void repaintWire(int x1, int y1, int x2, int y2, int div) {
        div = Math.max(div, 1);
        for (int i = 0; i < div; i++) {
            int xx1 = x1 + (x2 - x1) * i / div;                         
            int yy1 = y1 + (y2 - y1) * i / div;                         
            int xx2 = x1 + (x2 - x1) * (i + 1) / div;                         
            int yy2 = y1 + (y2 - y1) * (i + 1) / div;                         
            int x = Math.min(xx1, xx2);
            int y = Math.min(yy1, yy2);
            int w = Math.abs(xx1 - xx2) + 1;
            int h = Math.abs(yy1 - yy2) + 1;
            repaint(x, y, w, h);
        }
    }

    public void paint(Graphics g) {

        Dimension size = getSize();

        g.setColor(new Color(0xffcccccc) );
        g.fillRect(0, 0, tboxWidth, size.height);
        g.draw3DRect(0, 0, tboxWidth - 1, size.height - 1, false);
        g.setColor(getBackground() );
        g.fillRect(tboxWidth + sbarWidth, 0, 
            size.width - tboxWidth - sbarWidth, size.height);
        g.draw3DRect(tboxWidth + sbarWidth, 0, 
            size.width - tboxWidth - sbarWidth - 1, size.height - 1, false);

        super.paint(g);


        // wires
        int count = getComponentCount();
        for (int i = 0; i < count; i++) {
            DComponent comp = getComponent(i);
            if (comp instanceof DeviceWindow) {
                drawWires(g, (DeviceWindow)comp);
            }
        }

        if (currPoint != null && dragNode != null) {
            Point p1 = currPoint;
            Point p2 = dragNode.getLocation(this);
            Dimension s2 = dragNode.getSize();
            p2.x += (s2.width  / 2);
            p2.y += (s2.height / 2);
            drawWire(g, p1, p2);
        }

        // frame
/*
        g.setColor(getForeground() );
        g.drawRect(0, 0, size.width - 1, size.height - 1);
        g.setColor(getBackground() );
        g.draw3DRect(1, 1, size.width - 3, size.height - 3, false);
*/
    }

    private void drawWire(Graphics g, Point p1, Point p2) {
//        g.setColor(Color.gray);
        g.setColor(Color.blue);
        g.drawLine(p1.x, p1.y, p2.x, p2.y);
    }

    private void drawWires(Graphics g, DeviceWindow deviceWindow) {

        InputNode[] in = deviceWindow.getDevice().getInputNodes();

        for (int i = 0; i < in.length; i++) {

            OutputNode out = in[i].getOutput();

            if (out != null) {

                Point p1 = in[i].getLocation(this);
                Point p2 = out.getLocation(this);

                Dimension s1 = in[i].getSize();
                Dimension s2 = out.getSize();

                p1.x += (s1.width  / 2);
                p2.x += (s2.width  / 2);
                p1.y += (s1.height / 2);
                p2.y += (s2.height / 2);

                drawWire(g, p1, p2);
            }
        }
    }
}

class Selector extends DComponent {
    public void paint(Graphics g) {
        Dimension s = getSize();
        g.setColor(Color.gray);
        g.drawRect(0, 0, s.width - 1, s.height - 1);
    }
}
