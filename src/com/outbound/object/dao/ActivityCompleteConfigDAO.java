package com.outbound.object.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.google.gson.Gson;
import com.outbound.object.ActivityCompleteConfig;

public class ActivityCompleteConfigDAO extends HibernateDaoSupport {

	private Logger logger = Logger.getLogger(ActivityCompleteConfigDAO.class.getName());

	static Gson gson = new Gson();

	public List<ActivityCompleteConfig> getTActivityCompleteConfigs(String domain, final int startPage, final int pageNum) {
		final String hqlString = "from ActivityCompleteConfig where domain='" + domain + "'";
		logger.info("## getActivityCompleteConfigs [" + hqlString + "] startPage-" + startPage + "|pageNum-" + pageNum);
		@SuppressWarnings("unchecked")
		List<ActivityCompleteConfig> list = getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hqlString);
				query.setFirstResult(startPage * pageNum);
				query.setMaxResults(pageNum);
				List<ActivityCompleteConfig> list = query.list();
				return list;
			}
		});
		return list;
	}
	

	public boolean checkPoolName(String name, String domain) {
		String hqlString = "select count(*) from ActivityCompleteConfig where name='"+ name + "'";
		Long count = (Long) getHibernateTemplate().find(hqlString)
				.listIterator().next();
		logger.info("## getActivityCompleteConfigs [" + hqlString + "] result " + count.intValue());
		if(count == 0)
			return true;
		return false;
	}

	public int getTActivityCompleteConfigNum(String domain) {
		String hqlString = "select count(*) from ActivityCompleteConfig where domain='" + domain + "'";
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTActivityCompleteConfigNum [" + domain + "] result " + count.intValue());
		return count.intValue();
	}
	
	public int getTTrunkPoolNum(String domain, String poolname) {
		String hqlString = "select count(*) from ActivityCompleteConfig where domain='" + domain + "' "
				+ "and trunkPoolName='" + poolname + "'";
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTActivityCompleteConfigNum [" + domain + "] result " + count.intValue());
		return count.intValue();
	}

	public ActivityCompleteConfig findById(String id) {
		return (ActivityCompleteConfig) getHibernateTemplate().get("com.outbound.object.ActivityCompleteConfig", id);
	}

	public boolean createActivityCompleteConfig(ActivityCompleteConfig info) throws DataAccessException {
		logger.info("## ActivityCompleteConfig create " + gson.toJson(info));
		try {
			getHibernateTemplate().save(info);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

	public boolean updateActivityCompleteConfig(ActivityCompleteConfig info) throws DataAccessException {
		logger.info("## ActivityCompleteConfig update " + gson.toJson(info));
		try {
			getHibernateTemplate().update(info);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

	public boolean deleteActivityCompleteConfig(ActivityCompleteConfig info) throws DataAccessException {
		logger.info("## ActivityCompleteConfig delete " + gson.toJson(info));
		try {
			getHibernateTemplate().delete(info);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}


}