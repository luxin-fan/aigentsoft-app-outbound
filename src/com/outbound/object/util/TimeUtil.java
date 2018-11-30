package com.outbound.object.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

	static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	static SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	static SimpleDateFormat formatter4 = new SimpleDateFormat ("ss mm HH");
	static SimpleDateFormat formatter5 = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
	
	public static String getCurrentTimeStr() {
		String ctime = formatter2.format(new Date());
		return ctime;
	}
	
	public static String getExportCurrentTimeStr() {
		String ctime = formatter5.format(new Date());
		return ctime;
	}

	public static String getCurrentDateStr() {
		String ctime = formatter.format(new Date());
		return ctime;
	}
	
	public static long getDateTime(String time){
		Timestamp stamp = Timestamp.valueOf(time);
		return stamp.getTime();
	}
	
	public static String formatSysCurrentTimeStr(Long microSeconds)
	{
		return formatter2.format(microSeconds);
	}

	public static Date nextGivenSecondDate(int mseconds) {
		long currentTime = System.currentTimeMillis();
		currentTime += mseconds;
		Date date = new Date(currentTime);
		return date;
	}

	public static String formatCurrentTimeStr(Long microSeconds) {
		microSeconds = microSeconds / 1000;
		Timestamp stamp = new Timestamp(microSeconds);
		return stamp.toString();
	}

	public static long getTimeEclipse(Long sTime, Long eTime) {
		if (eTime < sTime) {
			return 0;
		}
		Long time = eTime - sTime;
		time = time / 1000000;
		return time;
	}

	public static long getTimeEclipse(String sTime, String eTime) {

		Timestamp stamp = Timestamp.valueOf(sTime);
		Timestamp etamp = Timestamp.valueOf(eTime);
		if (etamp.getTime() < stamp.getTime()) {
			return 0;
		}
		Long time = etamp.getTime() - stamp.getTime();
		time = time / 1000;
		return time;
	}
	
	@SuppressWarnings("deprecation")
	public static Date getDateTimeCron(String cron) {
		cron = cron.trim();
		String[] params = cron.split(" ");
		if(params.length == 3){
			Date d = new Date();
			d.setSeconds(Integer.parseInt(params[0]));
			d.setMinutes(Integer.parseInt(params[1]));
			d.setHours(Integer.parseInt(params[2]));
			return d;
		}else{
			return null;
		}
	}
	

	public static void main(String[] args){
		//System.out.println(getDateTime("2018-06-12 00:00:00 "));
		//Date d = new Date();
		System.out.println(System.currentTimeMillis());
		//System.out.println(d.getTime());
		
		String tStr = "0 0 9 * * ?|0 30 20 * * ?";
		String[] policyTimeAttr = tStr.split("[|]");
		String policyBTime = policyTimeAttr[0].substring(0, policyTimeAttr[0].indexOf("*"));
		String policyETime = policyTimeAttr[1].substring(0, policyTimeAttr[1].indexOf("*"));
		
		System.out.println(policyBTime);
		System.out.println(policyETime);
		
		Date bTime = TimeUtil.getDateTimeCron(policyBTime);
		Date eTime = TimeUtil.getDateTimeCron(policyETime);

		System.out.println(bTime.getTime());
		System.out.println(eTime.getTime());
		System.out.println(formatSysCurrentTimeStr(System.currentTimeMillis() + 300000));
	}

}
