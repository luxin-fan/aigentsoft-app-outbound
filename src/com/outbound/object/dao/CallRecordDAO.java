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
import com.outbound.object.CallRecord;

public class CallRecordDAO extends HibernateDaoSupport {

	private Logger logger = Logger.getLogger(CallRecordDAO.class.getName());
	
	static Gson gson = new Gson();

	public List<CallRecord> getAll() {
		@SuppressWarnings("unchecked")
		List<CallRecord> list = this.getHibernateTemplate().find(
				"From CallRecord ");
		logger.info("## get callrecord all size " + list.size());
		return list;
	}

	public List<CallRecord> getCallRecords(int switchId, final int startPage,
			final int pageNum, String querystr) {
		
		logger.info("## list agent get [" + startPage +" - " + pageNum+ "]");
		
		final String hqlString = "from CallRecord where caller like '%"
				+ querystr + "%' or callee like '%" + querystr
				+ "%'  or sipNetIp like '%" + querystr
				+ "%'  or startTime like '%" + querystr + "%' order by id desc";
		@SuppressWarnings("unchecked")
		List<CallRecord> list = getHibernateTemplate().executeFind(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query query = session.createQuery(hqlString);
						query.setFirstResult(startPage * pageNum);
						query.setMaxResults(pageNum);
						List<CallRecord> list = query.list();
						return list;
					}
				});
		 logger.info("## get callrecord sql [ "+ hqlString +  "] " + list.size());
		return list;
	}

	public int queryBind(String querystr) {
		final String hqlString = "select count(*) from CallRecord where caller like '%"
				+ querystr
				+ "%' or callee like '%"
				+ querystr
				+ "%'  or sipNetIp like '%"
				+ querystr
				+ "%'  or startTime like '%" + querystr + "%'";
		Long count = (Long) getHibernateTemplate().find(hqlString)
				.listIterator().next();
		 
		logger.info("## get callrecord sql [ "+ hqlString +  "] " + count.intValue());
		return count.intValue();
	}

	public List<CallRecord> queryCallRecord(String querystr){
		logger.info("queryCallRecord Get CallRecord All!");
		final String hqlString = "from CallRecord where caller like '%" + querystr+"%'  or callee like '%"+ querystr+"%' or agentId like '%"+ querystr+"%'";  
		 @SuppressWarnings("unchecked")
		List<CallRecord> list = getHibernateTemplate().find(hqlString);
		 
		 logger.info("## get CallRecord sql [ "+ hqlString +  "] " + list.size());
        return list;
		
	}
	
	public CallRecord queryRecordByUUID(String uuid){
		final String hqlString = "from CallRecord where uuid ='"+ uuid +"'";  
		 @SuppressWarnings("unchecked")
		List<CallRecord> list = getHibernateTemplate().find(hqlString);
		if(list != null & list.size()>0){
			return list.get(0);
		}
        return null;
		
	}
	
	public int getCallRecordNum() {
		String hqlString = "select count(*) from CallRecord ";
		Long count = (Long) getHibernateTemplate().find(hqlString)
				.listIterator().next();
		
		logger.info("## get callrecord sql [ "+ hqlString +  "] " + count.intValue());
		return count.intValue();
	}
	
	public List<CallRecord> getCallRecords2(final int startPage, final int pageNum){
		
		logger.info("## list CallRecord get [" + startPage +" - " + pageNum+ "]");
		final String hqlString = "from CallRecord ";  
		@SuppressWarnings("unchecked")
		List<CallRecord> list = getHibernateTemplate().executeFind(new HibernateCallback(){
            public Object doInHibernate(Session session) throws HibernateException,SQLException{
                Query query = session.createQuery(hqlString);
                query.setFirstResult(startPage*pageNum);
                query.setMaxResults(pageNum);
                List<CallRecord> list = query.list();
                return list;
            }
        });
		 logger.info("## get CallRecord all size " + list.size());
        return list;
	}
	
	public List<CallRecord> getCallRecordsByIvr(String ivrNum,String startTime,String endTime){
		
		final String hqlString = "from CallRecord where callee = '" + ivrNum 
				+ "' and startTime >= '"+ startTime 
				+"' and startTime < '"+ endTime
				+"'";  
		logger.info("## get CallRecord sql " + hqlString);
		@SuppressWarnings("unchecked")
		List<CallRecord> list = getHibernateTemplate().find(hqlString);
		logger.info("## get CallRecord all size " + list.size());
        return list;
	}
	
	public List<CallRecord> getCallRecordsByIvr(String ivrNum){
		
		final String hqlString = "from CallRecord where callee = '" + ivrNum +"'";  
		logger.info("## get CallRecord sql " + hqlString);
		@SuppressWarnings("unchecked")
		List<CallRecord> list = getHibernateTemplate().find(hqlString);
		logger.info("## get CallRecord all size " + list.size());
        return list;
	}
	
//	public int getCallRecordsByIvrNum(String ivrNum) {
//		String hqlString = "select count(*) from CallRecord where callee='"
//				+ ivrNum + "'";
//		Long count = (Long) getHibernateTemplate().find(hqlString)
//				.listIterator().next();
//		
//		logger.info("## get callrecord sql [ "+ hqlString +  "] " + count.intValue());
//		
//		return count.intValue();
//	}
	
	public List<CallRecord> getTCallRecords(String domain, final int startPage,
			final int pageNum) {
		
		logger.info("## list agent get [" + startPage +" - " + pageNum+ "]");
		
		final String hqlString = "from CallRecord where sipNetIp='" + domain
				+ "' order by id desc";
		@SuppressWarnings("unchecked")
		List<CallRecord> list = getHibernateTemplate().executeFind(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query query = session.createQuery(hqlString);
						query.setFirstResult(startPage * pageNum);
						query.setMaxResults(pageNum);
						List<CallRecord> list = query.list();
						return list;
					}
				});
		
		logger.info("## get callrecord sql [ "+ hqlString +  "] " + list.size());
		
		return list;
	}

	public int getTCallRecordNum(String domain) {
		String hqlString = "select count(*) from CallRecord where sipNetIp='"
				+ domain + "'";
		Long count = (Long) getHibernateTemplate().find(hqlString)
				.listIterator().next();
		
		logger.info("## get callrecord sql [ "+ hqlString +  "] " + count.intValue());
		
		return count.intValue();
	}

	public CallRecord findById(int id) {
		return (CallRecord) getHibernateTemplate().get(
				"com.conf.object.CallRecord", id);
	}

	public boolean createCallRecord(CallRecord callrecord)
			throws DataAccessException {
		
		logger.info("## callrecord create " + gson.toJson(callrecord) );
		
		try {
			getHibernateTemplate().save(callrecord);
		} catch (Exception e) {
			logger.error("dao error", e);
			return false;
		}
		return true;
	}

	public boolean updateCallRecord(CallRecord callrecord)
			throws DataAccessException {
		
		logger.info("## callrecord update " + gson.toJson(callrecord) );
		
		try {
			getHibernateTemplate().update(callrecord);
		} catch (Exception e) {
			logger.error("dao error", e);
			return false;
		}
		return true;
	}

	public boolean deleteCallRecord(CallRecord callrecord)
			throws DataAccessException {
		
		logger.info("## callrecord delete " + gson.toJson(callrecord) );
		
		try {
			getHibernateTemplate().delete(callrecord);
		} catch (Exception e) {
			logger.error("dao error", e);
			return false;
		}
		return true;
	}

}