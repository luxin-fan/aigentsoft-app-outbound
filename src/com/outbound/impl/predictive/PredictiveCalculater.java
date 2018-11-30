package com.outbound.impl.predictive;

import org.apache.log4j.Logger;

public class PredictiveCalculater {
	
	private static final Logger log = Logger.getLogger(PredictiveCalculater.class
			.getName());
	
	public float calculateBusyFactor(float avgTime, float hitrate, float avgSuccess, float avgFail){

		if(avgTime <=0 || avgSuccess <=0 || hitrate <=0 || avgFail <=0){
			log.info("params is zero fail !");
			return 0.0f;
		}
		
		float param_b = avgTime + (1 - hitrate)*avgFail/hitrate + avgSuccess;
		float result = avgTime / param_b;
		result = (float)(Math.round(result*100)/100f);
		return result;
	}
	
	//calculate as every minute
	public int calculateOutboundRate(int readyNum, float abandonRate, float avgTime, float hitrate){
		float erlang_value = ErlangDictionary.getInstance().
				calculateErlang(readyNum, abandonRate);
		
		int result = Math.round((erlang_value/avgTime)/hitrate);
		
		return result;
	}
	
	public static void main(String[] args){
		ErlangDictionary.getInstance().reload();
		float erlang_value = ErlangDictionary.getInstance().
				calculateErlang(30, 0.05f);
		System.out.println("compute erlang value " + erlang_value);
		
		int outbound_rate = new PredictiveCalculater().calculateOutboundRate(50, 0.05f, 5.0f, 0.6f);
		System.out.println("compute outbound_rate " + outbound_rate +" every minute");
	} 
}
