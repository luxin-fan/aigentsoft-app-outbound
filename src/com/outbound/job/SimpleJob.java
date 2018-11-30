package com.outbound.job;

import org.quartz.Job;  
import org.quartz.JobExecutionContext;  
import org.quartz.JobExecutionException;  
/** 
 * 实现Job接口，定义运行的任务 
 * @author Ickes 
 */  
public class SimpleJob implements Job {  
  
    public void execute(JobExecutionContext context)  
            throws JobExecutionException {  
        //打印任务详情  
        System.out.println(  
                context.getJobDetail().getGroup()   
                +"——"+context.getJobDetail().getName()  
                +"——"+context.getTrigger().getName()  
                +"——"+context.getTrigger().getGroup());  
    }  
  
}  