package com.outbound.job;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.google.gson.Gson;
import com.outbound.dialer.util.POIUtil;
import com.outbound.impl.metric.MetricUtil;
import com.outbound.object.ActivityInfo;
import com.outbound.object.AutoImportConfigModel;
import com.outbound.object.FilterConditionModel;
import com.outbound.object.RosterBatchInfo;
import com.outbound.object.RosterInfo;
import com.outbound.object.RosterTemplateInfo;
import com.outbound.object.dao.ActivityInfoDAO;
import com.outbound.object.dao.DNCNumberDAO;
import com.outbound.object.dao.RosterBatchInfoDAO;
import com.outbound.object.dao.RosterInfoDAO;
import com.outbound.object.dao.RosterTemplateDAO;
import com.outbound.object.util.ApplicationContextUtil;
import com.outbound.object.util.FileUtil;
import com.outbound.object.util.GsonFactory;
import com.outbound.object.util.TimeUtil;

public class RosterImportJob implements Job {

	private Logger logger = Logger.getLogger(RosterImportJob.class.getName());

	private RosterTemplateDAO rosterTemplateDao = (RosterTemplateDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("RosterTemplateDAO");

	private RosterInfoDAO rosterDao = (RosterInfoDAO) ApplicationContextUtil.getApplicationContext().getBean("RosterDAO");

	private RosterBatchInfoDAO rosterBatchDao = (RosterBatchInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("RosterBatchInfoDAO");
	
	private ActivityInfoDAO activityDao = (ActivityInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("ActivityInfoDAO");
	
	private DNCNumberDAO dncDao = (DNCNumberDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("DNCNumberDAO");

	private RosterTemplateInfo rosterTemplate;
	private AutoImportConfigModel config;

	// +"——"+context.getJobDetail().getName()
	// config = GsonFactory.getGson().fromJson(dncTemplate.getImportPath(),
	// ImportConfigModel.class);

	static Gson gson = new Gson();
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		long currentTiem = System.currentTimeMillis();
		logger.info("#@## roster [" + context.getJobDetail().getName() + "] import task start at "
				+ new Timestamp(currentTiem).toString());
		String jobname = context.getJobDetail().getName();
		jobname = jobname.replace("_roster_job", "");
		rosterTemplate = rosterTemplateDao.findById(jobname);
		if (rosterTemplate != null) {
			config = GsonFactory.getGson().fromJson(rosterTemplate.getImportPath(),
					AutoImportConfigModel.class);
			// file
			if (config.getSource().equals("file")) {
				/*
				String[] files = FileUtil.getConfFiles(config.getContext().getPath());
				if (files!= null && files.length > 0) {
					for (String fpath : files) {
						getRosterFile(config.getContext().getPath() + "/" + fpath);
					}
				}*/
				getRosterFile(config.getContext().getPath());
				FileUtil.deleteFile(config.getContext().getPath());
			}
		}
	}

	private void getRosterFile(String path) {
		try {
			path = path.trim();
			FilterConditionModel filterConditionModel = GsonFactory.getGson().fromJson(rosterTemplate.getFilterCondition(),
					FilterConditionModel.class);
			if (filterConditionModel.getClear() == 1) {
				rosterDao.clearTemplate(rosterTemplate.getName(), rosterTemplate.getDomain());
				rosterBatchDao.clearTemplate(rosterTemplate.getName(), rosterTemplate.getDomain());
			}
			//readRosterExcel
			ArrayList<HashMap<String,String>> rosters = new ArrayList<HashMap<String,String>>();
			List<String[]> params = POIUtil.readRosterExcel(path);
			if(params != null && params.size() >0 ){
				String[] headers = params.get(0);
				for(int i=1; i<params.size(); i++){
					HashMap<String,String> rosterInfos = new HashMap<String,String>();
					for(int j=0; j< headers.length; j++){
						if(params.get(i).length <= j){
							rosterInfos.put(headers[j], "");
						}else{
							rosterInfos.put(headers[j], params.get(i)[j]);	
						}
					}
					rosters.add(rosterInfos);
				}
			}
			

			//filter roster
			String templateName = rosterTemplate.getName();
			int count = 0;
			ActivityInfo info =null;
			String domain = rosterTemplate.getDomain();
			String batchName = rosterTemplate.getName()+"_"+System.currentTimeMillis();
			if(rosters != null && rosters.size() > 0){
				for(HashMap<String,String> rosterMap: rosters){
					RosterInfo rosterInfo = new RosterInfo();
					rosterInfo.setDomain(domain);
					if(info!= null){
						rosterInfo.setActivityName(info.getName());
					}
					rosterInfo.setTemplateName(templateName);
					rosterInfo.setBatchName(batchName);
					boolean hit = false;
					if(filterConditionModel.getKey() != null){
						for(String key : filterConditionModel.getKey()){
							if(rosterMap.containsKey(key)){
								//int length = rosterMap.get(key).trim().length();
								if((rosterMap.get(key)!=null)){
									int length = rosterMap.get(key).trim().length();
									if(length == 0){
										hit = true;
										continue;
									}
								}
							}else{
								hit = true;
								continue;
							}
						}
					}
					if(hit){
						continue;
					}
					if(rosterMap.containsKey("batchId")){
						
						rosterInfo.setBatchName(rosterMap.get("batchId"));
						rosterMap.remove("batchId");
					}
					
					if(rosterMap.containsKey("phoneNum1")){
						rosterInfo.setPhoneNum1(rosterMap.get("phoneNum1"));
						rosterMap.remove("phoneNum1");
					}
					if(rosterMap.containsKey("phoneNum2")){
						rosterInfo.setPhoneNum2(rosterMap.get("phoneNum2"));
						rosterMap.remove("phoneNum2");
					}
					if(rosterMap.containsKey("phoneNum3")){
						rosterInfo.setPhoneNum3(rosterMap.get("phoneNum3"));
						rosterMap.remove("phoneNum3");
					}
					if(rosterMap.containsKey("phoneNum4")){
						rosterInfo.setPhoneNum4(rosterMap.get("phoneNum4"));
						rosterMap.remove("phoneNum4");
					}
					if(rosterMap.containsKey("phoneNum5")){
						rosterInfo.setPhoneNum5(rosterMap.get("phoneNum5"));
						rosterMap.remove("phoneNum5");
					}
					if(rosterMap.containsKey("lastname")){
						rosterInfo.setLastname(rosterMap.get("lastname"));
						rosterMap.remove("lastname");
					}
					if(rosterMap.containsKey("firstname")){
						rosterInfo.setFirstname(rosterMap.get("firstname"));
						rosterMap.remove("firstname");
					}
					if(rosterMap.containsKey("age")){
						try{
							rosterInfo.setAge(Integer.parseInt(rosterMap.get("age")));
						}catch(Exception e){
							rosterInfo.setAge(0);
						}
						rosterMap.remove("age");
					}
					if(rosterMap.containsKey("sex")){
						rosterInfo.setSex(rosterMap.get("sex"));
						rosterMap.remove("sex");
					}
					if(rosterMap.containsKey("customerId")){
						rosterInfo.setCustomerId(rosterMap.get("customerId"));
						rosterMap.remove("customerId");
					}
					if(rosterMap.containsKey("address")){
						rosterInfo.setAddress(rosterMap.get("address"));
						rosterMap.remove("address");
					}
					if(rosterMap.containsKey("email")){
						rosterInfo.setEmail(rosterMap.get("email"));
						rosterMap.remove("email");
					}
					if(rosterMap.containsKey("cardType")){
						rosterInfo.setCardType(rosterMap.get("cardType"));
						rosterMap.remove("cardType");
					}
					if(rosterMap.containsKey("cardNum")){
						rosterInfo.setCardNum(rosterMap.get("cardNum"));
						rosterMap.remove("cardNum");
					}
					rosterInfo.setCreateTime(TimeUtil.getCurrentTimeStr());
					if(!rosterMap.isEmpty()){
						rosterInfo.setCustomFields(gson.toJson(rosterMap));
					}
					rosterInfo.setCallRound(1);
					if(filterConditionModel.getRemoval() == 1){
						if(rosterDao.isRosteExit(rosterInfo)){
							continue;
						}
					}
					if(filterConditionModel.getDNC() != null && filterConditionModel.getDNC().size()>0){
						if(rosterInfo.getPhoneNum1()!= null){
							int ret = dncDao.getTDNCNumberNumQuery(rosterInfo.getDomain(),
									filterConditionModel.getDNC().get(0), rosterInfo.getPhoneNum1());
							if(ret > 0){
								continue;
							}
						}
						
						if(rosterInfo.getPhoneNum2()!= null){
							int ret = dncDao.getTDNCNumberNumQuery(rosterInfo.getDomain(),
									filterConditionModel.getDNC().get(0), rosterInfo.getPhoneNum2());
							if(ret > 0){
								continue;
							}
						}
						
						if(rosterInfo.getPhoneNum3()!= null){
							int ret = dncDao.getTDNCNumberNumQuery(rosterInfo.getDomain(),
									filterConditionModel.getDNC().get(0), rosterInfo.getPhoneNum3());
							if(ret > 0){
								continue;
							}
						}
						
						if(rosterInfo.getPhoneNum4()!= null){
							int ret = dncDao.getTDNCNumberNumQuery(rosterInfo.getDomain(),
									filterConditionModel.getDNC().get(0), rosterInfo.getPhoneNum4());
							if(ret > 0){
								continue;
							}
						}
						
						if(rosterInfo.getPhoneNum5()!= null){
							int ret = dncDao.getTDNCNumberNumQuery(rosterInfo.getDomain(),
									filterConditionModel.getDNC().get(0), rosterInfo.getPhoneNum5());
							if(ret > 0){
								continue;
							}
						}
					}
					
					rosterDao.createRosterInfo(rosterInfo);
					count++;
				}
				
				RosterBatchInfo batchInfo = new RosterBatchInfo();
				if(batchName != null){
					batchInfo.setBatchId(batchName);
					batchInfo.setTemplateName(templateName);
					batchInfo.setCreateTime(TimeUtil.getCurrentTimeStr());
					batchInfo.setRoterNum(count);
					batchInfo.setCallRound(1);
					batchInfo.setDomain(domain);
					info = activityDao.getTActivityInfos(domain, templateName);
					if(info!= null){
						batchInfo.setActivityId(info.getId());
						batchInfo.setActivityName(info.getName());
						info.addRosterNum(count);
						info.addBatchNum();
						activityDao.updateActivityInfo(info);
						MetricUtil.addRostersDay(info.getName(), domain, count);
					}
					rosterBatchDao.createRosterBatchInfo(batchInfo);
				}
			}
		} catch (IOException e) {
			logger.error(e);
		}
	}
}
