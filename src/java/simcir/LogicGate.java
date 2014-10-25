package simcir;

import java.awt.Dimension;
import java.awt.Graphics;

/**
 * LogicGate
 * @author Kazuhiko Arase
 */
public class LogicGate extends Device {

    public static final int AND  = 0;
    public static final int NAND = 1;
    public static final int OR   = 2;
    public static final int NOR  = 3;
    public static final int EOR  = 4;
    public static final int ENOR = 5;
    public static final int NOT  = 6;

    int type;

    public LogicGate(int type) {

        super( (type != NOT)? 2 : 1, 1);
        
        this.type = type;

        switch(type) {
        case AND :
            setType("AND");
            break;
        case NAND :
            setType("NAND");
            break;
        case OR :
            setType("OR");
            break;
        case NOR :
            setType("NOR");
            break;
        case EOR :
            setType("EOR");
            break;
        case ENOR :
            setType("ENOR");
            break;
        case NOT :
            setType("NOT");
            break;
        default :
            break;
        }

        setLabel(getType() );
        setSize(40, 32);
        updateOutput();
    }

    public Device createClone() {
        return new LogicGate(type);
    }

    protected  void processInputEvent(InputEvent e) {
        updateOutput();
    }

    void updateOutput() {

        double v1 = in[0].getValue();
        double v2 = (type != NOT)? in[1].getValue() : v1;

        boolean a = (!Double.isNaN(v1) && v1 > 2);
        boolean b = (!Double.isNaN(v2) && v2 > 2);

        boolean z = false;

        switch(type) {
        case AND :
            z = (a & b);
            break;

        case NAND :
        case NOT :
            z = !(a & b);
            break;

        case OR :
            z = (a | b);
            break;
        case NOR :
            z = !(a | b);
            break;

        case EOR :
            z = (a ^ b);
            break;
        case ENOR :
            z = !(a ^ b);
            break;

        default :
            break;
        }

        out[0].setValue(z? 5 : 0);
    }

    public void paint(Graphics g) {
        super.paint(g);
        Dimension size = getSize();
        int r = size.height / 2;
        drawLogic(g, (size.width - r) / 2, (size.height - r) / 2, r, r);
    }

    private void drawLogic(Graphics g, int x, int y, int width, int height) {

        int div = 8;
        int pos = 1;
        int dep = 2;

        int wo = width * dep / div;

        g.setColor(getForeground() );

        switch(type) {
        case NAND :
        case NOR :
        case ENOR :
        case NOT :
            int r = 2;
            x -= r;
                g.drawOval(x + width, y + height / 2 - r, r * 2 - 1, r * 2 - 1);
            break;
        default :
            break;
        }

        int ho = height / 2;
        switch(type) {
        case NOT :
            g.drawLine(x, y,              x + width, y + ho);
            g.drawLine(x, y + height - 1, x + width, y + ho);
            g.drawLine(x, y,              x,                  y + height - 1);
            break;

        case AND :
        case NAND :
            drawArc(g, x + width - ho, y, ho, height);
            g.drawLine(x, y,              x + width - ho - 1, y);
            g.drawLine(x, y + height - 1, x + width - ho - 1, y + height - 1);
            g.drawLine(x, y,              x,                  y + height - 1);
            break;

        case OR :
        case NOR :
            drawArc(g, x, y, width, height);
            drawArc(g, x, y, wo, height);
            break;

        case EOR :
        case ENOR :
            int xo = width * pos / div;
            drawArc(g, x,       y, wo,            height);
            drawArc(g, x + xo,  y, wo,            height);
            drawArc(g, x + xo,  y, width - xo,    height);
            break;

        default :
            break;
        }
    }

    private static void drawArc(Graphics g, int x, int y, int width, int height) {
        g.drawArc(x - width, y, width * 2 - 1, height - 1, 0, 90);
        g.drawArc(x - width, y, width * 2 - 1, height - 1, 0, -90);
    }
}

