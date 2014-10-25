package simcir;

import com.d_project.ui.DEvent;

/**
 * InputEvent
 * @author Kazuhiko Arase
 */
public class InputEvent extends DEvent {

    double value;

    public InputEvent(InputNode source, double value) {
        super(source, 0);
        this.value  = value;
    }

    public double getValue() {
        return value;
    }
}
