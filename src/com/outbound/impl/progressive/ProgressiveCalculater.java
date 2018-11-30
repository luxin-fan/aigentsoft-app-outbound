package com.outbound.impl.progressive;

import org.apache.log4j.Logger;


public class ProgressiveCalculater extends Thread{
	
	private static final Logger log = Logger.getLogger(ProgressiveCalculater.class
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
	
	public int calculateOutboundRate(int Nr, int Nd){
		if(Nr >= Nd){
			return Nr-Nd;
		}
		return 0;
	}
	
	public static void main(String[] args){
		ProgressiveCalculater cal = new ProgressiveCalculater();
		float result = cal.calculateBusyFactor(60f, 0.6f, 70f, 15f);
		System.out.println("cal busy factor is " + result); 
	}
}
