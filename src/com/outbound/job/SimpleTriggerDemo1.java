package com.outbound.job;

import java.util.Date;  
import org.quartz.JobDetail;  
import org.quartz.Scheduler;  
import org.quartz.SchedulerFactory;  
import org.quartz.SimpleTrigger;  
import org.quartz.impl.StdSchedulerFactory;

import com.outbound.object.util.TimeUtil;  
  
/** 
 * @author Ickes 
 */  
public class SimpleTriggerDemo1 {  
    public static void main(String[] args) throws Exception {  
        //第一步：创建一个JobDetail实例  
        JobDetail jobDetail = new JobDetail("j_job1","j_group1", SimpleJob.class);  
        SimpleTrigger simpleTrigger = new SimpleTrigger("trigger1_1","tgroup1");
        Date startTime = TimeUtil.nextGivenSecondDate(30*1000);
        simpleTrigger.setStartTime(startTime);//开始运行时间  
        simpleTrigger.setRepeatInterval(0); //运行间隔单位为毫秒  
        simpleTrigger.setRepeatCount(0);     //运行次数  
        simpleTrigger.setJobGroup("j_group1");  
        simpleTrigger.setJobName("j_job1");  
        //第三步：通过SchedulerFactory获取一个调度器实例  
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();  
        Scheduler scheduler = schedulerFactory.getScheduler();  
        //第四步：将job跟trigger注册到scheduler中进行调度  
        scheduler.addJob(jobDetail, true);  
        scheduler.scheduleJob(simpleTrigger);  
        //第五步：调度启动  
        scheduler.start();  
    }  
}  