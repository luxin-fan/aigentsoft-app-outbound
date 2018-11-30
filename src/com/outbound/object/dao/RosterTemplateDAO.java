package com.outbound.object.dao;

import java.io.BufferedReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.outbound.object.RosterTemplateInfo;
import com.outbound.object.util.TimeUtil;

public class RosterTemplateDAO extends HibernateDaoSupport {

	private Logger logger = Logger.getLogger(RosterTemplateDAO.class.getName());

	static Gson gson = new Gson();

	public List<RosterTemplateInfo> getTRosterTemplateInfos(String domain, String templateName, final int startPage, final int pageNum) {
		final String hqlString = "from RosterTemplateInfo where domain='" + domain + "'"
				+ " and name like '%" + (StringUtils.isBlank(templateName) ? "" : "\\\\" + templateName) + "%'";
		logger.info("## getRosterTemplateInfos [" + hqlString + "] startPage-" + startPage + "|pageNum-" + pageNum);
		@SuppressWarnings("unchecked")
		List<RosterTemplateInfo> list = getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hqlString);
				query.setFirstResult(startPage * pageNum);
				query.setMaxResults(pageNum);
				List<RosterTemplateInfo> list = query.list();
				return list;
			}
		});
		return list;
	}
	
	public boolean checkName(String name, String domain) throws QueryException {
		String hqlString = "select count(*) from RosterTemplateInfo where name='"+ name + "' and domain='"+domain+"'";
		Long count = (Long) getHibernateTemplate().find(hqlString)
				.listIterator().next();
	//	logger.info("## getRosterTemplateInfos [" + hqlString + "] result " + count.intValue());
		if(count == 0)
			return true;
		return false;
	}

	public int getTRosterTemplateInfoNum(String domain) {
		String hqlString = "select count(*) from RosterTemplateInfo where domain='" + domain + "'";
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTRosterTemplateInfoNum [" + domain + "] result " + count.intValue());
		return count.intValue();
	}
	
	public int getTRosterTemplateInfoNum(String domain, String templateName) {
		String hqlString = "select count(*) from RosterTemplateInfo where domain='" + domain + "'"
						+ " and name like '%" + (StringUtils.isBlank(templateName) ? "" : "\\\\" + templateName) + "%'";
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTRosterTemplateInfoNum [" + domain + "] result " + count.intValue());
		return count.intValue();
	}
	
	
	public int getTTrunkPoolNum(String domain, String poolname) {
		String hqlString = "select count(*) from RosterTemplateInfo where domain='" + domain + "' "
				+ "and trunkPoolName='" + poolname + "'";
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTRosterTemplateInfoNum [" + domain + "] result " + count.intValue());
		return count.intValue();
	}

	public RosterTemplateInfo findById(String id) {
		return (RosterTemplateInfo) getHibernateTemplate().get("com.outbound.object.RosterTemplateInfo", Integer.parseInt(id));
	}
	
	public ArrayList<HashMap<String,String>> importIntoDateSource(String rosterName, BufferedReader reader, boolean isAuto, String batchId) throws Exception{
		String tempString = null;
		String[] arr = null;
		String[] arrColumn= null;
		int line = 0;
		ArrayList<HashMap<String,String>> contacts = new ArrayList<HashMap<String,String>>();
		//获取批次id
		String autoBatchId = TimeUtil.getCurrentTimeStr();
		try{
			while ((tempString = reader.readLine()) != null){
				arr = tempString.split(",");
				line++;
				if(line==1)//column array
					arrColumn=tempString.split(",");
				else {
					HashMap<String,String> map = new HashMap<String,String>();
					for(int i=0;i<arr.length;i++)
						map.put(arrColumn[i], arr[i]);
					
					if (!isAuto && StringUtils.isBlank(batchId))
						throw new IllegalArgumentException("请输入批次");
					
					if (!isAuto){
						map.put("batchId", batchId);
					} else {
						if (map.get("batchId") == null){
							map.put("batchId", autoBatchId);
						}
					}
					contacts.add(map);
				}
			} 
		}catch(Exception e){
			logger.info("upload compelete ");
		}
		return contacts;
	}
	
	
	
	public RosterTemplateInfo findByName(String domain, String templateName) {
		final String hqlString = "from RosterTemplateInfo where domain='" + domain + "' and name='"+ templateName+"'" ;
		logger.info("## getRosterTemplateInfos [" + hqlString + "] ");
		@SuppressWarnings("unchecked")
		List<RosterTemplateInfo> list = getHibernateTemplate().find(hqlString);
		if(list== null || list.size()==0){
			return null;
		}
		return list.get(0);
	}
	
	public RosterTemplateInfo findByNameNoDomain(String templateName) {
		final String hqlString = "from RosterTemplateInfo where name='"+ templateName+"'" ;
		logger.info("## getRosterTemplateInfos [" + hqlString + "] ");
		@SuppressWarnings("unchecked")
		List<RosterTemplateInfo> list = getHibernateTemplate().find(hqlString);
		if(list== null || list.size()==0){
			return null;
		}
		return list.get(0);
	}
	
	
	public boolean createRosterTemplateInfo(RosterTemplateInfo template) throws DataAccessException {
		logger.info("## RosterTemplateInfo create " + gson.toJson(template));
		try {
			getHibernateTemplate().save(template);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

	public boolean updateRosterTemplateInfo(RosterTemplateInfo template) throws DataAccessException {
		logger.info("## RosterTemplateInfo update " + gson.toJson(template));
		try {
			getHibernateTemplate().update(template);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

	public boolean deleteRosterTemplateInfo(RosterTemplateInfo template) throws DataAccessException {
		logger.info("## RosterTemplateInfo delete " + gson.toJson(template));
		try {
			getHibernateTemplate().delete(template);
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}


}