package com.outbound.impl.auto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import com.google.gson.Gson;
import com.outbound.dialer.util.RedisUtil;
import com.outbound.dialer.util.RosterUtil;
import com.outbound.dialer.util.XlsUtil;
import com.outbound.impl.TaskContainer;
import com.outbound.impl.metric.MetricUtil;
import com.outbound.impl.util.CallUtil;
import com.outbound.impl.util.MakeCall;
import com.outbound.impl.util.ReqResponse;
import com.outbound.job.CronJobManager;
import com.outbound.job.SimpleJobManager;
import com.outbound.object.ActivityInfo;
import com.outbound.object.AutoImportConfigModel;
import com.outbound.object.ConfigParam;
import com.outbound.object.Holiday;
import com.outbound.object.OutboundPolicyInfo;
import com.outbound.object.OutboundRecallPolicy;
import com.outbound.object.RosterBatchInfo;
import com.outbound.object.RosterInfo;
import com.outbound.object.TrunkNumber;
import com.outbound.object.TrunkPoolCorr;
import com.outbound.object.dao.ActivityInfoDAO;
import com.outbound.object.dao.ConfigParamDAO;
import com.outbound.object.dao.HolidayDAO;
import com.outbound.object.dao.OutboundRecallPolicyDAO;
import com.outbound.object.dao.RosterBatchInfoDAO;
import com.outbound.object.dao.RosterInfoDAO;
import com.outbound.object.dao.TrunkNumberDAO;
import com.outbound.object.dao.TrunkPoolCorrDAO;
import com.outbound.object.util.ApplicationContextUtil;
import com.outbound.object.util.GsonFactory;
import com.outbound.object.util.TimeUtil;

public class AutoCallThread extends Thread {

	private Logger logger = Logger.getLogger(AutoCallThread.class.getName());

	static Gson gson = new Gson();
	
	private ActivityInfoDAO activityDao = (ActivityInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("ActivityInfoDAO");
	
	private RosterBatchInfoDAO batchDao = (RosterBatchInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("RosterBatchInfoDAO");
	
	private RosterInfoDAO rosterDao = (RosterInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("RosterInfoDAO");
	
	private ConfigParamDAO configDao = (ConfigParamDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("ConfigParamDAO");
	
	private TrunkNumberDAO trunkNumberDao = (TrunkNumberDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("TrunkNumberDAO");
	
	private TrunkPoolCorrDAO trunkPoolCorrDao = (TrunkPoolCorrDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("TrunkPoolCorrDAO");
	
	private OutboundRecallPolicyDAO recallPolicyDao = (OutboundRecallPolicyDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("OutboundRecallPolicyDAO");
	
	private HolidayDAO holidayDao = (HolidayDAO) ApplicationContextUtil.getApplicationContext().getBean("HolidayDAO");

	// 0 未启动， 1 运行中， 2 暂停， 3 已停止
	private int status;
	private String taskType;
	private String activityName;
	private String domain;
	private int callLimit;
	
	private String dst;
	private String dstName;
	private String prefix;
	
	private List<TrunkNumber> trunks;
	private List<OutboundRecallPolicy> rounds;
	private int trunkSize;
	
	//private String uui = "name_gender_loanName_loanAmount_dueDate_days_idnum_wages_work1_wok2";
	private int trunkIndex = 0;

	private ActivityInfo activityInfo;
	private OutboundPolicyInfo policyInfo;
	
	private List<Holiday> holidayList;

	public AutoCallThread(String type, ActivityInfo _activity, OutboundPolicyInfo _policyInfo) {
		taskType = type;
		setActivityInfo(_activity);
		setPolicyInfo(_policyInfo);
		
		this.setName(activityName);
	}
	
	
	private TrunkNumber getAvaiableTrunk(){
		if(trunkSize <= 0){
			return null;
		}
		if(trunkIndex >= trunkSize){
			trunkIndex = trunkIndex % trunkSize;
		}
		return trunks.get(trunkIndex);
	}
	
	//firstName,sex,loanChannel,loanRepayAmount,sex,loanOverdueDay
	private String getUUIInfo(RosterInfo rInfo){
		String activity_uui = getActivityInfo().getUui();
		String[] uuis = activity_uui.split(",");
		if(uuis.length >0){
			Map<String, String> customerFields = gson.fromJson(rInfo.getCustomFields(),
					MetricUtil.mapList);
			String corrUUi="";
			for(String uui: uuis){
				if(uui.equals("phoneNum1")){
					corrUUi += rInfo.getPhoneNum1();
				}else if(uui.equals("phoneNum2")){
					corrUUi += rInfo.getPhoneNum2();
				}else if(uui.equals("phoneNum3")){
					corrUUi += rInfo.getPhoneNum3();
				}else if(uui.equals("phoneNum4")){
					corrUUi += rInfo.getPhoneNum4();
				}else if(uui.equals("phoneNum5")){
					corrUUi += rInfo.getPhoneNum5();
				}else if(uui.toLowerCase().equals("lastname")){
					corrUUi += rInfo.getLastname();
				}else if(uui.toLowerCase().equals("firstname")){
					corrUUi += rInfo.getFirstname();
				} else if(uui.equals("age")){
					corrUUi += rInfo.getAge();
				} else if(uui.equals("sex")){
					corrUUi += rInfo.getSex();
				} else if(uui.equals("customerId")){
					corrUUi += rInfo.getCustomerId();
				} else if(uui.equals("address")){
					corrUUi += rInfo.getAddress();
				} else if(uui.equals("email")){
					corrUUi += rInfo.getEmail();
				} else if(uui.equals("cardType")){
					corrUUi += rInfo.getCardType();
				} else if(uui.equals("cardNum")){
					corrUUi += rInfo.getCardNum();
				} else{
					if(customerFields!= null &&customerFields.containsKey(uui)){
						corrUUi += customerFields.get(uui);
					}else{
						corrUUi += "null";
					}
				}
				corrUUi += "_";
			}
			corrUUi = corrUUi.substring(0, corrUUi.length()-1);
			return corrUUi;
		}
		return "";
	}
	
	private void initParams(){
		holidayList = holidayDao.getTHolidays(domain, 0, Integer.MAX_VALUE);
		holidayList = (holidayList == null ? new ArrayList<Holiday>() : holidayList);
		String[] steps = getPolicyInfo().getCallAnswerStep().split("[|]");
		if(steps.length >=3 ){
			dstName = steps[2];
			ConfigParam pvalue = configDao.findByName(domain, dstName);
			if(pvalue != null){
				dst = pvalue.getValue().trim();
				logger.info("## param config dst number " + dst);
			}else{
				logger.info("## param config name error " + dstName);
			}
		}else{
			logger.info("## param answer setp error " + getPolicyInfo().getCallAnswerStep());
		}
		
		List<TrunkPoolCorr> corr =  trunkPoolCorrDao.getTTrunkPoolCorrs(domain, getActivityInfo().getTrunkGrp());
		if(corr != null && corr.size()>0){
			trunks = null;
			trunks = new ArrayList<TrunkNumber>();
			for(TrunkPoolCorr poolcorr : corr){
				TrunkNumber trunk = trunkNumberDao.findbyNum(domain, poolcorr.getDisplayNum());
				if(trunk != null){
					trunks.add(trunk);
				}
			}
			trunkSize = trunks.size();
		}
		
		rounds = recallPolicyDao.getTOutboundRecallPolicys(domain, getActivityInfo().getName(), 0, 10);
	}
	
	private String getRoundPhone(int round){
		if(rounds != null){
			for(OutboundRecallPolicy policy : rounds){
				if(policy.getRound()  == round){
					return policy.getPhoneNumCol();
				}
			}
		}
		return "phoneNum1";
	}
	
	private String getAreaPrefix(String phone){
		return "";
		/*
		String area = TelephoneDictionary.getInstance().getArea(phone);
		logger.info("getAreaPrefix " + phone + " "+ area);
		if(area != null){
			if(area.contains(this.getActivityInfo().getRegion())){
				return "";
			}
		}
		return "0";
		*/
	}
	

	
	private void callOutRoster(RosterInfo info,TrunkNumber t_trunk, RosterBatchInfo batchInfo){
		MakeCall mkcall = new MakeCall();
		mkcall.setAni(t_trunk.getDisplayNum());
		mkcall.setActivity_name(getActivityInfo().getName());
		mkcall.setCaller(prefix + info.getPhoneNum1());
		mkcall.setCallee(dst);
		mkcall.setDomain(info.getDomain());
		mkcall.setTrunkGrp(t_trunk.getTrunkGrp());
		mkcall.setRound(batchInfo.getCallRound());
		mkcall.setBatch_name(batchInfo.getBatchId());
		mkcall.setRosterinfo_id(""+info.getId());
		mkcall.setUui(getUUIInfo(info));
		
		String phoneCol = getRoundPhone(batchInfo.getCallRound());
		logger.info(batchInfo.getBatchId() + "   " + phoneCol);
		String areaFix ="";
		if(phoneCol.equals("phoneNum1")){
			areaFix = getAreaPrefix(info.getPhoneNum1());
			mkcall.setCaller(prefix+ areaFix + info.getPhoneNum1());
			info.setCurrentCallNum(info.getPhoneNum1());
		}else if(phoneCol.equals("phoneNum2")){
			areaFix = getAreaPrefix(info.getPhoneNum2());
			info.setCurrentCallNum(info.getPhoneNum2());
			mkcall.setCaller(prefix + areaFix+ info.getPhoneNum2());
		}else if(phoneCol.equals("phoneNum3")){
			areaFix = getAreaPrefix(info.getPhoneNum3());
			info.setCurrentCallNum(info.getPhoneNum3());
			mkcall.setCaller(prefix+ areaFix + info.getPhoneNum3());
		}else if(phoneCol.equals("phoneNum4")){
			areaFix = getAreaPrefix(info.getPhoneNum4());
			info.setCurrentCallNum(info.getPhoneNum4());
			mkcall.setCaller(prefix+ areaFix + info.getPhoneNum4());
		}else if(phoneCol.equals("phoneNum5")){
			areaFix = getAreaPrefix(info.getPhoneNum5());
			info.setCurrentCallNum(info.getPhoneNum5());
			mkcall.setCaller(prefix+ areaFix + info.getPhoneNum5());
		}
		
		info.setMakeCallNum(mkcall.getCaller());
		info.setAni(mkcall.getAni());
		info.setTrunkGroup(mkcall.getTrunkGrp());
		info.setUui(mkcall.getUui());
		
		ReqResponse result = CallUtil.makeCall(mkcall);
		if(result != null){
			RedisUtil.addRoster(info);
		}else{
			info.setMakeCallTime(TimeUtil.getCurrentTimeStr());
			info.setCallEndTime(TimeUtil.getCurrentTimeStr());
			info.setCallResult("外呼接口失败");
			info.setResultCode(12);
			info.setStatus(2);
			rosterDao.updateRosterInfo(info);
		}
		
	}
	
	private void waitTime(long mills){
		try {
			Thread.sleep(mills);
		} catch (InterruptedException e) {
			logger.error(e);
		}
	}

	@Override
	public void run() {
		long startTime = System.currentTimeMillis();
		logger.info("##### " + taskType + " taskthread [" + getActivityInfo().getName() + ":" + getActivityInfo().getDomain()
				+ "] start to run ");
		TaskContainer.autoCallActivityMap.put(activityName, this);
		initParams();
		if(status == 0){
			status = 1;	
		}
		boolean unFinishBatch = true;
		while(status != 3){
			checkActivityTime();
			while(status == 1){
				checkActivityTime();
				checkPolicyTime();
				RosterBatchInfo batchInfo = null;
				if(unFinishBatch)
				{
					batchInfo = batchDao.findinCallBatch(domain, getActivityInfo().getRosterTemplateName());
					if(batchInfo == null){
						batchInfo = batchDao.findNextUncallBatch(domain, getActivityInfo().getRosterTemplateName());
					}
					unFinishBatch = false;
				}else{
					batchInfo = batchDao.findNextUncallBatch(domain, getActivityInfo().getRosterTemplateName());
				}
				
				if(batchInfo != null){
					logger.info("## find batchInfo " + batchInfo.toString());
					List<RosterInfo> rosters = rosterDao.findBatchRosters(domain,  batchInfo.getBatchId(), this.getActivityInfo());
					if(rosters == null){
						logger.info("# no roster for batch " + batchInfo.getBatchId());
						batchInfo.setStatus(2);
						batchInfo.setStartTime(TimeUtil.getCurrentTimeStr());
						batchInfo.setCompleteTime(TimeUtil.getCurrentTimeStr());
						batchDao.updateRosterBatchInfo(batchInfo);
						waitTime(100);
						continue;
					}else{
						logger.info("# find roster "+ batchInfo+" size [" + rosters.size() +"]");
						batchInfo.setStatus(1);
						batchInfo.setStartTime(TimeUtil.getCurrentTimeStr());
						batchDao.updateRosterBatchInfo(batchInfo);
					}
					//MetricUtil.addRosterNum(getActivityInfo().getName(), getActivityInfo().getDomain(),
					//		batchInfo.getBatchId(), rosters.size() );
					for(RosterInfo info : rosters){
						logger.info("### start to process roster " + gson.toJson(info));
						while(status == 2){
							checkActivityTime();
							waitTime(100);
						}
						if(status == 3){
							break;
						}
						checkActivityTime();
						checkPolicyTime();
						TrunkNumber t_trunk = getAvaiableTrunk();
						if(t_trunk == null){
							logger.warn("## "+ batchInfo +"no current avaible trunk !");
						}else{
							info.setActivityName(getActivityInfo().getName());
							int currentCallnum = RedisUtil.getKeys(getActivityInfo().getName());
							if( currentCallnum < callLimit ){
								logger.info("## batchId ["+ batchInfo +"] currentCallnum call NUMBER [" + currentCallnum +"]");
								info.setCallee(dst);
								callOutRoster(info, t_trunk, batchInfo);
								MetricUtil.addCallOut(info);
							}else{
								logger.info("## "+ batchInfo +" out of call limt " + callLimit);
								while(currentCallnum >= callLimit){
									waitTime(100);
									currentCallnum = RedisUtil.getKeys(getActivityInfo().getName());
									if(status != 1){
										break;
									}
								}
								checkActivityTime();
								checkPolicyTime();
								if(status == 1){
									info.setCallee(dst);
									callOutRoster(info, t_trunk, batchInfo);
									MetricUtil.addCallOut(info);
								}
							}
						}
						waitTime(15);
					}
					logger.info("## roster batch all call out complete " + batchInfo.getBatchId());
					MetricUtil.addBatchInfo(batchInfo);
				}
				waitTime(1000);
			}
			waitTime(100);
		}
	
		long costtime = System.currentTimeMillis() - startTime;
		logger.info("---###### activity " + getActivityInfo().getName() + " excute complete costt "
				+ costtime / 1000 + " s");
		getActivityInfo().setStatus(3);
		getActivityInfo().setEndDatetime(TimeUtil.getCurrentTimeStr());
		activityDao.updateActivityInfo(getActivityInfo());
	
		TaskContainer.autoCallActivityMap.remove(activityName);
		SimpleJobManager.removeJob( getActivityInfo().getId() +"_back");
		CronJobManager.removeJob( getActivityInfo().getId() +"_back");
		excuteCallBack();
	}
	
	public void excuteCallBack(){
		if(getActivityInfo().getActivityBackAddrType() ==3)
		{
			AutoImportConfigModel config = GsonFactory.getGson().fromJson(getActivityInfo().getActivityBackAddr()
					, AutoImportConfigModel.class);
			if(config != null){
				List<RosterInfo> rosters = rosterDao.findFinishRosters(getActivityInfo().getDomain(),getActivityInfo().getName());
				if(rosters != null){
					List<List<String>> exportRosters = new ArrayList<List<String>>();
					for(RosterInfo roster: rosters){
						exportRosters.add(RosterUtil.getRosterInfos(roster));
					}
					rosterDao.updateFinishRosters(getActivityInfo().getDomain(),getActivityInfo().getName());
					try {
						String path = config.getContext().getPath() +"/" + getActivityInfo().getName()+"_"
								+ getActivityInfo().getDomain() +"_"+ TimeUtil.getExportCurrentTimeStr()+".xls";
						XlsUtil.exportRosterInfoXls(RosterUtil.getRosterHeaders(),
								exportRosters, path);
					} catch (IOException e) {
						logger.error(e);
					}
				}
			}
		}
	}
	

	public void stopTask() {
		status = 3;
		logger.info("##### stop taskthread " + activityName);
	}

	public void pauseTask() {
		status = 2;
		getActivityInfo().setStatus(2);
		activityDao.updateActivityInfo(getActivityInfo());
		logger.info("####pause taskthread " + activityName);
	}

	public void resumeTask() {
		initParams();
		status = 1;
		getActivityInfo().setStatus(1);
		//getActivityInfo().setActivityExecuteTime(TimeUtil.getCurrentTimeStr());
		activityDao.updateActivityInfo(getActivityInfo());
		
		logger.info("####resume taskthread " + activityName);
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}


	public ActivityInfo getActivityInfo() {
		return activityInfo;
	}


	public void setActivityInfo(ActivityInfo activityInfo) {
		this.activityInfo = activityInfo;
		if (activityInfo != null) {
			domain = getActivityInfo().getDomain();
			callLimit = getActivityInfo().getMaxCapacity();
			prefix = getActivityInfo().getPrefix();
			activityName = getActivityInfo().getName() + ":" + getActivityInfo().getDomain();
		}
	}

	private boolean isActivityEffective(){
		try{
			long bTime = TimeUtil.getDateTime(activityInfo.getBeginDatetime());
			long eTime = TimeUtil.getDateTime(activityInfo.getEndDatetime());
			//查看当前是否完成
			long cTime = System.currentTimeMillis();
			if (bTime > cTime){
				logger.info("## AutoCallThread isActivityEffective domain:" +activityInfo.getDomain() + 
						" activity: " + activityInfo.getName() + 
						" begintime:" + activityInfo.getBeginDatetime() + 
						"currentTime:" + cTime);
				return false;
			}
			if (eTime < cTime){
				logger.info("## AutoCallThread isActivityEffective domain:" +activityInfo.getDomain() + 
						" activity: " + activityInfo.getName() + 
						" endTime:" + activityInfo.getEndDatetime() + 
						"currentTime:" + cTime);
				this.stopTask();
				return false;
			}
		}catch(Exception e){
			return false;
		}
	
		return true;
	}
	
	private void checkActivityTime(){
		while(!isActivityEffective() && (status != 3)){
			try {
				logger.info("## check activity time fail !");
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				logger.error(e);
			}
		}
	}
	
	private void checkPolicyTime(){
		while(!isPolicyEffective() && (status != 3)){
			try {
				logger.info("## check policy time fail !");
				checkActivityTime();
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				logger.error(e);
			}
		}
	}
	
	private boolean isPolicyEffective(){
		if(isHoliday())
			return false;
		List<String> policyTimeList = policyInfo.getTimeRange();
		if (CollectionUtils.isEmpty(policyTimeList)) {
			return true;
		}
		boolean isEffective = false;
		try{
			for (String policyListStr : policyTimeList) {
				String[] policyTimeAttr = policyListStr.split("[|]");
				String policyBTime = policyTimeAttr[0].substring(0, policyTimeAttr[0].indexOf("*"));
				String policyETime = policyTimeAttr[1].substring(0, policyTimeAttr[1].indexOf("*"));
				Date bTime = TimeUtil.getDateTimeCron(policyBTime);
				Date eTime = TimeUtil.getDateTimeCron(policyETime);
				if (isEffective(bTime, eTime)){
					isEffective = true;
					break;
				} 
			}
		}catch(Exception e){
			return false;
		}
		return isEffective;
	}
	
	private boolean isEffective(Date bTime, Date eTime) {
		if(bTime == null || eTime == null){
			return false;
		}
		Date cTime = new Date();
		if (bTime.getTime() > cTime.getTime()){
			return false;
		}
		if (eTime.getTime() < cTime.getTime()){
			return false;
		}
		return true;
	}
	
	public boolean isWeekDay(){
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY||cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY)
		 return true;
		else 
		return false;
	}
	
	private boolean isHoliday(){
		boolean isHoliday = false;
		if (policyInfo.getWorkDayType() == 1){
			return isHoliday;
		}
		
		boolean isWeekDay = isWeekDay();
		if (isWeekDay){
			isHoliday = true;
		}
		Date cTime = new Date();
		for (Holiday holiday : holidayList){
			long bTime = TimeUtil.getDateTime(holiday.getStartDate());
			long eTime = TimeUtil.getDateTime(holiday.getEndDate());
			if (isWeekDay){
				isHoliday = true;
				//工作日
				if (holiday.getHolidayType() == 0){
					if (bTime <= cTime.getTime() && eTime >= cTime.getTime()){
						isHoliday = false;
						break;
					}
				}
			} else {
				isHoliday = false;
				//节假日
				if (holiday.getHolidayType() == 1){
					if (bTime <= cTime.getTime() && eTime >= cTime.getTime()){
						isHoliday = true;
						break;
					}
				}
			}
		}
		return isHoliday;

	}
	

	public OutboundPolicyInfo getPolicyInfo() {
		return policyInfo;
	}


	public void setPolicyInfo(OutboundPolicyInfo policyInfo) {
		this.policyInfo = policyInfo;
	}
}
