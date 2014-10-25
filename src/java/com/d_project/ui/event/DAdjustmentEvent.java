package com.d_project.ui.event;

import com.d_project.ui.DComponent;
import com.d_project.ui.DEvent;

/**
 * DAdjustmentEvent
 * @author Kazuhiko Arase
 */
public class DAdjustmentEvent extends DEvent {

    public static final int ADJUSTMENT_VALUE_CHANGED = 0;

    public static final int TRACK           = 0;
    public static final int BLOCK_INCREMENT = 1;
    public static final int BLOCK_DECREMENT = 2;
    public static final int UNIT_INCREMENT  = 3;
    public static final int UNIT_DECREMENT  = 4;

    int value;
    int type;

    public DAdjustmentEvent(DComponent comp, int id, int type, int value) {
        super(comp, id);
        this.value = value;
        this.type  = type;
    }

    public int getAdjustmentType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        return getClass().getName() + "{" + getSource() + "}";
    }
}
