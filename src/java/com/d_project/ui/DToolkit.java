package com.d_project.ui;

/**
 * DToolkit
 * @author Kazuhiko Arase
 */
public class DToolkit {
    private static DEventQueue queue = null;
    public static DEventQueue getQueue() {
        return (queue != null)? queue : (queue = new DEventQueue() );
    }
}
