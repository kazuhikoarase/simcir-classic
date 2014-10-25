package simcir;

import java.awt.Color;

/**
 * DirectCurrentSource
 * @author Kazuhiko Arase
 */
public class DirectCurrentSource extends Device {

    int voltage;

    public DirectCurrentSource(int voltage) {
        super(0, 1);
        this.voltage = voltage;
        setType("DC");
        setLabel("D.C." + voltage + "V");
        setBackground(new Color(0xffffcccc) );
        setSize(40, 32);
        out[0].setValue(voltage);
    }

    public int getVoltage() {
        return voltage;
    }

    public Device createClone() {
        return new DirectCurrentSource(voltage);
    }
}
