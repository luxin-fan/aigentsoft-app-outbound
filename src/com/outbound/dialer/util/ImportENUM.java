package com.outbound.dialer.util;


/**
 * 导入Enum信息
 * @author duanlsh
 *
 */
public enum ImportENUM {

	
	DB("DB"), FTP("FTP"), FILE("FILE");
	
	private String type;
	
	private ImportENUM(String type){
		this.type = type;
	}
	
	
	public static ImportENUM getImportEnum(String key){
		for (ImportENUM importEnum : values()){
			if (key.toUpperCase().equals(importEnum.type)){
				return importEnum;
			}
		}
		return null;
	}
}
