package com.lcw.threadPool;

import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ThreadPool {
    private BlockingQueue<Runnable> taskQueue;
    private HashSet<Worker> workers = new HashSet<>();
    private int coreSize;
    private int timeout;
    private TimeUnit timeUnit;
    private RejectPolicy<Runnable> rejectPolicy;


    public static void main(String[] args) {
        ThreadPool pool = new ThreadPool(1,1000, TimeUnit.MILLISECONDS,1,(queue,task) -> {
//            queue.put(task);        //调用者一直等待有线程可以执行
//            queue.put(task,TimeUnit.SECONDS,2);     //调用者等待一定时间的策略
//            log.info("放弃{}", task); //调用者放弃等待
//            throw new RuntimeException("任务执行失败 " + task);  //调用者抛出异常
//            task.run();     //调用者自己完成

        });
       /* for (int i = 0;i < 3;i++){
            int j = i;
            pool.execute(() -> {
//                log.info(String.valueOf(j));
                System.out.println(j);
                *//*try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*//*

         *//*try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*//*
            });
        }*/

        for (int i = 0; i < 4; i++) {
            int j = i;
            pool.execute(() -> {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("{}", j);
            });
        }
    }

    public ThreadPool(int coreSize, int timeout, TimeUnit timeUnit, int queueSize,RejectPolicy<Runnable> rejectPolicy) {
        this.coreSize = coreSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.taskQueue = new BlockingQueue<>(queueSize);
        this.rejectPolicy = rejectPolicy;

    }

    public void execute(Runnable task){
//        如果任务数小于核心线程数，可以直接处理,创建一个线程
        synchronized (workers) {
            if (workers.size() < coreSize){
                Worker worker = new Worker(task);
                log.info("创建work线程：{},任务:{}",worker,task);
                workers.add(worker);
                worker.start();
            }else{

    //        如果任务数大于核心线程数，无法直接处理，依据拒绝策略尝试去添加
                taskQueue.tryPut(rejectPolicy,task);
            }
        }


    }

    class Worker extends Thread{

        private Runnable task;

        public Worker(Runnable task) {
            this.task = task;
        }

        public void run(){
//           执行任务，并判断队列中还有没有任务
            while (task != null || (task = taskQueue.take(timeUnit,timeout)) != null){
//            while (task != null || (task = taskQueue.take()) != null){
                try {
                    log.info("正在执行...{}", task);
                    task.run();
//                    log.info("work线程：{},执行任务结束：{}",this,task);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    task = null;
                }
            }

            synchronized (workers){
                log.info("销毁work线程：{}",this);
                workers.remove(this);
            }
        }


    }

}

