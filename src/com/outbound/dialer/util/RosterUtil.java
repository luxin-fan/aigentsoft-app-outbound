package com.outbound.dialer.util;

import java.util.ArrayList;
import java.util.List;

import com.outbound.object.RosterInfo;

public class RosterUtil {

	public static List<String> getRosterHeaders(){
		
		 List<String> headers = new ArrayList<String>();
		 headers.add("记录Id");
		 headers.add("外呼号码");
		 //headers.add("号码2");
		 //headers.add("号码3");
		 headers.add("姓名");
		 /*
		 headers.add("年龄");
		 headers.add("性别");
		 headers.add("客户ID");
		 headers.add("Email");
		 headers.add("地址");
		 headers.add("卡类型");
		 headers.add("卡号");
		 headers.add("自定义字段");
		 */
		 headers.add("创建时间");
		 headers.add("呼叫ID");
		 headers.add("被叫号码");
		 headers.add("呼叫开始时间");
		 headers.add("呼叫接通时间");
		 headers.add("呼叫结束时间");
		 headers.add("呼叫结果");
		 headers.add("呼叫时长");
		 headers.add("呼叫接通时长");
		 headers.add("呼叫批次名");
		 headers.add("呼叫活动名");
		 headers.add("域名");
		 headers.add("轮次");
		 
		 return headers;
	}
	
	public static List<String> getRosterInfos(RosterInfo rinfo){
		
		 List<String> rinfoContent = new ArrayList<String>();
		 rinfoContent.add(""+rinfo.getId());
		 rinfoContent.add(rinfo.getCurrentCallNum());
		 //rinfoContent.add(rinfo.getPhoneNum2());
		 //rinfoContent.add(rinfo.getPhoneNum3());
		 rinfoContent.add(rinfo.getFirstname());
		 //rinfoContent.add(rinfo.getLastname());
		 /*
		 headers.add("年龄");
		 headers.add("性别");
		 headers.add("客户ID");
		 headers.add("Email");
		 headers.add("地址");
		 headers.add("卡类型");
		 headers.add("卡号");
		 headers.add("自定义字段");
		 */
		 rinfoContent.add(rinfo.getCreateTime());
		 rinfoContent.add(rinfo.getCallId());
		 rinfoContent.add(rinfo.getCallee());
		 rinfoContent.add(rinfo.getMakeCallTime());
		 rinfoContent.add(rinfo.getCallAnswerTime());
		 rinfoContent.add(rinfo.getCallEndTime());
		 rinfoContent.add(rinfo.getCallResult());
		 rinfoContent.add(rinfo.getCallTime()+"");
		 rinfoContent.add(rinfo.getAnswerCallTime()+"");
		 rinfoContent.add(rinfo.getBatchName());
		 rinfoContent.add(rinfo.getActivityName());
		 rinfoContent.add(rinfo.getDomain());
		 rinfoContent.add(rinfo.getCallRound()+"");
		 
		 return rinfoContent;
		 
	}
}
