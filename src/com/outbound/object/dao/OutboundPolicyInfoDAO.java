package com.outbound.object.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.QueryException;
import org.hibernate.Session;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.google.gson.Gson;
import com.outbound.object.OutboundPolicyInfo;

public class OutboundPolicyInfoDAO extends HibernateDaoSupport {

	private Logger logger = Logger.getLogger(OutboundPolicyInfoDAO.class.getName());

	static Gson gson = new Gson();

	public List<OutboundPolicyInfo> getTOutboundPolicyInfos(String domain, final int startPage, final int pageNum, String policyName) {
		final String hqlString = "from OutboundPolicyInfo where domain='" + domain + "' and name like '%" + (StringUtils.isBlank(policyName) ? "" : "\\\\" + policyName) + "%'";
		logger.info("## getOutboundPolicyInfos [" + hqlString + "] startPage-" + startPage + "|pageNum-" + pageNum);
		@SuppressWarnings("unchecked")
		List<OutboundPolicyInfo> list = getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hqlString);
				query.setFirstResult(startPage * pageNum);
				query.setMaxResults(pageNum);
				List<OutboundPolicyInfo> list = query.list();
				return list;
			}
		});
		if(list != null){
		}
		return list;
	}
	
	public boolean checkName(String name, String domain) throws QueryException{
		String hqlString = "select count(*) from OutboundPolicyInfo where name='"+ name + "' and domain='"+domain+"'";
		Long count = (Long) getHibernateTemplate().find(hqlString)
				.listIterator().next();
		logger.info("## getOutboundPolicyInfos [" + hqlString + "] result " + count.intValue());
		if(count == 0)
			return true;
		return false;
	}

	public int getTOutboundPolicyInfoNum(String domain) {
		String hqlString = "select count(*) from OutboundPolicyInfo where domain='" + domain + "'";
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTOutboundPolicyInfoNum [" + domain + "] result " + count.intValue());
		return count.intValue();
	}
	
	public int getTOutboundPolicyInfoNum(String domain, String policyName) {
		String hqlString = "select count(*) from OutboundPolicyInfo where domain='" + domain + "' and name like '%" + (StringUtils.isBlank(policyName) ? "" : "\\\\" + policyName) + "%'";
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTOutboundPolicyInfoNum [" + domain + "] result " + count.intValue());
		return count.intValue();
	}
	
	public int findByAnswerStep(String domain, String step) {
		String hqlString = "select count(*) from OutboundPolicyInfo where domain='" + domain + "' and callAnswerStep like '%"+step+"%'";
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTOutboundPolicyInfoNum [" + domain + "] result " + count.intValue());
		return count.intValue();
	}
	
	
	public int getTTrunkPoolNum(String domain, OutboundPolicyInfo outboundPolicyInfo) {
		
		StringBuilder sb = new StringBuilder("select count(*) from OutboundPolicyInfo");
		sb.append(" where domain='").append(domain).append("'");
		if (StringUtils.isNotBlank(outboundPolicyInfo.getCallAnswerStep())){
			sb.append(" and callAnswerStep = '").append(outboundPolicyInfo.getCallAnswerStep()).append("'");
		}
		Long count = (Long) getHibernateTemplate().find(sb.toString()).listIterator().next();
		logger.info("## getTOutboundPolicyInfoNum [" + domain + "] result " + count.intValue());
		return count.intValue();
	}
	
	public int getTTrunkPoolNum(String domain, String poolname) {
		String hqlString = "select count(*) from OutboundPolicyInfo where domain='" + domain + "' "
				+ "and trunkPoolName='" + poolname + "'";
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTOutboundPolicyInfoNum [" + domain + "] result " + count.intValue());
		return count.intValue();
	}
	
	public OutboundPolicyInfo getTOutboundPolicyInfos(String domain, String policyName) {
		final String hqlString = "from OutboundPolicyInfo where domain='" + domain + "' and name='" + policyName+"'";
		logger.info("## getOutboundPolicyInfos [" + hqlString + "]");
		@SuppressWarnings("unchecked")
		List<OutboundPolicyInfo> list = getHibernateTemplate().find(hqlString);
		if(list == null || list.size()==0)
			return null;
		return list.get(0);
	}

	public OutboundPolicyInfo findById(String id) {
		return (OutboundPolicyInfo) getHibernateTemplate().get("com.outbound.object.OutboundPolicyInfo", id);
	}

	public boolean createOutboundPolicyInfo(OutboundPolicyInfo template) throws DataAccessException {
		logger.info("## OutboundPolicyInfo create " + gson.toJson(template));
		try {
			getHibernateTemplate().save(template);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

	public boolean updateOutboundPolicyInfo(OutboundPolicyInfo template) throws DataAccessException {
		logger.info("## OutboundPolicyInfo update " + gson.toJson(template));
		try {
			getHibernateTemplate().update(template);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

	public boolean deleteOutboundPolicyInfo(OutboundPolicyInfo template) throws DataAccessException {
		logger.info("## OutboundPolicyInfo delete " + gson.toJson(template));
		try {
			getHibernateTemplate().delete(template);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}


}