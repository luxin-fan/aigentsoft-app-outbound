package com.outbound.object.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.google.gson.Gson;
import com.outbound.object.ChangeBatchInfo;
import com.outbound.object.RosterBatchInfo;
import com.outbound.object.RosterBatchMetric;
import com.outbound.object.util.TimeUtil;

public class RosterBatchInfoDAO extends HibernateDaoSupport
{

	private Logger logger = Logger.getLogger(RosterBatchInfoDAO.class.getName());

	static Gson gson = new Gson();

	public List<RosterBatchInfo> getTRosterBatchInfos(String domain, String templateName, final int startPage,
			final int pageNum)
	{
		final String hqlString = "from RosterBatchInfo where domain='" + domain + "' and templateName='" + templateName
				+ "' and createTime like '%" + TimeUtil.getCurrentDateStr() + "%' " + " order by id asc";
		// logger.info("## getRosterBatchInfos [" + hqlString + "] startPage-" +
		// startPage + "|pageNum-" + pageNum);
		@SuppressWarnings("unchecked")
		List<RosterBatchInfo> list = getHibernateTemplate().executeFind(new HibernateCallback()
		{
			public Object doInHibernate(Session session) throws HibernateException, SQLException
			{
				Query query = session.createQuery(hqlString);
				query.setFirstResult(startPage * pageNum);
				query.setMaxResults(pageNum);
				List<RosterBatchInfo> list = query.list();
				return list;
			}
		});
		return list;
	}

	public List<RosterBatchInfo> getTRosterBatchInfos(String domain, String templateName, String batchName,
			String callRound, final int startPage, final int pageNum)
	{
		String hqlString = "from RosterBatchInfo where domain='" + domain + "'";
		if (StringUtils.isNotBlank(batchName))
		{
			hqlString += " and batchId = '" + batchName + "'";
		}
		if (StringUtils.isNotBlank(callRound))
		{
			hqlString += " and callRound = " + callRound;
		}
		hqlString += " and templateName='" + templateName + "'" + "and (createTime like '%"
				+ TimeUtil.getCurrentDateStr() + "%' or status <> 2)" + " order by id asc";
		final String finalHqlString = hqlString;
		// logger.info("## getRosterBatchInfos [" + hqlString + "] startPage-" +
		// startPage + "|pageNum-" + pageNum);
		@SuppressWarnings("unchecked")
		List<RosterBatchInfo> list = getHibernateTemplate().executeFind(new HibernateCallback()
		{
			public Object doInHibernate(Session session) throws HibernateException, SQLException
			{
				Query query = session.createQuery(finalHqlString);
				query.setFirstResult(startPage * pageNum);
				query.setMaxResults(pageNum);
				List<RosterBatchInfo> list = query.list();
				return list;
			}
		});
		return list;
	}

	public List<RosterBatchInfo> getUnCallRosterBatchInfos(String domain, String templateName, String batchName,
			final int startPage, final int pageNum)
	{
		String tstr = "";
		if (batchName != null)
		{
			tstr = "from RosterBatchInfo where domain='" + domain + "' and templateName='" + templateName
					+ "' and batchId like '%" + batchName + "%' and (status < 2 or createTime like '%"
					+ TimeUtil.getCurrentDateStr() + "%') " + " order by id asc";
		} else
		{
			tstr = "from RosterBatchInfo where domain='" + domain + "' and templateName='" + templateName
					+ "' and (status < 2 or createTime like '%" + TimeUtil.getCurrentDateStr() + "%') "
					+ " order by id asc";
		}
		final String hqlString = tstr;
		logger.info("## getRosterBatchInfos [" + hqlString + "] startPage-" + startPage + "|pageNum-" + pageNum);
		@SuppressWarnings("unchecked")
		List<RosterBatchInfo> list = getHibernateTemplate().executeFind(new HibernateCallback()
		{
			public Object doInHibernate(Session session) throws HibernateException, SQLException
			{
				Query query = session.createQuery(hqlString);
				query.setFirstResult(startPage * pageNum);
				query.setMaxResults(pageNum);
				List<RosterBatchInfo> list = query.list();
				return list;
			}
		});
		return list;
	}

	public List<RosterBatchInfo> getTodayRosterBatchInfos()
	{
		final String hqlString = "from RosterBatchInfo where createTime like '%" + TimeUtil.getCurrentDateStr() + "%' "
				+ " order by id asc";
		// logger.info("## getRosterBatchInfos [" + hqlString + "] startPage-" +
		// startPage + "|pageNum-" + pageNum);
		@SuppressWarnings("unchecked")
		List<RosterBatchInfo> list = getHibernateTemplate().find(hqlString);
		return list;
	}

	public List<RosterBatchInfo> getFinishRosterBatchInfos()
	{
		final String hqlString = "from RosterBatchInfo where status = 2";
		// logger.info("## getRosterBatchInfos [" + hqlString + "] startPage-" +
		// startPage + "|pageNum-" + pageNum);
		@SuppressWarnings("unchecked")
		List<RosterBatchInfo> list = getHibernateTemplate().find(hqlString);
		return list;
	}

	public boolean checkName(String name, String domain)
	{
		String hqlString = "select count(*) from RosterBatchInfo where batchId='" + name + "' and domain='" + domain
				+ "'";
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getRosterBatchInfos [" + hqlString + "] result " + count.intValue());
		if (count == 0)
			return true;
		return false;
	}

	public int getTRosterBatchInfoNum(String domain, String templateName)
	{
		String hqlString = "select count(*) from RosterBatchInfo where domain='" + domain + "' and templateName='"
				+ templateName + "' and createTime like '%" + TimeUtil.getCurrentDateStr() + "%'";
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		// logger.info("## getTRosterBatchInfoNum [" + domain + "] result " +
		// count.intValue());
		return count.intValue();
	}

	public int getTRosterBatchInfoNum(String domain, String templateName, String batchName, String callRound)
	{
		String hqlString = "select count(*) from RosterBatchInfo where domain='" + domain + "'" + " and batchId like '%"
				+ (StringUtils.isBlank(batchName) ? "" : "\\\\" + batchName) + "%'";
		if (StringUtils.isNotBlank(callRound))
		{
			hqlString += " and callRound = " + callRound;
		}
		hqlString += " and templateName='" + templateName + "' and createTime like '%" + TimeUtil.getCurrentDateStr()
				+ "%'";
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		// logger.info("## getTRosterBatchInfoNum [" + domain + "] result " +
		// count.intValue());
		return count.intValue();
	}

	public int getUncallRosterBatchInfoNum(String domain, String templateName)
	{
		String hqlString = "select count(*) from RosterBatchInfo where domain='" + domain + "' and templateName='"
				+ templateName + "' and createTime like '%" + TimeUtil.getCurrentDateStr() + "%'";
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		// logger.info("## getTRosterBatchInfoNum [" + domain + "] result " +
		// count.intValue());
		return count.intValue();
	}

	public int getTTrunkPoolNum(String domain, String poolname)
	{
		String hqlString = "select count(*) from RosterBatchInfo where domain='" + domain + "' " + "and trunkPoolName='"
				+ poolname + "'";
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		logger.info("## getTRosterBatchInfoNum [" + domain + "] result " + count.intValue());
		return count.intValue();
	}

	public RosterBatchInfo findById(String id)
	{
		return (RosterBatchInfo) getHibernateTemplate().get("com.outbound.object.RosterBatchInfo",
				Integer.parseInt(id));
	}

	public RosterBatchInfo findByName(String domain, String templateName)
	{
		final String hqlString = "from RosterBatchInfo where domain='" + domain + "' and templateName='" + templateName
				+ "'";
		logger.info("## getRosterBatchInfos [" + hqlString + "] ");
		@SuppressWarnings("unchecked")
		List<RosterBatchInfo> list = getHibernateTemplate().find(hqlString);
		if (list == null || list.size() == 0)
		{
			return null;
		}
		return list.get(0);
	}

	public RosterBatchInfo findByBatchName(String domain, String batchId)
	{
		final String hqlString = "from RosterBatchInfo where domain='" + domain + "' and batchId='" + batchId + "'";
		logger.info("## getRosterBatchInfos [" + hqlString + "] ");
		@SuppressWarnings("unchecked")
		List<RosterBatchInfo> list = getHibernateTemplate().find(hqlString);
		if (list == null || list.size() == 0)
		{
			return null;
		}
		return list.get(0);
	}

	public RosterBatchInfo findByBatchNameAndRound(String domain, String activityName, String templateName, String batchId, int callRound)
	{
		final String hqlString = "from RosterBatchInfo where domain='" + domain + "' and batchId='" + batchId + 
								 "' and activityName='" + activityName + "' and templateName='" + templateName +
								 "' and callRound=" + callRound;
		logger.info("## getRosterBatchInfos [" + hqlString + "] ");
		@SuppressWarnings("unchecked")
		List<RosterBatchInfo> list = getHibernateTemplate().find(hqlString);
		if (list == null || list.size() == 0)
		{
			return null;
		}
		return list.get(0);
	}
	public RosterBatchInfo findNextUncallBatch(String domain, String templateName)
	{
		final String hqlString = "from RosterBatchInfo where domain='" + domain + "' and templateName='" + templateName
				+ "'" + " and status=0 order by id asc";
		@SuppressWarnings("unchecked")
		List<RosterBatchInfo> list = getHibernateTemplate().find(hqlString);
		if (list == null || list.size() == 0)
		{
			return null;
		}
		logger.info("## getRosterBatchInfos [" + hqlString + "] ");
		return list.get(0);
	}

	public List<RosterBatchInfo> findUncallBatch(String domain, String templateName)
	{
		final String hqlString = "from RosterBatchInfo where domain='" + domain + "' and templateName='" + templateName
				+ "'" + " and status=0 order by id asc";
		@SuppressWarnings("unchecked")
		List<RosterBatchInfo> list = getHibernateTemplate().find(hqlString);
		if (list == null || list.size() == 0)
		{
			return null;
		}
		logger.info("## getRosterBatchInfos [" + hqlString + "] ");
		return list;
	}

	public RosterBatchInfo findinCallBatch(String domain, String templateName)
	{
		final String hqlString = "from RosterBatchInfo where domain='" + domain + "' and templateName='" + templateName
				+ "' and status=1 order by id desc";
		// logger.info("## getRosterBatchInfos [" + hqlString + "] ");
		@SuppressWarnings("unchecked")
		List<RosterBatchInfo> list = getHibernateTemplate().find(hqlString);
		if (list == null || list.size() == 0)
		{
			return null;
		}
		return list.get(0);
	}

	public List<RosterBatchInfo> findCompleteBatch(String domain, String templateName)
	{
		final String hqlString = "from RosterBatchInfo where domain='" + domain + "' and templateName='" + templateName
				+ "'" + "' and status=3 order by id asc";
		logger.info("## getRosterBatchInfos [" + hqlString + "] ");
		@SuppressWarnings("unchecked")
		List<RosterBatchInfo> list = getHibernateTemplate().find(hqlString);
		if (list == null || list.size() == 0)
		{
			return null;
		}
		return list;
	}

	public synchronized boolean createRosterBatchInfo(RosterBatchInfo template) throws DataAccessException
	{
		logger.info("## RosterBatchInfo create " + gson.toJson(template));
		try
		{
			// template.setId(template.getBatchId());
			getHibernateTemplate().save(template);
		} catch (Exception e)
		{
			logger.error(e);
			// template.setId(Integer.parseInt(template.getBatchId()));
			// getHibernateTemplate().save(template);
			return false;
		}
		return true;
	}

	public boolean updateRosterBatchInfo(RosterBatchInfo template) throws DataAccessException
	{
		logger.info("## RosterBatchInfo update " + gson.toJson(template));
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

	public int updateRosterBatchIdo(ChangeBatchInfo template) throws DataAccessException
	{
		String hqlString = "update RosterBatchInfo set id = " + template.getNewId() + "where id=" + template.getOldId();
		int count = (int) getHibernateTemplate().bulkUpdate(hqlString);
		// logger.info("## getTRosterBatchInfoNum [" + domain + "] result " +
		// count.intValue());
		return count;
	}

	public boolean deleteRosterBatchInfo(RosterBatchInfo template) throws DataAccessException
	{
		logger.info("## RosterBatchInfo delete " + gson.toJson(template));
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

	/**
	 * 根据rosterinfo的名字删除rosterBatchInfo信息
	 * @return
	 */
	public boolean deleteByRosterId(String batchId, String domain) {
		
		String hql = "delete from RosterBatchInfo where batchId='" + batchId + "' and domain='" + domain + "'";
		int count = getHibernateTemplate().bulkUpdate(hql);
		if(count != 0) {
			return true;
		}
		return false;	
	}
	
	public boolean clearTemplate(String id, String domain)
	{
		String hqlString = "delete from RosterBatchInfo where templateName='" + id + "' and domain='" + domain + "'";
		int count = getHibernateTemplate().bulkUpdate(hqlString);
		logger.info("## clear DNCNumbers [" + hqlString + "] result " + count);
		if (count > 0)
			return true;
		return false;
	}

	public List<RosterBatchInfo> getTRosterBatchInfos2(String condition, final int startPage, final int pageNum)
	{
		final String hqlString = "from RosterBatchInfo where " + condition + " order by id asc";
		logger.info("## getRosterBatchInfos [" + hqlString + "] startPage-" + startPage + "|pageNum-" + pageNum);
		@SuppressWarnings("unchecked")
		List<RosterBatchInfo> list = getHibernateTemplate().executeFind(new HibernateCallback()
		{
			public Object doInHibernate(Session session) throws HibernateException, SQLException
			{
				Query query = session.createQuery(hqlString);
				query.setFirstResult(startPage * pageNum);
				query.setMaxResults(pageNum);
				List<RosterBatchInfo> list = query.list();
				return list;
			}
		});
		return list;
	}

	public List<RosterBatchInfo> getTRosterBatchInfos2(String condition)
	{
		final String hqlString = "from RosterBatchInfo where " + condition + " order by id asc";
		logger.info("## getRosterBatchInfos [" + hqlString + "] ");
		@SuppressWarnings("unchecked")
		List<RosterBatchInfo> list = getHibernateTemplate().find(hqlString);
		return list;
	}

	public int getTRosterBatchInfoNum2(String condition)
	{
		String hqlString = "select count(*) from RosterBatchInfo where " + condition;
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		// logger.info("## getTRosterBatchInfoNum [" + domain + "] result " +
		// count.intValue());
		return count.intValue();
	}

	public int getTotalRosterBatchInfoNum(String templateName, String domain)
	{
		String hqlString = "select count(*) from RosterBatchInfo where templateName='" + templateName + "' and domain='"
				+ domain + "' and callRound = 1";
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		return count.intValue();
	}

	public RosterBatchMetric getRosterBatchMetric(String templateName, String domain)
	{
		try
		{
			long startTime = System.currentTimeMillis();
			String dateStr = TimeUtil.getCurrentDateStr();
			String sql1 = "(select count(*) as Counts from t_roster_batch_info where templateName='" + templateName
					+ "' and domain='" + domain + "' and callRound = 1) a, ";
			String sql2 = "(select count(*) as Counts from t_roster_batch_info where templateName='" + templateName
					+ "' and domain='" + domain + "' and callRound=1 and status=2) b, ";
			String sql3 = "(select count(*) as Counts from t_roster_batch_info where templateName='" + templateName
					+ "' and domain='" + domain + "' and callRound = 1 and status=2 and createTime like '%" + dateStr
					+ "%') c ";
			final String sql = "select a.Counts as m1,b.Counts as m2 ,c.Counts as m3 from " + sql1 + sql2 + sql3;
			logger.info("## [" + sql + "] ");

			Session session = getHibernateTemplate().getSessionFactory().openSession();
			SQLQuery sqlQuery = session.createSQLQuery(sql);
			@SuppressWarnings("unchecked")
			List<Object> list = (List<Object>) sqlQuery.list();
			// List<AppMetric> list = this.getHibernateTemplate().find (sql);
			session.close();
			logger.info("## all RosterInfo get size [" + list.size() + "]");
			if (list != null && list.size() > 0)
			{
				Object[] map = (Object[]) list.get(0);
				RosterBatchMetric aMetric = new RosterBatchMetric();
				aMetric.setTotalRosterBatchNum(Integer.parseInt(map[0].toString()));
				aMetric.setFinishedRosterBatchNum(Integer.parseInt(map[1].toString()));
				aMetric.setTodayRosterBatchNum(Integer.parseInt(map[2].toString()));

				long costTime = System.currentTimeMillis() - startTime;
				logger.info("## get batch metric cost [" + costTime + "] ");
				return aMetric;
			}

		} catch (Exception e)
		{
			logger.error(e);
		}
		return null;
	}

	public int getTodayRosterBatchInfoNum(String templateName, String domain)
	{
		String dateStr = TimeUtil.getCurrentDateStr();
		String hqlString = "select count(*) from RosterBatchInfo where templateName='" + templateName + "' and domain='"
				+ domain + "' and callRound = 1 and status=2 and createTime like '%" + dateStr + "%'";
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		return count.intValue();
	}

	public int getFinishedRosterBatchInfoNum(String templateName, String domain)
	{
		String hqlString = "select count(*) from RosterBatchInfo where templateName='" + templateName + "' and domain='"
				+ domain + "' and callRound=1 and status=2";
		Long count = (Long) getHibernateTemplate().find(hqlString).listIterator().next();
		return count.intValue();
	}

}