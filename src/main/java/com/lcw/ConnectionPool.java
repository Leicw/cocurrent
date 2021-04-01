package com.lcw;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.locks.LockSupport;

public class ConnectionPool {

    public static void main(String[] args) {
        ConnectionPool connectionPool = new ConnectionPool(2);


        List<Thread> list = new ArrayList<>();
        for (int i = 0;i<10;i++){
            list.add(new Thread(() -> {
                Connection x = connectionPool.get();
//                System.out.println(x);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                connectionPool.put(x);
            }));
        }
        list.forEach(thread -> thread.start());

    }

    private int maxSize;
    private Connection[] cons;
    private AtomicIntegerArray busyCon;

    public ConnectionPool(int maxSize) {
        this.maxSize = maxSize;
        this.cons = new Connection[maxSize];
        this.busyCon = new AtomicIntegerArray(maxSize);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {

            for (int i = 0;i<maxSize;i++){
//                cons[i] = DriverManager.getConnection("jdbc:mysql://localhost:3306/test","root","333");
                cons[i] = DriverManager.getConnection("jdbc:mysql://localhost:3306/test?serverTimezone=Asia/Shanghai","root","333");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public Connection get(){
        while (true) {
            for (int i = 0; i < maxSize; i++) {
                if (busyCon.get(i) == 0) {
                    if (busyCon.compareAndSet(i, 0, 1)) {
                        System.out.println(Thread.currentThread().getName() + "  取走  " + cons[i] + i);
                        return cons[i];
                    }
                }
            }
            synchronized (this){
                try {
                    System.out.println(Thread.currentThread().getName() + "  等待   ");
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void put(Connection connection){
        for (int i = 0;i<maxSize;i++){
            if (cons[i] == connection){
                busyCon.set(i,0);
                System.out.println(Thread.currentThread().getName() + "   归还   " + cons[i] +i);
                synchronized (this){
                    this.notify();
                }
                return;
            }

        }
    }


}
