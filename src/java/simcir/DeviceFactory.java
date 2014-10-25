package simcir;

import java.util.StringTokenizer;

import com.d_project.xml.Element;

/**
 * DeviceFactory
 * @author Kazuhiko Arase
 */
public class DeviceFactory {

    public Device createDevice(Element e) {
        String type = e.getAttribute("type");
        Device device = null;

        if (type.equals("LED") ) {        
            device = new LED();
        } else if (type.equals("DC") ) {        
            device = new DirectCurrentSource(5);
        } else if (type.equals("SWITCH") ) {
            String action = e.getAttribute("action", "PUSH_ON");
            if (action.equals("PUSH_ON") ) {
                device = new Switch(Switch.PUSH_ON);
            } else if (action.equals("PUSH_OFF") ) {
                device = new Switch(Switch.PUSH_OFF);
            } else if (action.equals("TOGGLE") ) {
                device = new Switch(Switch.TOGGLE);
            }        
        } else if (type.equals("NAND") ) {        
            device = new LogicGate(LogicGate.NAND);
        } else if (type.equals("AND") ) {        
            device = new LogicGate(LogicGate.AND);
        } else if (type.equals("NOR") ) {        
            device = new LogicGate(LogicGate.NOR);
        } else if (type.equals("OR") ) {        
            device = new LogicGate(LogicGate.OR);
        } else if (type.equals("ENOR") ) {        
            device = new LogicGate(LogicGate.ENOR);
        } else if (type.equals("EOR") ) {        
            device = new LogicGate(LogicGate.EOR);
        } else if (type.equals("NOT") ) {        
            device = new LogicGate(LogicGate.NOT);
        }

        if (device != null) {
            String label = e.getAttribute("label");
            if (label != null) {
                device.setLabel(label);
            }
            device.setName(e.getAttribute("name") );
            StringTokenizer st = new StringTokenizer(e.getAttribute("location", "0,0"), ",");
            int x = Integer.parseInt(st.nextToken() );
            int y = Integer.parseInt(st.nextToken() );
            device.getDeviceWindow().setLocation(x, y);
        }

        return device;
    }
}
