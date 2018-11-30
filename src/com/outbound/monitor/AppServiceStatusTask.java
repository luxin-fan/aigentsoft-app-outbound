package com.outbound.monitor;

import org.apache.log4j.Logger;

import com.ces.appserver.ServiceInfoTask;
import com.ces.appserver.ServiceStatusTask;
import com.outbound.OutboundStarter;
import com.outbound.conf.InitConfig;

public class AppServiceStatusTask extends Thread {

	private static final Logger log = Logger.getLogger(AppServiceStatusTask.class.getName());

	private ServiceStatusTask statusTask = null;
	private ServiceInfoTask metricTask = null;

	public void run() {

		try {
			statusTask = new ServiceStatusTask(InitConfig.DB_URL, InitConfig.DB_PWD);
			statusTask.setProductInfo(InitConfig.INSTANCE_ID, OutboundStarter.version);
			//statusTask.subscriberInfo("switch_conf", new ConfSubscriber());
			statusTask.start();

			metricTask = new ServiceInfoTask(InitConfig.DB_URL, InitConfig.DB_PWD);
			metricTask.setProductInfo(InitConfig.INSTANCE_ID);
			metricTask.start();
		} catch (Exception e) {
			log.error(e);
		}

	}
}
