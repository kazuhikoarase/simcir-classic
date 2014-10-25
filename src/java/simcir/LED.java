package simcir;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

/**
 * LED
 * @author Kazuhiko Arase
 */
public class LED extends Device {

    public LED() {
        super(1, 0);
        setType("LED");
        setLabel(getType() );
        setBackground(new Color(0xffcccccc) );
        setSize(40, 32);
    }

    public Device createClone() {
        return new LED();
    }

    protected  void processInputEvent(InputEvent e) {
        repaint();
    }

    public void paint(Graphics g) {
        super.paint(g);
        Dimension size = getSize();
        double value = in[0].getValue();
        if (!Double.isNaN(value) && value > 2) {
            g.setColor(Color.red);
        } else {
            g.setColor(Color.black);
        }
        int r = size.height / 2;
        g.fillOval( (size.width - r) / 2, (size.height - r) / 2, r, r);
    }
}
