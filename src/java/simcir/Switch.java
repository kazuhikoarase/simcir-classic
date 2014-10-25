package simcir;

import java.awt.Color;
import java.awt.Dimension;

import com.d_project.ui.DButton;
import com.d_project.ui.DEvent;
import com.d_project.ui.event.DMouseEvent;
import com.d_project.ui.event.DMouseListener;
import com.d_project.xml.Element;

/**
 * Switch
 * @author Kazuhiko Arase
 */
public class Switch extends Device implements DMouseListener {

    public static final int PUSH_ON  = 0;
    public static final int TOGGLE   = 1;
    public static final int PUSH_OFF = 2;

    DButton button;

    int action;

    public Switch(int action) {

        super(1, 1);

        setBackground(new Color(0xffccccff) );
        setSize(40, 32);
        setType("SWITCH");

        this.action = action;

        switch(action) {
        case PUSH_ON :
            setLabel("PushOn Switch");
            break;
        case TOGGLE :
            setLabel("Toggle Switch");
            break;
        case PUSH_OFF :
            setLabel("PushOff Switch");
            break;
        }

        button = new DButton(null, action == TOGGLE);
        button.addMouseListener(this);
        add(button);        

        updateOutput();
    }

    public Element createElement() {
        Element e = super.createElement();
        switch(action) {
        case PUSH_ON :
            e.setAttribute("action", "PUSH_ON");
            break;
        case TOGGLE :
            e.setAttribute("action", "TOGGLE");
            break;
        case PUSH_OFF :
            e.setAttribute("action", "PUSH_OFF");
            break;
        }
        return e;
    }

    public void setActive(boolean active) {
        super.setActive(active);
        if (active) {
            button.enableEvents(DEvent.MOUSE_EVENT_MASK);
        } else {
            button.disableEvents(DEvent.MOUSE_EVENT_MASK);
        }
    }

    public Device createClone() {
        return new Switch(action);
    }

    public void doLayout() {
        Dimension size = getSize();
        int r = size.height / 2;
        button.setBounds( (size.width - r) / 2, (size.height - r) / 2, r, r);
        super.doLayout();
    }

    public void mousePressed(DMouseEvent e) {
        updateOutput();
    }

    public void mouseReleased(DMouseEvent e) {
        updateOutput();
    }

    protected void processInputEvent(InputEvent e) {
        updateOutput();
    }

    private void updateOutput() {

        boolean connect = false;

        switch(action) {
        case PUSH_ON :
             connect = button.isPressed();
            break;
        case TOGGLE :
             connect = button.isPressed() || button.isSelected();
            break;
        case PUSH_OFF :
             connect = !button.isPressed();
            break;
        }

         out[0].setValue(connect? in[0].getValue() : Double.NaN);
    }
}

