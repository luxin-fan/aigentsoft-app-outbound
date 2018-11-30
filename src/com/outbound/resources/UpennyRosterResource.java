package com.outbound.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.OSSObject;
import com.google.gson.Gson;
import com.outbound.common.Util;
import com.outbound.impl.metric.MetricUtil;
import com.outbound.impl.util.CallUtil;
import com.outbound.impl.util.MakeCall;
import com.outbound.impl.util.ReqResponse;
import com.outbound.object.ActivityInfo;
import com.outbound.object.ConfigParam;
import com.outbound.object.OutboundPolicyInfo;
import com.outbound.object.RosterBatchInfo;
import com.outbound.object.RosterInfo;
import com.outbound.object.TrunkNumber;
import com.outbound.object.TrunkPoolCorr;
import com.outbound.object.dao.ActivityInfoDAO;
import com.outbound.object.dao.ConfigParamDAO;
import com.outbound.object.dao.OutboundPolicyInfoDAO;
import com.outbound.object.dao.RosterBatchInfoDAO;
import com.outbound.object.dao.RosterInfoDAO;
import com.outbound.object.dao.TrunkNumberDAO;
import com.outbound.object.dao.TrunkPoolCorrDAO;
import com.outbound.object.util.ApplicationContextUtil;
import com.outbound.object.util.TimeUtil;
import com.outbound.upenny.BaseUpennyRosterRequest;
import com.outbound.upenny.UpennyRosterBatchListRequest;
import com.outbound.upenny.UpennyRosterBatchRequest;
import com.outbound.upenny.UpennyRosterRequest;
import com.outbound.upenny.UpennyRosterResponse;
import com.outbound.upenny.UserInfoModel;

/**
 * 针对仟才项目定制的名单接口实现类 1、名单传入接口格式解析 2、oss名单文件下载功能
 * 
 * @author bruce
 *
 */
@Path("/upennyRoster")
public class UpennyRosterResource
{

	// endpoint以杭州为例，其它region请按实际情况填写
	private String endpoint = "http://vpc100-oss-cn-shanghai.aliyuncs.com";
	// private String endpoint = "http://oss-cn-shanghai.aliyuncs.com";
	// 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录
	// https://ram.console.aliyun.com 创建
	private String accessKeyId = "LTAI2oXtShBlnqcE";
	private String accessKeySecret = "c4k59xtOu0NzbDwX55wSniURpdFQem";
	private String bucketName = "sh-voice-file";
	// private String prefix_key = "File/";

	static Gson gson = new Gson();

	private RosterInfoDAO rosterDao = (RosterInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("RosterInfoDAO");

	private ActivityInfoDAO activityDao = (ActivityInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("ActivityInfoDAO");

	private OutboundPolicyInfoDAO outboundPolicyInfoDao = (OutboundPolicyInfoDAO) ApplicationContextUtil
			.getApplicationContext().getBean("OutboundPolicyInfoDAO");

	private RosterBatchInfoDAO rosterBatchDao = (RosterBatchInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("RosterBatchInfoDAO");

	private ConfigParamDAO configDao = (ConfigParamDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("ConfigParamDAO");

	private TrunkNumberDAO trunkNumberDao = (TrunkNumberDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("TrunkNumberDAO");

	private TrunkPoolCorrDAO trunkPoolCorrDao = (TrunkPoolCorrDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("TrunkPoolCorrDAO");

	/**
	 * 当前类公共返回结果信息
	 * 
	 * @param resCount
	 * @return
	 */
	public UpennyRosterResponse getCommonResponse(int resCount)
	{
		UpennyRosterResponse response = null;
		if (resCount == -1)
			response = new UpennyRosterResponse(0, "not found rostername");
		else if (resCount == -2)
			response = new UpennyRosterResponse(0, "expire time format wrong");
		else if (resCount > 0)
		{
			response = new UpennyRosterResponse(1, "success [" + resCount + "]");
		} else
			response = new UpennyRosterResponse(0, "fail");

		return response;
	}

	private TrunkNumber getTrunk(String domain, String trunkGrp)
	{
		List<TrunkPoolCorr> corr = trunkPoolCorrDao.getTTrunkPoolCorrs(domain, trunkGrp);
		if (corr != null && corr.size() > 0)
		{
			for (TrunkPoolCorr poolcorr : corr)
			{
				TrunkNumber trunk = trunkNumberDao.findbyNum(domain, poolcorr.getDisplayNum());
				return trunk;
			}
		}
		return null;
	}

	private String getDstNum(String domain, String answerStep)
	{
		String dstName = null;
		String[] steps = answerStep.split("[|]");
		if (steps.length >= 3)
		{
			dstName = steps[2];
			ConfigParam pvalue = configDao.findByName(domain, dstName);
			if (pvalue != null)
			{
				String dst = pvalue.getValue().trim();
				return dst;
			}
		}
		return null;
	}

	/**
	 * 单个呼叫任务
	 * 
	 * @param upennyRosterRequest
	 */
	@POST
	@Path("/job/single")
	@Produces(
	{ MediaType.APPLICATION_JSON })
	public String addSingleTask(UpennyRosterRequest upennyRosterRequest)
	{

		Util.info(this, "receive request " + gson.toJson(upennyRosterRequest));
		UpennyRosterResponse response = null;
		try
		{
			if (StringUtils.isBlank(upennyRosterRequest.getJobId()))
				throw new IllegalArgumentException("呼叫ID有误");
			if (StringUtils.isBlank(upennyRosterRequest.getTemplateCode()))
				throw new IllegalArgumentException("流程模板编号不能为空");
			if (upennyRosterRequest.getPhone() == null || upennyRosterRequest.getPhone() <= 0)
				throw new IllegalArgumentException("手机号码有误");
			if (upennyRosterRequest.getJobData() == null)
				throw new IllegalArgumentException("客户信息有误");
			if (StringUtils.isBlank(upennyRosterRequest.getCallbackUrl()))
				throw new IllegalArgumentException("请输入回调地址");

			ActivityInfo aInfo = activityDao.findActivityInfoByTemplate(upennyRosterRequest.getTemplateCode());
			if (aInfo != null)
			{
				Util.info(this, " find activity " + aInfo.getName());
				OutboundPolicyInfo info = outboundPolicyInfoDao.getTOutboundPolicyInfos(aInfo.getDomain(),
						aInfo.getPolicyName());
				if (info != null)
				{
					Util.info(this, " find policy info " + info.getName());
					TrunkNumber number = getTrunk(aInfo.getDomain(), aInfo.getTrunkGrp());
					if (number != null)
					{
						Util.info(this, " find number info " + number.getTrunkGrp());
						String dstNum = getDstNum(aInfo.getDomain(), info.getCallAnswerStep());
						if (dstNum != null)
						{
							Util.info(this, " find dst number info " + dstNum);
							MakeCall mkcall = new MakeCall();
							mkcall.setAni(upennyRosterRequest.getPhone() + "");
							mkcall.setActivity_name(aInfo.getName());
							mkcall.setCaller(upennyRosterRequest.getPhone() + "");
							mkcall.setCallee(dstNum);
							mkcall.setDomain(aInfo.getDomain());
							mkcall.setTrunkGrp(number.getTrunkGrp());
							mkcall.setRosterinfo_id("" + info.getId());
							mkcall.setBatch_name(upennyRosterRequest.getJobId());
							// firstname,sex,loanChannel,loanRepayAmount,,loanExpireDate,loanOverdueDay
							String uui = "name_gender_loanName_loanAmount_dueDate_days";
							if (upennyRosterRequest.getJobData().getLoanUsername() != null)
							{
								uui = uui.replace("name", upennyRosterRequest.getJobData().getLoanUsername());
							} else
							{
								uui = uui.replace("name", "");
							}

							if (upennyRosterRequest.getJobData().getLoanUserGender() != null)
							{
								if ("男".equals(upennyRosterRequest.getJobData().getLoanUserGender()))
									uui = uui.replace("gender", "0");
								else
									uui = uui.replace("gender", "1");
							} else
							{
								uui = uui.replace("gender", "");
							}

							if (upennyRosterRequest.getJobData().getLoanChannel() != null)
							{
								uui = uui.replace("loanName", upennyRosterRequest.getJobData().getLoanChannel());
							} else
							{
								uui = uui.replace("loanName", "");
							}

							if (upennyRosterRequest.getJobData().getLoanRepayAmount() != null)
							{
								uui = uui.replace("loanAmount", upennyRosterRequest.getJobData().getLoanRepayAmount());
							} else
							{
								uui = uui.replace("loanAmount", "");
							}

							if (upennyRosterRequest.getJobData().getLoanExpireDate() != null)
							{
								uui = uui.replace("dueDate", upennyRosterRequest.getJobData().getLoanExpireDate());
							} else
							{
								uui = uui.replace("dueDate", "");
							}

							if (upennyRosterRequest.getJobData().getLoanOverdueDay() != null)
							{
								uui = uui.replace("days", upennyRosterRequest.getJobData().getLoanOverdueDay());
							} else
							{
								uui = uui.replace("days", "0");
							}

							mkcall.setUui(uui);

							ReqResponse result = CallUtil.makeCall(mkcall);
							if (result == null)
							{
								response = new UpennyRosterResponse(0, "make call interface error !");
								return gson.toJson(response);
							} else
							{
								response = new UpennyRosterResponse(1, "make call success");
								return gson.toJson(response);
							}
						} else
						{
							response = new UpennyRosterResponse(0, "param activity dstNum null error !");
							return gson.toJson(response);
						}
						// MetricUtil.uppenyJobMap.put(upennyRosterRequest.getJobId(),
						// upennyRosterRequest);

					} else
					{
						response = new UpennyRosterResponse(0, "param activity trunk null error !");
						return gson.toJson(response);
					}
				} else
				{
					response = new UpennyRosterResponse(0, "param activity policy null error !");
					return gson.toJson(response);
				}
			} else
			{
				response = new UpennyRosterResponse(0, "param template code error !");
				return gson.toJson(response);
			}
		} catch (IllegalArgumentException e)
		{
			Util.warn(this.getClass().getName(),
					"UpennyRosterResource.addSingleTask execute warning!" + e.getMessage());
			response = new UpennyRosterResponse(0, e.getMessage());
		} catch (Exception e)
		{
			response = new UpennyRosterResponse(0, "fail");
			Util.error(this, "UpennyRosterResource.addSingleTask execute Fail!", e);
		}
		return gson.toJson(response);
	}

	/**
	 * 批量呼叫任务 从OSS获取信息
	 * 
	 * @param upennyRosterRequest
	 */
	@POST
	@Path("/job/batch")
	@Produces(
	{ MediaType.APPLICATION_JSON })
	public String addSingleTaskList(UpennyRosterBatchRequest upennyRosterBatchRequest)
	{
		Util.info(this, "receive request " + gson.toJson(upennyRosterBatchRequest));
		UpennyRosterResponse response = null;
		try
		{
			if (StringUtils.isBlank(upennyRosterBatchRequest.getAccessKey()))
				throw new IllegalArgumentException("OSS账号有误");
			if (StringUtils.isBlank(upennyRosterBatchRequest.getAccessKeySecret()))
				throw new IllegalArgumentException("OSS账号对应密钥值有误");
			if (StringUtils.isBlank(upennyRosterBatchRequest.getTemplateCode()))
				throw new IllegalArgumentException("流程模板编号不能为空");
			if (StringUtils.isBlank(upennyRosterBatchRequest.getBucket()))
				throw new IllegalArgumentException("OSS桶名有误");
			if (upennyRosterBatchRequest.getBatchId() == null || upennyRosterBatchRequest.getBatchId().length() <= 0)
				throw new IllegalArgumentException("批次编号有误");
			if (StringUtils.isBlank(upennyRosterBatchRequest.getObjectName()))
				throw new IllegalArgumentException("批量文件文件名有误");

			ArrayList<HashMap<String, String>> contactList = this.downloadSSOFile(upennyRosterBatchRequest);
			if (contactList != null && contactList.size() > 0)
			{
				String templateName = upennyRosterBatchRequest.getTemplateCode();
				int resCount = contactList.size();
				ActivityInfo aInfo = activityDao.findActivityInfoByTemplate(upennyRosterBatchRequest.getTemplateCode());
				if (aInfo != null)
				{
					String domain = aInfo.getDomain();
					for (HashMap<String, String> rosterMap : contactList)
					{
						RosterInfo rosterInfo = new RosterInfo();
						rosterInfo.setDomain(domain);
						if (aInfo != null)
						{
							rosterInfo.setActivityName(aInfo.getName());
						}
						rosterInfo.setTemplateName(templateName);
						rosterInfo.setCallRound(1);
						if (rosterMap.containsKey("batchId"))
						{
							rosterInfo.setBatchName(rosterMap.get("batchId"));
							rosterMap.remove("batchId");
						}

						if (rosterMap.containsKey("id"))
						{
							rosterInfo.setJobId(rosterMap.get("id"));
							rosterMap.remove("id");
						}

						if (rosterMap.containsKey("phoneNum1"))
						{
							rosterInfo.setPhoneNum1(rosterMap.get("phoneNum1"));
							rosterMap.remove("phoneNum1");
						}
						if (rosterMap.containsKey("phoneNum2"))
						{
							rosterInfo.setPhoneNum2(rosterMap.get("phoneNum2"));
							rosterMap.remove("phoneNum2");
						}
						if (rosterMap.containsKey("phoneNum3"))
						{
							rosterInfo.setPhoneNum3(rosterMap.get("phoneNum3"));
							rosterMap.remove("phoneNum3");
						}
						if (rosterMap.containsKey("phoneNum4"))
						{
							rosterInfo.setPhoneNum4(rosterMap.get("phoneNum4"));
							rosterMap.remove("phoneNum4");
						}
						if (rosterMap.containsKey("phoneNum5"))
						{
							rosterInfo.setPhoneNum5(rosterMap.get("phoneNum5"));
							rosterMap.remove("phoneNum5");
						}
						if (rosterMap.containsKey("lastname"))
						{
							rosterInfo.setLastname(rosterMap.get("lastname"));
							rosterMap.remove("lastname");
						}
						if (rosterMap.containsKey("firstname"))
						{
							rosterInfo.setFirstname(rosterMap.get("firstname"));
							rosterMap.remove("firstname");
						}
						if (rosterMap.containsKey("firstName"))
						{
							rosterInfo.setFirstname(rosterMap.get("firstName"));
							rosterMap.remove("firstName");
						}
						if (rosterMap.containsKey("age"))
						{
							rosterInfo.setAge(Integer.parseInt(rosterMap.get("age")));
							rosterMap.remove("age");
						}
						if (rosterMap.containsKey("sex"))
						{
							rosterInfo.setSex(rosterMap.get("sex"));
							rosterMap.remove("sex");
						}
						if (rosterMap.containsKey("customerId"))
						{
							rosterInfo.setCustomerId(rosterMap.get("customerId"));
							rosterMap.remove("customerId");
						}
						if (rosterMap.containsKey("address"))
						{
							rosterInfo.setAddress(rosterMap.get("address"));
							rosterMap.remove("address");
						}
						if (rosterMap.containsKey("email"))
						{
							rosterInfo.setEmail(rosterMap.get("email"));
							rosterMap.remove("email");
						}
						if (rosterMap.containsKey("cardType"))
						{
							rosterInfo.setCardType(rosterMap.get("cardType"));
							rosterMap.remove("cardType");
						}
						if (rosterMap.containsKey("cardNum"))
						{
							rosterInfo.setCardNum(rosterMap.get("cardNum"));
							rosterMap.remove("cardNum");
						}
						rosterInfo.setCreateTime(TimeUtil.getCurrentTimeStr());
						if (!rosterMap.isEmpty())
						{
							rosterInfo.setCustomFields(gson.toJson(rosterMap));
						}
						rosterDao.createRosterInfo(rosterInfo);
					}

					RosterBatchInfo batchInfo = new RosterBatchInfo();
					if (upennyRosterBatchRequest.getBatchId() != null)
					{
						String batchId = upennyRosterBatchRequest.getBatchId();
						batchInfo.setBatchId(batchId);
						batchInfo.setTemplateName(templateName);
						batchInfo.setCreateTime(TimeUtil.getCurrentTimeStr());
						batchInfo.setRoterNum(resCount);
						batchInfo.setCallRound(1);
						batchInfo.setDomain(domain);
						if (aInfo != null)
						{
							batchInfo.setActivityId(aInfo.getId());
							batchInfo.setActivityName(aInfo.getName());
							aInfo.addRosterNum(resCount);
							aInfo.addBatchNum();
							activityDao.updateActivityInfo(aInfo);

							MetricUtil.addRostersDay(aInfo.getName(), domain, resCount);
						}
						rosterBatchDao.createRosterBatchInfo(batchInfo);
					}
				}
				response = this.getCommonResponse(resCount);
			} else
				response = new UpennyRosterResponse(0, "文件不存在或者文件无数据");
		} catch (IllegalArgumentException e)
		{
			e.printStackTrace();
			response = new UpennyRosterResponse(0, e.getMessage());
		} catch (Exception e)
		{
			response = new UpennyRosterResponse(0, "fail");
			Util.error(this, "UpennyRosterResource.addSingleTaskList execute Fail!", e);
		}

		return gson.toJson(response);
	}

	/**
	 * 批量呼叫任务 接口信息
	 * 
	 * @param upennyRosterRequest
	 */
	@POST
	@Path("/job/batch/jobList")
	@Produces(
	{ MediaType.APPLICATION_JSON })
	public String addSingleTaskListInterface(UpennyRosterBatchListRequest upennyRosterBatchListRequest)
	{
		Util.info(this, "receive request " + gson.toJson(upennyRosterBatchListRequest));
		UpennyRosterResponse response = null;
		try
		{

			Assert.notNull(upennyRosterBatchListRequest.getBatchId(), "批次id不能为空");
			Assert.hasText(upennyRosterBatchListRequest.getTemplateCode(), "流程模板编号不能为空");
			Assert.notEmpty(upennyRosterBatchListRequest.getJobList(), "任务列表不能为空");

			ArrayList<HashMap<String, String>> contactList = new ArrayList<HashMap<String, String>>();

			for (BaseUpennyRosterRequest baseRosterBatchJobParam : upennyRosterBatchListRequest.getJobList())
			{
				if (baseRosterBatchJobParam.getJobData() == null)
					continue;
				HashMap<String, String> contactMap = new HashMap<>();
				// 存入值信息
				contactMap.put("id", baseRosterBatchJobParam.getJobId());
				contactMap.put("phoneNum1", baseRosterBatchJobParam.getPhone().toString());
				contactMap.put("batchId", upennyRosterBatchListRequest.getBatchId().toString());
				contactMap.put("callbackUrl", "");
				contactMap.put("firstname", baseRosterBatchJobParam.getJobData().getLoanUsername());
				contactMap.put("lastName", "");
				if ("男".equals(baseRosterBatchJobParam.getJobData().getLoanUserGender()))
				{
					contactMap.put("sex", "0");
				} else if ("女".equals(baseRosterBatchJobParam.getJobData().getLoanUserGender()))
				{
					contactMap.put("sex", "1");
				} else
				{
					contactMap.put("sex", "null");
				}
				contactMap.put("loanChannel", baseRosterBatchJobParam.getJobData().getLoanChannel());
				contactMap.put("loanOverdueDay", baseRosterBatchJobParam.getJobData().getLoanOverdueDay());
				contactMap.put("loanRepayAmount", baseRosterBatchJobParam.getJobData().getLoanRepayAmount() == null
						? "0" : baseRosterBatchJobParam.getJobData().getLoanRepayAmount().toString());
				if (StringUtils.isNotBlank(baseRosterBatchJobParam.getJobData().getLoanExpireDate()))
				{
					contactMap.put("loanExpireDate", baseRosterBatchJobParam.getJobData().getLoanExpireDate());
				}
				if (baseRosterBatchJobParam.getJobData().getLoanAmount() != null)
				{
					contactMap.put("loanAmount", baseRosterBatchJobParam.getJobData().getLoanAmount());
				}
				if (baseRosterBatchJobParam.getJobData().getLoanDays() != null)
				{
					contactMap.put("loanDays", baseRosterBatchJobParam.getJobData().getLoanDays());
				}
				if (baseRosterBatchJobParam.getJobData().getLoanUserIdNums() != null)
				{
					contactMap.put("loanUserIdNums", baseRosterBatchJobParam.getJobData().getLoanUserIdNums());
				}
				if (baseRosterBatchJobParam.getJobData().getLoanUserWages() != null)
				{
					contactMap.put("loanUserWages", baseRosterBatchJobParam.getJobData().getLoanUserWages());
				}
				if (baseRosterBatchJobParam.getJobData().getCompanyFullName() != null)
				{
					contactMap.put("companyFullName", baseRosterBatchJobParam.getJobData().getCompanyFullName());
				}
				if (baseRosterBatchJobParam.getJobData().getCompanyShortName() != null)
				{
					contactMap.put("companyShortName", baseRosterBatchJobParam.getJobData().getCompanyShortName());
				}
				if (baseRosterBatchJobParam.getJobData().getExtra1() != null)
				{
					contactMap.put("extra1", baseRosterBatchJobParam.getJobData().getExtra1());
				}
				if (baseRosterBatchJobParam.getJobData().getExtra2() != null)
				{
					contactMap.put("extra2", baseRosterBatchJobParam.getJobData().getExtra2());
				}
				if (baseRosterBatchJobParam.getJobData().getExtra3() != null)
				{
					contactMap.put("extra3", baseRosterBatchJobParam.getJobData().getExtra3());
				}
				if (baseRosterBatchJobParam.getJobData().getExtra4() != null)
				{
					contactMap.put("extra4", baseRosterBatchJobParam.getJobData().getExtra4());
				}
				if (baseRosterBatchJobParam.getJobData().getExtra5() != null)
				{
					contactMap.put("extra5", baseRosterBatchJobParam.getJobData().getExtra5());
				}
				if (baseRosterBatchJobParam.getJobData().getExtra6() != null)
				{
					contactMap.put("extra6", baseRosterBatchJobParam.getJobData().getExtra6());
				}
				if (baseRosterBatchJobParam.getJobData().getExtra7() != null)
				{
					contactMap.put("extra7", baseRosterBatchJobParam.getJobData().getExtra7());
				}
				if (baseRosterBatchJobParam.getJobData().getExtra8() != null)
				{
					contactMap.put("extra8", baseRosterBatchJobParam.getJobData().getExtra8());
				}
				if (baseRosterBatchJobParam.getJobData().getExtra9() != null)
				{
					contactMap.put("extra9", baseRosterBatchJobParam.getJobData().getExtra9());
				}
				if (baseRosterBatchJobParam.getJobData().getExtra10() != null)
				{
					contactMap.put("extra10", baseRosterBatchJobParam.getJobData().getExtra10());
				}
				contactList.add(contactMap);
			}
			if (contactList != null && contactList.size() > 0)
			{
				String templateName = upennyRosterBatchListRequest.getTemplateCode();
				int resCount = contactList.size();
				ActivityInfo aInfo = activityDao
						.findActivityInfoByTemplate(upennyRosterBatchListRequest.getTemplateCode());
				if (aInfo != null)
				{
					String domain = aInfo.getDomain();
					for (HashMap<String, String> rosterMap : contactList)
					{
						RosterInfo rosterInfo = new RosterInfo();
						rosterInfo.setDomain(domain);
						rosterInfo.setCallRound(1);
						if (aInfo != null)
						{
							rosterInfo.setActivityName(aInfo.getName());
						}
						rosterInfo.setTemplateName(templateName);
						if (rosterMap.containsKey("batchId"))
						{
							rosterInfo.setBatchName(rosterMap.get("batchId"));
							rosterMap.remove("batchId");
						}

						if (rosterMap.containsKey("id"))
						{
							rosterInfo.setJobId(rosterMap.get("id"));
							rosterMap.remove("id");
						}

						if (rosterMap.containsKey("phoneNum1"))
						{
							rosterInfo.setPhoneNum1(rosterMap.get("phoneNum1"));
							rosterMap.remove("phoneNum1");
						}
						if (rosterMap.containsKey("phoneNum2"))
						{
							rosterInfo.setPhoneNum2(rosterMap.get("phoneNum2"));
							rosterMap.remove("phoneNum2");
						}
						if (rosterMap.containsKey("phoneNum3"))
						{
							rosterInfo.setPhoneNum3(rosterMap.get("phoneNum3"));
							rosterMap.remove("phoneNum3");
						}
						if (rosterMap.containsKey("phoneNum4"))
						{
							rosterInfo.setPhoneNum4(rosterMap.get("phoneNum4"));
							rosterMap.remove("phoneNum4");
						}
						if (rosterMap.containsKey("phoneNum5"))
						{
							rosterInfo.setPhoneNum5(rosterMap.get("phoneNum5"));
							rosterMap.remove("phoneNum5");
						}
						if (rosterMap.containsKey("lastname"))
						{
							rosterInfo.setLastname(rosterMap.get("lastname"));
							rosterMap.remove("lastname");
						}
						if (rosterMap.containsKey("firstname"))
						{
							rosterInfo.setFirstname(rosterMap.get("firstname"));
							rosterMap.remove("firstname");
						}
						if (rosterMap.containsKey("age"))
						{
							rosterInfo.setAge(Integer.parseInt(rosterMap.get("age")));
							rosterMap.remove("age");
						}
						if (rosterMap.containsKey("sex"))
						{
							rosterInfo.setSex(rosterMap.get("sex"));
							rosterMap.remove("sex");
						}
						if (rosterMap.containsKey("customerId"))
						{
							rosterInfo.setCustomerId(rosterMap.get("customerId"));
							rosterMap.remove("customerId");
						}
						if (rosterMap.containsKey("address"))
						{
							rosterInfo.setAddress(rosterMap.get("address"));
							rosterMap.remove("address");
						}
						if (rosterMap.containsKey("email"))
						{
							rosterInfo.setEmail(rosterMap.get("email"));
							rosterMap.remove("email");
						}
						if (rosterMap.containsKey("cardType"))
						{
							rosterInfo.setCardType(rosterMap.get("cardType"));
							rosterMap.remove("cardType");
						}
						if (rosterMap.containsKey("cardNum"))
						{
							rosterInfo.setCardNum(rosterMap.get("cardNum"));
							rosterMap.remove("cardNum");
						}
						rosterInfo.setCreateTime(TimeUtil.getCurrentTimeStr());
						if (!rosterMap.isEmpty())
						{
							rosterInfo.setCustomFields(gson.toJson(rosterMap));
						}
						rosterDao.createRosterInfo(rosterInfo);
					}

					RosterBatchInfo batchInfo = new RosterBatchInfo();
					if (upennyRosterBatchListRequest.getBatchId() != null)
					{
						String batchId = upennyRosterBatchListRequest.getBatchId() + "";
						batchInfo.setBatchId(batchId);
						batchInfo.setTemplateName(templateName);
						batchInfo.setCreateTime(TimeUtil.getCurrentTimeStr());
						batchInfo.setRoterNum(resCount);
						batchInfo.setDomain(domain);
						batchInfo.setCallRound(1);
						if (aInfo != null)
						{
							batchInfo.setActivityId(aInfo.getId());
							batchInfo.setActivityName(aInfo.getName());
							aInfo.addRosterNum(resCount);
							aInfo.addBatchNum();
							activityDao.updateActivityInfo(aInfo);
							MetricUtil.addRostersDay(aInfo.getName(), domain, resCount);
						}
						rosterBatchDao.createRosterBatchInfo(batchInfo);
					}
				}
				response = this.getCommonResponse(resCount);
			} else
				response = new UpennyRosterResponse(0, "批量导入失败");
		} catch (IllegalArgumentException e)
		{
			response = new UpennyRosterResponse(0, e.getMessage());
		} catch (Exception e)
		{
			response = new UpennyRosterResponse(0, "fail");
			Util.error(this, "UpennyRosterResource.addSingleTaskListInterface execute Fail!", e);
		}

		return gson.toJson(response);
	}

	/**
	 * 下载SSO文件信息
	 * 
	 * @return
	 * @throws IOException
	 */
	private ArrayList<HashMap<String, String>> downloadSSOFile(UpennyRosterBatchRequest upennyRosterBatchRequest)
			throws IOException
	{

		ArrayList<HashMap<String, String>> contacts = new ArrayList<HashMap<String, String>>();

		// 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录
		// https://ram.console.aliyun.com 创建
		accessKeyId = upennyRosterBatchRequest.getAccessKey();
		accessKeySecret = upennyRosterBatchRequest.getAccessKeySecret();
		bucketName = upennyRosterBatchRequest.getBucket();
		// String key = prefix_key + bucketName +
		// upennyRosterBatchRequest.getObjectName();

		// 创建OSSClient实例
		OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
		OSSObject ossObject = ossClient.getObject(bucketName, upennyRosterBatchRequest.getObjectName());
		// 读Object内容
		BufferedReader reader = new BufferedReader(new InputStreamReader(ossObject.getObjectContent(), "UTF-8"));
		while (true)
		{
			String line = reader.readLine();
			Util.info(this, "UpennyRosterResource.downloadSSOFile call sso backResult:%s", line);
			if (line == null)
				break;
			if (!"".equals(line) && line.contains("{"))
			{

				int indexOf = line.indexOf("{");
				String strBefor = line.substring(0, indexOf);
				String strAfter = line.substring(indexOf);

				UserInfoModel userModel = null;
				if (StringUtils.isNotBlank(strAfter))
				{
					userModel = gson.fromJson(strAfter, UserInfoModel.class);
				}

				String jobId = null;
				String phoneNum = null;
				if (StringUtils.isNotBlank(strBefor))
				{
					String[] beforArr = strBefor.split(",");
					if (beforArr != null && beforArr.length >= 2)
					{
						jobId = beforArr[0];
						phoneNum = beforArr[1];
					}
				}

				if (StringUtils.isBlank(jobId) || StringUtils.isBlank(phoneNum))
					continue;

				HashMap<String, String> contactMap = new HashMap<>();
				// contactMap.put("templateCode",
				// upennyRosterBatchRequest.getTemplateCode());
				contactMap.put("batchId", upennyRosterBatchRequest.getBatchId().toString());

				contactMap.put("id", jobId);
				contactMap.put("phoneNum1", phoneNum);
				if (userModel != null)
				{
					contactMap.put("firstname", userModel.getLoanUsername());
					contactMap.put("lastname", "");
					if ("男".equals(userModel.getLoanUserGender()))
					{
						contactMap.put("sex", "0");
					} else if ("女".equals(userModel.getLoanUserGender()))
					{
						contactMap.put("sex", "1");
					} else
					{
						contactMap.put("sex", "null");
					}
					contactMap.put("loanChannel", userModel.getLoanChannel());
					contactMap.put("loanOverdueDay", userModel.getLoanOverdueDay());
					contactMap.put("loanRepayAmount",
							userModel.getLoanRepayAmount() == null ? "0" : userModel.getLoanRepayAmount().toString());
					if (StringUtils.isNotBlank(userModel.getLoanExpireDate()))
					{
						contactMap.put("loanExpireDate", userModel.getLoanExpireDate());
					}
					if (userModel.getLoanAmount() != null)
					{
						contactMap.put("loanAmount", userModel.getLoanAmount());
					}
					if (userModel.getLoanDays() != null)
					{
						contactMap.put("loanDays", userModel.getLoanDays());
					}
					if (userModel.getLoanUserIdNums() != null)
					{
						contactMap.put("loanUserIdNums", userModel.getLoanUserIdNums());
					}
					if (userModel.getLoanUserWages() != null)
					{
						contactMap.put("loanUserWages", userModel.getLoanUserWages());
					}
					if (userModel.getCompanyFullName() != null)
					{
						contactMap.put("companyFullName", userModel.getCompanyFullName());
					}
					if (userModel.getCompanyShortName() != null)
					{
						contactMap.put("companyShortName", userModel.getCompanyShortName());
					}
					if (userModel.getExtra1() != null)
					{
						contactMap.put("extra1", userModel.getExtra1());
					}
					if (userModel.getExtra2() != null)
					{
						contactMap.put("extra2", userModel.getExtra2());
					}
					if (userModel.getExtra3() != null)
					{
						contactMap.put("extra3", userModel.getExtra3());
					}
					if (userModel.getExtra4() != null)
					{
						contactMap.put("extra4", userModel.getExtra4());
					}
					if (userModel.getExtra5() != null)
					{
						contactMap.put("extra5", userModel.getExtra5());
					}
					if (userModel.getExtra6() != null)
					{
						contactMap.put("extra6", userModel.getExtra6());
					}
					if (userModel.getExtra7() != null)
					{
						contactMap.put("extra7", userModel.getExtra7());
					}
					if (userModel.getExtra8() != null)
					{
						contactMap.put("extra8", userModel.getExtra8());
					}
					if (userModel.getExtra9() != null)
					{
						contactMap.put("extra9", userModel.getExtra9());
					}
					if (userModel.getExtra10() != null)
					{
						contactMap.put("extra10", userModel.getExtra10());
					}

				}
				contacts.add(contactMap);
			}
		}
		reader.close();
		// 关闭client
		ossClient.shutdown();
		return contacts;
	}

	/**
	 * 工单报告查询
	 * 
	 * @param upennyRosterRequest
	 */
	@POST
	@Path("/jobReport/get")
	@Produces(
	{ MediaType.APPLICATION_JSON })
	public UpennyRosterResponse getSingleTask(UpennyRosterRequest upennyRosterRequest)
	{
		UpennyRosterResponse response = null;
		try
		{
			if (StringUtils.isBlank(upennyRosterRequest.getJobId()))
				throw new IllegalArgumentException("呼叫ID有误");

			// Map<String, Object> activityCallResultMap =
			// activityDAO.getActivityByContactId(upennyRosterRequest.getJobId());
			response = new UpennyRosterResponse(1, "获取成功", null);
		} catch (IllegalArgumentException e)
		{
			response = new UpennyRosterResponse(0, e.getMessage());
		} catch (Exception e)
		{
			response = new UpennyRosterResponse(0, "服务端执行异常");
			Util.error(this, "UpennyRosterResource.getSingleTask execute Fail!", e);
		}
		return response;
	}

	/**
	 * 工单报告查询
	 * 
	 * @param upennyRosterRequest
	 */
	@POST
	@Path("/jobReport/callBack")
	@Produces(
	{ MediaType.APPLICATION_JSON })
	public UpennyRosterResponse getCallBackSingleTask(UpennyRosterRequest upennyRosterRequest)
	{
		UpennyRosterResponse response = null;
		try
		{
			if (StringUtils.isBlank(upennyRosterRequest.getJobId()))
				throw new IllegalArgumentException("呼叫ID有误");
			if (StringUtils.isBlank(upennyRosterRequest.getTemplateCode()))
				throw new IllegalArgumentException("流程模板编号不能为空");
			// boolean activityCallResultMap =
			// activityDAO.upennyRosterCallBack(upennyRosterRequest.getTemplateCode(),
			// upennyRosterRequest.getJobId());
			boolean activityCallResultMap = false;
			response = new UpennyRosterResponse(1, "获取成功", activityCallResultMap);
		} catch (IllegalArgumentException e)
		{
			response = new UpennyRosterResponse(0, e.getMessage());
		} catch (Exception e)
		{
			response = new UpennyRosterResponse(0, "服务端执行异常");
			Util.error(this, "UpennyRosterResource.getSingleTask execute Fail!", e);
		}
		return response;
	}

	public static void main(String[] args)
	{
		/*
		 * UpennyRosterRequest request = new UpennyRosterRequest();
		 * request.setTemplateCode("wqw1228001");
		 * request.setCallbackUrl("http://192.168.20.123"); request.setIdle("");
		 * request.setJobData(new UserInfoModel());
		 * request.getJobData().setLoanChannel("qbk");
		 * request.getJobData().setLoanExpireDate("12月31日");
		 * request.getJobData().setLoanOverdueDay("3");
		 * request.getJobData().setLoanRepayAmount("2");
		 * request.getJobData().setLoanUserGender("男");
		 * request.getJobData().setLoanUsername("张三");
		 * request.setJobId("job001"); request.setPhone(18051228180L);
		 */

		UpennyRosterBatchRequest request = new UpennyRosterBatchRequest();
		request.setAccessKey("LTAIf6iRTe8WVxwH");
		request.setAccessKeySecret("d388YOw31tShDirJDsY4BavwcgWbNv");
		request.setBatchId("1ssdds");
		request.setBucket("wilcom-record-bucket");
		request.setCallbackUrl("1234");
		request.setTemplateCode("C0");
		request.setObjectName("roster/test.csv");

		Gson gson = new Gson();
		System.out.print(gson.toJson(request));
	}
}
