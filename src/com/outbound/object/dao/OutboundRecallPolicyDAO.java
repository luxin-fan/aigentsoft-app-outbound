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
import com.outbound.object.OutboundRecallPolicy;

public class OutboundRecallPolicyDAO extends HibernateDaoSupport {

	private Logger logger = Logger.getLogger(OutboundRecallPolicyDAO.class.getName());

	static Gson gson = new Gson();

	public List<OutboundRecallPolicy> getTOutboundRecallPolicys(String domain, String activityName,
			final int startPage, final int pageNum) {
		final String hqlString = "from OutboundRecallPolicy where domain='" + domain + "' and activityName='"+ activityName+"' order by round asc ";
		//logger.info("## getOutboundRecallPolicys [" + hqlString + "] startPage-" + startPage + "|pageNum-" + pageNum);
		@SuppressWarnings("unchecked")
		List<OutboundRecallPolicy> list = getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hqlString);
				query.setFirstResult(startPage * pageNum);
				query.setMaxResults(pageNum);
				List<OutboundRecallPolicy> list = query.list();
				return list;
			}
		});
		return list;
	}
	
	public OutboundRecallPolicy findRecallPolicy(String domain, String activityName, int round
			) {
		final String hqlString = "from OutboundRecallPolicy where domain='" + domain + "' and activityName='"+ activityName+"'"
				+ " and round=" + round;
		@SuppressWarnings("unchecked")
		List<OutboundRecallPolicy> list = getHibernateTemplate().find(hqlString);
		if(list == null || list.size() ==0){
			return null;
		}
		return list.get(0);
	}
	
	public List<OutboundRecallPolicy> findRoundPolicy(String domain, String activityName)
	{
		final String hqlString = "from OutboundRecallPolicy where domain='" + domain + "' and activityName='"+ activityName+"'";
		@SuppressWarnings("unchecked")
		List<OutboundRecallPolicy> list = getHibernateTemplate().find(hqlString);
		return list;
	}
	

	public boolean checkPoolName(String name, String policyName, String domain) {
		String hqlString = "select count(*) from OutboundRecallPolicy where name='"+ name + "'";
		Long count = (Long) getHibernateTemplate().find(hqlString)
				.listIterator().next();
		logger.info("## getOutboundRecallPolicys [" + hqlString + "] result " + count.intValue());
		if(count == 0)
			return true;
		return false;
	}

	public int getTOutboundRecallPolicyNum(String domain, String policyName) {
		String hqlString = "select count(*) from OutboundRecallPolicy where domain='" + domain + "' and policyName='"+ policyName+"'";
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTOutboundRecallPolicyNum [" + domain + "] result " + count.intValue());
		return count.intValue();
	}
	
	public int getTTrunkPoolNum(String domain, String poolname) {
		String hqlString = "select count(*) from OutboundRecallPolicy where domain='" + domain + "' "
				+ "and trunkPoolName='" + poolname + "'";
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTOutboundRecallPolicyNum [" + domain + "] result " + count.intValue());
		return count.intValue();
	}

	public OutboundRecallPolicy findById(String id) {
		return (OutboundRecallPolicy) getHibernateTemplate().get("com.outbound.object.OutboundRecallPolicy", Integer.parseInt(id));
	}

	public boolean createOutboundRecallPolicy(OutboundRecallPolicy template) throws DataAccessException {
		logger.info("## OutboundRecallPolicy create " + gson.toJson(template));
		try {
			getHibernateTemplate().save(template);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

	public boolean updateOutboundRecallPolicy(OutboundRecallPolicy template) throws DataAccessException {
		logger.info("## OutboundRecallPolicy update " + gson.toJson(template));
		try {
			getHibernateTemplate().update(template);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}
	
	public boolean deleteAll(OutboundRecallPolicy template) throws DataAccessException {
		logger.info("## OutboundRecallPolicy delete " + gson.toJson(template));
		try {
			getHibernateTemplate().delete(template);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

	public boolean deleteOutboundRecallPolicy(String domain, String activityName) throws DataAccessException {
		//logger.info("## OutboundRecallPolicy delete " + gson.toJson(template));
		final String hqlString = "delete from OutboundRecallPolicy where domain='" + domain + "' and activityName='"+ activityName+"'";
		try {
			getHibernateTemplate().bulkUpdate(hqlString);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}


}