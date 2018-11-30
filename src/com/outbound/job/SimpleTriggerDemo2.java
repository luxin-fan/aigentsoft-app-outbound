package com.outbound.job;

import java.util.Date;  
import org.quartz.JobDetail;  
import org.quartz.Scheduler;  
import org.quartz.SchedulerFactory;  
import org.quartz.SimpleTrigger;  
import org.quartz.impl.StdSchedulerFactory;  
  
/** 
 * @author Ickes 
 */  
public class SimpleTriggerDemo2 {  
    public static void main(String[] args) throws Exception {  
   
    	SimpleJobManager.addJob("test", SimpleJob.class,1, 1000, -1);
    	//SimpleJobManager.addJob("test2", SimpleJob.class, 1000, 5);
    	
    	//SimpleJobManager.shutdownJobs();
    }  
}  