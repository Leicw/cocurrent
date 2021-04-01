package com.lcw;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.List;

public class UnChangeClassTest {
    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        /*for(int i = 0;i<1000;i++){
            new Thread( () -> {
                System.out.println(sdf.format(new Date()));
            }).start();
        }*/

        DateTimeFormatter  dif = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i =0;i<1000;i++) {
            new Thread(() -> {
                TemporalAccessor parse = dif.parse("1998-04-19");
                System.out.println(parse);
            }).start();
        }

    }
}
