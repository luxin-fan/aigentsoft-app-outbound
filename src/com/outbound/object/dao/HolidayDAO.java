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
import com.outbound.object.Holiday;

public class HolidayDAO extends HibernateDaoSupport {

	private Logger logger = Logger.getLogger(HolidayDAO.class.getName());
	
	static Gson gson = new Gson();

	public List<Holiday> getTHolidays(String domain, final int startPage, final int pageNum) {
		final String hqlString = "from Holiday where domain='" + domain+ "'";
		logger.info("## getHolidays [" + hqlString + "] startPage-"
					+ startPage +"|pageNum-"+ pageNum);
		@SuppressWarnings("unchecked")
		List<Holiday> list = getHibernateTemplate().executeFind(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query query = session.createQuery(hqlString);
						query.setFirstResult(startPage * pageNum);
						query.setMaxResults(pageNum);
						List<Holiday> list = query.list();
						return list;
					}
				});
		return list;
	}

	public int getTHolidayNum(String domain) {
		String hqlString = "select count(*) from Holiday where domain='"+ domain + "'";
		Long count = (Long) getHibernateTemplate().find(hqlString)
				.listIterator().next();
		logger.info("## getTHolidayNum [" + domain + "] result " + count.intValue());
		return count.intValue();
	}

	public Holiday findById(String id) {
		return (Holiday) getHibernateTemplate().get("com.outbound.object.Holiday", id);
	}

	public boolean createHoliday(Holiday holiday) throws DataAccessException {
		logger.info("## Holiday create " + gson.toJson(holiday));
		try {
			getHibernateTemplate().save(holiday);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

	public boolean updateHoliday(Holiday holiday) throws DataAccessException {
		logger.info("## Holiday update " + gson.toJson(holiday));
		try {
			getHibernateTemplate().update(holiday);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

	public boolean deleteHoliday(Holiday holiday) throws DataAccessException {
		logger.info("## Holiday delete " + gson.toJson(holiday));
		try {
			getHibernateTemplate().delete(holiday);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

}