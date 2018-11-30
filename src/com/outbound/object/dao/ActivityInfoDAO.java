package com.outbound.object.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.QueryException;
import org.hibernate.Session;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.google.gson.Gson;
import com.outbound.common.PageRequest;
import com.outbound.object.ActivityInfo;
import com.outbound.object.util.TimeUtil;

public class ActivityInfoDAO extends HibernateDaoSupport {

	private Logger logger = Logger.getLogger(ActivityInfoDAO.class.getName());

	static Gson gson = new Gson();

	public List<ActivityInfo> getTActivityInfos(PageRequest request) {
		final int startPage = request.getStartPage()-1;
		final int pageNum = request.getPageNum();
		String paramHqlString = "from ActivityInfo where domain='" + request.getDomain() + "' and name like '%" + (StringUtils.isBlank(request.getActivityName()) ? "" :  request.getActivityName()) + "%'";
		if (StringUtils.isNotBlank(request.getActivityStatus())){
			paramHqlString += " and status = " + request.getActivityStatus();
		}
		if (StringUtils.isNotBlank(request.getActivityBeginTime())){
			paramHqlString += " and beginDatetime >= '" + request.getActivityBeginTime() + "'";
		}
		if (StringUtils.isNotBlank(request.getActivityEndTime())){
			paramHqlString += " and beginDatetime <= '" + request.getActivityEndTime() + "'";
		}
		final String hqlString = paramHqlString;
		
		logger.info("## getActivityInfos [" + hqlString + "] startPage-" + startPage + "|pageNum-" + pageNum);
		@SuppressWarnings("unchecked")
		List<ActivityInfo> list = getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hqlString);
				query.setFirstResult(startPage * pageNum);
				query.setMaxResults(pageNum);
				List<ActivityInfo> list = query.list();
				return list;
			}
		});
		return list;
	}
	

	public int getTActivityInfoNum(PageRequest request) {
		String paramHqlString = "select count(*) from ActivityInfo where domain='" + request.getDomain() + "' and name like '%" + (StringUtils.isBlank(request.getActivityName()) ? "" : "\\\\"+request.getActivityName()) + "%'";
		if (StringUtils.isNotBlank(request.getActivityStatus())){
			paramHqlString += " and status = " + request.getActivityStatus();
		}
		if (StringUtils.isNotBlank(request.getActivityBeginTime())){
			paramHqlString += " and beginDatetime >= '" + request.getActivityBeginTime() + "'";
		}
		if (StringUtils.isNotBlank(request.getActivityEndTime())){
			paramHqlString += " and beginDatetime <= '" + request.getActivityEndTime() + "'";
		}
		final String hqlString = paramHqlString;
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
	//	logger.info("## getTActivityInfoNum [" + domain + "] result " + count.intValue());
		return count.intValue();
	}
	public List<ActivityInfo> getTActivityInfos(String domain, final int startPage, final int pageNum) {
		final String hqlString = "from ActivityInfo where domain='" + domain + "'";
	//	logger.info("## getActivityInfos [" + hqlString + "] startPage-" + startPage + "|pageNum-" + pageNum);
		@SuppressWarnings("unchecked")
		List<ActivityInfo> list = getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hqlString);
				query.setFirstResult(startPage * pageNum);
				query.setMaxResults(pageNum);
				List<ActivityInfo> list = query.list();
				return list;
			}
		});
		return list;
	}
	
	public List<ActivityInfo> getTActivityInfos2(String domain, final int startPage, final int pageNum,String activityName) {
		final String hqlString = "from ActivityInfo where domain='" + domain + "' and name = '" + activityName +"'";
//		logger.info("## getActivityInfos [" + hqlString + "] startPage-" + startPage + "|pageNum-" + pageNum);
		@SuppressWarnings("unchecked")
		List<ActivityInfo> list = getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hqlString);
				query.setFirstResult(startPage * pageNum);
				query.setMaxResults(pageNum);
				List<ActivityInfo> list = query.list();
				return list;
			}
		});
		return list;
	}
	
	public ActivityInfo getTActivityInfos(String domain, String templateName) {
		final String hqlString = "from ActivityInfo where domain='" + domain 
				+ "' and rosterTemplateName='"+templateName+"'";
		@SuppressWarnings("unchecked")
		List<ActivityInfo> list = getHibernateTemplate().find(hqlString);
		logger.info("## getActivityInfos [" + hqlString + "] ");
		if(list == null || list.isEmpty()){
			return null;
		}
		return list.get(0);
	}
	
	public List<ActivityInfo> getAllActivityInfos() {
		final String hqlString = "from ActivityInfo ";
		@SuppressWarnings("unchecked")
		List<ActivityInfo> list = getHibernateTemplate().find(hqlString);
		if(list == null || list.isEmpty()){
			return null;
		}
	//	logger.info("## getActivityInfos [" + hqlString + "] startPage-" + startPage + "|pageNum-" + pageNum);
		return list;
	}
	
	public List<ActivityInfo> getStoppedActivityInfos() {
		final String hqlString = "from ActivityInfo where status=3 ";
		@SuppressWarnings("unchecked")
		List<ActivityInfo> list = getHibernateTemplate().find(hqlString);
		if(list == null || list.isEmpty()){
			return null;
		}
		return list;
	}

	public ActivityInfo finActivityInfoByActivityName(String activityName, String domain){
		final String hqlString = "from ActivityInfo where name = '" + activityName +"' and domain = '" + domain + "'";
		@SuppressWarnings("unchecked")
		List<ActivityInfo> list = getHibernateTemplate().find(hqlString);
		if(list == null || list.isEmpty()){
			return null;
		}
	//	logger.info("## getActivityInfos [" + hqlString + "] startPage-" + startPage + "|pageNum-" + pageNum);
		return list.get(0);
	}
	
	public ActivityInfo findActivityInfoByTemplate(String templateName) {
		final String hqlString = "from ActivityInfo where rosterTemplateName = '" + templateName+"'";
		@SuppressWarnings("unchecked")
		List<ActivityInfo> list = getHibernateTemplate().find(hqlString);
		if(list == null || list.isEmpty()){
			return null;
		}
	//	logger.info("## getActivityInfos [" + hqlString + "] startPage-" + startPage + "|pageNum-" + pageNum);
		return list.get(0);
	}

	public ActivityInfo findActivityInfoByPolicy(String policy) {
		final String hqlString = "from ActivityInfo where policyName = '" + policy+"'";
		@SuppressWarnings("unchecked")
		List<ActivityInfo> list = getHibernateTemplate().find(hqlString);
		if(list == null || list.isEmpty()){
			return null;
		}
	//	logger.info("## getActivityInfos [" + hqlString + "] startPage-" + startPage + "|pageNum-" + pageNum);
		return list.get(0);
	}
	
	public ActivityInfo findActivityInfoByTrunk(String domain, String trunkGrp) {
		final String hqlString = "from ActivityInfo where domain='"+domain+"' and trunkGrp = '" + trunkGrp+"'";
		@SuppressWarnings("unchecked")
		List<ActivityInfo> list = getHibernateTemplate().find(hqlString);
		if(list == null || list.isEmpty()){
			return null;
		}
	//	logger.info("## getActivityInfos [" + hqlString + "] startPage-" + startPage + "|pageNum-" + pageNum);
		return list.get(0);
	}


	public boolean checkName(String name, String domain) throws QueryException{
		String hqlString = "select count(*) from ActivityInfo where name='"+ name + "' and domain='"+domain+"'";
		Long count = (Long) getHibernateTemplate().find(hqlString)
				.listIterator().next();
	//	logger.info("## getActivityInfos [" + hqlString + "] result " + count.intValue());
		if(count == 0)
			return true;
		return false;
	}

	public int getTActivityInfoNum(String domain, String activityName) {
		String hqlString = "select count(*) from ActivityInfo where domain='" + domain + "' and name like '" + activityName +"'";
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
	//	logger.info("## getTActivityInfoNum [" + domain + "] result " + count.intValue());
		return count.intValue();
	}
	
	public int getTTrunkPoolNum(String domain, String poolname) {
		String hqlString = "select count(*) from ActivityInfo where domain='" + domain + "' "
				+ "and trunkPoolName='" + poolname + "'";
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTActivityInfoNum [" + domain + "] result " + count.intValue());
		return count.intValue();
	}

	public ActivityInfo findById(String id) {
		return (ActivityInfo) getHibernateTemplate().get("com.outbound.object.ActivityInfo", Integer.parseInt(id));
	}

	public boolean createActivityInfo(ActivityInfo info) throws DataAccessException {
		logger.info("## ActivityInfo create " + gson.toJson(info));
		try {
			getHibernateTemplate().save(info);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

	public boolean updateActivityInfo(ActivityInfo info) throws DataAccessException {
		logger.info("## ActivityInfo update " + gson.toJson(info));
		try {
			info.setLastUpdateTime(TimeUtil.getCurrentTimeStr());
			getHibernateTemplate().update(info);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

	public boolean deleteActivityInfo(ActivityInfo info) throws DataAccessException {
		logger.info("## ActivityInfo delete " + gson.toJson(info));
		try {
			getHibernateTemplate().delete(info);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}
	
	public List<ActivityInfo> getTActivityInfos2(String condition, final int startPage, final int pageNum) {
		final String hqlString = "from ActivityInfo where " + condition + " order by id asc";
		logger.info("## getActivityInfos [" + hqlString + "] startPage-" + startPage + "|pageNum-" + pageNum);
		@SuppressWarnings("unchecked")
		List<ActivityInfo> list = getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hqlString);
				query.setFirstResult(startPage * pageNum);
				query.setMaxResults(pageNum);
				List<ActivityInfo> list = query.list();
				return list;
			}
		});
		return list;
	}
	
	public List<ActivityInfo> getTActivityInfos2(String condition) {
		final String hqlString = "from ActivityInfo where " + condition + " order by id asc";
		logger.info("## getActivityInfos [" + hqlString + "] " );
		@SuppressWarnings("unchecked")
		List<ActivityInfo> list = getHibernateTemplate().find(hqlString);
		return list;
	}
	public int getTActivityInfoNum2(String condition) {
		String hqlString = "select count(*) from ActivityInfo where " + condition;
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTActivityInfoNum [" + condition + "] result " + count.intValue());
		return count.intValue();
	}

}