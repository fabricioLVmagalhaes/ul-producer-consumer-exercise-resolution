/*
 * (c) 2014 UL TS BV
 */
package com.ul;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class Consumer {

    private  static  final Logger logger = Logger.getLogger(Consumer.class);

    private BlockingQueue<Message> queue;
    private Thread consumerThread = null;

    private int messageCounter = 0;

    public Consumer(BlockingQueue<Message> queue) {
        this.queue = queue;
    }

    public void startConsuming() {
        consumerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                List<Message> messages = new ArrayList<>();
                setUpLogger();
                while (messageCounter != 100) {
                    try {
                        Message message = queue.take();
                        messageCounter++;
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
            logger.info(message);
        }
        messages.clear();
    }

    private void setUpLogger() {

        PatternLayout layout = getLogPattern();
        FileAppender fileAppender = getFileAppender(layout);

        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.ALL);
        rootLogger.addAppender(fileAppender);
    }

    private FileAppender getFileAppender(PatternLayout layout) {
        FileAppender fileAppender = new FileAppender();
        fileAppender.setFile("consumer.log");
        fileAppender.setLayout(layout);
        fileAppender.activateOptions();
        return fileAppender;
    }

    private PatternLayout getLogPattern() {
        PatternLayout layout = new PatternLayout();
        String conversionPattern = "%m%n";
        layout.setConversionPattern(conversionPattern);
        return layout;
    }
}
