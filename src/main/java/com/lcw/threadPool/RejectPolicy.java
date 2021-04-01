package com.lcw.threadPool;

@FunctionalInterface
public interface RejectPolicy<T> {

    void reject(BlockingQueue<T> queue,T task);
}
