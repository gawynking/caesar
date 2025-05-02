package com.caesar.model.code.func;

import com.caesar.model.code.enums.DatePeriod;
import com.caesar.util.DateUtils;

import java.text.ParseException;
import java.util.Date;

public class Functions {


    /**
     * 获取dt
     *
     * @return
     */
    public static String getDt(){
        try {
            return DateUtils.dateFormat(DateUtils.dateAdd(new Date(),-1,false),"yyyyMMdd");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDt(DatePeriod period, Date date){
        String etlDate = getEtlDate();
        if(null == period || null == date){
            return etlDate;
        }
        try {
            switch (period) {
                case DAY:
                    etlDate = DateUtils.dateFormat(date, "yyyyMMdd");
                    break;
                case WEEK:
                    break;
                case MONTH:
                    etlDate = DateUtils.dateFormat(DateUtils.dateParse(DateUtils.getMonthStart(date),"yyyy-MM-dd"),"yyyyMMdd");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return etlDate;
    }


    /**
     * 获取ETL日期
     *
     * @return
     */
    public static String getEtlDate(){
        try {
            return DateUtils.dateFormat(DateUtils.dateAdd(new Date(),-1,false),"yyyy-MM-dd");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getEtlDate(DatePeriod period, Date date){
        String etlDate = getEtlDate();
        if(null == period || null == date){
            return etlDate;
        }
        try {
            switch (period) {
                case DAY:
                    etlDate = DateUtils.dateFormat(date, "yyyy-MM-dd");
                    break;
                case WEEK:
                    break;
                case MONTH:
                    etlDate = DateUtils.getMonthStart(date);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return etlDate;
    }


    /**
     * 获取开始日期
     *
     * @return
     */
    public static String getStartDate(){
        try {
            return DateUtils.dateFormat(DateUtils.dateAdd(new Date(),-1,false),"yyyy-MM-dd");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getStartDate(DatePeriod period, Date date){
        String etlDate = getStartDate();
        if(null == period || null == date){
            return etlDate;
        }
        try {
            switch (period) {
                case DAY:
                    etlDate = DateUtils.dateFormat(date, "yyyy-MM-dd");
                    break;
                case WEEK:
                    break;
                case MONTH:
                    etlDate = DateUtils.getMonthStart(date);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return etlDate;
    }


    /**
     * 获取结束日期
     *
     * @return
     */
    public static String getEndDate(){
        try {
            return DateUtils.dateFormat(DateUtils.dateAdd(new Date(),-1,false),"yyyy-MM-dd");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getEndDate(DatePeriod period, Date date){
        String etlDate = getStartDate();
        if(null == period || null == date){
            return etlDate;
        }
        try {
            switch (period) {
                case DAY:
                    etlDate = DateUtils.dateFormat(date, "yyyy-MM-dd");
                    break;
                case WEEK:
                    break;
                case MONTH:
                    etlDate = DateUtils.getMonthEnd(date);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return etlDate;
    }


}
