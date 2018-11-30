package com.outbound.job;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.outbound.dialer.util.POIUtil;
import com.outbound.object.AutoImportConfigModel;
import com.outbound.object.DNCNumber;
import com.outbound.object.DNCTemplate;
import com.outbound.object.FilterConditionModel;
import com.outbound.object.dao.DNCNumberDAO;
import com.outbound.object.dao.DNCTemplateDAO;
import com.outbound.object.util.ApplicationContextUtil;
import com.outbound.object.util.FileUtil;
import com.outbound.object.util.GsonFactory;

public class DNCImportJob implements Job {

	private Logger logger = Logger.getLogger(DNCImportJob.class.getName());

	private DNCTemplateDAO dncDao = (DNCTemplateDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("DNCTemplateDAO");
	private DNCNumberDAO dncNumDao = (DNCNumberDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("DNCNumberDAO");

	private DNCTemplate dncTemplate;
	private AutoImportConfigModel config;

	// +"——"+context.getJobDetail().getName()
	// config = GsonFactory.getGson().fromJson(dncTemplate.getImportPath(),
	// ImportConfigModel.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		long currentTiem = System.currentTimeMillis();
		logger.info("#@## dnc [" + context.getJobDetail().getName() + "] import task start at "
				+ new Timestamp(currentTiem).toString());
		String jobname = context.getJobDetail().getName();
		jobname = jobname.replace("_dnc_job", "");
		dncTemplate = dncDao.findById(jobname);
		if (dncTemplate != null) {
			config = GsonFactory.getGson().fromJson(dncTemplate.getImportPath(),
					AutoImportConfigModel.class);
			// file
			if (config.getSource().equals("file")) {
				/*
				String[] files = FileUtil.getConfFiles(config.getContext().getPath());
				if (files.length > 0) {
					for (String fpath : files) {
						getDnsFile(config.getContext().getPath() + "/" + fpath);
					}
				}
				*/
				getDnsFile(config.getContext().getPath());
				FileUtil.deleteFile(config.getContext().getPath());
			}
		}
	}

	private void getDnsFile(String path) {
		try {
			FilterConditionModel filterConditionModel = GsonFactory.getGson().fromJson(dncTemplate.getFilterCondition(),
					FilterConditionModel.class);
			if (filterConditionModel.getClear() == 1) {
				dncNumDao.clear(dncTemplate.getId() + "", dncTemplate.getDomain());
			}
			List<String[]> params = POIUtil.readExcel(path);
			if (params != null && params.size() > 0) {
				for (String[] paramLine : params) {
					DNCNumber dncNum = new DNCNumber();
					dncNum.setPhoneNum(paramLine[0]);
					dncNum.setDncTemplateId("" + dncTemplate.getId());
					dncNum.setDomain(dncTemplate.getDomain());
					if (filterConditionModel.getRemoval() == 1) {
						int count = dncNumDao.getTDNCNumberNumQuery(dncNum.getDomain(), "" + dncNum.getDncTemplateId(),
								dncNum.getPhoneNum());
						if (count > 0) {
							continue;
						}
					}
					dncNumDao.createDNCNumber(dncNum);
				}
			}
		} catch (IOException e) {
			logger.error(e);
		}
	}
}
