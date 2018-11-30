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
import com.outbound.object.TrunkPoolCorr;

public class TrunkPoolCorrDAO extends HibernateDaoSupport {

	private Logger logger = Logger.getLogger(TrunkPoolCorrDAO.class.getName());

	static Gson gson = new Gson();

	public List<TrunkPoolCorr> getTTrunkPoolCorrs(String domain,String poolName, final int startPage, final int pageNum) {
		final String hqlString = "from TrunkPoolCorr where domain='" + domain + "' and trunkPoolName='"+ poolName +"'";
		logger.info("## getTrunkPoolCorrs [" + hqlString + "] startPage-" + startPage + "|pageNum-" + pageNum);
		@SuppressWarnings("unchecked")
		List<TrunkPoolCorr> list = getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hqlString);
				query.setFirstResult(startPage * pageNum);
				query.setMaxResults(pageNum);
				List<TrunkPoolCorr> list = query.list();
				return list;
			}
		});
		return list;
	}
	

	public boolean checkPoolName(String name, String domain) {
		String hqlString = "select count(*) from TrunkPoolCorr where name='"+ name + "'";
		Long count = (Long) getHibernateTemplate().find(hqlString)
				.listIterator().next();
		logger.info("## getTrunkPoolCorrs [" + hqlString + "] result " + count.intValue());
		if(count == 0)
			return true;
		return false;
	}
	
	public List<TrunkPoolCorr> getTTrunkPoolCorrs(String domain,String poolName) {
		final String hqlString = "from TrunkPoolCorr where domain='" + domain + "' and trunkPoolName='"+ poolName +"'";
		logger.info("## getTrunkPoolCorrs [" + hqlString + "] ");
		@SuppressWarnings("unchecked")
		List<TrunkPoolCorr> list = getHibernateTemplate().find(hqlString);
		return list;
	}

	public int getTTrunkPoolCorrNum(String domain, String poolName) {
		String hqlString = "select count(*) from TrunkPoolCorr where domain='" + domain + "' and trunkPoolName='"+ poolName +"'";
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTTrunkPoolCorrNum [" + domain + "] result " + count.intValue());
		return count.intValue();
	}
	
	public int getTTrunkPoolNum(String domain, String poolname) {
		String hqlString = "select count(*) from TrunkPoolCorr where domain='" + domain + "' "
				+ "and trunkPoolName='" + poolname + "'";
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTTrunkPoolCorrNum [" + domain + "] result " + count.intValue());
		return count.intValue();
	}

	public TrunkPoolCorr findById(String id) {
		return (TrunkPoolCorr) getHibernateTemplate().get("com.outbound.object.TrunkPoolCorr", id);
	}

	public boolean createTrunkPoolCorr(TrunkPoolCorr trunkPoolCorr) throws DataAccessException {
		logger.info("## TrunkPoolCorr create " + gson.toJson(trunkPoolCorr));
		try {
			getHibernateTemplate().save(trunkPoolCorr);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

	public boolean updateTrunkPoolCorr(TrunkPoolCorr trunkPoolCorr) throws DataAccessException {
		logger.info("## TrunkPoolCorr update " + gson.toJson(trunkPoolCorr));
		try {
			getHibernateTemplate().update(trunkPoolCorr);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

	public boolean deleteTrunkPoolCorr(TrunkPoolCorr trunkPoolCorr) throws DataAccessException {
		logger.info("## TrunkPoolCorr delete " + gson.toJson(trunkPoolCorr));
		try {
			getHibernateTemplate().delete(trunkPoolCorr);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

	public boolean deleteByNumber(String number, String domain) throws DataAccessException {
		String hqlString = "delete from TrunkPoolCorr where domain='" + domain + "' "
				+ "and displayNum='" + number + "'";
		int count = (int) getHibernateTemplate().bulkUpdate(hqlString);
		return true;
	}

}