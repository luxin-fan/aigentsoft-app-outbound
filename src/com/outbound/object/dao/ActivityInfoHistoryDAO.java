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

import com.google.gson.Gson;
import com.outbound.common.PageRequest;
import com.outbound.object.ActivityInfoHistory;

public class ActivityInfoHistoryDAO extends HibernateDaoSupport {

	private Logger logger = Logger.getLogger(ActivityInfoHistoryDAO.class.getName());

	static Gson gson = new Gson();

	public List<ActivityInfoHistory> getTActivityInfoHistorys(String domain,PageRequest request, final int startPage, final int pageNum) {
		String paramHqlString = "from ActivityInfoHistory where domain='" + domain + "' and name like '%" + (StringUtils.isBlank(request.getActivityName()) ? "" : "\\\\"+request.getActivityName()) + "%'";
		if (StringUtils.isNotBlank(request.getActivityBeginTime())){
			paramHqlString += " and beginDatetime >= '" + request.getActivityBeginTime() + "'";
		}
		if (StringUtils.isNotBlank(request.getActivityEndTime())){
			paramHqlString += " and beginDatetime <= '" + request.getActivityEndTime() + "'";
		}
		final String hqlString = paramHqlString;
		logger.info("## getActivityInfoHistorys [" + hqlString + "] startPage-" + startPage + "|pageNum-" + pageNum);
		@SuppressWarnings("unchecked")
		List<ActivityInfoHistory> list = getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hqlString);
				query.setFirstResult(startPage * pageNum);
				query.setMaxResults(pageNum);
				List<ActivityInfoHistory> list = query.list();
				return list;
			}
		});
		return list;
	}
	
	public ActivityInfoHistory getTActivityInfosHis(String domain, String templateName) {
		final String hqlString = "from ActivityInfoHistory where domain='" + domain 
				+ "' and rosterTemplateName='"+templateName+"'";
		@SuppressWarnings("unchecked")
		List<ActivityInfoHistory> list = getHibernateTemplate().find(hqlString);
		if(list == null || list.isEmpty()){
			return null;
		}
	//	logger.info("## getActivityInfos [" + hqlString + "] startPage-" + startPage + "|pageNum-" + pageNum);
		return list.get(0);
	}

	public boolean checkPoolName(String name, String domain) {
		String hqlString = "select count(*) from ActivityInfoHistory where name='"+ name + "'";
		Long count = (Long) getHibernateTemplate().find(hqlString)
				.listIterator().next();
		logger.info("## getActivityInfoHistorys [" + hqlString + "] result " + count.intValue());
		if(count == 0)
			return true;
		return false;
	}

	public int getTActivityInfoHistoryNum(String domain, PageRequest request) {
		String hqlString = "select count(*) from ActivityInfoHistory where domain='" + domain + "' and name like '%" + (StringUtils.isBlank(request.getActivityName()) ? "" : "\\\\"+request.getActivityName()) + "%'";
		if (StringUtils.isNotBlank(request.getActivityBeginTime())){
			hqlString += " and beginDatetime >= '" + request.getActivityBeginTime() + "'";
		}
		if (StringUtils.isNotBlank(request.getActivityEndTime())){
			hqlString += " and beginDatetime <= '" + request.getActivityEndTime() + "'";
		}
		if (StringUtils.isNotBlank(request.getTemplateName())){
			hqlString += " and rosterTemplateName = '" + request.getTemplateName() + "'";
		}
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTActivityInfoHistoryNum [" + domain + "] result " + count.intValue());
		return count.intValue();
	}
	
	public int getTTrunkPoolNum(String domain, String poolname) {
		String hqlString = "select count(*) from ActivityInfoHistory where domain='" + domain + "' "
				+ "and trunkPoolName='" + poolname + "'";
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTActivityInfoHistoryNum [" + domain + "] result " + count.intValue());
		return count.intValue();
	}

	public ActivityInfoHistory findById(String id) {
		return (ActivityInfoHistory) getHibernateTemplate().get("com.outbound.object.ActivityInfoHistory", id);
	}

	public boolean createActivityInfoHistory(ActivityInfoHistory info) throws DataAccessException {
		logger.info("## ActivityInfoHistory create " + gson.toJson(info));
		try {
			getHibernateTemplate().save(info);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

	public boolean updateActivityInfoHistory(ActivityInfoHistory info) throws DataAccessException {
		logger.info("## ActivityInfoHistory update " + gson.toJson(info));
		try {
			getHibernateTemplate().update(info);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

	public boolean deleteActivityInfoHistory(ActivityInfoHistory info) throws DataAccessException {
		logger.info("## ActivityInfoHistory delete " + gson.toJson(info));
		try {
			getHibernateTemplate().delete(info);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}
	public List<ActivityInfoHistory> getTActivityInfoHistorys2(String condition, final int startPage, final int pageNum) {
		final String hqlString = "from ActivityInfoHistory where " + condition + " order by id asc";
		logger.info("## getActivityInfoHistorys [" + hqlString + "] startPage-" + startPage + "|pageNum-" + pageNum);
		@SuppressWarnings("unchecked")
		List<ActivityInfoHistory> list = getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hqlString);
				query.setFirstResult(startPage * pageNum);
				query.setMaxResults(pageNum);
				List<ActivityInfoHistory> list = query.list();
				return list;
			}
		});
		return list;
	}
	
	public List<ActivityInfoHistory> getTActivityInfoHistorys2(String condition) {
		final String hqlString = "from ActivityInfoHistory where " + condition + " order by id asc";
		logger.info("## getActivityInfoHistorys [" + hqlString + "]");
		@SuppressWarnings("unchecked")
		List<ActivityInfoHistory> list = getHibernateTemplate().find(hqlString);
		return list;
	}
	
	public int getTActivityInfoHistoryNum2(String condition) {
		String hqlString = "select count(*) from ActivityInfoHistory where "+condition;
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTActivityInfoHistoryNum [" + condition + "] result " + count.intValue());
		return count.intValue();
	}

}