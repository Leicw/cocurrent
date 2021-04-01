package com.lcw.threadPool;


import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class BlockingQueue<T> extends ReentrantLock {
    private Deque<T> queue = new LinkedList<>();
    private final int capacity;
    private final Condition emptyCond = newCondition();
    private final Condition fullCond = newCondition();


    public T take(TimeUnit timeUnit,long time){
        lock();
        try {
            long waitTime = timeUnit.toNanos(time);
            while (queue.isEmpty()){
                try {
                    if (waitTime <= 0){
                        return null;
                    }
                    waitTime = emptyCond.awaitNanos(waitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T t = queue.removeLast();
            fullCond.signal();
            return t;

        }finally {
            unlock();
        }
    }
    public T take(){
        lock();
        try {
            while (queue.isEmpty()){
                try {
                    emptyCond.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            T t = queue.removeLast();
            fullCond.signal();
            return t;

        } finally {
            unlock();
        }

    }

//    一直等待的拒绝策略
    public void put(T task){
        lock();
        try {
            while (queue.size() == capacity){
                try {
                    log.info("任务已经满了：{}",task);
                    fullCond.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            log.info("任务太多放入任务队列：{}",task);
            queue.addFirst(task);
            emptyCond.signal();

        }finally {
            unlock();
        }
    }
//等待一定时间的拒绝策略
    public boolean put(T task,TimeUnit unit,long timeout){
        lock();
        try {
            while (queue.size() == capacity){
                timeout = unit.toNanos(timeout);
                try {

                    if (timeout <= 0 ) return false;
                    log.info("任务已经满了：{},等待:{}",task,timeout);
                    timeout = fullCond.awaitNanos(timeout);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            log.info("任务太多放入任务队列：{}",task);
            queue.addFirst(task);
            emptyCond.signal();
            return true;
        }finally {
            unlock();
        }


    }

    public int size(){
        lock();
        try {
            return queue.size();
        }finally {
            unlock();
        }
    }

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
    }

//    任务太多,尝试放入,如果队列满了,使用拒绝策略
    public void tryPut(RejectPolicy<T> rejectPolicy, T task) {
        lock();
        try {
            if (queue.size() == capacity){
                rejectPolicy.reject(this,task);
            }else {
                log.info("任务太多放入任务队列：{}",task);
                queue.addFirst(task);
                emptyCond.signal();
            }
        }finally {
            unlock();
        }
    }
}
