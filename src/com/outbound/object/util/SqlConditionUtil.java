package com.outbound.object.util;

public class SqlConditionUtil {
	public static boolean isAppendCondition(Object str){
		if(str instanceof String){
			if(str!=null&&!"".equals(str)&&!"-1".equals(str)){
				return true;
			}
		}else if(str instanceof Integer){
			if(str!=null){
				if((int)str >= 0){
					return true;
				}
			}
		}
		return false;
	}
}
