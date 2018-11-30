package com.outbound.impl.plan;

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

public class PlanRosterThread implements Runnable {

	private Logger logger = Logger.getLogger(PlanRosterThread.class);

	private PlanRosterPool rosterpoll = null;

	private volatile boolean isDone = false;

	private ActivityInfo activityInfo;

	private List<RosterBatch> newBatchList = new ArrayList<RosterBatch>();

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

	public PlanRosterThread(ActivityInfo _activityInfo, PlanRosterPool rosterPoll) {
		this.rosterpoll = rosterPoll;
		activityInfo = _activityInfo;
	}

	public Map<String, BatchRosters> getBatchRosterList() {
		return batchRosterList;
	}

	public PlanRosterPool getRosterpoll() {
		return rosterpoll;
	}

	public Map<Integer, Roster> getReCallList() {
		return reCallList;
	}

	public Queue<String> getRoundList() {
		return roundList;
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
			logger.warn("### add call roster find batchroster is null planActivityName:" + roster.getActivityName()
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

	/**
	 * 获取回呼结果 是否重复 放入recallList
	 * 
	 * @param rosterInfo
	 */
	public synchronized void processCallRoster(RosterInfo rosterInfo) {
		// 外呼成功
		logger.info("### process call roster info:" + gson.toJson(rosterInfo));
		BatchRosters batchRosters = batchRosterList.get(rosterInfo.getBatchName());
		if (rosterInfo.getResultCode() == 0) {
			if (reCallList.containsKey(rosterInfo.getRosterId())) {
				reCallList.remove(rosterInfo.getRosterId());
			}

			if (batchRosters != null) {
				Roster roster = batchRosters.findRoster(rosterInfo.getRosterId());
				if (roster != null) {
					roster.setStatus(3);
					rosterDao.updateRoster(roster);
				}
				batchRosters.setCallRosterNum(batchRosters.getCallRosterNum() + 1);
				batchRosters.delRoster(rosterInfo.getRosterId());
			}
		} else if (rosterInfo.getResultCode() > 0) {
			if (batchRosters != null) {
				Roster roster = batchRosters.findRoster(rosterInfo.getRosterId());
				/**
				 * @author fanlx
				 * @date 2018.10.19
				 */

				if (roster != null) {
					if (roster.getReCall() == 0) {
						batchRosters.setCallRosterNum(batchRosters.getCallRosterNum() + 1);
					}

					// 处理回呼
					boolean isReCall = false;
					String aName = roster.getActivityName() + ":" + roster.getDomain();
					OutboundPolicyInfo policy = TaskContainer.policyMap.get(aName);
					if (policy != null) {
						policy.genCallRoundList();
						if (policy.getCallResultList().size() > 0) {
							for (String cResult : policy.getCallResultList()) {
								String[] params = cResult.split("[|]");
								if (params[0].equals(rosterInfo.getCallResult())) {
									if (params[1].equals("retry")) {
										int retryTimes = Integer.parseInt(params[2]);
										if (retryTimes > roster.getReCall()) {
											int waitTime = Integer.parseInt(params[3]);

											roster.setReCall(roster.getReCall() + 1);

											roster.setMakeCallTime(TimeUtil.formatSysCurrentTimeStr(
													System.currentTimeMillis() + waitTime * 1000));

											reCallList.put(roster.getId(), roster);
											rosterDao.updateRoster(roster);

											isReCall = true;

											logger.info(
													"### process call roster add recall roster:" + gson.toJson(roster));
										}
									}
								}
							}
						}
					}

					// 不需要回呼
					if (!isReCall) {
						// 是否符合轮次的条件
						logger.info("### process call roster not recall roster:" + gson.toJson(roster));

						if (roster.getCallRound() < batchRosters.getMaxCallRound()) {
							OutboundRecallPolicy recallPolicy = batchRosters
									.findReCallPolicy(roster.getCallRound() + 1);

							if (recallPolicy != null) {
								if (recallPolicy.getCallResult() != null || recallPolicy.getCallResult().length() > 0) {

									if (recallPolicy.getCallResult().indexOf(rosterInfo.getCallResult()) < 0
											&& recallPolicy.getCallResult().indexOf("其他") < 0) {
										roster.setStatus(3);
										rosterDao.updateRoster(roster);

										if (reCallList.containsKey(roster.getId())) {
											reCallList.remove(roster.getId());
										}

										batchRosters.delRoster(roster.getId());
									}
								}
							} else {
								roster.setStatus(3);
								rosterDao.updateRoster(roster);

								if (reCallList.containsKey(roster.getId())) {
									reCallList.remove(roster.getId());
								}

								batchRosters.delRoster(roster.getId());
							}
						} else {
							roster.setStatus(3);
							rosterDao.updateRoster(roster);
							if (reCallList.containsKey(roster.getId())) {
								reCallList.remove(roster.getId());
							}

							batchRosters.delRoster(roster.getId());
						}
					}
				}
			}
		}

		// 处理轮次
		if (batchRosters.getCallRosterNum() + batchRosters.getDncNum() == batchRosters.getRosterNumber()) {
			RosterBatchInfo batchInfo = batchInfoDao.findByBatchNameAndRound(batchRosters.getDomain(),
					batchRosters.getActivityName(), batchRosters.getTemplateName(), batchRosters.getBatchId(),
					batchRosters.getCurrentRound());

			if (batchInfo != null) {
				MetricUtil.removeBatchInfo(batchInfo);
				batchInfo.setStatus(2);
				batchInfoDao.updateRosterBatchInfo(batchInfo);
			}

			if (batchRosters.getCurrentRound() == 1) {
				RosterBatch rosterBatch = batchDao.findByBatchName(batchRosters.getDomain(), batchRosters.getBatchId());
				if (rosterBatch == null) {
					logger.warn("## process call roster 1 round not find batch:" + batchRosters.getBatchId());
				} else {
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
					 * 删除completebatch中的batchinfo
					 */
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

	private synchronized void processBatch(RosterBatch rosterBatch) {
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

			rosterBatch.setStatus(1);
			rosterBatch.setStartTime(TimeUtil.getCurrentTimeStr());
			batchDao.updateRosterBatch(rosterBatch);

			findAndCreateBatchInfo(batchRosters);
		}
	}

	public void getUncallBatch() {
		List<RosterBatch> uncallBatch = batchDao.findUncallBatch(activityInfo.getDomain(),
				activityInfo.getRosterTemplateName());
		if (uncallBatch != null) {
			logger.info("import rosterBatchs number:" + uncallBatch.size());
			newBatchList.addAll(uncallBatch);
		}
	}

	/**
	 * 在刚运行程序时 库里可能有状态未完成的批次 需要导入
	 * 
	 * @author zzj
	 */
	public void getFirstUncallBatch() {
		List<RosterBatch> planuncallBatch = batchDao.findPlancallBatch(activityInfo.getDomain(),
				activityInfo.getRosterTemplateName());
		logger.info("first import rosterBatchs number:" + planuncallBatch.size());
		if (planuncallBatch != null) {
			newBatchList.addAll(planuncallBatch);
		}
	}

	private synchronized void processInBatch(RosterBatch rosterBatch, int curRosterNum) {
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

			rosterBatch.setStatus(1);
			rosterBatch.setStartTime(TimeUtil.getCurrentTimeStr());
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

	public synchronized void checkAndDelRoster(RosterBatch rosterBatch, List<Roster> rosterList, String action) {
		Collection<Roster> c = null;
		// PlanRosterPool PlanRosterPool = planRosterThread.getRosterpoll();
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
		logger.info("##### planRoster pool thread [roster:" + activityInfo.getName() + ":" + activityInfo.getDomain()
				+ "] start to run ");
		TaskContainer.addPlanRosterThread(AOUtil.genernateRosterThdId(activityInfo.getDomain(), activityInfo.getName()),
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
				List<Roster> rosterList = null;

				// 查找已拨打、未完成的批次
				// 若是计划任务 那么rosterbatch就是getUncallBatch中的batch
				for (RosterBatch rosterBatch : newBatchList) {
					if (rosterBatch != null) {
						if (runFlag == 0) {
							runFlag = 1;
							rosterList = rosterDao.findInBatchRosters(activityInfo.getDomain(),
									rosterBatch.getBatchId(), activityInfo);
						} else {
							rosterList = rosterDao.findBatchRosters(activityInfo.getDomain(), rosterBatch.getBatchId(),
									activityInfo);
						}

						if (rosterList == null || rosterList.size() == 0) {
							continue;
						} else {
							// 获取到正在进行批次的名单数据
							if (runFlag == 1) {
								runFlag = 2;
							}
						}
						if (runFlag == 2) {
							runFlag = 3;
							processInBatch(rosterBatch, rosterList.size());
						} else {
							processBatch(rosterBatch);
						}

						if (rosterList == null || rosterList.size() == 0) {
							rosterList = rosterDao.findBatchRosters(activityInfo.getDomain(), rosterBatch.getBatchId(),
									activityInfo);
						}

						if (rosterList == null) {
							logger.info("### no roster for batch " + rosterBatch.toString());
						} else {
							logger.info(
									"### find roster " + rosterBatch.toString() + " size [" + rosterList.size() + "]");

							for (int i = 0; i < rosterList.size(); ++i) {
								Roster roster = rosterList.get(i);

								switch (roster.getStatus()) {
								case 0: // 系统没有抽取
								{

									roster.setStatus(1);
									// 更新名单的状态
									rosterDao.updateRoster(roster);
									// 添加到待外呼名单池
									rosterpoll.addCallRoster(roster);
									addRoster(roster);
								}
									break;
								case 1: // 已抽取但没有外呼，如果是第一次启动等同未抽取
								{
									// 添加到待外呼名单池
									rosterpoll.addCallRoster(roster);
									addRoster(roster);
								}
									break;
								case 2: // 已呼，但是轮次或回呼未执行完成
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
				newBatchList.clear();
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
				logger.error("##### plan roster pool thread [activity:" + activityInfo.getName() + ":"
						+ activityInfo.getDomain() + "] run error:" + e.getMessage());
			}
		}

		TaskContainer.deleteRosterThread(AOUtil.genernateRosterThdId(activityInfo.getDomain(), activityInfo.getName()));
		logger.info("###### plan roster pool thread  activity:" + activityInfo.getName() + " excute complete");
	}

	public void stopRoster() {
		isDone = true;
		logger.info("plan roster tread isDone changed, isDone :" + isDone);
	}


	public void pauseRoster() {
		activityInfo.setStatus(2);
		logger.info("##### change plan rosterThread activityStatus");
	}
	
	public void resumeRoster() {
		activityInfo.setStatus(1);
		logger.info("##### change plan rosterThread activityStatus to 1");
	}
	
	public void stopRosterStatus() {
		activityInfo.setStatus(3);
		logger.info("##### change plan rosterThread activityStatus to 3");
	}

}
