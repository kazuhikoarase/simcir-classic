package com.d_project.ui.event;

import com.d_project.ui.DComponent;
import com.d_project.ui.DEvent;

/**
 * DFocusEvent
 * @author Kazuhiko Arase
 */
public class DFocusEvent extends DEvent {

    public static final int FOCUS_GAINED = 0;
    public static final int FOCUS_LOST   = 1;

    public DFocusEvent(DComponent comp, int id) {
        super(comp, id);
    }

    public String toString() {
        return getClass().getName() + "{" + getIDName() + " on " + getSource() + "}";
    }

    String getIDName() {
        switch(getID() ) {
        case FOCUS_GAINED : return "gained";
        case FOCUS_LOST   : return "lost";
        default : return "undefined";
        }
    }
}
