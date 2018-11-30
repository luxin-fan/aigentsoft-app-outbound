package com.outbound.resources;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.outbound.common.BaseResource;
import com.outbound.common.Util;
import com.outbound.dialer.util.DBENUM;
import com.outbound.dialer.util.DBUtil;
import com.outbound.dialer.util.FTPOperatUtil;
import com.outbound.dialer.util.ImportENUM;
import com.outbound.object.AutoImportConfigModel;
import com.outbound.object.ConfigParam;
import com.outbound.object.ImportConfigModel;
import com.outbound.object.dao.ConfigParamDAO;
import com.outbound.object.dao.OutboundPolicyInfoDAO;
import com.outbound.object.util.ApplicationContextUtil;
import com.outbound.object.util.ResponseUtil;
import com.outbound.request.ConfigParamRequest;

@Path("/configParam")
public class ConfigParamResource extends BaseResource {

	private ConfigParamDAO configParamDao = (ConfigParamDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("ConfigParamDAO");
	
	private OutboundPolicyInfoDAO outboundPolicyInfoDao = (OutboundPolicyInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("OutboundPolicyInfoDAO");
	
	static Gson gson = new Gson();

	@POST
	@Path("list")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getConfigParmas(ConfigParamRequest request) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			int startPage = request.getStartPage();
			if(startPage > 0){
				startPage--;
			}
			List<ConfigParam> lists = configParamDao.getTConfigParams(request.getDomain(), request.getParamType(),
					startPage, request.getPageNum());
			int count = configParamDao.getTConfigParamNum(request.getDomain(), request.getParamType());
			responseUtil = setResponseUtil(1, "getConfigParmas Suc",
					super.getMergeSumAndList(lists == null ? new ArrayList<>() : lists, count));
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
		}
		return gson.toJson(responseUtil);
	}

	@POST
	@Path("add")
	@Produces(MediaType.APPLICATION_JSON)
	public ResponseUtil addConfigParam(ConfigParam configParam) {
		ResponseUtil responseUtil = new ResponseUtil();

		try {
			if (configParam.getName() == null)
				throw new IllegalArgumentException("请输入名称");
			configParamDao.createConfigParam(configParam);
			responseUtil = setResponseUtil(1, "addConfigParam Suc", null);

		} catch (Exception e) {
			Util.error(this, "addConfigParam fail!", e);
			responseUtil = setResponseUtil(0, e.getMessage(), null);
		}
		return responseUtil;

	}

	@POST
	@Path("delete")
	@Produces(MediaType.APPLICATION_JSON)
	public ResponseUtil deleteConfigParam(ConfigParam configParam) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			String step = configParam.getParamType()+"|"+ configParam.getName();
			int count = outboundPolicyInfoDao.findByAnswerStep(configParam.getDomain(), step);
			if(count > 0){
				responseUtil = setResponseUtil(0, "该配置被策略占用", null);
				return responseUtil;
			}
			configParamDao.deleteConfigParam(configParam);
			responseUtil = setResponseUtil(1, "deleteConfigParam Suc", null);
		} catch (Exception e) {
			Util.error(this, "deleteConfigParam fail!", e);
			responseUtil = setResponseUtil(0, e.getMessage(), null);
		}

		return responseUtil;

	}

	@POST
	@Path("update")
	@Produces(MediaType.APPLICATION_JSON)
	public ResponseUtil updateConfigParam(ConfigParam configParam) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			configParamDao.updateConfigParam(configParam);
			responseUtil = setResponseUtil(1, "updateConfigParam Suc", null);
		} catch (Exception e) {
			Util.error(this, "updateConfigParam fail!", e);
			responseUtil = setResponseUtil(0, e.getMessage(), null);
		}
		return responseUtil;
	}

	@POST
	@Path("check")
	@Produces(MediaType.APPLICATION_JSON)
	public ResponseUtil checkConfigParamName(ConfigParam configParam) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			boolean ret = configParamDao.checkConfigParamName(configParam.getName(), configParam.getDomain());
			responseUtil = setResponseUtil(1, ret == true ? "true" : "false", null);
		} catch (Exception e) {
			Util.error(this, "checkConfigParamName fail!", e);
			responseUtil = setResponseUtil(0, e.getMessage(), null);
		}
		return responseUtil;
	}
	
	@POST
	@Path("connectTest")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResponseUtil connectTest(AutoImportConfigModel autoImportConfigModel){
		ResponseUtil responseUtil = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		FTPOperatUtil ftpUtil = null;
		int result = 0;
		try {
			if (autoImportConfigModel.getContext() == null){
				throw new IllegalArgumentException("服务器内容信息不能为空");
			}
			ImportENUM importEnum = ImportENUM.getImportEnum(autoImportConfigModel.getSource());
			if (importEnum == null){
				throw new IllegalArgumentException("导入类型有误");
			}
			
			ImportConfigModel content = autoImportConfigModel.getContext();
			if (importEnum == ImportENUM.FTP){
				ftpUtil = new FTPOperatUtil();
				result = ftpUtil.connectFTPServer(content.getServer(), content.getP(), content.getUname(), content.getPwd()) ? 1 : 0;
				
			} else if (importEnum == ImportENUM.DB){
				DBENUM dbEnum = DBENUM.getInstance(content.getType());
				String url = MessageFormat.format(dbEnum.getUrl(), content.getServer(), content.getP()+"", content.getName());
				connection = DBUtil.getConnection(dbEnum.getDriver(), url, content.getUname(), content.getPwd());
				String sql = "";
				if (dbEnum == DBENUM.ORACLE){
					sql = "select 1 as result from dual";
				} else {
					sql = "SELECT count(1) as result";
				}
				
				if(connection != null){
					pstmt = connection.prepareStatement(sql);
					resultSet = pstmt.executeQuery();
					if (resultSet.next()){
						result = resultSet.getInt("result") > 0 ? 1 : result;
					}
				}
			}
			if (result > 0)
				responseUtil = super.setResponseUtil(1, "连接成功", null);
			else 
				responseUtil = super.setResponseUtil(0, "连接失败", null);
		} catch (IllegalArgumentException e){
			responseUtil = super.setResponseUtil(0, e.getMessage(), null);
		} catch (Exception e){
			Util.error(this, "ConfigParamResource.connectTest", e);
			responseUtil = super.setResponseUtil(0,"连接失败", null);
		} finally {
			DBUtil.closeAll(resultSet, pstmt, connection);
			if (ftpUtil != null) {
				ftpUtil.closeFTPClient();
			}
		}
		return responseUtil;
	}

}
