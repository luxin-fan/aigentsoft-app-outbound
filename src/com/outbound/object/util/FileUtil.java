package com.outbound.object.util;

import java.io.File;
import java.text.DecimalFormat;

public class FileUtil {
	
	public static String[] getConfFiles(){
		String foldpath ="conf/fs_configs";
		File file = new File(foldpath);
		String[] filelist = file.list();
		return filelist;
 	}
	
	public static String[] getConfFiles(String foldPath){
		File file = new File(foldPath);
		String[] filelist = file.list();
		return filelist;
 	}
	
	public static void deleteFile(String filePath){
		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
		}
 	}
	
	public static String formetFileSize(long fileS) {// 转换文件大小
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}
}
