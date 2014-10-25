package simcir;

import java.awt.Rectangle;
import java.util.Vector;

/**
 * Toolbox
 * @author Kazuhiko Arase
 */
class Toolbox {

    CircuitBoard board;
    Vector deviceList = new Vector();

    public Toolbox(CircuitBoard board) {
        this.board = board;
    }

    public void addDevice(Device dev) {
        dev.setActive(false);
        board.add(dev.getDeviceWindow() );
        deviceList.addElement(dev);            
    }

    public int getDeviceCount() {
        return deviceList.size();
    }

    public Device getDeviceAt(int index) {
        return (Device)deviceList.elementAt(index);
    }

    void replaceDevice(Device dev, Device newDev) {
        int index = deviceList.indexOf(dev);
        if (index >= 0) {
            newDev.setActive(false);
            board.add(newDev.getDeviceWindow() );
            Rectangle r = dev.getDeviceWindow().getBounds();
            newDev.getDeviceWindow().setBounds(r.x, r.y, r.width, r.height);
            deviceList.setElementAt(newDev, index);
        }
    }
}
