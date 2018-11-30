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
import com.outbound.object.ChangeBatchInfo;
import com.outbound.object.RosterBatch;
import com.outbound.object.util.TimeUtil;


public class RosterBatchDAO extends HibernateDaoSupport
{
	private Logger logger = Logger.getLogger(RosterBatchDAO.class.getName());

	static Gson gson = new Gson();
	
	
	public synchronized boolean createRosterBatch(RosterBatch template) throws DataAccessException
	{
		logger.info("## RosterBatch create " + gson.toJson(template));
		try
		{
			getHibernateTemplate().save(template);
		} 
		catch (Exception e)
		{
			logger.error(e);
			return false;
		}
		return true;
	}

	public boolean updateRosterBatch(RosterBatch template) throws DataAccessException
	{
		logger.info("## RosterBatch update " + gson.toJson(template));
		try
		{
			getHibernateTemplate().update(template);
		} catch (Exception e)
		{
			logger.error(e);
			return false;
		}
		return true;
	}

	public RosterBatch findByBatchName(String domain, String batchId)
	{
		final String hqlString = "from RosterBatch where domain='" + domain + "' and batchId='" + batchId + "'";
		logger.info("## getRosterBatchs [" + hqlString + "] ");
		@SuppressWarnings("unchecked")
		List<RosterBatch> list = getHibernateTemplate().find(hqlString);
		if (list == null || list.size() == 0)
		{
			return null;
		}
		return list.get(0);
	}

	public RosterBatch findNextUncallBatch(String domain, String templateName)
	{
		final String hqlString = "from RosterBatch where domain='" + domain + "' and templateName='" + templateName
				+ "'" + " and status=0 order by id asc";
		@SuppressWarnings("unchecked")
		List<RosterBatch> list = getHibernateTemplate().find(hqlString);
		if (list == null || list.size() == 0)
		{
			return null;
		}
		logger.info("## getRosterBatchs [" + hqlString + "] ");
		return list.get(0);
	}

	public List<RosterBatch> findUncallBatch(String domain, String templateName)
	{
		final String hqlString = "from RosterBatch where domain='" + domain + "' and templateName='" + templateName
				+ "'" + " and status=0 order by id asc";
		@SuppressWarnings("unchecked")
		List<RosterBatch> list = getHibernateTemplate().find(hqlString);
		if (list == null || list.size() == 0)
		{
			return null;
		}
		logger.info("## getRosterBatchs [" + hqlString + "] ");
		return list;
	}
	
	/**
	 * 查找计划类型的批次，status小于3的批次 status为完成
	 * @param domain
	 * @param templateName
	 * @return
	 * @author zzj
	 */
	public List<RosterBatch> findPlancallBatch(String domain, String templateName)
	{
		final String hqlString = "from RosterBatch where domain='" + domain + "' and templateName='" + templateName
				+ "'" + " and status<3 order by id asc";
		@SuppressWarnings("unchecked")
		List<RosterBatch> list = getHibernateTemplate().find(hqlString);
		if (list == null || list.size() == 0)
		{
			return null;
		}
		logger.info("## getRosterBatchs [" + hqlString + "] ");
		return list;
	}

	public RosterBatch findinCallBatch(String domain, String templateName)
	{
		final String hqlString = "from RosterBatch where domain='" + domain + "' and templateName='" + templateName
				+ "' and status=1 order by id desc";
		
		@SuppressWarnings("unchecked")
		List<RosterBatch> list = getHibernateTemplate().find(hqlString);
		if (list == null || list.size() == 0)
		{
			return null;
		}
		return list.get(0);
	}

	public List<RosterBatch> findCompleteBatch(String domain, String templateName)
	{
		final String hqlString = "from RosterBatch where domain='" + domain + "' and templateName='" + templateName
				+ "'" + "' and status=3 order by id asc";
		logger.info("## getRosterBatchs [" + hqlString + "] ");
		@SuppressWarnings("unchecked")
		List<RosterBatch> list = getHibernateTemplate().find(hqlString);
		if (list == null || list.size() == 0)
		{
			return null;
		}
		return list;
	}

	public boolean clearTemplate(String id, String domain)
	{
		String hqlString = "delete from RosterBatch where templateName='" + id + "' and domain='" + domain + "'";
		int count = getHibernateTemplate().bulkUpdate(hqlString);
		logger.info("## clear templates [" + hqlString + "] result " + count);
		if (count > 0)
		{
			return true;
		}
		
		return false;
	}
	
	
	public List<RosterBatch> getTRosterBatchInfos(String domain, String templateName, final int startPage,
			final int pageNum)
	{
		final String hqlString = "from RosterBatch where domain='" + domain + "' and templateName='" + templateName
				+ "' and createTime like '%" + TimeUtil.getCurrentDateStr() + "%' " + " order by id asc";
		// logger.info("## getRosterBatchInfos [" + hqlString + "] startPage-" +
		// startPage + "|pageNum-" + pageNum);
		@SuppressWarnings("unchecked")
		List<RosterBatch> list = getHibernateTemplate().executeFind(new HibernateCallback()
		{
			public Object doInHibernate(Session session) throws HibernateException, SQLException
			{
				Query query = session.createQuery(hqlString);
				query.setFirstResult(startPage * pageNum);
				query.setMaxResults(pageNum);
				List<RosterBatch> list = query.list();
				return list;
			}
		});
		return list;
	}

	
	public List<RosterBatch> getUnCallRosterBatchInfos(String domain, String templateName, String batchName,
			final int startPage, final int pageNum)
	{
		String tstr = "";
		if (batchName != null)
		{
			tstr = "from RosterBatch where domain='" + domain + "' and templateName='" + templateName
					+ "' and batchId like '%" + batchName + "%' and (status < 3 or createTime like '%"
					+ TimeUtil.getCurrentDateStr() + "%') " + " order by id asc";
		} else
		{
			tstr = "from RosterBatch where domain='" + domain + "' and templateName='" + templateName
					+ "' and (status < 3 or createTime like '%" + TimeUtil.getCurrentDateStr() + "%') "
					+ " order by id asc";
		}
		final String hqlString = tstr;
		logger.info("## getRosterBatchInfos [" + hqlString + "] startPage-" + startPage + "|pageNum-" + pageNum);
		@SuppressWarnings("unchecked")
		List<RosterBatch> list = getHibernateTemplate().executeFind(new HibernateCallback()
		{
			public Object doInHibernate(Session session) throws HibernateException, SQLException
			{
				Query query = session.createQuery(hqlString);
				query.setFirstResult(startPage * pageNum);
				query.setMaxResults(pageNum);
				List<RosterBatch> list = query.list();
				return list;
			}
		});
		return list;
	}
	
	public int getUncallRosterBatchInfoNum(String domain, String templateName)
	{
		String hqlString = "select count(*) from RosterBatch where domain='" + domain + "' and templateName='"
				+ templateName + "' and createTime like '%" + TimeUtil.getCurrentDateStr() + "%'";
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();

		return count.intValue();
	}
	
	public boolean checkName(String name, String domain) throws QueryException
	{
		String hqlString = "select count(*) from RosterBatch where batchId='" + name + "' and domain='" + domain
				+ "'";
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getRosterBatchInfos [" + hqlString + "] result " + count.intValue());
		if (count == 0)
			return true;
		return false;
	}

	public int updateRosterBatchIdo(ChangeBatchInfo template) throws DataAccessException
	{
		String hqlString = "update RosterBatch set id = " + template.getNewId() + "where id=" + template.getOldId();
		int count = (int) getHibernateTemplate().bulkUpdate(hqlString);

		return count;
	}
	
	
	public boolean deleteRosterBatch(RosterBatch template) throws DataAccessException
	{
		logger.info("## RosterBatch delete " + gson.toJson(template));
		try
		{
			getHibernateTemplate().delete(template);
		} catch (Exception e)
		{
			logger.error(e);
			return false;
		}
		return true;
	}
}
