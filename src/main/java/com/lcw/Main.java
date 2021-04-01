package com.lcw;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

public class Main {
    static boolean flag = true;
    static boolean flag1 = true;
    volatile static int i = 0;

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
//            LockSupport.park();
            long start = System.currentTimeMillis();
            int i = 0;
            while (flag1) {
//                System.out.println(111);
                synchronized (Main.class) {
//                    i++;
                }

                if (!flag){
                    break;
                }
            }
            System.out.println(System.currentTimeMillis()-start);

            /*for (int o = 0;o<10000;o++){
                i++;
            }*/
        });

        thread.start();
        /*for (int o = 0;o<10000;o++){
            i++;
        }
        Thread.sleep(10);
        System.out.println(i);*/
        Thread.sleep(1000);
        flag=false;
//        LockSupport.unpark(thread);

    }


}
