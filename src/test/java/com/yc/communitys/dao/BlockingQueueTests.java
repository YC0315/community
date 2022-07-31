package com.yc.communitys.dao;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class BlockingQueueTests {
    public static void main(String[] args) {
        // 实例化阻塞队列
        BlockingQueue queue = new ArrayBlockingQueue(10);
        try {
            // 启动生产者线程
            new Thread(new Producer(queue)).start();
            // 启动消费者线程
            new Thread(new Consumer(queue)).start();
            new Thread(new Consumer(queue)).start();
            new Thread(new Consumer(queue)).start();
        } finally {
            System.out.println("------结束-----------");
        }
    }
}

/**
 * 生产者
 * */
class Producer implements Runnable {
    // 传入一个阻塞队列
    private BlockingQueue<Integer> queue;
    // 实例化的时候传入
    public Producer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 100; i++) {
                Thread.sleep(20);
//                queue.put(i);
                // 生产数据
                queue.offer(i, 10, TimeUnit.SECONDS);
                System.out.println(Thread.currentThread().getName() + "生产:" + queue.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/**
 * 消费者
 * */
class Consumer implements Runnable {

    private BlockingQueue<Integer> queue;

    public Consumer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            do{
                // 设定消费的频率
                Thread.sleep(new Random().nextInt(1000));
//                queue.take();
                // 消费数据
                queue.poll(10, TimeUnit.SECONDS);
                System.out.println(Thread.currentThread().getName() + "消费:" + queue.size());
            } while (queue.size() > 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}