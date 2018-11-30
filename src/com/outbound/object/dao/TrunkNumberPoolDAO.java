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
import com.outbound.object.TrunkNumberPool;

public class TrunkNumberPoolDAO extends HibernateDaoSupport {

	private Logger logger = Logger.getLogger(TrunkNumberPoolDAO.class.getName());

	static Gson gson = new Gson();

	public List<TrunkNumberPool> getTTrunkNumberPools(String domain, final int startPage, final int pageNum) {
		final String hqlString = "from TrunkNumberPool where domain='" + domain + "'";
		logger.info("## getTrunkNumberPools [" + hqlString + "] startPage-" + startPage + "|pageNum-" + pageNum);
		@SuppressWarnings("unchecked")
		List<TrunkNumberPool> list = getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hqlString);
				query.setFirstResult(startPage * pageNum);
				query.setMaxResults(pageNum);
				List<TrunkNumberPool> list = query.list();
				return list;
			}
		});
		return list;
	}
	
	public boolean checkPoolName(String name, String domain) throws QueryException{
		String hqlString = "select count(*) from TrunkNumberPool where name='"+ name + "'";
		Long count = (Long) getHibernateTemplate().find(hqlString)
				.listIterator().next();
		logger.info("## getTrunkNumberPools [" + hqlString + "] result " + count.intValue());
		if(count == 0)
			return true;
		return false;
	}

	public int getTTrunkNumberPoolNum(String domain) {
		String hqlString = "select count(*) from TrunkNumberPool where domain='" + domain + "'";
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTTrunkNumberPoolNum [" + domain + "] result " + count.intValue());
		return count.intValue();
	}

	public TrunkNumberPool findById(String id) {
		return (TrunkNumberPool) getHibernateTemplate().get("com.outbound.object.TrunkNumberPool", id);
	}

	public boolean createTrunkNumberPool(TrunkNumberPool trunkNumberPool) throws DataAccessException {
		logger.info("## TrunkNumberPool create " + gson.toJson(trunkNumberPool));
		try {
			getHibernateTemplate().save(trunkNumberPool);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

	public boolean updateTrunkNumberPool(TrunkNumberPool trunkNumberPool) throws DataAccessException {
		logger.info("## TrunkNumberPool update " + gson.toJson(trunkNumberPool));
		try {
			getHibernateTemplate().update(trunkNumberPool);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

	public boolean deleteTrunkNumberPool(TrunkNumberPool trunkNumberPool) throws DataAccessException {
		logger.info("## TrunkNumberPool delete " + gson.toJson(trunkNumberPool));
		try {
			getHibernateTemplate().delete(trunkNumberPool);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}


}