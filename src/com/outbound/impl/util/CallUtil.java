package com.outbound.impl.util;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.outbound.conf.InitConfig;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

public class CallUtil {

	private static final Logger log = Logger.getLogger(CallUtil.class.getName());

	static Gson gson = new Gson();
	static Client c = Client.create();
	static WebResource r = c.resource(InitConfig.OUTBOUND_URL);

	public static ReqResponse makeCall(MakeCall req) {
		
		if(req.getCaller().length() <=2){
			return null;
		}
		
		long startTime = System.currentTimeMillis();
		log.info("## MAKE CALL  post " + gson.toJson(req));
		//return null;
		GenericType<ReqResponse> genericType = new GenericType<ReqResponse>() {};
		try {
			ReqResponse t_result = r.accept(MediaType.APPLICATION_JSON).post(genericType, req);
			if (t_result != null) {
				log.info(t_result.toString() +"|" + req.getCaller());
			}
			long costTime = System.currentTimeMillis() - startTime;
			log.info("## MAKE CALL  post cost [" + costTime +"]ms");
			return t_result;
		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}
}
