package com.outbound.object.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.util.CollectionUtils;

import com.google.gson.Gson;
import com.outbound.common.PageRequest;
import com.outbound.object.ActivityConditionModel;
import com.outbound.object.ActivityInfo;
import com.outbound.object.Roster;
import com.outbound.object.RosterBatch;
import com.outbound.object.RosterInfo;
import com.outbound.object.util.TimeUtil;


/*
 * add by potti at:2018-09-11 16:00
 */
public class RosterDAO extends HibernateDaoSupport
{
	private Logger logger = Logger.getLogger(RosterDAO.class.getName());

	static Gson gson = new Gson();
	
	
	public boolean createRoster(Roster roster) throws DataAccessException
	{
		logger.info("## Roster create " + gson.toJson(roster));
		try
		{
			getHibernateTemplate().save(roster);
		} 
		catch (Exception e)
		{
			logger.error(e);
			return false;
		}
		return true;
	}
	
	
	public List<Roster> findInBatchRosters(String domain, String batchName,  ActivityInfo activityInfo) 
	{
		String hqlString = "from Roster where domain='" + domain + "' and batchName='" + batchName + "'"
				+ " and status < 2";

		if (!CollectionUtils.isEmpty(activityInfo.getConditionInfoList()))
		{
			StringBuilder sb = new StringBuilder();
			for (ActivityConditionModel conditionInfo : activityInfo.getConditionInfoList())
			{
				if (conditionInfo.getCondition() != null)
				{
					if (conditionInfo.getCondition().equals("null"))
					{
						sb.append(" and ").append(conditionInfo.getName()).append(conditionInfo.getSymbol()).append("'")
								.append("'");
					} else
					{
						sb.append(" and ").append(conditionInfo.getName()).append(conditionInfo.getSymbol()).append("'")
								.append(conditionInfo.getCondition()).append("'");
					}
				}
			}
			hqlString += sb.toString();
		}

		if (activityInfo.getOrderTypeList() != null && activityInfo.getOrderTypeList().size() > 0)
		{
			StringBuilder sb = new StringBuilder(" order by ");
			int i = 0;
			for (String orderType : activityInfo.getOrderTypeList())
			{
				if (i++ != 0)
				{
					sb.append(",");
				}
				sb.append(orderType);
			}
			if (sb.toString().trim().length() > 8)
			{
				hqlString += sb.toString();
			}
		}

		//logger.info("## findBatchRosters [" + hqlString + "] ");
		@SuppressWarnings("unchecked")
		List<Roster> list = getHibernateTemplate().find(hqlString);
		if (list == null || list.size() == 0)
		{
			return null;
		}
		return list;
	}
	
	public List<Roster> findBatchRosters(String domain, String batchName,  ActivityInfo activityInfo) 
	{
		String hqlString = "from Roster where domain='" + domain + "' and batchName='" + batchName + "'"
				+ " and status = 0";

		if (!CollectionUtils.isEmpty(activityInfo.getConditionInfoList()))
		{
			StringBuilder sb = new StringBuilder();
			for (ActivityConditionModel conditionInfo : activityInfo.getConditionInfoList())
			{
				if (conditionInfo.getCondition() != null)
				{
					if (conditionInfo.getCondition().equals("null"))
					{
						sb.append(" and ").append(conditionInfo.getName()).append(conditionInfo.getSymbol()).append("'")
								.append("'");
					} else
					{
						sb.append(" and ").append(conditionInfo.getName()).append(conditionInfo.getSymbol()).append("'")
								.append(conditionInfo.getCondition()).append("'");
					}
				}
			}
			hqlString += sb.toString();
		}

		if (activityInfo.getOrderTypeList() != null && activityInfo.getOrderTypeList().size() > 0)
		{
			StringBuilder sb = new StringBuilder(" order by ");
			int i = 0;
			for (String orderType : activityInfo.getOrderTypeList())
			{
				if (i++ != 0)
				{
					sb.append(",");
				}
				sb.append(orderType);
			}
			if (sb.toString().trim().length() > 8)
			{
				hqlString += sb.toString();
			}
		}

		//logger.info("## findBatchRosters [" + hqlString + "] ");
		@SuppressWarnings("unchecked")
		List<Roster> list = getHibernateTemplate().find(hqlString);
		if (list == null || list.size() == 0)
		{
			return null;
		}
		return list;
	}
	
	/**
	 * 根据批次名查找对应的roster 查找status小于4的 roster
	 * @param domain
	 * @param batchName
	 * @param activityInfo
	 * @return
	 * @author zzj
	 */
	public List<Roster> findPlanBatchRosters(String domain, String batchName,  ActivityInfo activityInfo) 
	{
		String hqlString = "from Roster where domain='" + domain + "' and batchName='" + batchName + "'"
				+ " and status = 0";
		
		if (!CollectionUtils.isEmpty(activityInfo.getConditionInfoList()))
		{
			StringBuilder sb = new StringBuilder();
			for (ActivityConditionModel conditionInfo : activityInfo.getConditionInfoList())
			{
				if (conditionInfo.getCondition() != null)
				{
					if (conditionInfo.getCondition().equals("null"))
					{
						sb.append(" and ").append(conditionInfo.getName()).append(conditionInfo.getSymbol()).append("'")
						.append("'");
					} else
					{
						sb.append(" and ").append(conditionInfo.getName()).append(conditionInfo.getSymbol()).append("'")
						.append(conditionInfo.getCondition()).append("'");
					}
				}
			}
			hqlString += sb.toString();
		}
		
		if (activityInfo.getOrderTypeList() != null && activityInfo.getOrderTypeList().size() > 0)
		{
			StringBuilder sb = new StringBuilder(" order by ");
			int i = 0;
			for (String orderType : activityInfo.getOrderTypeList())
			{
				if (i++ != 0)
				{
					sb.append(",");
				}
				sb.append(orderType);
			}
			if (sb.toString().trim().length() > 8)
			{
				hqlString += sb.toString();
			}
		}
		
		//logger.info("## findBatchRosters [" + hqlString + "] ");
		@SuppressWarnings("unchecked")
		List<Roster> list = getHibernateTemplate().find(hqlString);
		if (list == null || list.size() == 0)
		{
			return null;
		}
		return list;
	}

	public List<Roster> firstFindRosters(String domain, ActivityInfo activityInfo)
	{
		String hqlString = "from Roster where domain='" + domain +  "'" + " and status < 3";

		if (!CollectionUtils.isEmpty(activityInfo.getConditionInfoList()))
		{
			StringBuilder sb = new StringBuilder();
			for (ActivityConditionModel conditionInfo : activityInfo.getConditionInfoList())
			{
				if (conditionInfo.getCondition() != null)
				{
					if (conditionInfo.getCondition().equals("null"))
					{
						sb.append(" and ").append(conditionInfo.getName()).append(conditionInfo.getSymbol()).append("'")
								.append("'");
					} 
					else
					{
						sb.append(" and ").append(conditionInfo.getName()).append(conditionInfo.getSymbol()).append("'")
								.append(conditionInfo.getCondition()).append("'");
					}
				}
			}
			hqlString += sb.toString();
		}

		if (activityInfo.getOrderTypeList() != null && activityInfo.getOrderTypeList().size() > 0)
		{
			StringBuilder sb = new StringBuilder(" order by ");
			int i = 0;
			for (String orderType : activityInfo.getOrderTypeList())
			{
				if (i++ != 0)
				{
					sb.append(",");
				}
				sb.append(orderType);
			}
			if (sb.toString().trim().length() > 8)
			{
				hqlString += sb.toString();
			}
		}

		logger.info("## First Find Roster [" + hqlString + "] ");
		@SuppressWarnings("unchecked")
		List<Roster> list = getHibernateTemplate().find(hqlString);
		if (list == null || list.size() == 0)
		{
			return null;
		}
		return list;
	}
	
	public List<Roster> findRosters(String domain, ActivityInfo activityInfo)
	{
		String hqlString = "from Roster where domain='" + domain +  "'" + " and status = 0";

		if (!CollectionUtils.isEmpty(activityInfo.getConditionInfoList()))
		{
			StringBuilder sb = new StringBuilder();
			for (ActivityConditionModel conditionInfo : activityInfo.getConditionInfoList())
			{
				if (conditionInfo.getCondition() != null)
				{
					if (conditionInfo.getCondition().equals("null"))
					{
						sb.append(" and ").append(conditionInfo.getName()).append(conditionInfo.getSymbol()).append("'")
								.append("'");
					} 
					else
					{
						sb.append(" and ").append(conditionInfo.getName()).append(conditionInfo.getSymbol()).append("'")
								.append(conditionInfo.getCondition()).append("'");
					}
				}
			}
			hqlString += sb.toString();
		}

		if (activityInfo.getOrderTypeList() != null && activityInfo.getOrderTypeList().size() > 0)
		{
			StringBuilder sb = new StringBuilder(" order by ");
			int i = 0;
			for (String orderType : activityInfo.getOrderTypeList())
			{
				if (i++ != 0)
				{
					sb.append(",");
				}
				sb.append(orderType);
			}
			if (sb.toString().trim().length() > 8)
			{
				hqlString += sb.toString();
			}
		}

		logger.info("## Roster [" + hqlString + "] ");
		@SuppressWarnings("unchecked")
		List<Roster> list = getHibernateTemplate().find(hqlString);
		if (list == null || list.size() == 0)
		{
			return null;
		}
		return list;
	}
	
	public boolean clearTemplate(String id, String domain)
	{
		String hqlString = "delete from Roster where templateName='"+ id + "' and domain='"+ domain+"'";
		int count = getHibernateTemplate().bulkUpdate(hqlString);
		logger.info("## clear DNCNumbers [" + hqlString + "] result " + count);
		if(count >0)
		{
			return true;
		}
		
		return false;
	}
	
	public int getContactNums(String domain, String templateName  )
	{
		String tString = "select count(*) from Roster where domain='" + domain + "'";
		if(templateName != null)
		{
			/**
			 * 未呼叫的列表名单数量的状态为0或者1
			 * @author zzj
			 * @date 2018/11/27
			 * */
			tString += " and templateName='" + templateName +"' and status <2 ";
		}
		final String hqlString = tString;
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTRosterInfoNum [" + domain + "] result " + count.intValue());
		return count.intValue();
	}
	
	
	public boolean isRosteExit(Roster rInfo)
	{
		String hqlString = "select count(*) from Roster where domain='" + rInfo.getDomain() + "' " + "and batchName='"
				+ rInfo.getBatchName() + "' ";
		if (rInfo.getPhoneNum1() != null)
		{
			hqlString += "and phoneNum1='" + rInfo.getPhoneNum1() + "' ";
		}
		if (rInfo.getPhoneNum2() != null)
		{
			hqlString += "and phoneNum2='" + rInfo.getPhoneNum2() + "' ";
		}
		if (rInfo.getPhoneNum3() != null)
		{
			hqlString += "and phoneNum3='" + rInfo.getPhoneNum3() + "' ";
		}
		if (rInfo.getPhoneNum3() != null)
		{
			hqlString += "and phoneNum4='" + rInfo.getPhoneNum4() + "' ";
		}
		if (rInfo.getPhoneNum3() != null)
		{
			hqlString += "and phoneNum5='" + rInfo.getPhoneNum5() + "' ";
		}
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTRosterInfoNum [" + hqlString + "] result " + count.intValue());
		if (count.intValue() > 0)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * 判断当前批次下是否有名单数据
	 * 
	 * @author fanlx
	 * @date 2018.10.24
	 * */
	public boolean isRosterExist(RosterBatch rosterBatch) {
		String hqlString = "select count(*) from Roster where domain='" + rosterBatch.getDomain() + "' " + "and batchName='"
				+ rosterBatch.getBatchId() + "' ";
		logger.info("## getRosters [ ");
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTRostersNum [" + hqlString + "] result " + count.intValue());
		if (count.intValue() > 0)
		{
			return true;
		}
		return false;
	}
	
	public boolean updateRoster(Roster roster) throws DataAccessException
	{
		logger.info("## Roster update " + gson.toJson(roster));
		try
		{
			getHibernateTemplate().update(roster);
		}
		catch (Exception e)
		{
			logger.error(e);
			return false;
		}
		return true;
	}
	
	public boolean clear(String id, String domain) {
		String hqlString = "delete from Roster where batchName='"+ id + "' and domain='"+ domain+"'";
		int count = getHibernateTemplate().bulkUpdate(hqlString);
		logger.info("## clear roster [" + hqlString + "] result " + count);
		if(count >0)
			return true;
		return false;
	}
	

	public Roster findById(String id) {
		return (Roster) getHibernateTemplate().get("com.outbound.object.Roster", Integer.parseInt(id));
	}
	
	public List<Roster> getTRosterInfos(String domain, PageRequest req, final int startPage, final int pageNum) {
		String tString = "from Roster where domain='" + domain + "' "; //and callRound = 1 and callOutTimes=0
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
		List<Roster> list = getHibernateTemplate().executeFind(new HibernateCallback() {
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
	
	public int getTRosterInfoNum(String domain, PageRequest req  ) {
		String tString = "select count(*) from Roster where domain='" + domain + "'";//and callRound = 1 
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
			/**
			 * @author zzj
			 * 
			 * 如果查询未呼叫的号码  则有两个状态  0,1 所以这里要再次判断
			 */
			if("0".equals(req.getRosterStatus())) {
				tString += " and status < 2 ";
			}else {
				tString += " and status = " + req.getRosterStatus();
			}
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
	
	public boolean deleteRoster(Roster roster) throws DataAccessException {
		logger.info("## Roster delete " + gson.toJson(roster));
		try {
			getHibernateTemplate().delete(roster);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}
	
	public List<Roster> getTRosterInfos2(String condition, final int startPage, final int pageNum) {
		String tString = "from Roster where " + condition + " order by makeCallTime desc";;
		final String hqlString = tString;
		logger.info("## getRosterInfos [" + hqlString + "] startPage-" + startPage + "|pageNum-" + pageNum);
		@SuppressWarnings("unchecked")
		List<Roster> list = getHibernateTemplate().executeFind(new HibernateCallback() {
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
	
	public List<Roster> getTRosterInfos2(String condition) {
		String tString = "from RosterInfo where " + condition + " order by id asc";;
		final String hqlString = tString;
		logger.info("## getRosterInfos [" + hqlString + "] " );
		@SuppressWarnings("unchecked")
		List<Roster> list = getHibernateTemplate().find(tString);
		return list;
	}
}
