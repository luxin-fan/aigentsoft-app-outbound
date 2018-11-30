package com.outbound.object.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.QueryException;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.google.gson.Gson;
import com.outbound.object.RosterTemplatePreparedField;

public class RosterTemplatePreFieldDAO extends HibernateDaoSupport {

	private Logger logger = Logger.getLogger(RosterTemplatePreFieldDAO.class.getName());

	static Gson gson = new Gson();

	public List<RosterTemplatePreparedField> getTRosterTemplatePreparedFields() {
		final String hqlString = "from RosterTemplatePreparedField order by id asc ";
		logger.info("## getRosterTemplatePreparedFields [" + hqlString + "]");
		@SuppressWarnings("unchecked")
		List<RosterTemplatePreparedField> list = getHibernateTemplate().find(hqlString);
		return list;
	}
	
	public boolean checkPoolName(String name, String domain) throws QueryException{
		String hqlString = "select count(*) from RosterTemplatePreparedField where name='"+ name + "'";
		Long count = (Long) getHibernateTemplate().find(hqlString)
				.listIterator().next();
		logger.info("## getRosterTemplatePreparedFields [" + hqlString + "] result " + count.intValue());
		if(count == 0)
			return true;
		return false;
	}

	public int getTRosterTemplatePreparedFieldNum(String domain) {
		String hqlString = "select count(*) from RosterTemplatePreparedField where domain='" + domain + "'";
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTRosterTemplatePreparedFieldNum [" + domain + "] result " + count.intValue());
		return count.intValue();
	}
	
	public int getTTrunkPoolNum(String domain, String poolname) {
		String hqlString = "select count(*) from RosterTemplatePreparedField where domain='" + domain + "' "
				+ "and trunkPoolName='" + poolname + "'";
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTRosterTemplatePreparedFieldNum [" + domain + "] result " + count.intValue());
		return count.intValue();
	}

	public RosterTemplatePreparedField findById(String id) {
		return (RosterTemplatePreparedField) getHibernateTemplate().get("com.outbound.object.RosterTemplatePreparedField", id);
	}
	
	public RosterTemplatePreparedField findByName(String domain, String templateName) {
		final String hqlString = "from RosterTemplatePreparedField where domain='" + domain + "' and name='"+ templateName+"'" ;
		logger.info("## getRosterTemplatePreparedFields [" + hqlString + "] ");
		@SuppressWarnings("unchecked")
		List<RosterTemplatePreparedField> list = getHibernateTemplate().find(hqlString);
		if(list== null || list.size()==0){
			return null;
		}
		return list.get(0);
	}
	
	public boolean createRosterTemplatePreparedField(RosterTemplatePreparedField template) throws DataAccessException {
		logger.info("## RosterTemplatePreparedField create " + gson.toJson(template));
		try {
			getHibernateTemplate().save(template);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

	public boolean updateRosterTemplatePreparedField(RosterTemplatePreparedField template) throws DataAccessException {
		logger.info("## RosterTemplatePreparedField update " + gson.toJson(template));
		try {
			getHibernateTemplate().update(template);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

	public boolean deleteRosterTemplatePreparedField(RosterTemplatePreparedField template) throws DataAccessException {
		logger.info("## RosterTemplatePreparedField delete " + gson.toJson(template));
		try {
			getHibernateTemplate().delete(template);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}


}