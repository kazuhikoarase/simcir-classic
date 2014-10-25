package com.d_project.ui.event;

import com.d_project.ui.DEvent;

/**
 * DActionEvent
 * @author Kazuhiko Arase
 */
public class DActionEvent extends DEvent {

    String command;

    public static final int ACTION_PERFORMED = 0;

    public DActionEvent(Object source, int id, String command) {
        super(source, id);
        this.command = command;
    }

    public String getActionCommand() {
        return command;
    }
}
