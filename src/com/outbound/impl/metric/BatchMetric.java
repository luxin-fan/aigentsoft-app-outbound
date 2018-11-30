package com.outbound.impl.metric;

public class BatchMetric {

	//private Logger logger = Logger.getLogger(BatchMetric.class.getName());
	private String activityName;
	private String batchName;
	private String domain;
	private int roterNum;
	private int outCallNum;
	private int unCallNum;
	private int answerCallNum;
	private int dncNum;
	private int status;
	private String startTime;
	private int round = 1;
	//空号
	private int fail1Num;
	//关机
	private int fail2Num;
	//用户正忙
	private int fail3Num;
	//振铃没接听
	private int fail4Num;
	//停机
	private int fail5Num;
	//无法接通
	private int fail6Num;
	//正在通话中
	private int fail7Num;
	//其它
	private int failOtherNum;
	
	public String toString(){
		return "roterNum-" + roterNum +"|"+
				"outCallNum-" + outCallNum +"|"+
				"round-" + round +"|"+
				"unCallNum-" + unCallNum +"|"+
				"answerCallNum-" + answerCallNum +"|"+
				"空号-" + fail1Num +"|"+
				"关机-" + fail2Num +"|"+
				"用户正忙-" + fail3Num +"|"+
				"振铃没接听-" + fail4Num +"|"+
				"停机-" + getFail5Num() +"|"+
				"其它-" + failOtherNum +"|"+
				"dncNum-" + dncNum +"|";
	}
	
	public void clear(){
		roterNum = 0;
		outCallNum = 0;
		unCallNum = 0;
		answerCallNum = 0;
		dncNum = 0;
		
		fail1Num = 0;
		fail2Num = 0;
		fail3Num = 0;
		fail4Num = 0;
		setFail5Num(0);
		failOtherNum = 0;
		
	}

	public int getRoterNum() {
		return roterNum;
	}

	public void setRoterNum(int roterNum) {
		this.roterNum = roterNum;
		unCallNum = roterNum - outCallNum;
	}
	
	public int getOutCallNum() {
		return outCallNum;
	}

	public void setOutCallNum(int outCallNum) {
		this.outCallNum = outCallNum;
		//MetricUtil.logger.info(" ### " + batchName +" set outcallnum ["+outCallNum+"]");
		unCallNum = roterNum - outCallNum;
		if(unCallNum <=0){
			unCallNum = 0;
		}
	}
	
	public void addOutCallNum() {
		this.outCallNum ++;
		//MetricUtil.logger.info(" ### " + batchName +" add outcallnum ["+outCallNum+"]");
		unCallNum = roterNum - outCallNum;
		if(unCallNum <=0){
			unCallNum = 0;
		}
	}

	public int getUnCallNum() {
		return unCallNum;
	}

	public void setUnCallNum(int unCallNum) {
		this.unCallNum = unCallNum;
	}

	public int getAnswerCallNum() {
		return answerCallNum;
	}

	public void setAnswerCallNum(int answerCallNum) {
		this.answerCallNum = answerCallNum;
	}
	
	public void addAnswerCallNum() {
		this.answerCallNum ++;
	}

	public int getDncNum() {
		return dncNum;
	}

	public void setDncNum(int dncNum) {
		this.dncNum = dncNum;
	}
	
	public void addDncNum() {
		this.dncNum ++;
	}

	public int getFail1Num() {
		return fail1Num;
	}

	public void setFail1Num(int fail1Num) {
		this.fail1Num = fail1Num;
	}
	
	public void addFail1Num() {
		this.fail1Num ++;
	}

	public int getFail2Num() {
		return fail2Num;
	}

	public void setFail2Num(int fail2Num) {
		this.fail2Num = fail2Num;
	}
	
	public void addFail2Num() {
		this.fail2Num ++;
	}

	public int getFail3Num() {
		return fail3Num;
	}

	public void setFail3Num(int fail3Num) {
		this.fail3Num = fail3Num;
	}
	
	public void addFail3Num() {
		this.fail3Num ++;
	}

	public int getFail4Num() {
		return fail4Num;
	}

	public void setFail4Num(int fail4Num) {
		this.fail4Num = fail4Num;
	}
	
	public void addFail4Num() {
		this.fail4Num ++;
	}

	public int getFailOtherNum() {
		return failOtherNum;
	}

	public void setFailOtherNum(int failOtherNum) {
		this.failOtherNum = failOtherNum;
	}
	
	public void addFailOtherNum() {
		this.failOtherNum ++;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getBatchName() {
		return batchName;
	}

	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public int getFail5Num() {
		return fail5Num;
	}

	public void setFail5Num(int fail5Num) {
		this.fail5Num = fail5Num;
	}
	
	public void addFail5Num() {
		this.fail5Num ++;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public int getFail6Num() {
		return fail6Num;
	}

	public void setFail6Num(int fail6Num) {
		this.fail6Num = fail6Num;
	}

	public int getFail7Num() {
		return fail7Num;
	}

	public void setFail7Num(int fail7Num) {
		this.fail7Num = fail7Num;
	}
	
	public void addFail6Num() {
		this.fail6Num ++;
	}
	
	public void addFail7Num() {
		this.fail7Num ++;
	}

}
