package com.outbound.dialer.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.outbound.object.DbColumn;
import com.outbound.object.Roster;
import com.outbound.object.RosterInfo;

public abstract class EncryptRosterUtil {

	
	static Logger logger = LoggerFactory.getLogger(EncryptRosterUtil.class);
	
	public static Roster encryptRoster(Roster rosterInfo, List<DbColumn> dbColumnList){
		try {
			if (rosterInfo == null){
				return rosterInfo;
			}
			String currentCallNum  = rosterInfo.getCurrentCallNum();
			for (DbColumn dbcolumn : dbColumnList){
				if (dbcolumn.getEncrypt() <= 0){
					if (dbcolumn.getName().startsWith("phoneNum")){
						rosterInfo.setCurrentCallNum(currentCallNum);
					}
					continue;
				}
				String content = defaultSymbol(4);
				
				
				Field field = null; 
				try{
					field = rosterInfo.getClass().getDeclaredField(dbcolumn.getName());
					field.setAccessible(true);
				} catch (Exception e){
					content = getCustomerField(rosterInfo.getCustomFields(), dbcolumn);
					field =  rosterInfo.getClass().getDeclaredField("customFields");
					field.setAccessible(true);
					field.set(rosterInfo, content);
					continue;
				}
				
				if (dbcolumn.getName().startsWith("phoneNum")){
					content = field.get(rosterInfo) == null ? "" : encryptPhoneNum(field.get(rosterInfo).toString());
					if(field.get(rosterInfo) != null){
						if(rosterInfo.getCurrentCallNum() != null){
							if(rosterInfo.getCurrentCallNum().equals(field.get(rosterInfo))){
								rosterInfo.setCurrentCallNum(encryptPhoneNum(rosterInfo.getCurrentCallNum()));
							}
						}
					}
				}
				
				if (dbcolumn.getName().equals("firstname")){
					//content =  defaultSymbol(2);
					String contentLocal = field.get(rosterInfo).toString();
					if (contentLocal.length() > 0){
						content = contentLocal.substring(0, 1) + defaultSymbol(contentLocal.length()-1); 
					} else {
						content = ""; 
					}
				}
				
				if (dbcolumn.getName().equals("age")){
					content = defaultSymbol(2);
				}
				
				if (dbcolumn.getName().equals("customerId")){
					if (field.get(rosterInfo) != null){
						String contentLocal = field.get(rosterInfo).toString();
						if (contentLocal.length() > 3){
							content = defaultSymbol(4) + contentLocal.substring(contentLocal.length() -3, contentLocal.length()); 
						} else {
							content = defaultSymbol(4) + contentLocal; 
						}
					} 
				}
				
				if (dbcolumn.getName().equals("address") || dbcolumn.getName().equals("email")){
					content = defaultSymbol(4);
				}
				
				if (dbcolumn.getName().equals("cardNum")){
					content = encryptCardNum(field.get(rosterInfo)== null ? "" : field.get(rosterInfo).toString());
				}
				
				field.set(rosterInfo, content);
			}
		} catch (Exception e){
			logger.error("## EncryptRosterUtil encryptRoster execute fail", e);
		}
		return rosterInfo;
		
	}
	
	public static RosterInfo encryptRoster(RosterInfo rosterInfo, List<DbColumn> dbColumnList){
		try {
			if (rosterInfo == null){
				return rosterInfo;
			}
			String currentCallNum  = rosterInfo.getCurrentCallNum();
			for (DbColumn dbcolumn : dbColumnList){
				if (dbcolumn.getEncrypt() <= 0){
					if (dbcolumn.getName().startsWith("phoneNum")){
						rosterInfo.setCurrentCallNum(currentCallNum);
					}
					continue;
				}
				String content = defaultSymbol(4);
				
				
				Field field = null; 
				try{
					field = rosterInfo.getClass().getDeclaredField(dbcolumn.getName());
					field.setAccessible(true);
				} catch (Exception e){
					content = getCustomerField(rosterInfo.getCustomFields(), dbcolumn);
					field =  rosterInfo.getClass().getDeclaredField("customFields");
					field.setAccessible(true);
					field.set(rosterInfo, content);
					continue;
				}
				
				if (dbcolumn.getName().startsWith("phoneNum")){
					content = field.get(rosterInfo) == null ? "" : encryptPhoneNum(field.get(rosterInfo).toString());
					if(field.get(rosterInfo) != null){
						if(rosterInfo.getCurrentCallNum() != null){
							if(rosterInfo.getCurrentCallNum().equals(field.get(rosterInfo))){
								rosterInfo.setCurrentCallNum(encryptPhoneNum(rosterInfo.getCurrentCallNum()));
							}
						}
					}
				}
				
				if (dbcolumn.getName().equals("firstname")){
					//content =  defaultSymbol(2);
					String contentLocal = field.get(rosterInfo).toString();
					if (contentLocal.length() > 0){
						content = contentLocal.substring(0, 1) + defaultSymbol(contentLocal.length()-1); 
					} else {
						content = ""; 
					}
				}
				
				if (dbcolumn.getName().equals("age")){
					content = defaultSymbol(2);
				}
				
				if (dbcolumn.getName().equals("customerId")){
					if (field.get(rosterInfo) != null){
						String contentLocal = field.get(rosterInfo).toString();
						if (contentLocal.length() > 3){
							content = defaultSymbol(4) + contentLocal.substring(contentLocal.length() -3, contentLocal.length()); 
						} else {
							content = defaultSymbol(4) + contentLocal; 
						}
					} 
				}
				
				if (dbcolumn.getName().equals("address") || dbcolumn.getName().equals("email")){
					content = defaultSymbol(4);
				}
				
				if (dbcolumn.getName().equals("cardNum")){
					content = encryptCardNum(field.get(rosterInfo)== null ? "" : field.get(rosterInfo).toString());
				}
				
				field.set(rosterInfo, content);
			}
		} catch (Exception e){
			logger.error("## EncryptRosterUtil encryptRoster execute fail", e);
		}
		return rosterInfo;
		
	}
	
	private static String getCustomerField(String customerField, DbColumn dbcolumn){
		String result = null;
		if (StringUtils.isBlank(customerField)){
			return result;
		}
		JsonObject jsonObj = new JsonParser().parse(customerField).getAsJsonObject();
		if (jsonObj.entrySet().size()<0){
			return result;
		}
		
		JsonObject resultJsonObj = new JsonObject();
		for (Entry<String, JsonElement> str : jsonObj.entrySet()){
			String value = str.getValue().getAsString();
			if (!dbcolumn.getName().equals(str.getKey())){
				resultJsonObj.addProperty(str.getKey(), value);
				continue;
			}
			String param = encryptPhoneNum(value);
			if (!param.equals(defaultSymbol(4))){
				resultJsonObj.addProperty(str.getKey(), param);
			} else {
				String localContent = null;
				try {
					Integer.valueOf(value);
					if (value.length() > 2){
						localContent = defaultSymbol(4) + value.substring(value.length() - 2, value.length());
					} else {
						localContent = defaultSymbol(4) + value;
					}
				} catch (Exception e){
					localContent = defaultSymbol(4);
				}
				if (StringUtils.isNotBlank(localContent)){
					resultJsonObj.addProperty(str.getKey(), localContent);
				}
			}
			
		}
		return resultJsonObj.toString();
	}
	
	
	/**
	 * 加密电话号码
	 * @param phoneNum
	 * @return
	 */
	private static String encryptPhoneNum(String phoneNum){
		String result = "";
		String defaultValue = defaultSymbol(4);
		String landLinePhoneNumPattern = "(0\\d{2}-\\d{8}(-\\d{1,4})?)|(0\\d{3}-\\d{7,8}(-\\d{1,4})?)$"; //固定电话
		String phoneNumPattern = "^1[0-9]{10}$";
		Pattern pattern = Pattern.compile(landLinePhoneNumPattern);
		if (pattern.matcher(phoneNum).matches()){
			String[] phoneSplit = phoneNum.split("-");
			if (phoneSplit.length >= 2){
				String param = phoneSplit[1].substring(0, 2) + defaultValue + phoneSplit[1].substring(phoneSplit.length -2);
				result = phoneNum.replace(phoneSplit[1], param);
			}
		}else{
			pattern = Pattern.compile(phoneNumPattern);
			if(pattern.matcher(phoneNum).matches()){
				result = phoneNum.substring(0,3) + defaultValue + phoneNum.substring(7);
			}else {
				landLinePhoneNumPattern = "([0-9]{7})|([0-9]{8})$";
				pattern = Pattern.compile(landLinePhoneNumPattern);
				if (pattern.matcher(phoneNum).matches()){
					result = phoneNum.substring(0, 2) + defaultValue + phoneNum.substring(phoneNum.length() -2);
				}
			}
		}
		
//		if (StringUtils.isBlank(result)){
//			result = defaultValue;
//		}
		return result;
	}
	
	
	/**
	 * 身份证
	 * @param cardNum
	 * @return
	 */
	private static String encryptCardNum(String cardNum){
		String cardNumPattern = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$";
		Pattern pattern = Pattern.compile(cardNumPattern);
		if (pattern.matcher(cardNum).matches()){
//			
			String header = cardNum.substring(0, 6);
			String body = defaultSymbol(6);
			String tail = cardNum.substring(12);
			return header + body + tail;
		} else {
			cardNumPattern = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X|x)$";
			pattern = Pattern.compile(cardNumPattern);
			if (pattern.matcher(cardNum).matches()){
//				
				String header = cardNum.substring(0, 6);
				String body = defaultSymbol(8);
				String tail = cardNum.substring(14);
				return header + body + tail;
			}
		}
		
		if (cardNum.length() > 4){
			return defaultSymbol(4) + cardNum.substring(cardNum.length() - 4, cardNum.length()); 
		} else if (cardNum.length() > 0){
			return defaultSymbol(4) + cardNum;
		}
		return defaultSymbol(8);
	}
	
	
	
	/**
	 * 加*长度
	 * @param length
	 * @return
	 */
	private static String defaultSymbol(int length){
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i< length; i++){
			sb.append("*");
		}
		return sb.toString();
	}
	
	
	public static void main(String[] args) {
		DbColumn dbcolumn = new DbColumn();
		dbcolumn.setName("aa");
		dbcolumn.setEncrypt(1);
		System.out.println(getCustomerField("{\"aa\":\"自定义1-1\",\"bb\":\"adb\"}", dbcolumn));
		String contentLocal = "1236789";
		System.out.println(contentLocal.substring(contentLocal.length() -3, contentLocal.length()));
		System.out.println(encryptPhoneNum("18721983182"));
		
		DbColumn d1 = new DbColumn();
		d1.setName("phoneNum1");
		d1.setEncrypt(0);
		
		DbColumn d2 = new DbColumn();
		d2.setName("phoneNum2");
		d2.setEncrypt(1);
		
		DbColumn d3 = new DbColumn();
		d3.setName("phoneNum3");
		d3.setEncrypt(0);
		
		DbColumn d4 = new DbColumn();
		d4.setName("firstName");
		d4.setEncrypt(0);
		
		List<DbColumn> list = new ArrayList<DbColumn>();
		list.add(d1);
		list.add(d2);
		list.add(d3);
		list.add(d4);
		for(DbColumn db : list){
			System.out.println(db.getName());
		}
		Roster roster = new Roster();
		roster.setPhoneNum1("18621313686");
		roster.setPhoneNum2("18621313686");
		roster.setPhoneNum3("18621313686");
		roster.setCurrentCallNum("18621313686");
		Roster r = encryptRoster(roster,list);
		System.out.println(r.getPhoneNum1());
		System.out.println(r.getPhoneNum2());
		System.out.println(r.getPhoneNum3());
		System.out.println(r.getCurrentCallNum());
		
	}
	
}
