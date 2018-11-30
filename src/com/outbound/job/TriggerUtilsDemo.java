package com.outbound.job;

import java.text.SimpleDateFormat;  
import java.util.Date;  
import org.quartz.TriggerUtils;  
/** 
 *  
 * @author Ickes 
 * 
 */  
public class TriggerUtilsDemo {  
    public static SimpleDateFormat fomat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
      
    /** 
     * 方法太多，但是他的注释非常详细 
     * @param args 
     */  
    public static void main(String[] args) {  
        //创建一个每秒执行一次的Trigger  
        //TriggerUtils.makeSecondlyTrigger("t_name");  
        //创建一个每星期某一特定时间点执行一次的Trigger  
        //TriggerUtils.makeWeeklyTrigger("t_name", 1, 1, 1);  
        //创建一个每5秒执行一次，总共执行10次的Trigger  
        TriggerUtils.makeSecondlyTrigger("t_name",5,10);  
        //获得时间的方法  
        println(TriggerUtils.getDateOf(0, 0, 0));  
        //返回下一个小时的时间，例如2015-05-04 11:20:00 返回2015-05-04 12:00:00  
        println(TriggerUtils.getEvenHourDate(new Date()));    
    }  
      
    public static void println(Date date){  
        System.out.println(fomat.format(date));  
    }  
}  