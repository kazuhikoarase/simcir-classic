package simcir;

import java.awt.Color;
import java.util.Vector;

/**
 * OutputNode
 * @author Kazuhiko Arase
 */
class OutputNode extends Node {

    Vector inputs = new Vector();

    public OutputNode(Device device) {
        super(device);
        setBackground(Color.white);
    }

    public void setValue(double value) {
        synchronized(NODE_LOCK) {
            if (this.value != value) {
                this.value = value;

                int count = getInputCount();
                for (int i = 0; i < count; i++) {
                    getInputAt(i).setValue(value);
                }
            }
        }
    }

    public int getInputCount() {
        return inputs.size();
    }

    public InputNode getInputAt(int index) {
        return (InputNode)inputs.elementAt(index);
    }

    public void connect(InputNode node) {
        synchronized(NODE_LOCK) {
            if (node.output != null) node.output.disconnect(node);        
            inputs.addElement(node);
            node.output = this;
            node.setValue(value);
            repaintLine(node);
        }
    }

    public void disconnect(InputNode node) {
        synchronized(NODE_LOCK) {
            if (inputs.removeElement(node) ) {
                node.output = null;
                node.setValue(Double.NaN);
                repaintLine(node);
            }
        }
    }
}
