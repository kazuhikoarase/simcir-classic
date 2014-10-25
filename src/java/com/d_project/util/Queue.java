package com.d_project.util;

/**
 * Queue
 * @author Kazuhiko Arase
 */
public class Queue {

    QueueItem head;
    QueueItem tail;

    public Queue() {
    }

    public synchronized void put(Object obj) {
        // put tail
        QueueItem item = new QueueItem(obj);
        if (tail != null) {
            item.next = tail;
            tail.prev = item;
            tail = item;
        } else {
            head = item;
            tail = item;
        }
    }

    public synchronized void push(Object obj) {
        // put head
        QueueItem item = new QueueItem(obj);
        if (head != null) {
            item.prev = head;
            head.next = item;
            head = item;
        } else {
            head = item;
            tail = item;
        }
    }

    public Object pop() {
        return get();
    }

    public synchronized boolean isEmpty() {
        return (head == null);
    }

    public synchronized Object peek() {
        // peek head
        return (head != null)? head.obj : null;
    }
  
    public synchronized Object get() {
        // get head
        if (head != null) {

            Object obj = head.obj;

            if (head == tail) {
                head = null;
                tail = null;
            } else {
                head = head.prev;
            }

            return obj;
        }
        return null;
    }

/*
    public static void main(String[] args) {
        Queue queue = new Queue();

        System.out.println("QUEUE");

        for (int i = 0; i < 10; i++) {
            queue.put("ITEM" + i);
        }

        while (queue.peek() != null) {
            System.out.println(queue.get() );
        }

        System.out.println("STACK");

        for (int i = 0; i < 10; i++) {
            queue.push("ITEM" + i);
        }

        while (queue.peek() != null) {
            System.out.println(queue.pop() );
        }

    }
*/

}

class QueueItem {

    Object obj;
    QueueItem prev;
    QueueItem next;

    public QueueItem(Object obj) {
        this.obj = obj;
        prev = null;
        next = null;
    }
}
