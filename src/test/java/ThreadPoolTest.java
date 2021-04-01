import com.lcw.threadPool.ThreadPool;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

@Slf4j
public class ThreadPoolTest {
    @Test
    public void noRejectPoolTest(){
        ThreadPool pool = new ThreadPool(1,1000, TimeUnit.MILLISECONDS,1,(queue,task) -> {
            queue.put(task);        //一直等待有线程可以执行
//            queue.put(task,TimeUnit.SECONDS,2);     //等待一定时间的策略

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

        for (int i = 0; i < 2; i++) {
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

}
