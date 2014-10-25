package simcir;

import java.awt.Color;
import java.util.Vector;

import com.d_project.ui.DEvent;

/**
 * InputNode
 * @author Kazuhiko Arase
 */
class InputNode extends Node {

    Vector listeners = new Vector();

    OutputNode output;

    public InputNode(Device device) {
        super(device);
        setBackground(Color.orange);
    }

    protected void processEvent(DEvent e) {
        if (e instanceof InputEvent) {
            processInputEvent( (InputEvent)e);
        } else {
            super.processEvent(e);
        }
    }

    protected void processInputEvent(InputEvent e) {
        int count = listeners.size();
        for (int i = 0; i < count; i++) {
            ( (InputListener)listeners.elementAt(i) ).inputValueChanged(e);
        }        
    }

    public OutputNode getOutput() {
        return output;
    }

    public void setValue(double value) {
        if (this.value != value) {
            this.value = value;
            getQueue().postEvent(new InputEvent(this, value) );
        }
    }

    public void disconnect() {
        synchronized(NODE_LOCK) {
            if (output != null) {
                output.disconnect(this);
            }
        }
    }

    public void addInputListener(InputListener listener) {
        listeners.addElement(listener);
    }

    public void removeInputListener(InputListener listener) {
        listeners.removeElement(listener);
    }

}

