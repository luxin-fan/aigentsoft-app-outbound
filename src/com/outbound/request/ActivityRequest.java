package com.outbound.request;

import lombok.Data;
import lombok.EqualsAndHashCode;




/**
 * 活动请求对象
 * @author duanlsh
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class ActivityRequest extends BaseRequest{
	
	private String domain; //域名 all 表示全部
	
	private String activityName; //活动名字
	
	private String activityBeginTime; //活动开始时间
	
	private String activityEndTime; //活动结束时间

	private int offset = 0; //索引
	
	private int pageSize = 10; //数量

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getActivityBeginTime() {
		return activityBeginTime;
	}

	public void setActivityBeginTime(String activityBeginTime) {
		this.activityBeginTime = activityBeginTime;
	}

	public String getActivityEndTime() {
		return activityEndTime;
	}

	public void setActivityEndTime(String activityEndTime) {
		this.activityEndTime = activityEndTime;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
}
