package com.outbound.impl.util;

import javax.ws.rs.core.MediaType;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.outbound.conf.InitConfig;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

public class BatchUtil {

	private static final Logger log = Logger.getLogger(BatchUtil.class.getName());

	static{
		try {
			InitConfig.init();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	static Gson gson = new Gson();
	static Client c = Client.create();
	static WebResource r = c.resource(InitConfig.PENNY_URL);

	public static BatchResponse postBatchResult(BatchReq req) {
		long startTime = System.currentTimeMillis();
		log.info("## BATCH COMPLETE  post " + gson.toJson(req));
		//return null;
		GenericType<BatchResponse> genericType = new GenericType<BatchResponse>() {};
		try {
			BatchResponse t_result = r.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).post(genericType, req);
			if (t_result != null) {
				log.info("# Result " +t_result.toString() +"|" + req.getBatchId());
			}
			long costTime = System.currentTimeMillis() - startTime;
			log.info("## BATCH COMPLETE  post cost [" + costTime +"] ms");
			return t_result;
		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}
	
	public static void main(String[] args){
		
		BatchReq req = new BatchReq();
		req.setBatchId("13496");
		req.setType("batch");
		
		postBatchResult(req);
	}
	
}
