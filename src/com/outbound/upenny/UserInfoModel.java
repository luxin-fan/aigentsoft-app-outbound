package com.outbound.upenny;

import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;

import com.outbound.object.util.TimeUtil;

/**
 * 用户信息
 * 
 * @author duanlsh
 *
 */

@XmlRootElement
public class UserInfoModel {

	private String loanUsername; // 姓名
	private String loanUserGender; // 性别
	private String loanChannel; // 渠道
	private String loanOverdueDay;
	private String loanRepayAmount;
	private String loanExpireDate; // 日期信息
	private String loanAmount;
	private String loanDays;
	private String loanUserIdNums;
	private String loanUserWages;
	private String companyFullName;
	private String companyShortName;
	private String extra1;
	private String extra2;
	private String extra3;
	private String extra4;
	private String extra5;
	private String extra6;
	private String extra7;
	private String extra8;
	private String extra9;
	private String extra10;

	public String getLoanUsername() {
		return loanUsername;
	}

	public void setLoanUsername(String loanUsername) {
		this.loanUsername = loanUsername;
	}

	public String getLoanUserGender() {
		return loanUserGender;
	}

	public void setLoanUserGender(String loanUserGender) {
		this.loanUserGender = loanUserGender;
	}

	public String getLoanChannel() {
		return loanChannel;
	}

	public void setLoanChannel(String loanChannel) {
		this.loanChannel = loanChannel;
	}

	public String getLoanOverdueDay() {
		return loanOverdueDay;
	}

	public void setLoanOverdueDay(String loanOverdueDay) {
		this.loanOverdueDay = loanOverdueDay;
	}

	public String getLoanRepayAmount() {
		return loanRepayAmount;
	}

	public void setLoanRepayAmount(String loanRepayAmount) {
		this.loanRepayAmount = loanRepayAmount;
	}

	public String getLoanExpireDate() {
		return loanExpireDate;
	}

	public void setLoanExpireDate(String loanExpireDate) {
		this.loanExpireDate = loanExpireDate;
	}

	public String getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmount(String loanAmount) {
		this.loanAmount = loanAmount;
	}

	public String getLoanDays() {
		return loanDays;
	}

	public void setLoanDays(String loanDays) {
		this.loanDays = loanDays;
	}

	public String getLoanUserIdNums() {
		return loanUserIdNums;
	}

	public void setLoanUserIdNums(String loanUserIdNums) {
		this.loanUserIdNums = loanUserIdNums;
	}

	public String getLoanUserWages() {
		return loanUserWages;
	}

	public void setLoanUserWages(String loanUserWages) {
		this.loanUserWages = loanUserWages;
	}

	public String getCompanyFullName() {
		return companyFullName;
	}

	public void setCompanyFullName(String companyFullName) {
		this.companyFullName = companyFullName;
	}

	public String getCompanyShortName() {
		return companyShortName;
	}

	public void setCompanyShortName(String companyShortName) {
		this.companyShortName = companyShortName;
	}

	public String getExtra1() {
		return extra1;
	}

	public void setExtra1(String extra1) {
		this.extra1 = extra1;
	}

	public String getExtra2() {
		return extra2;
	}

	public void setExtra2(String extra2) {
		this.extra2 = extra2;
	}

	public String getExtra3() {
		return extra3;
	}

	public void setExtra3(String extra3) {
		this.extra3 = extra3;
	}

	public String getExtra4() {
		return extra4;
	}

	public void setExtra4(String extra4) {
		this.extra4 = extra4;
	}

	public String getExtra5() {
		return extra5;
	}

	public void setExtra5(String extra5) {
		this.extra5 = extra5;
	}

	public String getExtra6() {
		return extra6;
	}

	public void setExtra6(String extra6) {
		this.extra6 = extra6;
	}

	public String getExtra7() {
		return extra7;
	}

	public void setExtra7(String extra7) {
		this.extra7 = extra7;
	}

	public String getExtra8() {
		return extra8;
	}

	public void setExtra8(String extra8) {
		this.extra8 = extra8;
	}

	public String getExtra9() {
		return extra9;
	}

	public void setExtra9(String extra9) {
		this.extra9 = extra9;
	}

	public String getExtra10() {
		return extra10;
	}

	public void setExtra10(String extra10) {
		this.extra10 = extra10;
	}

	public static void main(String[] args) {
		System.out.println(UUID.randomUUID().toString());
		System.out.println(TimeUtil.getCurrentTimeStr());
	}
}
