package com.d_project.ui;

import com.d_project.util.Queue;

/**
 * DEventQueue
 * @author Kazuhiko Arase
 */
public class DEventQueue {

    final Object lock = new Object();

    Queue queue = new Queue();

    DEventDispatcher dispatcher;


    public DEventQueue() {
        this(true);
    }

    public DEventQueue(boolean startOnCreate) {
        if (startOnCreate) start();
    }

    public void start() {
        synchronized(lock) {
            if (dispatcher == null) {
                dispatcher = new DEventDispatcher(this);
                dispatcher.start();
            }
        }
    }

    public void stop() {
        synchronized(lock) {
            if (dispatcher != null) {
                postEvent(new DSystemEvent(null, DSystemEvent.SYSTEM_STOP) );
                try {
                    dispatcher.join();
                    dispatcher = null;
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public synchronized DEvent getNext() {
        while (queue.isEmpty() ) {
            try {
                wait();
            } catch(InterruptedException e) {
                System.out.println("Interrupted in queue.");
            }
        }
        return (DEvent)queue.get();
    }

    public synchronized DEvent peek() {
        return (DEvent)queue.peek();
    }

    public synchronized void postEvent(DEvent event) {
        queue.put(event);
        notifyAll();
    }
}

