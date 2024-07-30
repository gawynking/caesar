package com.caesar;

import com.caesar.util.DateUtils;

import java.util.Date;
import java.util.UUID;

public class DateTest {
    public static void main(String[] args) throws Exception{


        Date startDate = DateUtils.dateParse("2024-07-01", "yyyy-MM-dd");
        Date endDate = DateUtils.dateParse("2024-07-02", "yyyy-MM-dd");

        int i = DateUtils.dateCompare(startDate, endDate);
        System.out.println(i);

        startDate = DateUtils.dateAdd(startDate,1,false);

        System.out.println(startDate);

        startDate = DateUtils.addMonth(startDate,1);

        System.out.println(startDate);

        System.out.println(UUID.randomUUID().toString().toLowerCase().replaceAll("-",""));


    }
}
