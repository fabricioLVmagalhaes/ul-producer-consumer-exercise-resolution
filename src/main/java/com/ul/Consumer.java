/*
 * (c) 2014 UL TS BV
 */
package com.ul;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class Consumer {

    private BlockingQueue<Message> queue;
    private Thread consumerThread = null;

    public Consumer(BlockingQueue<Message> queue) {
        this.queue = queue;
    }

    public void startConsuming() {
        consumerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                List<Message> messages = new ArrayList<>();
                while (true) {
                    try {
                        Message message = queue.take();
                        messages.add(message);
                        if (messages.size() == 10) {
                            logMessages(messages);
                        }
                    } catch (InterruptedException e) {
                        // executing thread has been interrupted, exit loop
                        break;
                    }
                }
            }
        });
        consumerThread.start();
    }

    private void logMessages(List<Message> messages) {
        messages.sort(Comparator.comparing(Message::getPriority));
        for (Message message : messages) {
            System.out.println(message);
        }
        messages.clear();
    }

    public void stopConsuming() {
        consumerThread.interrupt();
    }
}
