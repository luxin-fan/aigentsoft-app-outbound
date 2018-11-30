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
import com.outbound.object.DNCTemplate;

public class DNCTemplateDAO extends HibernateDaoSupport {

	private Logger logger = Logger.getLogger(DNCTemplateDAO.class.getName());

	static Gson gson = new Gson();

	public List<DNCTemplate> getTDNCTemplates(String domain, final int startPage, final int pageNum) {
		final String hqlString = "from DNCTemplate where domain='" + domain + "'";
		logger.info("## getDNCTemplates [" + hqlString + "] startPage-" + startPage + "|pageNum-" + pageNum);
		@SuppressWarnings("unchecked")
		List<DNCTemplate> list = getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hqlString);
				query.setFirstResult(startPage * pageNum);
				query.setMaxResults(pageNum);
				List<DNCTemplate> list = query.list();
				return list;
			}
		});
		return list;
	}
	
	public List<DNCTemplate> getTDNCTemplates(String domain, String tname, final int startPage, final int pageNum) {
		final String hqlString = "from DNCTemplate where domain='" + domain + "' and dncTemplateName like '%"+ tname+"%'";
		logger.info("## getDNCTemplates [" + hqlString + "] startPage-" + startPage + "|pageNum-" + pageNum);
		@SuppressWarnings("unchecked")
		List<DNCTemplate> list = getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hqlString);
				query.setFirstResult(startPage * pageNum);
				query.setMaxResults(pageNum);
				List<DNCTemplate> list = query.list();
				return list;
			}
		});
		return list;
	}
	
	public boolean checkDNCName(String name, String domain) throws QueryException{
		String hqlString = "select count(*) from DNCTemplate where dncTemplateName='"+ name + "' and domain='" + domain + "'";;
		Long count = (Long) getHibernateTemplate().find(hqlString)
				.listIterator().next();
		logger.info("## getDNCTemplates [" + hqlString + "] result " + count.intValue());
		if(count == 0)
			return true;
		return false;
	}

	public int getTDNCTemplateNum(String domain) {
		String hqlString = "select count(*) from DNCTemplate where domain='" + domain + "'";
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTDNCTemplateNum [" + domain + "] result " + count.intValue());
		return count.intValue();
	}
	
	public int getTDNCTemplateNum(String domain, String tname) {
		String hqlString = "select count(*) from DNCTemplate where domain='" + domain + "' and dncTemplateName like '%"+ tname+"%'";
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTDNCTemplateNum [" + domain + "] result " + count.intValue());
		return count.intValue();
	}
	
	
	public int getTTrunkPoolNum(String domain, String poolname) {
		String hqlString = "select count(*) from DNCTemplate where domain='" + domain + "' "
				+ "and trunkPoolName='" + poolname + "'";
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTDNCTemplateNum [" + domain + "] result " + count.intValue());
		return count.intValue();
	}

	public DNCTemplate findById(String id) {
		return (DNCTemplate) getHibernateTemplate().get("com.outbound.object.DNCTemplate", Integer.parseInt(id));
	}

	public boolean createDNCTemplate(DNCTemplate info) throws DataAccessException {
		logger.info("## DNCTemplate create " + gson.toJson(info));
		try {
			getHibernateTemplate().save(info);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

	public boolean updateDNCTemplate(DNCTemplate info) throws DataAccessException {
		logger.info("## DNCTemplate update " + gson.toJson(info));
		try {
			getHibernateTemplate().update(info);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

	public boolean deleteDNCTemplate(DNCTemplate info) throws DataAccessException {
		logger.info("## DNCTemplate delete " + gson.toJson(info));
		try {
			getHibernateTemplate().delete(info);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}


}