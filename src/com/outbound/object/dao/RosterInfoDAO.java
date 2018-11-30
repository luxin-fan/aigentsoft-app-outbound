package com.outbound.object.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.util.CollectionUtils;

import com.google.gson.Gson;
import com.outbound.common.PageRequest;
import com.outbound.object.ActivityConditionModel;
import com.outbound.object.ActivityInfo;
import com.outbound.object.RosterInfo;
import com.outbound.object.RosterInfoMetric;
import com.outbound.object.util.TimeUtil;

public class RosterInfoDAO extends HibernateDaoSupport {

	private Logger logger = Logger.getLogger(RosterInfoDAO.class.getName());

	static Gson gson = new Gson();

	/*
	public List<RosterInfo> getTRosterInfos(String domain, PageRequest req, final int startPage, final int pageNum) {
		String tString = "from RosterInfo where domain='" + domain + "'";
		if(req.getTemplateName() != null){
			tString += " and templateName='" + req.getTemplateName()+"'";
		}
		if(req.getBatchName()!= null){
			tString += " and batchName='" + req.getBatchName()+"'";
		}
		if(req.getRosterStatus() != null){
			tString += " and status='" + req.getRosterStatus()+"'";
		}
		if(req.getPhoneNum() != null){
			tString += " and (phoneNum1 like '%" + req.getPhoneNum()+"%' or phoneNum2 like '%" + req.getPhoneNum()+"% ' or phoneNum3 like '%" + req.getPhoneNum()+"%' )";
		}
		
		final String hqlString = tString;
		logger.info("## getRosterInfos [" + hqlString + "] startPage-" + startPage + "|pageNum-" + pageNum);
		@SuppressWarnings("unchecked")
		List<RosterInfo> list = getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hqlString);
				query.setFirstResult(startPage * pageNum);
				query.setMaxResults(pageNum);
				List<RosterInfo> list = query.list();
				return list;
			}
		});
		return list;
	}
	*/
	public List<RosterInfo> getRosterInfosByIvr(String ivrNum,String startTime,String endTime){
		
		final String hqlString = "from RosterInfo where callee = '" + ivrNum 
				+ "' and makeCallTime >= '"+ startTime 
				+"' and makeCallTime < '"+ endTime +"' and answerCallTime >0";  
		logger.info("## get RosterInfo sql " + hqlString);
		@SuppressWarnings("unchecked")
		List<RosterInfo> list = getHibernateTemplate().find(hqlString);
		logger.info("## get RosterInfo all size " + list.size());
        return list;
	}
	
	
	public List<RosterInfo> getTRosterInfos(String domain, PageRequest req, final int startPage, final int pageNum) {
		String tString = "from RosterInfo where domain='" + domain + "' and callRound = 1 and callOutTimes=0";
		if(StringUtils.isNotBlank(req.getTemplateName())){
			tString += " and templateName='" + req.getTemplateName()+"'";
		}
		if(StringUtils.isNotBlank(req.getBatchName())){
			tString += " and batchName = '" + req.getBatchName()+"'";
		}
		if(StringUtils.isNotBlank(req.getPhoneNum())){
			tString += " and (phoneNum1 like '%" + req.getPhoneNum()+"%' or phoneNum2 like '%" + req.getPhoneNum() 
					+ "%' or phoneNum3 like '%" + req.getPhoneNum()+"%' or phoneNum4 like '%" + req.getPhoneNum() 
					+ "%' or phoneNum5 like '%" + req.getPhoneNum()+"%') ";
		}
		if (StringUtils.isNotBlank(req.getRosterStatus())){
			tString += " and status = " + req.getRosterStatus();
		}
		if (req.getListType() == 1){
		 	tString += " and status = 0 and createTime like '%" + TimeUtil.getCurrentDateStr() +"%' ";
		}
		if (!CollectionUtils.isEmpty(req.getConditionInfoList())){
			StringBuilder sb = new StringBuilder();
			for (ActivityConditionModel conditionInfo : req.getConditionInfoList()){
				if(conditionInfo.getCondition()!= null){
					if (conditionInfo.getCondition().equals("null")){
						sb.append(" and ").append(conditionInfo.getName()).append(conditionInfo.getSymbol()).append("'").append("'");
					} else {
						sb.append(" and ").append(conditionInfo.getName()).append(conditionInfo.getSymbol()).append("'").append(conditionInfo.getCondition()).append("'");
					}
				}
			}
			tString += sb.toString();
		}
		
		if (req.getOrderTypeList() != null && req.getOrderTypeList().size()>0){
			StringBuilder sb = new StringBuilder(" order by ");
			int i = 0; 
			for (String orderType : req.getOrderTypeList()){
				if (i++ != 0){
					sb.append(",");
				}
				sb.append(orderType);
			}
			if(sb.toString().trim().length() > 8){
				tString += sb.toString();
			}
		}
		
		final String hqlString = tString;
		logger.info("## getRosterInfos [" + hqlString + "] startPage-" + startPage + "|pageNum-" + pageNum);
	
		@SuppressWarnings("unchecked")
		List<RosterInfo> list = getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hqlString);
				query.setFirstResult(startPage * pageNum);
				query.setMaxResults(pageNum);
				List<RosterInfo> list = query.list();
				return list;
			}
		});
		return list;
	}
	
	
	public boolean checkPoolName(String name, String domain) {
		String hqlString = "select count(*) from RosterInfo where name='"+ name + "'";
		Long count = (Long) getHibernateTemplate().find(hqlString)
				.listIterator().next();
		logger.info("## getRosterInfos [" + hqlString + "] result " + count.intValue());
		if(count == 0)
			return true;
		return false;
	}


	public int getTRosterInfoNum(String domain, PageRequest req  ) {
		String tString = "select count(*) from RosterInfo where domain='" + domain + "' and callRound = 1 ";
		if(StringUtils.isNotBlank(req.getTemplateName())){
			tString += " and templateName='" + req.getTemplateName()+"'";
		}
		if(StringUtils.isNotBlank(req.getBatchName())){
			tString += " and batchName = '" + req.getBatchName()+"'";
		}
		if(StringUtils.isNotBlank(req.getPhoneNum())){
			tString += " and (phoneNum1 like '%" + req.getPhoneNum()+"%' or phoneNum2 like '%" + req.getPhoneNum() 
					+ "%' or phoneNum3 like '%" + req.getPhoneNum()+"%' or phoneNum4 like '%" + req.getPhoneNum() 
					+ "%' or phoneNum5 like '%" + req.getPhoneNum()+"%') ";
		}
		if (StringUtils.isNotBlank(req.getRosterStatus())){
			tString += " and status = " + req.getRosterStatus();
		}
		if (req.getListType() == 1){
			tString += " and status = 0 and createTime like '%" + TimeUtil.getCurrentDateStr() +"%' ";
		}
		if (!CollectionUtils.isEmpty(req.getConditionInfoList())){
			StringBuilder sb = new StringBuilder();
			for (ActivityConditionModel conditionInfo : req.getConditionInfoList()){
				if(conditionInfo.getCondition()!= null){
					if (conditionInfo.getCondition().equals("null")){
						sb.append(" and ").append(conditionInfo.getName()).append(conditionInfo.getSymbol()).append("'").append("'");
					} else {
						sb.append(" and ").append(conditionInfo.getName()).append(conditionInfo.getSymbol()).append("'").append(conditionInfo.getCondition()).append("'");
					}
				}
			}
			tString += sb.toString();
		}
		
		if (req.getOrderTypeList() != null && req.getOrderTypeList().size()>0){
			StringBuilder sb = new StringBuilder(" order by ");
			int i = 0; 
			for (String orderType : req.getOrderTypeList()){
				if (i++ != 0){
					sb.append(",");
				}
				sb.append(orderType);
			}
			if(sb.toString().trim().length() > 8){
				tString += sb.toString();
			}
		}
		
		final String hqlString = tString;
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTRosterInfoNum [" + domain + "] result " + count.intValue());
		return count.intValue();
	}
	

	public int getContactNums(String domain, String templateName  ) {
		String tString = "select count(*) from RosterInfo where domain='" + domain + "'";
		if(templateName != null){
			tString += " and templateName='" + templateName +"' and status =0 ";
		}
		final String hqlString = tString;
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTRosterInfoNum [" + domain + "] result " + count.intValue());
		return count.intValue();
	}
	
	public int getTTrunkPoolNum(String domain, String poolname) {
		String hqlString = "select count(*) from RosterInfo where domain='" + domain + "' "
				+ "and trunkPoolName='" + poolname + "'";
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTRosterInfoNum [" + domain + "] result " + count.intValue());
		return count.intValue();
	}
	
	public List<RosterInfo> findBatchRosters(String domain, String batchName,  ActivityInfo activityInfo) {
		String hqlString = "from RosterInfo where domain='" + domain + "' and batchName='"+ batchName+"'" 
				+ " and status < 2";
				//+ " order by id asc" ;
		
		if (!CollectionUtils.isEmpty(activityInfo.getConditionInfoList())){
			StringBuilder sb = new StringBuilder();
			for (ActivityConditionModel conditionInfo : activityInfo.getConditionInfoList()){
				if(conditionInfo.getCondition()!= null){
					if (conditionInfo.getCondition().equals("null")){
						sb.append(" and ").append(conditionInfo.getName()).append(conditionInfo.getSymbol()).append("'").append("'");
					} else {
						sb.append(" and ").append(conditionInfo.getName()).append(conditionInfo.getSymbol()).append("'").append(conditionInfo.getCondition()).append("'");
					}
				}
			}
			hqlString += sb.toString();
		}
		
		if (activityInfo.getOrderTypeList() != null && activityInfo.getOrderTypeList().size()>0){
			StringBuilder sb = new StringBuilder(" order by ");
			int i = 0; 
			for (String orderType : activityInfo.getOrderTypeList()){
				if (i++ != 0){
					sb.append(",");
				}
				sb.append(orderType);
			}
			if(sb.toString().trim().length() > 8){
				hqlString += sb.toString();
			}
		}
		
		logger.info("## findBatchRosters [" + hqlString + "] ");
		@SuppressWarnings("unchecked")
		List<RosterInfo> list = getHibernateTemplate().find(hqlString);
		if(list== null || list.size()==0){
			return null;
		}
		return list;
	}
	
	public List<RosterInfo> findFinishBatchRosters(String domain, String batchName) {
		final String hqlString = "from RosterInfo where domain='" + domain + "' and batchName='"+ batchName+"'" 
				+ " and status <> 0 ";
		logger.info("## findBatchRosters [" + hqlString + "] ");
		@SuppressWarnings("unchecked")
		List<RosterInfo> list = getHibernateTemplate().find(hqlString);
		if(list== null || list.size()==0){
			return null;
		}
		return list;
	}
	
	public List<RosterInfo> findFinishBatchRostersRound (String domain, String batchName, int round) {
		final String hqlString = "from RosterInfo where domain='" + domain + "' and batchName='"+ batchName+"'" 
				+ " and status <> 0 and callRound="+ round;
		logger.info("## findBatchRosters [" + hqlString + "] ");
		@SuppressWarnings("unchecked")
		List<RosterInfo> list = getHibernateTemplate().find(hqlString);
		if(list== null || list.size()==0){
			return null;
		}
		return list;
	}
	
	public List<RosterInfo> findFinishRosters(String domain, String activityName) {
		final String hqlString = "from RosterInfo where domain='" + domain + "' and activityName='"+ activityName+"'" 
				+ " and status = 2  and isExport =0";
		logger.info("## findBatchRosters [" + hqlString + "] ");
		@SuppressWarnings("unchecked")
		List<RosterInfo> list = getHibernateTemplate().find(hqlString);
		if(list== null || list.size()==0){
			return null;
		}
		return list;
	}
	
	public int updateFinishRosters(String domain, String activityName) {
		final String hqlString = "update RosterInfo set isExport =1 where domain='" + domain + "' and activityName='"+ activityName+"'" 
				+ " and status = 2  and isExport =0";
		logger.info("## update [" + hqlString + "] ");
		int count = getHibernateTemplate().bulkUpdate(hqlString);
		return count;
	}
	
	public int updateFinishUncallRosters(String domain, String activityName) {
		final String hqlString = "update RosterInfo set status =2 where domain='" + domain + "' and activityName='"+ activityName+"'" 
				+ " and status = 0 ";
		logger.info("## update [" + hqlString + "] ");
		int count = getHibernateTemplate().bulkUpdate(hqlString);
		return count;
	}
	
	public boolean clear(String id, String domain) {
		String hqlString = "delete from RosterInfo where batchName='"+ id + "' and domain='"+ domain+"'";
		int count = getHibernateTemplate().bulkUpdate(hqlString);
		logger.info("## clear DNCNumbers [" + hqlString + "] result " + count);
		if(count >0)
			return true;
		return false;
	}
	
	public boolean clearTemplate(String id, String domain) {
		String hqlString = "delete from RosterInfo where templateName='"+ id + "' and domain='"+ domain+"'";
		int count = getHibernateTemplate().bulkUpdate(hqlString);
		logger.info("## clear DNCNumbers [" + hqlString + "] result " + count);
		if(count >0)
			return true;
		return false;
	}

	public RosterInfo findById(String id) {
		return (RosterInfo) getHibernateTemplate().get("com.outbound.object.RosterInfo", Integer.parseInt(id));
	}

	public boolean createRosterInfo(RosterInfo roster) throws DataAccessException {
		logger.info("## RosterInfo create " + gson.toJson(roster));
		try {
			getHibernateTemplate().save(roster);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

	public boolean updateRosterInfo(RosterInfo roster) throws DataAccessException {
		logger.info("## RosterInfo update " + gson.toJson(roster));
		try {
			getHibernateTemplate().update(roster);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

	public boolean deleteRosterInfo(RosterInfo roster) throws DataAccessException {
		logger.info("## RosterInfo delete " + gson.toJson(roster));
		try {
			getHibernateTemplate().delete(roster);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

	public boolean isRosteExit(RosterInfo rInfo) {
		String hqlString = "select count(*) from RosterInfo where domain='" + rInfo.getDomain() + "' "
				+ "and batchName='" + rInfo.getBatchName() + "' ";
		if(rInfo.getPhoneNum1()!= null){
			hqlString += "and phoneNum1='" + rInfo.getPhoneNum1() + "' ";
		}
		if(rInfo.getPhoneNum2()!= null){
			hqlString += "and phoneNum2='" + rInfo.getPhoneNum2() + "' ";
		}
		if(rInfo.getPhoneNum3()!= null){
			hqlString += "and phoneNum3='" + rInfo.getPhoneNum3() + "' ";
		}
		if(rInfo.getPhoneNum3()!= null){
			hqlString += "and phoneNum4='" + rInfo.getPhoneNum4() + "' ";
		}
		if(rInfo.getPhoneNum3()!= null){
			hqlString += "and phoneNum5='" + rInfo.getPhoneNum5() + "' ";
		}
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTRosterInfoNum [" + hqlString + "] result " + count.intValue());
		if(count.intValue() > 0)
			return true;
		return false;
	}
	
	public List<RosterInfo> getTRosterInfos2(String condition, final int startPage, final int pageNum) {
		String tString = "from RosterInfo where " + condition + " order by makeCallTime desc";
		final String hqlString = tString;
		logger.info("## getRosterInfos [" + hqlString + "] startPage-" + startPage + "|pageNum-" + pageNum);
		@SuppressWarnings("unchecked")
		List<RosterInfo> list = getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hqlString);
				query.setFirstResult(startPage * pageNum);
				query.setMaxResults(pageNum);
				List<RosterInfo> list = query.list();
				return list;
			}
		});
		return list;
	}
	
	public List<RosterInfo> getTRosterInfos2(String condition) {
		String tString = "from RosterInfo where " + condition + " order by id asc";;
		final String hqlString = tString;
		logger.info("## getRosterInfos [" + hqlString + "] " );
		@SuppressWarnings("unchecked")
		List<RosterInfo> list = getHibernateTemplate().find(tString);
		return list;
	}
	
	public int getTRosterInfoNum2(String condition  ) {
		String tString = "select count(*) from RosterInfo where " + condition;
		
		final String hqlString = tString;
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTRosterInfoNum [" + condition + "] result " + count.intValue());
		return count.intValue();
	}
	
	public int getAllRosterNums(String domain, String templateName) {
		String tString = "select count(*) from RosterInfo where domain='" + domain + "'";
		if(templateName != null){
			tString += " and templateName='" + templateName +"' and callRound =1  and callOutTimes=0 ";
		}
		final String hqlString = tString;
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		return count.intValue();
	}
	
	public int getTodayAllRosterNums(String domain, String templateName) {
		String tString = "select count(*) from RosterInfo where domain='" + domain + "'";
		if(templateName != null){
			tString += " and templateName='" + templateName +"' and callRound =1 and callOutTimes=0  and createTime like '%"
					+ TimeUtil.getCurrentDateStr() +"%'";
		}
		final String hqlString = tString;
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		return count.intValue();
	}
	
	public int getCalledRosterNums(String domain, String templateName) {
		String tString = "select count(*) from RosterInfo where domain='" + domain + "'";
		if(templateName != null){
			tString += " and templateName='" + templateName +"' and callRound =1 and status <> 0 ";
		}
		final String hqlString = tString;
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		return count.intValue();
	}
	
	public int getTodayCalledRosterNums(String domain, String templateName) {
		String tString = "select count(*) from RosterInfo where domain='" + domain + "'";
		if(templateName != null){
			tString += " and templateName='" + templateName +"' and callRound =1 and status <> 0 and createTime like '%"+ TimeUtil.getCurrentDateStr() +"%'";;
		}
		final String hqlString = tString;
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		return count.intValue();
	}
	
	//createTime like '%"+ TimeUtil.getCurrentDateStr() +"%'";
	
	public int getOutCallNums(String domain, String templateName) {
		String tString = "select count(*) from RosterInfo where domain='" + domain + "'";
		if(templateName != null){
			tString += " and templateName='" + templateName +"' and status <> 0 ";
		}
		final String hqlString = tString;
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		return count.intValue();
	}
	
	public int getTodayOutCallNums(String domain, String templateName) {
		String tString = "select count(*) from RosterInfo where domain='" + domain + "'";
		if(templateName != null){
			tString += " and templateName='" + templateName +"' and status <> 0 and createTime like '%"+ TimeUtil.getCurrentDateStr() +"%'";
		}
		final String hqlString = tString;
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		return count.intValue();
	}
	
	public int getOutCallAnswerNums(String domain, String templateName) {
		String tString = "select count(*) from RosterInfo where domain='" + domain + "'";
		if(templateName != null){
			tString += " and templateName='" + templateName +"' and resultCode=0 ";
		}
		final String hqlString = tString;
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		return count.intValue();
	}
	
	public int getTodayOutCallAnswerNums(String domain, String templateName) {
		String tString = "select count(*) from RosterInfo where domain='" + domain + "'";
		if(templateName != null){
			tString += " and templateName='" + templateName +"' and resultCode=0 and createTime like '%"+ TimeUtil.getCurrentDateStr() +"%'";
		}
		final String hqlString = tString;
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		return count.intValue();
	}
	
	public int getEffectAnswerNums(String domain, String batchId, int effectiveTime) {
		String tString = "select count(*) from RosterInfo where domain='" + domain + "'";
		if(batchId != null){
			tString += " and batchName='" + batchId +"' and resultCode=0 and answerCallTime >= "+effectiveTime;
		}
		final String hqlString = tString;
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		return count.intValue();
	}
	
	public String getAvgOutCallAnswerTime(String domain, String templateName) {
		String tString = "select AVG(answerCallTime) from RosterInfo where domain='" + domain + "'";
		if(templateName != null){
			tString += " and templateName='" + templateName +"' and resultCode=0 ";
		}
		@SuppressWarnings("unchecked")
		List<Double> list = getHibernateTemplate().find(tString);
		if(list.size()>0){
			Double result = list.get(0);
			if(result!=null && !result.equals("null")){
				String str = result.toString();
				return str.substring(0,str.indexOf("."));
			}
		}
		return "0";
	}
	
	public RosterInfoMetric getRosterMetric(String domain, String templateName) {
		try {
			long startTime = System.currentTimeMillis();
			String dateStr = TimeUtil.getCurrentDateStr();
			String sql1 = "(select count(*) as Counts from t_roster_info where domain='" + domain + "' and templateName='" + templateName +"' and callRound =1 and callOutTimes=0) a, ";
			String sql2 = "(select count(*) as Counts from t_roster_info where domain='" + domain + "' and templateName='" + templateName +"' and callRound =1 and callOutTimes=0  and createTime like '%"+ dateStr +"%') b, ";
			String sql3 = "(select count(*) as Counts from t_roster_info where domain='" + domain + "' and templateName='" + templateName +"' and status <> 0 and status <> 3 and createTime like '%"+ dateStr +"%') c, ";
			String sql4 = "(select count(*) as Counts from t_roster_info where domain='" + domain + "' and templateName='" + templateName +"' and callRound =1 and status <> 0  and status <> 3 ) d, ";
			String sql5 = "(select count(*) as Counts from t_roster_info where domain='" + domain + "' and templateName='" + templateName +"' and resultCode=0 and createTime like '%"+ dateStr +"%') e ";
			
			final String sql =  "select a.Counts as m1,b.Counts as m2,c.Counts as m3,d.Counts as m4,e.Counts as m5 from "
					+ sql1
					+ sql2
					+ sql3
					+ sql4
					+ sql5;
			logger.info("## ["+ sql +"] ");
			
			Session session=getHibernateTemplate().getSessionFactory().openSession();
			SQLQuery sqlQuery=session.createSQLQuery(sql);
			@SuppressWarnings("unchecked")
			List<Object> list= (List<Object>) sqlQuery.list();
			//List<AppMetric> list = this.getHibernateTemplate().find (sql);
			session.close();
			logger.info("## all RosterInfo get size [" + list.size() + "]");
			if(list != null && list.size() > 0){
				Object[] map = (Object[])list.get(0);
				RosterInfoMetric aMetric = new RosterInfoMetric();
				aMetric.setAllRosterNum(Integer.parseInt(map[0].toString()));
				aMetric.setTodayAllRosterNum(Integer.parseInt(map[1].toString()));
				aMetric.setTodayOutCallNum(Integer.parseInt(map[2].toString()));
				aMetric.setAllCalledRosterNum(Integer.parseInt(map[3].toString()));
				aMetric.setTodayOutCallAnswerNum(Integer.parseInt(map[4].toString()));
				long costTime = System.currentTimeMillis() - startTime;
				logger.info("## get batch metric cost ["+ costTime +"] ");
				return aMetric;
			}
			
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}
	
}