package com.outbound.impl.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.apache.log4j.Logger;
import com.google.gson.Gson;
import com.outbound.common.Util;
import com.outbound.dialer.util.RedisUtil;
import com.outbound.impl.TaskContainer;
import com.outbound.impl.metric.MetricUtil;
import com.outbound.impl.util.AOUtil;
import com.outbound.job.CallRoundJob;
import com.outbound.job.SimpleJobManager;
import com.outbound.object.ActivityInfo;
import com.outbound.object.BatchRosters;
import com.outbound.object.OutboundPolicyInfo;
import com.outbound.object.OutboundRecallPolicy;
import com.outbound.object.Roster;
import com.outbound.object.RosterBatch;
import com.outbound.object.RosterBatchInfo;
import com.outbound.object.RosterInfo;
import com.outbound.object.dao.OutboundRecallPolicyDAO;
import com.outbound.object.dao.RosterBatchDAO;
import com.outbound.object.dao.RosterBatchInfoDAO;
import com.outbound.object.dao.RosterDAO;
import com.outbound.object.util.ApplicationContextUtil;
import com.outbound.object.util.TimeUtil;

public class RosterThread implements Runnable {

	private Logger logger = Logger.getLogger(RosterThread.class);

	private RosterPool rosterpoll = null;

	private volatile boolean isDone = false;

	private ActivityInfo activityInfo;

	private static Gson gson = new Gson();

	private Map<Integer, Roster> reCallList = new HashMap<>();

	private Map<String, BatchRosters> batchRosterList = new HashMap<String, BatchRosters>();

	private Queue<String> roundList = new LinkedList<>();

	private RosterDAO rosterDao = (RosterDAO) ApplicationContextUtil.getApplicationContext().getBean("RosterDAO");

	private RosterBatchDAO batchDao = (RosterBatchDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("RosterBatchDAO");

	private RosterBatchInfoDAO batchInfoDao = (RosterBatchInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("RosterBatchInfoDAO");

	private OutboundRecallPolicyDAO recallDao = (OutboundRecallPolicyDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("OutboundRecallPolicyDAO");

	public RosterThread(ActivityInfo _activityInfo, RosterPool rosterPoll) {
		this.rosterpoll = rosterPoll;
		activityInfo = _activityInfo;
	}
	
	public Map<String, BatchRosters> getBatchRosterList() {
		return batchRosterList;
	}

	public synchronized void addRound(String batchId) {
		BatchRosters batchRosters = batchRosterList.get(batchId);

		if (batchRosters != null) {

			if (batchRosters.getCurrentRosterNum() > 0) {
				RosterBatchInfo batchInfo = batchInfoDao.findByBatchNameAndRound(batchRosters.getDomain(),
						batchRosters.getActivityName(), batchRosters.getTemplateName(), batchRosters.getBatchId(),
						batchRosters.getCurrentRound());

				if (batchInfo != null) {
					MetricUtil.removeBatchInfo(batchInfo);
					batchInfo.setStatus(2);
					batchInfoDao.updateRosterBatchInfo(batchInfo);
				}

				batchRosters.setCurrentRound(batchRosters.getCurrentRound() + 1);
				batchRosters.setRosterNumber(batchRosters.getCurrentRosterNum());
				batchRosters.setCallRosterNum(0);
				/**
				 * 第二轮禁呼名单数一定为0
				 * 
				 * @author fanlx
				 * @date 2018/10/29
				 */
				batchRosters.setDncNum(0);

				findAndCreateBatchInfo(batchRosters);

				RosterBatch rosterBatch = batchDao.findByBatchName(batchRosters.getDomain(), batchRosters.getBatchId());
				if (rosterBatch != null) {
					rosterBatch.setCallRound(batchRosters.getCurrentRound());
					batchDao.updateRosterBatch(rosterBatch);
				}

				roundList.offer(batchId);
			}
		}
	}

	private synchronized List<Roster> getRoundRosters() {
		List<Roster> rosterList = new ArrayList<>();

		while (roundList.size() > 0) {
			String batchId = roundList.poll();
			if (batchId != null) {
				BatchRosters batchRosters = batchRosterList.get(batchId);
				if (batchRosters != null) {
					Collection<Roster> c = batchRosters.getRosterList().values();

					for (Roster r : c) {
						r.setReCall(0);
						r.setCallRound(batchRosters.getCurrentRound());
						r.setMakeCallTime(TimeUtil.getCurrentTimeStr());
						rosterList.add(r);
					}
				}
			}
		}

		return rosterList;
	}

	private synchronized void addRoster(Roster roster) {
		BatchRosters batchRosters = batchRosterList.get(roster.getBatchName());

		if (batchRosters == null) {
			logger.warn("### add call roster find batchroster is null activityName:" + roster.getActivityName()
					+ " batchName:" + roster.getBatchName());
		} else {
			batchRosters.addRoster(roster);
		}
	}

	private synchronized void addReCallRoster(Roster roster) {
		if (!reCallList.containsKey(roster.getId())) {
			reCallList.put(roster.getId(), roster);
		}
	}

	private synchronized Roster getReCallRoster() {
		Roster roster = null;

		Collection<Roster> c = reCallList.values();

		for (Roster r : c) {
			long lMakeCallTime = TimeUtil.getDateTime(r.getMakeCallTime());

			long tiff = System.currentTimeMillis() - lMakeCallTime;

			if (tiff > 0) {
				roster = r;
				break;
			}
		}

		if (roster != null) {
			reCallList.remove(roster.getId());
		}

		return roster;
	}

	public synchronized void removeReCall(Roster roster) {
		if (reCallList.containsKey(roster.getId())) {
			reCallList.remove(roster.getId());
		}
	}

	public synchronized void processCallRoster(RosterInfo rosterInfo)
	{
		//外呼成功
		logger.info("### process call roster info:" + gson.toJson(rosterInfo));
		BatchRosters batchRosters = batchRosterList.get(rosterInfo.getBatchName());
		if (rosterInfo.getResultCode() == 0)
		{
			if (reCallList.containsKey(rosterInfo.getRosterId()))
			{
				reCallList.remove(rosterInfo.getRosterId());
			}
			
			if (batchRosters != null)
			{
				Roster roster = batchRosters.findRoster(rosterInfo.getRosterId());
				if (roster != null)
				{
					roster.setStatus(2);
					rosterDao.updateRoster(roster);
				}
				batchRosters.setCallRosterNum(batchRosters.getCallRosterNum() + 1);
				batchRosters.delRoster(rosterInfo.getRosterId());
			}
		}
		else if (rosterInfo.getResultCode() > 0)
		{
			if (batchRosters != null )
			{
				Roster roster = batchRosters.findRoster(rosterInfo.getRosterId());
				if (roster != null)
				{
					if (roster.getReCall() == 0)
					{
						batchRosters.setCallRosterNum(batchRosters.getCallRosterNum() + 1);
					}
					
					//处理回呼
					boolean isReCall = false;
					String aName = roster.getActivityName() + ":" + roster.getDomain();
					OutboundPolicyInfo policy = TaskContainer.policyMap.get(aName);
					if (policy != null)
					{
						policy.genCallRoundList();
						if (policy.getCallResultList().size() > 0)
						{
							for (String cResult : policy.getCallResultList())
							{
								String[] params = cResult.split("[|]");
								if (params[0].equals(rosterInfo.getCallResult()))
								{
									if (params[1].equals("retry"))
									{
										int retryTimes = Integer.parseInt(params[2]);
										if (retryTimes > roster.getReCall())
										{	
											int waitTime = Integer.parseInt(params[3]);
											
											roster.setReCall(roster.getReCall() + 1);
											
											roster.setMakeCallTime(TimeUtil.formatSysCurrentTimeStr(System.currentTimeMillis() + waitTime*1000));
											
											reCallList.put(roster.getId(), roster);
											rosterDao.updateRoster(roster);
											
											isReCall = true;
											
											logger.info("### process call roster add recall roster:" + gson.toJson(roster));
										}
									}
								}
							}
						}
					}
					
					//不需要回呼
					if (!isReCall)
					{
						//是否符合轮次的条件
						logger.info("### process call roster not recall roster:" + gson.toJson(roster));
						
						if (roster.getCallRound() < batchRosters.getMaxCallRound())
						{
							OutboundRecallPolicy recallPolicy = batchRosters.findReCallPolicy(roster.getCallRound() + 1);
							
							if (recallPolicy != null)
							{
								if (recallPolicy.getCallResult() != null ||
									recallPolicy.getCallResult().length() > 0)
								{
									
									if (recallPolicy.getCallResult().indexOf(rosterInfo.getCallResult()) < 0 &&
										recallPolicy.getCallResult().indexOf("其他") < 0)
									{
										roster.setStatus(2);
										rosterDao.updateRoster(roster);
									
										if (reCallList.containsKey(roster.getId()))
										{
											reCallList.remove(roster.getId());
										}
										
										batchRosters.delRoster(roster.getId());
									}
								}
							}
							else
							{
								roster.setStatus(2);
								rosterDao.updateRoster(roster);
								
								if (reCallList.containsKey(roster.getId()))
								{
									reCallList.remove(roster.getId());
								}
								
								batchRosters.delRoster(roster.getId());
							}
						}
						else
						{
							roster.setStatus(2);
							rosterDao.updateRoster(roster);
							if (reCallList.containsKey(roster.getId()))
							{
								reCallList.remove(roster.getId());
							}
							
							batchRosters.delRoster(roster.getId());
						}
					}
				}
			}
		}

		// 处理轮次
		logger.info("当前的批次中已呼名单数为：" + batchRosters.getCallRosterNum() + " 禁呼名单数为：" + batchRosters.getDncNum()
				+ " 批次名单总数为：" + batchRosters.getRosterNumber());
		if (batchRosters.getCallRosterNum() + batchRosters.getDncNum() == batchRosters.getRosterNumber()) {
			RosterBatchInfo batchInfo = batchInfoDao.findByBatchNameAndRound(batchRosters.getDomain(),
					batchRosters.getActivityName(), batchRosters.getTemplateName(), batchRosters.getBatchId(),
					batchRosters.getCurrentRound());

			if (batchInfo != null) {
				MetricUtil.removeBatchInfo(batchInfo);
				batchInfo.setStatus(2);
				/**
				 * @author zzj
				 * 解决批次中没有结束时间
				 */
				batchInfo.setCompleteTime(TimeUtil.getCurrentTimeStr());
				batchInfoDao.updateRosterBatchInfo(batchInfo);
			}

			if (batchRosters.getCurrentRound() == 1) {
				RosterBatch rosterBatch = batchDao.findByBatchName(batchRosters.getDomain(), batchRosters.getBatchId());
				if (rosterBatch == null) {
					logger.warn("## process call roster 1 round not find batch:" + batchRosters.getBatchId());
				} else {
					logger.info("###########rosterbatch  " + rosterBatch.getStartTime());
					rosterBatch.setStatus(2);
					batchDao.updateRosterBatch(rosterBatch);
				}
			}

			// 新轮次处理
			if (batchRosters.getCurrentRound() < batchRosters.getMaxCallRound()
					&& batchRosters.getCurrentRosterNum() > 0) {
				// 启动新一轮
				OutboundRecallPolicy recallPolicy = batchRosters.findReCallPolicy(batchRosters.getCurrentRound() + 1);
				if (recallPolicy == null) {

				} else {
					String crJobName = batchRosters.getBatchId() + "&" + batchRosters.getActivityName() + "&"
							+ batchRosters.getDomain() + "&callround_job";

					if (recallPolicy.getCallInterval() >= 0) {
						SimpleJobManager.removeJob(crJobName);
						SimpleJobManager.addJob(crJobName, CallRoundJob.class, recallPolicy.getCallInterval(), 0, 0);
						logger.info(" # add call round " + crJobName + " at " + recallPolicy.getCallInterval() + "s");
					}
				}

			} else {
				RosterBatch rosterBatch = batchDao.findByBatchName(batchRosters.getDomain(), batchRosters.getBatchId());
				if (rosterBatch == null) {
					logger.warn("## process call roster round complete not find batch:" + batchRosters.getBatchId());
				} else {
					rosterBatch.setStatus(3);
					rosterBatch.setCompleteTime(TimeUtil.getCurrentTimeStr());
					batchDao.updateRosterBatch(rosterBatch);
					/**
					 * 当前轮次已经完成，需要移除metric中的completeBatchs内容，避免job的批次检测
					 * 需重写rosterbatchinfo的hashcode和equals方法
					 */
					/**
					 * @author zzj
					 * 修改批次完成时间，否则无完成时间
					 */
					batchInfo.setCompleteTime(TimeUtil.getCurrentTimeStr());
					batchInfoDao.updateRosterBatchInfo(batchInfo);
					MetricUtil.removeBatchInfo(batchInfo);
				}
			}
		}
	}

	private void waitTime(long mills) {
		try {
			Thread.sleep(mills);
		} catch (InterruptedException e) {
			logger.error(e);
		}
	}

	/**
	 * @author zzj
	 * 
	 *         解决空批次一直读取名单 并将rosterbatchinfo也改变
	 * @param rosterBatch
	 */
	public synchronized void finshNullBatch(RosterBatch rosterBatch) {
		rosterBatch.setStatus(3);
		rosterBatch.setCompleteTime(TimeUtil.getCurrentTimeStr());
		batchDao.updateRosterBatch(rosterBatch);
		BatchRosters batchRosters = batchRosterList.get(rosterBatch.getBatchId());
		RosterBatchInfo batchInfo = batchInfoDao.findByBatchNameAndRound(batchRosters.getDomain(),
				batchRosters.getActivityName(), batchRosters.getTemplateName(), batchRosters.getBatchId(),
				batchRosters.getCurrentRound());
		batchInfo.setStatus(2);
		batchInfo.setCompleteTime(TimeUtil.getCurrentTimeStr());
		batchInfoDao.updateRosterBatchInfo(batchInfo);
		MetricUtil.removeBatchInfo(batchInfo);
	}

	private synchronized void processBatch(RosterBatch rosterBatch, int activityStatus) {
		BatchRosters batchRosters = batchRosterList.get(rosterBatch.getBatchId());
		if (batchRosters == null) {
			batchRosters = new BatchRosters(rosterBatch.getBatchId());
			batchRosters.setBatchLevel(0);
			batchRosters.setDomain(rosterBatch.getDomain());
			batchRosters.setActivityName(rosterBatch.getActivityName());
			batchRosters.setCurrentRound(rosterBatch.getCallRound());
			batchRosters.setRosterNumber(rosterBatch.getRoterNum());
			batchRosters.setTemplateName(rosterBatch.getTemplateName());
			batchRosters
					.setPolicyList(recallDao.findRoundPolicy(rosterBatch.getDomain(), rosterBatch.getActivityName()));

			batchRosterList.put(rosterBatch.getBatchId(), batchRosters);
			logger.info("当前活动状态为：" + activityStatus);
			/**
			 * @author zzj
			 * 此时rosterbatch状态和开始时间 到activitythread中判定
			 */
			/*if (activityStatus == 1) {
				rosterBatch.setStatus(1);
				rosterBatch.setStartTime(TimeUtil.getCurrentTimeStr());
			}*/
			batchDao.updateRosterBatch(rosterBatch);

			findAndCreateBatchInfo(batchRosters);
		}
	}

	private synchronized void processInBatch(RosterBatch rosterBatch, int curRosterNum, int activityStatus) {
		BatchRosters batchRosters = batchRosterList.get(rosterBatch.getBatchId());
		if (batchRosters == null) {
			batchRosters = new BatchRosters(rosterBatch.getBatchId());
			batchRosters.setBatchLevel(0);
			batchRosters.setDomain(rosterBatch.getDomain());
			batchRosters.setActivityName(rosterBatch.getActivityName());
			batchRosters.setTemplateName(rosterBatch.getTemplateName());
			batchRosters.setCurrentRound(rosterBatch.getCallRound());
			batchRosters.setRosterNumber(curRosterNum);

			batchRosters
					.setPolicyList(recallDao.findRoundPolicy(rosterBatch.getDomain(), rosterBatch.getActivityName()));

			batchRosterList.put(rosterBatch.getBatchId(), batchRosters);
			logger.info("当前活动状态为：" + activityStatus);
			/**
			 * @author zzj
			 * 此时rosterbatch状态和开始时间 到activitythread中判定
			 */
			/*if (activityStatus == 1) {
				rosterBatch.setStatus(1);
				rosterBatch.setStartTime(TimeUtil.getCurrentTimeStr());
			}*/
			batchDao.updateRosterBatch(rosterBatch);

			findAndCreateBatchInfo(batchRosters);
		}
	}

	private RosterBatchInfo findAndCreateBatchInfo(BatchRosters batchRosters) {
		RosterBatchInfo batchInfo = batchInfoDao.findByBatchNameAndRound(batchRosters.getDomain(),
				batchRosters.getActivityName(), batchRosters.getTemplateName(), batchRosters.getBatchId(),
				batchRosters.getCurrentRound());

		if (batchInfo == null) {
			batchInfo = new RosterBatchInfo();
			batchInfo.setActivityName(batchRosters.getActivityName());
			batchInfo.setAnswerCallNum(0);
			batchInfo.setBatchId(batchRosters.getBatchId());
			batchInfo.setCallRound(batchRosters.getCurrentRound());
			batchInfo.setCreateTime(TimeUtil.getCurrentTimeStr());
			batchInfo.setStartTime(TimeUtil.getCurrentTimeStr());
			batchInfo.setDncNum(batchRosters.getDncNum());
			batchInfo.setDomain(batchRosters.getDomain());
			batchInfo.setOutCallNum(0);
			batchInfo.setRoterNum(batchRosters.getRosterNumber());
			batchInfo.setStatus(1);
			batchInfo.setTemplateName(batchRosters.getTemplateName());
			batchInfo.setUnCallNum(0);
			batchInfoDao.createRosterBatchInfo(batchInfo);

			MetricUtil.addBatchInfo(batchInfo);
		}

		return batchInfo;
	}

	/**
	 * 禁呼或清空列表或删除批次 清理待呼中间存储内容
	 * 
	 * @author fanlx
	 * @date 2018/10/26
	 */
	public synchronized void checkAndDelRoster(RosterBatch rosterBatch, List<Roster> rosterList, String action) {
		Collection<Roster> c = null;
		BatchRosters batchRosters = batchRosterList.get(rosterBatch.getBatchId());
		if (batchRosters != null) {
			Map<Integer, Roster> dncRosters = batchRosters.getDncRosterList();
			if (rosterList == null) {
				Util.info("", "delete the batch......");
				c = batchRosters.getRosterList().values();
				if (roundList.contains(rosterBatch.getBatchId())) {
					if (roundList.remove(rosterBatch.getBatchId())) {
						Util.info("", "remove batchId from roundList success");
					} else {
						Util.info("", "remove batchId from roundList fail");
					}
				} else {
					Util.info("", "No batchId in roundList");
				}
				// 删除相应的completeBatchs中的批次，直接删除batchMetrics中内容即可
				MetricUtil.removeRosterBatch(rosterBatch);
			} else {
				c = rosterList;
			}
			for (Roster r : c) {
				if (r != null) {
					Util.info("", "需要移除的roster为：" + r.getId());
					dncRosters.put(r.getId(), r);
					rosterpoll.removeCallRoster(r);
					batchRosters.delRoster(r.getId());
					removeReCall(r);
					// 禁止其结果回传
					RedisUtil.delRoster(r);
				} else {
					Util.info("", "roster is null");
				}
			}
			/**
			 * 若是批次的删除操作 则必须要清空batchRosterList中的batchId 否则会出现同名批次状态更新问题 若为禁呼操作或者是清空列表操作
			 * 禁呼操作：如果当前批次名单全部禁呼，不做处理 清空列表操作：也需要移除相关batchRosters
			 */
			if ("deleteBatch".equals(action) || "clearRoster".equals(action)) {
				batchRosterList.remove(batchRosters.getBatchId());
			}
		} else {
			Util.info("", "Not find the batchRosters");
		}
	}

	/**
	 * 取消禁呼 删除相应禁呼列表
	 * 
	 * @author fanlx
	 * @date 2018/10/26
	 * @param rosterList
	 *            用户勾选的取消禁呼列表，必定非空
	 */
	public synchronized void delBanRoster(RosterBatch rosterBatch, List<Roster> rosterList) {
		BatchRosters batchRosters = batchRosterList.get(rosterBatch.getBatchId());
		if (batchRosters != null) {
			Map<Integer, Roster> dncRosters = batchRosters.getDncRosterList();
			for (Roster roster : rosterList) {
				dncRosters.remove(roster.getId());
			}
		} else {
			Util.info("", "Not find the batchRosters");
		}
	}

	@Override
	public void run() {
		logger.info("##### roster pool thread [roster:" + activityInfo.getName() + ":" + activityInfo.getDomain()
				+ "] start to run ");
		TaskContainer.addRosterThread(AOUtil.genernateRosterThdId(activityInfo.getDomain(), activityInfo.getName()),
				this);

		/*
		 * 程序启动的时候，当前可能存在已进行的批次，这样的批次需要恢复正常的运行，所以设置此标识
		 */
		int runFlag = 0; // 0 线程启动第一次运行 1 获取到未完成的批次 2获取到未完成批次下的名单 3 未完成的批次处理完成
		while (!isDone) {
			try {
				long startTime = System.currentTimeMillis();

				List<Roster> roundRosterList = getRoundRosters();

				if (roundRosterList != null && roundRosterList.size() > 0) {
					logger.info("### find call round rosters: " + roundRosterList.size());
				}

				for (int i = 0; i < roundRosterList.size(); ++i) {
					Roster roster = roundRosterList.get(i);
					if (roster != null) {
						removeReCall(roster);
						rosterDao.updateRoster(roster);
						rosterpoll.addCallRoster(roster);
					}
				}

				// 查找已拨打、未完成的批次
				RosterBatch rosterBatch = batchDao.findinCallBatch(activityInfo.getDomain(),
						activityInfo.getRosterTemplateName());
				List<Roster> rosterList = null;
				if (rosterBatch == null) {
					// 查找未拨打的批次
					rosterBatch = batchDao.findNextUncallBatch(activityInfo.getDomain(),
							activityInfo.getRosterTemplateName());
				} else {
					/**
					 * @author zzj
					 * 修复先导入名单 再关联名单出现rosterbatch中没有activityname
					 */
					if(rosterBatch.getActivityName() == null || rosterBatch.getActivityName() == "") {
						rosterBatch.setActivityName(activityInfo.getName());
					}
					if (runFlag == 0) {
						runFlag = 1;
						rosterList = rosterDao.findInBatchRosters(activityInfo.getDomain(), rosterBatch.getBatchId(),
								activityInfo);
					} else {
						rosterList = rosterDao.findBatchRosters(activityInfo.getDomain(), rosterBatch.getBatchId(),
								activityInfo);
					}

					if (rosterList == null || rosterList.size() == 0) {
						rosterBatch = batchDao.findNextUncallBatch(activityInfo.getDomain(),
								activityInfo.getRosterTemplateName());
					} else {
						// 获取到正在进行批次的名单数据
						if (runFlag == 1) {
							runFlag = 2;
						}
					}
				}
				
				/**
				 * @author zzj
				 * 修改活动暂停时 可能出现将批次读取加入到batchlist中
				 */
				if(activityInfo.getStatus() == 1) {
					
					if (rosterBatch != null) {
						/**
						 * @author zzj
						 * 修复先导入名单 再关联名单出现rosterbatch中没有activityname
						 */
						if(rosterBatch.getActivityName() == null || rosterBatch.getActivityName() == "") {
							rosterBatch.setActivityName(activityInfo.getName());
						}
						if (runFlag == 2) {
							runFlag = 3;
							processInBatch(rosterBatch, rosterList.size(), activityInfo.getStatus());
						} else {
							processBatch(rosterBatch, activityInfo.getStatus());
						}
						
						if (rosterList == null || rosterList.size() == 0) {
							rosterList = rosterDao.findBatchRosters(activityInfo.getDomain(), rosterBatch.getBatchId(),
									activityInfo);
						}
						
						if (rosterList == null) {
							//若批次中为空 则将其状态改变
							logger.info("### no roster for batch " + rosterBatch.toString());
							if(rosterBatch.getRoterNum() == 0) {
								finshNullBatch(rosterBatch);
							}
							
						} else {
							logger.info("### find roster " + rosterBatch.toString() + " size [" + rosterList.size() + "]");
							
							for (int i = 0; i < rosterList.size(); ++i) {
								Roster roster = rosterList.get(i);
								/**
								 * @author zzj
								 * 防止roster中没有活动名
								 */
								if(roster.getActivityName() == null || roster.getActivityName() == "") {
									roster.setActivityName(activityInfo.getName());
								}
								switch (roster.getStatus()) {
								case 0: // 系统没有抽取
								{
									logger.info("当前活动状态为：" + activityInfo.getStatus());
									if (activityInfo.getStatus() == 1) {
										roster.setStatus(1);
										// 更新名单的状态
										rosterDao.updateRoster(roster);
										// 添加到待外呼名单池，保证呼叫顺序
										rosterpoll.addCallRoster(roster);
										addRoster(roster);
									}
								}
								break;
								case 1: // 已抽取但没有外呼，如果是第一次启动等同未抽取
								{
									// 添加到待外呼名单池
									rosterpoll.addCallRoster(roster);
									addRoster(roster);
								}
								break;
								case 2: //已呼，但是轮次或回呼未执行完成
								{
									addReCallRoster(roster);
									addRoster(roster);
								}
								break;
								default:
									break;
								}
							}
							
						}
					}
				}

				Roster roster = getReCallRoster();

				if (roster != null) {
					rosterpoll.addReCallRoster(roster);
				}

				runFlag = 3;

				long costtime = System.currentTimeMillis() - startTime;
				if (costtime < 100) {
					waitTime(1500);
				}
			} catch (Exception e) {
				runFlag = 3;
				logger.error("##### roster pool thread [activity:" + activityInfo.getName() + ":"
						+ activityInfo.getDomain() + "] run error:" + e.getMessage());
			}
		}

		TaskContainer.deleteRosterThread(AOUtil.genernateRosterThdId(activityInfo.getDomain(), activityInfo.getName()));
		logger.info("###### roster pool thread  activity:" + activityInfo.getName() + " excute complete");
	}

	public void stopRoster() {
		isDone = true;
		logger.info("roster tread isDone changed, isDone :" + isDone);
	}

	public void pauseRoster() {
		activityInfo.setStatus(2);
		logger.info("##### change rosterThread activityStatus to 2");
	}

	public void resumeRoster() {
		activityInfo.setStatus(1);
		logger.info("##### change rosterThread activityStatus to 1");
	}

	public void stopRosterStatus() {
		activityInfo.setStatus(3);
		logger.info("##### change rosterThread activityStatus to 3");
	}
}
