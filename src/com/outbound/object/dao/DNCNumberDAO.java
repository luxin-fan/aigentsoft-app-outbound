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
import com.outbound.object.DNCNumber;

public class DNCNumberDAO extends HibernateDaoSupport {

	private Logger logger = Logger.getLogger(DNCNumberDAO.class.getName());

	static Gson gson = new Gson();

	public List<DNCNumber> getTDNCNumbers(String domain, String dncSet, final int startPage, final int pageNum) {
		final String hqlString = "from DNCNumber where domain='" + domain + "' and dncTemplateId='" + dncSet +"' order by id asc";
		logger.info("## getDNCNumbers [" + hqlString + "] startPage-" + startPage + "|pageNum-" + pageNum);
		@SuppressWarnings("unchecked")
		List<DNCNumber> list = getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hqlString);
				query.setFirstResult(startPage * pageNum);
				query.setMaxResults(pageNum);
				List<DNCNumber> list = query.list();
				return list;
			}
		});
		return list;
	}
	
	public List<DNCNumber> getTDNCNumbersQuery(String domain, String dncSet, String phoneNum, final int startPage, final int pageNum) {
		final String hqlString = "from DNCNumber where domain='" + domain 
				+ "' and dncTemplateId='" + dncSet +"'"
				+ " and phoneNum='" + phoneNum +"'";
		logger.info("## getDNCNumbers [" + hqlString + "] startPage-" + startPage + "|pageNum-" + pageNum);
		@SuppressWarnings("unchecked")
		List<DNCNumber> list = getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hqlString);
				query.setFirstResult(startPage * pageNum);
				query.setMaxResults(pageNum);
				List<DNCNumber> list = query.list();
				return list;
			}
		});
		return list;
	}
	
	public boolean checkPoolName(String name, String domain) {
		String hqlString = "select count(*) from DNCNumber where name='"+ name + "'";
		Long count = (Long) getHibernateTemplate().find(hqlString)
				.listIterator().next();
		logger.info("## getDNCNumbers [" + hqlString + "] result " + count.intValue());
		if(count == 0)
			return true;
		return false;
	}
	
	public boolean clear(String id, String domain) {
		String hqlString = "delete from DNCNumber where dncTemplateId='"+ id + "'";
		int count = getHibernateTemplate().bulkUpdate(hqlString);
		logger.info("## clear DNCNumbers [" + hqlString + "] result " + count);
		if(count >0)
			return true;
		return false;
	}

	public int getTDNCNumberNum(String domain, String dncSet) {
		String hqlString = "select count(*) from DNCNumber where domain='"+domain + "' and dncTemplateId='" + dncSet +"'";
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTDNCNumberNum [" + domain + "] result " + count.intValue());
		return count.intValue();
	}
	
	public int getTDNCNumberNumQuery(String domain, String dncSet, String phoneNum) {
		String hqlString = "select count(*) from DNCNumber where domain='"+domain 
				+ "' and dncTemplateId='" + dncSet +"'"
				+ " and phoneNum='" + phoneNum +"'";;
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTDNCNumberNum [" + domain + "] result " + count.intValue());
		return count.intValue();
	}
	
	public int getTTrunkPoolNum(String domain, String poolname) {
		String hqlString = "select count(*) from DNCNumber where domain='" + domain + "' "
				+ "and trunkPoolName='" + poolname + "'";
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTDNCNumberNum [" + domain + "] result " + count.intValue());
		return count.intValue();
	}

	public DNCNumber findById(String id) {
		return (DNCNumber) getHibernateTemplate().get("com.outbound.object.DNCNumber", Integer.parseInt(id));
	}

	public boolean createDNCNumber(DNCNumber info) throws DataAccessException {
		logger.info("## DNCNumber create " + gson.toJson(info));
		try {
			getHibernateTemplate().save(info);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

	public boolean updateDNCNumber(DNCNumber info) throws DataAccessException {
		logger.info("## DNCNumber update " + gson.toJson(info));
		try {
			getHibernateTemplate().update(info);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

	public boolean deleteDNCNumber(DNCNumber info) throws DataAccessException {
		logger.info("## DNCNumber delete " + gson.toJson(info));
		try {
			getHibernateTemplate().delete(info);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}


}