package com.outbound.impl.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import com.ces.telephonedictionary.TelephoneDictionary;
import com.google.gson.Gson;
import com.outbound.common.Util;
import com.outbound.dialer.util.RedisUtil;
import com.outbound.dialer.util.RosterUtil;
import com.outbound.dialer.util.XlsUtil;
import com.outbound.impl.TaskContainer;
import com.outbound.impl.metric.MetricUtil;
import com.outbound.impl.util.AOUtil;
import com.outbound.impl.util.CallUtil;
import com.outbound.impl.util.MakeCall;
import com.outbound.impl.util.ReqResponse;
import com.outbound.job.CronJobManager;
import com.outbound.job.SimpleJobManager;
import com.outbound.object.ActivityInfo;
import com.outbound.object.AutoImportConfigModel;
import com.outbound.object.BatchRosters;
import com.outbound.object.ConfigParam;
import com.outbound.object.Holiday;
import com.outbound.object.OutboundPolicyInfo;
import com.outbound.object.OutboundRecallPolicy;
import com.outbound.object.Roster;
import com.outbound.object.RosterBatch;
import com.outbound.object.RosterInfo;
import com.outbound.object.TrunkNumber;
import com.outbound.object.TrunkPoolCorr;
import com.outbound.object.dao.ActivityInfoDAO;
import com.outbound.object.dao.ConfigParamDAO;
import com.outbound.object.dao.HolidayDAO;
import com.outbound.object.dao.RosterBatchDAO;
import com.outbound.object.dao.RosterDAO;
import com.outbound.object.dao.RosterInfoDAO;
import com.outbound.object.dao.TrunkNumberDAO;
import com.outbound.object.dao.TrunkPoolCorrDAO;
import com.outbound.object.util.ApplicationContextUtil;
import com.outbound.object.util.GsonFactory;
import com.outbound.object.util.TimeUtil;

public class ActivityThread implements Runnable {

	private Logger logger = Logger.getLogger(ActivityThread.class);

	private static Gson gson = new Gson();

	private String activityName;
	private String domain;
	private int callLimit;

	private String dst;
	private String dstName;
	private String prefix;

	// 0 未启动， 1 运行， 2 暂停， 3 停止
	private int status = 0;

	private List<TrunkNumber> trunks;
	private List<OutboundRecallPolicy> rounds;
	private int trunkSize = 0;

	private int trunkIndex = 0;

	private ActivityInfo activityInfo;
	private OutboundPolicyInfo policyInfo;

	private List<Holiday> holidayList;

	private RosterPool rosterPoll = null;

	private HolidayDAO holidayDao = (HolidayDAO) ApplicationContextUtil.getApplicationContext().getBean("HolidayDAO");

	private ActivityInfoDAO activityDao = (ActivityInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("ActivityInfoDAO");

	private RosterInfoDAO rosterInfoDao = (RosterInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("RosterInfoDAO");

	private RosterDAO rosterDao = (RosterDAO) ApplicationContextUtil.getApplicationContext().getBean("RosterDAO");

	private RosterBatchDAO batchDAO = (RosterBatchDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("RosterBatchDAO");
	
	private ConfigParamDAO configDao = (ConfigParamDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("ConfigParamDAO");

	private TrunkNumberDAO trunkNumberDao = (TrunkNumberDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("TrunkNumberDAO");

	private TrunkPoolCorrDAO trunkPoolCorrDao = (TrunkPoolCorrDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("TrunkPoolCorrDAO");

	public void setPolicyInfo(OutboundPolicyInfo pInfo) {
		policyInfo = pInfo;
	}

	public ActivityThread(ActivityInfo _activity, OutboundPolicyInfo _policyInfo, RosterPool rosterPoll) {
		// TODO Auto-generated constructor stub
		activityInfo = _activity;
		policyInfo = _policyInfo;
		this.rosterPoll = rosterPoll;

		domain = activityInfo.getDomain();
		callLimit = activityInfo.getMaxCapacity();
		prefix = activityInfo.getPrefix();
		activityName = activityInfo.getName() + ":" + activityInfo.getDomain();
	}

	private void init() {
		holidayList = holidayDao.getTHolidays(domain, 0, Integer.MAX_VALUE);
		holidayList = (holidayList == null ? new ArrayList<Holiday>() : holidayList);
		String[] steps = policyInfo.getCallAnswerStep().split("[|]");
		if (steps.length >= 3) {
			dstName = steps[2];
			ConfigParam pvalue = configDao.findByName(domain, dstName);
			if (pvalue != null) {
				dst = pvalue.getValue().trim();
				logger.info("##### param config dst number " + dst);
			} else {
				logger.info("##### param config name error " + dstName);
			}
		} else {
			logger.info("##### param answer setp error " + policyInfo.getCallAnswerStep());
		}

		List<TrunkPoolCorr> corr = trunkPoolCorrDao.getTTrunkPoolCorrs(domain, activityInfo.getTrunkGrp());

		if (corr != null && corr.size() > 0) {
			trunks = null;
			trunks = new ArrayList<TrunkNumber>();
			for (TrunkPoolCorr poolcorr : corr) {
				TrunkNumber trunk = trunkNumberDao.findbyNum(domain, poolcorr.getDisplayNum());
				if (trunk != null) {
					trunks.add(trunk);
				}
			}
			trunkSize = trunks.size();
		}
	}

	public void setActivityInfo(ActivityInfo activityInfo) {
		this.activityInfo = activityInfo;
		if (activityInfo != null) {
			logger.info("##### get trunks size:" + trunkSize + " " + gson.toJson(trunks));
			domain = activityInfo.getDomain();
			callLimit = activityInfo.getMaxCapacity();
			prefix = activityInfo.getPrefix();
			activityName = activityInfo.getName() + ":" + activityInfo.getDomain();
		}
	}

	private boolean isActivityEffective() {
		try {
			long bTime = TimeUtil.getDateTime(activityInfo.getBeginDatetime());
			long eTime = TimeUtil.getDateTime(activityInfo.getEndDatetime());
			// 查看当前是否完成
			long cTime = System.currentTimeMillis();

			if (bTime > cTime) {
				logger.info("#### ActivityThread isActivityEffective domain:" + activityInfo.getDomain() + " activity: "
						+ activityInfo.getName() + " begintime:" + activityInfo.getBeginDatetime() + "currentTime:"
						+ TimeUtil.formatSysCurrentTimeStr(cTime));
				return false;
			}

			if (eTime < cTime) {
				logger.info("#### ActivityThread isActivityEffective domain:" + activityInfo.getDomain() + " activity: "
						+ activityInfo.getName() + " endTime:" + activityInfo.getEndDatetime() + "currentTime:"
						+ TimeUtil.formatSysCurrentTimeStr(cTime));
				this.stopTask();
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private void checkActivityPause() {
		while (status == 2) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void checkActivityTime() {
		while (!isActivityEffective() && (status != 3)) {
			try {
				logger.info("#### check activity time fail !");
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				logger.error(e);
			}
		}
	}

	private void checkPolicyTime() {
		while (!isPolicyEffective() && (status != 3)) {
			try {
				logger.info("#### check policy time fail !");
				checkActivityTime();
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				logger.error(e);
			}
		}
	}

	private boolean isPolicyEffective() {
		if (isHoliday()) {
			return false;
		}
		List<String> policyTimeList = policyInfo.getTimeRange();
		if (CollectionUtils.isEmpty(policyTimeList)) {
			return true;
		}
		boolean isEffective = false;
		try {
			for (String policyListStr : policyTimeList) {
				String[] policyTimeAttr = policyListStr.split("[|]");
				String policyBTime = policyTimeAttr[0].substring(0, policyTimeAttr[0].indexOf("*"));
				String policyETime = policyTimeAttr[1].substring(0, policyTimeAttr[1].indexOf("*"));
				Date bTime = TimeUtil.getDateTimeCron(policyBTime);
				Date eTime = TimeUtil.getDateTimeCron(policyETime);

				if (isEffective(bTime, eTime)) {
					isEffective = true;
					break;
				}
			}
		} catch (Exception e) {
			return false;
		}
		return isEffective;
	}

	private boolean isEffective(Date bTime, Date eTime) {
		if (bTime == null || eTime == null) {
			return false;
		}
		Date cTime = new Date();
		if (bTime.getTime() > cTime.getTime()) {
			return false;
		}
		if (eTime.getTime() < cTime.getTime()) {
			return false;
		}
		return true;
	}

	public boolean isWeekDay() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isHoliday() {
		boolean isHoliday = false;
		if (policyInfo.getWorkDayType() == 1) {
			return isHoliday;
		}

		boolean isWeekDay = isWeekDay();
		if (isWeekDay) {
			isHoliday = true;
		}
		Date cTime = new Date();
		for (Holiday holiday : holidayList) {
			long bTime = TimeUtil.getDateTime(holiday.getStartDate());
			long eTime = TimeUtil.getDateTime(holiday.getEndDate());
			if (isWeekDay) {
				isHoliday = true;
				// 工作日
				if (holiday.getHolidayType() == 0) {
					if (bTime <= cTime.getTime() && eTime >= cTime.getTime()) {
						isHoliday = false;
						break;
					}
				}
			} else {
				isHoliday = false;
				// 节假日
				if (holiday.getHolidayType() == 1) {
					if (bTime <= cTime.getTime() && eTime >= cTime.getTime()) {
						isHoliday = true;
						break;
					}
				}
			}
		}
		return isHoliday;
	}

	private TrunkNumber getAvaiableTrunk() {
		if (trunkSize <= 0) {
			return null;
		}
		if (trunkIndex >= trunkSize) {
			trunkIndex = trunkIndex % trunkSize;
		}
		return trunks.get(trunkIndex);
	}

	private String getAreaPrefix(String phone) {
		String area = TelephoneDictionary.getInstance().getArea(phone);
		logger.info("getAreaPrefix " + phone + " " + area);
		if (area != null) {
			if (area.contains(this.activityInfo.getRegion())) {
				return "";
			}
		}
		return "0";
	}

	private String getUUIInfo(RosterInfo roster) {
		String activity_uui = activityInfo.getUui();
		String[] uuis = activity_uui.split(",");
		if (uuis.length > 0) {
			Map<String, String> customerFields = gson.fromJson(roster.getCustomFields(), MetricUtil.mapList);
			String corrUUi = "";
			for (String uui : uuis) {
				if (uui.equals("phoneNum1")) {
					corrUUi += roster.getPhoneNum1();
				} else if (uui.equals("phoneNum2")) {
					corrUUi += roster.getPhoneNum2();
				} else if (uui.equals("phoneNum3")) {
					corrUUi += roster.getPhoneNum3();
				} else if (uui.equals("phoneNum4")) {
					corrUUi += roster.getPhoneNum4();
				} else if (uui.equals("phoneNum5")) {
					corrUUi += roster.getPhoneNum5();
				} else if (uui.toLowerCase().equals("lastname")) {
					corrUUi += roster.getLastname();
				} else if (uui.toLowerCase().equals("firstname")) {
					corrUUi += roster.getFirstname();
				} else if (uui.equals("age")) {
					corrUUi += roster.getAge();
				} else if (uui.equals("sex")) {
					corrUUi += roster.getSex();
				} else if (uui.equals("customerId")) {
					corrUUi += roster.getCustomerId();
				} else if (uui.equals("address")) {
					corrUUi += roster.getAddress();
				} else if (uui.equals("email")) {
					corrUUi += roster.getEmail();
				} else if (uui.equals("cardType")) {
					corrUUi += roster.getCardType();
				} else if (uui.equals("cardNum")) {
					corrUUi += roster.getCardNum();
				} else {
					if (customerFields != null && customerFields.containsKey(uui)) {
						corrUUi += customerFields.get(uui);
					} else {
						corrUUi += "null";
					}
				}
				corrUUi += "_";
			}
			corrUUi = corrUUi.substring(0, corrUUi.length() - 1);
			return corrUUi;
		}
		return "";
	}

	private String getRoundPhone(int round) {
		if (rounds != null) {
			for (OutboundRecallPolicy policy : rounds) {
				if (policy.getRound() == round) {
					return policy.getPhoneNumCol();
				}
			}
		}
		return "phoneNum1";
	}

	private RosterInfo createInfoByRoster(Roster roster) {
		if (roster == null) {
			return null;
		}

		RosterInfo rosterInfo = new RosterInfo();

		rosterInfo.setRosterId(roster.getId());
		rosterInfo.setActivityName(roster.getActivityName());
		rosterInfo.setAddress(roster.getAddress());
		rosterInfo.setAge(roster.getAge());
		rosterInfo.setBatchName(roster.getBatchName());
		rosterInfo.setCardNum(roster.getCardNum());
		rosterInfo.setCardType(roster.getCardType());
		rosterInfo.setCreateTime(roster.getCreateTime());
		rosterInfo.setCustomerId(roster.getCustomerId());
		rosterInfo.setCustomFields(roster.getCustomFields());
		rosterInfo.setDomain(roster.getDomain());
		rosterInfo.setEmail(roster.getEmail());
		rosterInfo.setFirstname(roster.getFirstname());
		rosterInfo.setLastname(roster.getLastname());
		rosterInfo.setPhoneNum1(roster.getPhoneNum1());
		rosterInfo.setPhoneNum2(roster.getPhoneNum2());
		rosterInfo.setPhoneNum3(roster.getPhoneNum3());
		rosterInfo.setPhoneNum4(roster.getPhoneNum4());
		rosterInfo.setPhoneNum5(roster.getPhoneNum5());
		rosterInfo.setTemplateName(roster.getTemplateName());
		rosterInfo.setCallRound(roster.getCallRound());
		rosterInfo.setIsrecall(roster.getReCall());

		return rosterInfo;
	}

	private void callOutRoster(RosterInfo info, TrunkNumber t_trunk) {
		MakeCall mkcall = new MakeCall();
		mkcall.setAni(t_trunk.getDisplayNum());
		mkcall.setActivity_name(activityInfo.getName());
		mkcall.setCaller(prefix + info.getPhoneNum1());
		mkcall.setCallee(dst);
		mkcall.setDomain(info.getDomain());
		mkcall.setTrunkGrp(t_trunk.getTrunkGrp());
		mkcall.setRound(info.getCallRound());
		mkcall.setBatch_name(info.getBatchName());
		mkcall.setRosterinfo_id("" + info.getRosterId());
		mkcall.setUui(getUUIInfo(info));

		String phoneCol = getRoundPhone(info.getCallRound());
		String areaFix = "";
		if (phoneCol.equals("phoneNum1")) {
			areaFix = getAreaPrefix(info.getPhoneNum1());
			mkcall.setCaller(prefix + areaFix + info.getPhoneNum1());
			info.setCurrentCallNum(info.getPhoneNum1());
		} else if (phoneCol.equals("phoneNum2")) {
			areaFix = getAreaPrefix(info.getPhoneNum2());
			info.setCurrentCallNum(info.getPhoneNum2());
			mkcall.setCaller(prefix + areaFix + info.getPhoneNum2());
		} else if (phoneCol.equals("phoneNum3")) {
			areaFix = getAreaPrefix(info.getPhoneNum3());
			info.setCurrentCallNum(info.getPhoneNum3());
			mkcall.setCaller(prefix + areaFix + info.getPhoneNum3());
		} else if (phoneCol.equals("phoneNum4")) {
			areaFix = getAreaPrefix(info.getPhoneNum4());
			info.setCurrentCallNum(info.getPhoneNum4());
			mkcall.setCaller(prefix + areaFix + info.getPhoneNum4());
		} else if (phoneCol.equals("phoneNum5")) {
			areaFix = getAreaPrefix(info.getPhoneNum5());
			info.setCurrentCallNum(info.getPhoneNum5());
			mkcall.setCaller(prefix + areaFix + info.getPhoneNum5());
		}
		info.setMakeCallNum(mkcall.getCaller());
		info.setAni(mkcall.getAni());
		info.setTrunkGroup(mkcall.getTrunkGrp());
		info.setUui(mkcall.getUui());

		ReqResponse result = CallUtil.makeCall(mkcall);
		if (result != null) {
			RedisUtil.addRoster(info);
		} else {
			info.setMakeCallTime(TimeUtil.getCurrentTimeStr());
			info.setCallEndTime(TimeUtil.getCurrentTimeStr());
			info.setCallResult("外呼接口失败");
			info.setResultCode(12);
			info.setStatus(2);
			rosterInfoDao.updateRosterInfo(info);
		}
	}

	private void waitTime(long mills) {
		try {
			Thread.sleep(mills);
		} catch (InterruptedException e) {
			logger.error(e);
		}
	}

	public void excuteCallBack() {
		if (activityInfo.getActivityBackAddrType() == 3) {
			AutoImportConfigModel config = GsonFactory.getGson().fromJson(activityInfo.getActivityBackAddr(),
					AutoImportConfigModel.class);
			if (config != null) {
				List<RosterInfo> rosters = rosterInfoDao.findFinishRosters(activityInfo.getDomain(),
						activityInfo.getName());
				if (rosters != null) {
					List<List<String>> exportRosters = new ArrayList<List<String>>();
					for (RosterInfo roster : rosters) {
						exportRosters.add(RosterUtil.getRosterInfos(roster));
					}
					rosterInfoDao.updateFinishRosters(activityInfo.getDomain(), activityInfo.getName());
					try {
						String path = config.getContext().getPath() + "/" + activityInfo.getName() + "_"
								+ activityInfo.getDomain() + "_" + TimeUtil.getExportCurrentTimeStr() + ".xls";
						XlsUtil.exportRosterInfoXls(RosterUtil.getRosterHeaders(), exportRosters, path);
					} catch (IOException e) {
						logger.error(e);
					}
				}
			}
		}
	}

	private void checkActivity() {
		checkActivityPause();

		checkActivityTime();

		checkPolicyTime();

	}

	@Override
	public void run() {
		long startTime = System.currentTimeMillis();

		logger.info("#####  activity thread [activity:" + activityInfo.getName() + ":" + activityInfo.getDomain()
				+ "] start to run ");

		TaskContainer.addActivityThread(AOUtil.genernateActivityThdId(activityInfo.getDomain(), activityInfo.getName()),
				this);

		init();

		if (status == 0) {
			status = 1;
		}

		while (status != 3) {
			try {
				checkActivity();

				TrunkNumber t_trunk = getAvaiableTrunk();

				if (t_trunk == null) {
					logger.warn("#### activity:" + activityInfo.getName() + " no current avaible trunk!");
					waitTime(1500);
					continue;
				}

				int currentCallnum = RedisUtil.getKeys(activityInfo.getName());

				if (currentCallnum >= callLimit) {
					logger.warn("## activity [" + activityInfo.getName() + "] out of call limt [" + callLimit + "]");

					while (currentCallnum >= callLimit) {
						waitTime(1000);
						currentCallnum = RedisUtil.getKeys(activityInfo.getName());
						if (status != 1) {
							break;
						}
					}
				}

				checkActivity();

				// 导入名单的那一刻已经有roster callList有值 中间删除只删除了callList内容
				/**
				 * 活动停止后 无法回传 一直阻塞在此处
				 */
				Roster roster = rosterPoll.getRoster();
				/**
				 * @author zzj 防止结束活动 空指针
				 */
				if (roster != null) {
					Util.info("", "rosterPool中roster为：" + roster.getId());
				}

				/**
				 * 当未导入时 线程阻塞，直到名单线程读取到相关名单，唤醒当前wait的线程，仍会呼叫，所以需要进行状态判断 解决bug：暂停后导入名单有时会呼
				 * 
				 * @author zhangzijian
				 * @date 2018.10.16
				 */

				checkActivity();

				/**
				 * 可能已经获取roster而阻塞，此过程删除callList无用，需要与中间设置的禁呼列表比对手动移除 解决bug：暂停后禁呼或删除批次无用
				 * 
				 * @author fanlx
				 * @date 2018/10/17
				 */

				/**
				 * @author zzj 防止空指针异常
				 */
				if (roster != null) {
					roster = getCurrentRoster(roster);
					Util.info("", "最终的roster为：" + roster);
				}

				if (roster != null) {
					checkActivity();

					logger.info("### start to process roster " + gson.toJson(roster));

					RosterInfo rosterInfo = createInfoByRoster(roster);

					if (rosterInfo != null) {
						logger.info("## activity [" + activityInfo.getName() + "] batchId [" + roster.getBatchName()
								+ "] currentCallnum call NUMBER [" + currentCallnum + "]");

						rosterInfo.setCallee(dst);
						callOutRoster(rosterInfo, t_trunk);
						MetricUtil.addCallOut(rosterInfo);
						/**
						 * @author zzj 改变roster状态4 表示正在外呼 1读取名单但未外呼 0未读取 2 外呼完成
						 */
						roster.setStatus(4);
						rosterDao.updateRoster(roster);
						/**
						 * @author zzj
						 * 修改未呼到的批次 出现进行中的情况
						 */
						RosterBatch rosterBatch = batchDAO.findByBatchName(roster.getDomain(), roster.getBatchName());
						if(rosterBatch.getStatus() == 0) {
							rosterBatch.setStatus(1);
							rosterBatch.setStartTime(TimeUtil.getCurrentTimeStr());
							batchDAO.updateRosterBatch(rosterBatch);
						}
					}

				} else
					waitTime(1000);
			} catch (Exception e) {
				logger.error("###### activity thread" + activityInfo.getName() + " run error:" + e.toString());
			}
		}

		long costtime = System.currentTimeMillis() - startTime;
		logger.info("###### activity " + activityInfo.getName() + " excute complete costt " + costtime / 1000 + " s");
		activityInfo.setStatus(3);
		activityInfo.setEndDatetime(TimeUtil.getCurrentTimeStr());
		activityDao.updateActivityInfo(activityInfo);
		TaskContainer
				.deleteActivityThread(AOUtil.genernateActivityThdId(activityInfo.getDomain(), activityInfo.getName()));
		SimpleJobManager.removeJob(activityInfo.getId() + "_back");
		CronJobManager.removeJob(activityInfo.getId() + "_back");

		excuteCallBack();
	}

	/**
	 * @author fanlx
	 * @date 2018/10/17
	 */
	public Roster getCurrentRoster(Roster roster) {
		if (roster != null) {
			RosterThread rosterThread = TaskContainer
					.findRosterThread(AOUtil.genernateRosterThdId(roster.getDomain(), roster.getActivityName()));
			if (rosterThread != null) {
				BatchRosters batchRosters = rosterThread.getBatchRosterList().get(roster.getBatchName());
				if (batchRosters != null) {
					Map<Integer, Roster> dncRoster = batchRosters.getDncRosterList();
					if (dncRoster.isEmpty()) {
						return roster;
					}

					// 说明为禁呼的roster
					if (dncRoster.containsKey(roster.getId())) {
						dncRoster.remove(roster.getId());
						return null;
					}
				}
			}
		}
		return roster;
	}

	public void stopTask() {
		/**
		 * 名单池中没有名单时线程阻塞，需要唤醒才能执行后续回传内容
		 * 
		 * @author fanlx
		 * @date 2018.11.07
		 */
		// RosterThread rosterThread = TaskContainer
		// .findRosterThread(AOUtil.genernateRosterThdId(activityInfo.getDomain(),
		// activityInfo.getName()));
		// if (rosterThread != null)
		// rosterThread.notifyAll();
		/**
		 * @author zzj 唤醒线程 导出呼叫结果
		 */
		rosterPoll.addCallRoster(null);
		status = 3;
		logger.info("##### stop activity thread:" + activityName);
	}

	public void pauseTask() {
		status = 2;
		activityInfo.setStatus(2);
		activityDao.updateActivityInfo(activityInfo);
		logger.info("##### pause activity thread:" + activityName);
	}

	public void resumeTask() {
		init();
		status = 1;
		activityInfo.setStatus(1);
		activityDao.updateActivityInfo(activityInfo);
		logger.info("#### resume activity thread:" + activityName);
	}
}
