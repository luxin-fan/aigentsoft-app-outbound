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
import com.outbound.object.TrunkNumber;

public class TrunkNumberDAO extends HibernateDaoSupport {

	private Logger logger = Logger.getLogger(TrunkNumberDAO.class.getName());

	static Gson gson = new Gson();

	public List<TrunkNumber> getTTrunkNumbers(String domain, final int startPage, final int pageNum) {
		final String hqlString = "from TrunkNumber where domain='" + domain + "'";
		logger.info("## getTrunkNumbers [" + hqlString + "] startPage-" + startPage + "|pageNum-" + pageNum);
		@SuppressWarnings("unchecked")
		List<TrunkNumber> list = getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hqlString);
				query.setFirstResult(startPage * pageNum);
				query.setMaxResults(pageNum);
				List<TrunkNumber> list = query.list();
				return list;
			}
		});
		return list;
	}

	public boolean checkPoolName(String name, String domain) throws QueryException{
		/*
		 * String hqlString = "select count(*) from getTrunkNumbers where domain='"+
		 * domain + "' and trunkGrp='"+ name +"'";
		 */
		String hqlString = "select count(*) from TrunkNumber where displayNum='" + name + "'";
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTrunkNumbers [" + hqlString + "] result " + count.intValue());
		if (count == 0)
			return true;
		return false;
	}

	public int getTTrunkNumberNum(String domain) {
		String hqlString = "select count(*) from TrunkNumber where domain='" + domain + "'";
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTTrunkNumberNum [" + domain + "] result " + count.intValue());
		return count.intValue();
	}

	public TrunkNumber findById(String id) {
		return (TrunkNumber) getHibernateTemplate().get("com.outbound.object.TrunkNumber", id);
	}
	

	public TrunkNumber findbyNum(String domain,String number) {
		final String hqlString = "from TrunkNumber where domain='" + domain + "' and displayNum='"+ number+"'";
		logger.info("## getTrunkNumbers [" + hqlString + "] ");
		@SuppressWarnings("unchecked")
		List<TrunkNumber> list = getHibernateTemplate().find(hqlString);
		if(list == null || list.size() ==0){
			return null;
		}
		return list.get(0);
	}

	public boolean createTrunkNumber(TrunkNumber trunkNumber) throws DataAccessException {
		logger.info("## TrunkNumber create " + gson.toJson(trunkNumber));
		try {
			getHibernateTemplate().save(trunkNumber);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

	public boolean updateTrunkNumber(TrunkNumber trunkNumber) throws DataAccessException {
		logger.info("## TrunkNumber update " + gson.toJson(trunkNumber));
		try {
			getHibernateTemplate().update(trunkNumber);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

	public boolean deleteTrunkNumber(TrunkNumber trunkNumber) throws DataAccessException {
		logger.info("## TrunkNumber delete " + gson.toJson(trunkNumber));
		try {
			getHibernateTemplate().delete(trunkNumber);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

}