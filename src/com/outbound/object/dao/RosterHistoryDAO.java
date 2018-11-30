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
import com.outbound.common.PageRequest;
import com.outbound.object.RosterInfoHistory;

public class RosterHistoryDAO extends HibernateDaoSupport {

	private Logger logger = Logger.getLogger(RosterHistoryDAO.class.getName());

	static Gson gson = new Gson();

	public List<RosterInfoHistory> getTRosterInfoHistorys(String domain, PageRequest req, final int startPage, final int pageNum) {
		String tString = "from RosterInfoHistory where domain='" + domain + "'";
		if(req.getTemplateName() != null){
			tString += " and templateName='" + req.getTemplateName()+"'";
		}
		if(req.getBatchName()!= null){
			tString += " and batchName='" + req.getBatchName()+"'";
		}
		final String hqlString = tString;
		logger.info("## getRosterInfoHistorys [" + hqlString + "] startPage-" + startPage + "|pageNum-" + pageNum);
		@SuppressWarnings("unchecked")
		List<RosterInfoHistory> list = getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hqlString);
				query.setFirstResult(startPage * pageNum);
				query.setMaxResults(pageNum);
				List<RosterInfoHistory> list = query.list();
				return list;
			}
		});
		return list;
	}
	

	public boolean checkPoolName(String name, String domain) {
		String hqlString = "select count(*) from RosterInfoHistory where name='"+ name + "'";
		Long count = (Long) getHibernateTemplate().find(hqlString)
				.listIterator().next();
		logger.info("## getRosterInfoHistorys [" + hqlString + "] result " + count.intValue());
		if(count == 0)
			return true;
		return false;
	}

	public int getTRosterInfoHistoryNum(String domain, PageRequest req  ) {
		String tString = "select count(*) from RosterInfoHistory where domain='" + domain + "'";
		if(req.getTemplateName() != null){
			tString += " and templateName='" + req.getTemplateName()+"'";
		}
		if(req.getBatchName()!= null){
			tString += " and batchName='" + req.getBatchName()+"'";
		}
		final String hqlString = tString;
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTRosterInfoHistoryNum [" + domain + "] result " + count.intValue());
		return count.intValue();
	}
	
	public int getTTrunkPoolNum(String domain, String poolname) {
		String hqlString = "select count(*) from RosterInfoHistory where domain='" + domain + "' "
				+ "and trunkPoolName='" + poolname + "'";
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTRosterInfoHistoryNum [" + domain + "] result " + count.intValue());
		return count.intValue();
	}
	
	public List<RosterInfoHistory> findBatchRosters(String domain, String batchName) {
		final String hqlString = "from RosterInfoHistory where domain='" + domain + "' and batchName='"+ batchName+"'" 
				+ " and status=0"
				+ " order by id asc" ;
		logger.info("## findBatchRosters [" + hqlString + "] ");
		@SuppressWarnings("unchecked")
		List<RosterInfoHistory> list = getHibernateTemplate().find(hqlString);
		if(list== null || list.size()==0){
			return null;
		}
		return list;
	}

	public RosterInfoHistory findById(String id) {
		return (RosterInfoHistory) getHibernateTemplate().get("com.outbound.object.RosterInfoHistory", Integer.parseInt(id));
	}

	public boolean createRosterInfoHistory(RosterInfoHistory roster) throws DataAccessException {
		logger.info("## RosterInfoHistory create " + gson.toJson(roster));
		try {
			getHibernateTemplate().save(roster);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

	public boolean updateRosterInfoHistory(RosterInfoHistory roster) throws DataAccessException {
		logger.info("## RosterInfoHistory update " + gson.toJson(roster));
		try {
			getHibernateTemplate().update(roster);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

	public boolean deleteRosterInfoHistory(RosterInfoHistory roster) throws DataAccessException {
		logger.info("## RosterInfoHistory delete " + gson.toJson(roster));
		try {
			getHibernateTemplate().delete(roster);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}
	public List<RosterInfoHistory> getTRosterInfoHistorys2(String condition, final int startPage, final int pageNum) {
		String tString = "from RosterInfoHistory where " + condition + " order by id asc";;
		final String hqlString = tString;
		logger.info("## getRosterInfoHistorys [" + hqlString + "] startPage-" + startPage + "|pageNum-" + pageNum);
		@SuppressWarnings("unchecked")
		List<RosterInfoHistory> list = getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hqlString);
				query.setFirstResult(startPage * pageNum);
				query.setMaxResults(pageNum);
				List<RosterInfoHistory> list = query.list();
				return list;
			}
		});
		return list;
	}
	
	public List<RosterInfoHistory> getTRosterInfoHistorys2(String condition) {
		String tString = "from RosterInfoHistory where " + condition + " order by id asc";;
		final String hqlString = tString;
		logger.info("## getRosterInfoHistorys [" + hqlString + "] " );
		@SuppressWarnings("unchecked")
		List<RosterInfoHistory> list = getHibernateTemplate().find(tString);
		return list;
	}
	public int getTRosterInfoHistoryNum2(String condition  ) {
		String tString = "select count(*) from RosterInfoHistory where " + condition;
		
		final String hqlString = tString;
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTRosterInfoHistoryNum [" + condition + "] result " + count.intValue());
		return count.intValue();
	}


}