package com.outbound;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;

import com.ces.telephonedictionary.TelephoneDictionary;
import com.outbound.conf.InitConfig;
import com.outbound.dialer.util.RedisUtil;
import com.outbound.job.ActivityUtil;
import com.outbound.job.JobUtil;
import com.outbound.monitor.AppServiceStatusTask;

public class OutboundStarter {

	private static Logger logger = Logger.getLogger(OutboundStarter.class.getName());

	public static String version;

	public static void main(String[] args) throws Exception {

		try {
			version = "v1.4.1-2018/11/27";
			InitConfig.init();
			Server server = new Server();

			Connector connector = new SelectChannelConnector();
			connector.setPort(Integer.parseInt(InitConfig.PORT));
			connector.setHost(InitConfig.BIND_IP);

			server.setConnectors(new Connector[] { connector });

			WebAppContext webAppContext = new WebAppContext("WebContent", "/WebRoot");

			webAppContext.setContextPath("/orm");
			webAppContext.setDescriptor("WebRoot/WEB-INF/web.xml");
			webAppContext.setResourceBase("WebRoot");
			webAppContext.setDisplayName("orm");
			webAppContext.setClassLoader(Thread.currentThread().getContextClassLoader());
			webAppContext.setConfigurationDiscovered(true);
			webAppContext.setParentLoaderPriority(true);
			server.setHandler(webAppContext);
			logger.info(webAppContext.getContextPath());
			logger.info(webAppContext.getDescriptor());
			logger.info(webAppContext.getResourceBase());
			logger.info(webAppContext.getBaseResource());

			server.start();
			logger.info("initialize server... ");

			RedisUtil.init();
			JobUtil.init();
			ActivityUtil.init();

			TelephoneDictionary.getInstance().reload();
			AppServiceStatusTask appstatus = new AppServiceStatusTask();
			appstatus.start();
			logger.info("server is start ip " + InitConfig.BIND_IP);
			logger.info("server is start port " + InitConfig.PORT);
			logger.info("server version is " + version);
		} catch (Exception e) {
			logger.error("global error", e);
		}
	}
}
