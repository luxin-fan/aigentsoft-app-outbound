package com.outbound.object.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.QueryException;
import org.hibernate.Session;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.google.gson.Gson;
import com.outbound.object.ConfigParam;

public class ConfigParamDAO extends HibernateDaoSupport {

	private Logger logger = Logger.getLogger(ConfigParamDAO.class.getName());
	
	static Gson gson = new Gson();

	public List<ConfigParam> getTConfigParams(String domain, String paramType, final int startPage, final int pageNum) {
		final String hqlString = "from ConfigParam where domain='" + domain+ "' and paramType='"+ paramType +"'";
		logger.info("## getConfigParams [" + hqlString + "] startPage-"
					+ startPage +"|pageNum-"+ pageNum);
		@SuppressWarnings("unchecked")
		List<ConfigParam> list = getHibernateTemplate().executeFind(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query query = session.createQuery(hqlString);
						query.setFirstResult(startPage * pageNum);
						query.setMaxResults(pageNum);
						List<ConfigParam> list = query.list();
						return list;
					}
				});
		return list;
	}
	
	public ConfigParam findByName(String domain, String name) {
		final String hqlString = "from ConfigParam where domain='" + domain+ "' and name='"+ name +"'";
		logger.info("## getConfigParams [" + hqlString + "]");
		@SuppressWarnings("unchecked")
		List<ConfigParam> list = getHibernateTemplate().find(hqlString);
		if(list!= null && list.size() >0)
			return list.get(0);
		return null;
	}
	

	public int getTConfigParamNum(String domain, String paramType) {
		String hqlString = "select count(*) from ConfigParam where domain='"+ domain 
				+ "' and paramType='"+ paramType +"'";
		Long count = (Long) getHibernateTemplate().find(hqlString)
				.listIterator().next();
		logger.info("## getTConfigParamNum [" + domain + "] result " + count.intValue());
		return count.intValue();
	}
	
	public ConfigParam findValidLength(String ivr) {
		final String hqlString = "from ConfigParam where paramType = 'ivr'" + " and value='"+ ivr +"'";
		logger.info("## getConfigParams [" + hqlString + "]");
		@SuppressWarnings("unchecked")
		List<ConfigParam> list = getHibernateTemplate().find(hqlString);
		if(list!= null && list.size() >0)
			return list.get(0);
		return null;
	}
	
	public boolean checkConfigParamName(String name, String domain) throws QueryException{
		/*
		String hqlString = "select count(*) from ConfigParam where domain='"+ domain 
				+ "' and name='"+ name +"'";
				*/
		String hqlString = "select count(*) from ConfigParam where name='"+ name 
				+ "'";
		Long count = (Long) getHibernateTemplate().find(hqlString)
				.listIterator().next();
		logger.info("## getTConfigParamNum [" + hqlString + "] result " + count.intValue());
		if(count == 0)
			return true;
		return false;
	}


	public ConfigParam findById(String id) {
		return (ConfigParam) getHibernateTemplate().get("com.outbound.object.ConfigParam", id);
	}

	public boolean createConfigParam(ConfigParam configParam) throws DataAccessException {
		logger.info("## ConfigParam create " + gson.toJson(configParam));
		try {
			getHibernateTemplate().save(configParam);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

	public boolean updateConfigParam(ConfigParam configParam) throws DataAccessException {
		logger.info("## ConfigParam update " + gson.toJson(configParam));
		try {
			getHibernateTemplate().update(configParam);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

	public boolean deleteConfigParam(ConfigParam configParam) throws DataAccessException {
		logger.info("## ConfigParam delete " + gson.toJson(configParam));
		try {
			getHibernateTemplate().delete(configParam);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

}