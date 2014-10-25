package com.d_project.ui;

/**
 * DSystemEvent
 * @author Kazuhiko Arase
 */
class DSystemEvent extends DEvent {

    public static final int SYSTEM_STOP = 0;

    public DSystemEvent(DComponent comp, int id) {
        super(comp, id);
    }
}
