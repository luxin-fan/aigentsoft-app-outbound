package com.outbound.common;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

public class XlsUtil {

	public static Map<String,List<Object>> map = new HashMap<String,List<Object>>();
	
	public static void exportXls(HttpServletResponse response) throws IOException {
//		HSSFWorkbook wb = new HSSFWorkbook();
//		HSSFSheet sheet = wb.createSheet("sheet0");
//		sheet.setDefaultColumnWidth(20);
//
//		List<String> headers = new ArrayList<String>();
//		headers.add("标题1");
//		headers.add("标题2");
//
//		createHeader(wb, sheet, headers);
//
//		List<String> contents = new ArrayList<String>();
//		contents.add("231");
//		contents.add("2322");
//
//		createRowValue(wb, sheet, 1, contents);
//		createRowValue(wb, sheet, 2, contents);
//
//		OutputStream output = response.getOutputStream();
//		response.reset();
//		response.setHeader("Content-disposition", "attachment; filename=details.xls");
//		response.setContentType("application/msexcel");
//		wb.write(output);
//		output.close();
//
//		wb.close();
	}
	
	public static void exportXls2(HttpServletResponse response,List<Object> object , List<String> title,List<String> condition,String table_title,String excelName) throws Exception {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("sheet0");
		sheet.setDefaultColumnWidth(20);
		
		HSSFCellStyle cellStyle = wb.createCellStyle();
		HSSFFont f = wb.createFont();
		f.setFontHeightInPoints((short) 9);
		f.setFontName("微软雅黑");
		cellStyle.setFont(f);
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		
		
		List<String> headers = new ArrayList<String>();
		List<String> contents = new ArrayList<String>();
		headers = title;
		
		createHeader(wb, sheet, headers);
		
		int i = 1;
		
		Map<String,Object> recordMap = new HashMap<String,Object>();
		for(Object record: object){
			recordMap = BeanUtils.describe(record);
			for(String key:condition){
				contents.add(getTransferStr(key,recordMap,table_title));
			}
			createRowValue(wb, sheet, i, contents,cellStyle);
			contents=contents = new ArrayList<String>();
			i = i+1;
		}
		OutputStream output = response.getOutputStream();
		response.reset();
		response.setHeader("Content-disposition", "attachment; filename=" + excelName +".xls");
		response.setContentType("application/msexcel");

		wb.write(output);
		output.close();

		wb.close();
	}
	
	private static void createHeader(HSSFWorkbook wb, HSSFSheet sheet, List<String> headers) {
		HSSFRow row = sheet.createRow(0);
		row.setHeightInPoints(20);

		HSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setFillForegroundColor(HSSFColorPredefined.TAN.getIndex());
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		HSSFFont f = wb.createFont();
		f.setFontHeightInPoints((short) 9);
		f.setFontName("微软雅黑");
		cellStyle.setFont(f);
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

		for (int i = 0; i < headers.size(); i++) {
			HSSFCell cell = row.createCell(i);
			cell.setCellValue(headers.get(i));
			cell.setCellStyle(cellStyle);
		}
	}

	private static void createRowValue(HSSFWorkbook wb, HSSFSheet sheet, int rowline, List<String> contents,HSSFCellStyle cellStyle) {
		HSSFRow row = sheet.createRow(rowline);
		row.setHeightInPoints(20);

		for (int i = 0; i < contents.size(); i++) {
			HSSFCell cell = row.createCell(i);
			cell.setCellValue(contents.get(i));
			cell.setCellStyle(cellStyle);
		}
	}
	private static String getTransferStr(String key,Map<String,Object> recordMap,String table_title){
		DecimalFormat decimalFormat=new DecimalFormat(".00");
		String status= "";
		
		if("completeRosterRate".equals(key)){
			int answerCallNum = Integer.parseInt((String) recordMap.get("answerCallNum"));
			int rosterNum = Integer.parseInt((String) recordMap.get("rosterNum"));
			if(answerCallNum == 0 || rosterNum == 0)
				return "0.00%";
			if(answerCallNum >= rosterNum)
				return "100.00%";
			status = decimalFormat.format(answerCallNum * 100.0f / rosterNum)+"%" ;
			return status;
		}else if("answerRate".equals(key)){
			int answerCallNum = Integer.parseInt((String) recordMap.get("answerCallNum"));
			int outCallNum = Integer.parseInt((String) recordMap.get("outCallNum"));
			if(answerCallNum == 0 || outCallNum == 0)
				return "0.00%";
			if(answerCallNum >= outCallNum)
				return "100.00%";
			status = decimalFormat.format(answerCallNum  * 100.0f / outCallNum )+"%" ;
			return status;
		}
		if("completeRosterRateBatch".equals(key)){
			int answerCallNum = Integer.parseInt((String) recordMap.get("answerCallNum"));
			int rosterNum = Integer.parseInt((String) recordMap.get("roterNum"));
			if(answerCallNum == 0 || rosterNum == 0)
				return "0.00%";
			System.out.println(answerCallNum +" "+rosterNum);
			if(answerCallNum >= rosterNum)
				return "100.00%";
			status = decimalFormat.format(answerCallNum * 100.0f / rosterNum)+"%" ;
			return status;
		}else if("answerRateBatch".equals(key)){
			int answerCallNum = Integer.parseInt((String) recordMap.get("answerCallNum"));
			int outCallNum = Integer.parseInt((String) recordMap.get("outCallNum"));
			if(answerCallNum == 0 || outCallNum == 0)
				return "0.00%";
			if(answerCallNum >= outCallNum)
				return "100.00%";
			status = decimalFormat.format(answerCallNum  * 100.0f / outCallNum )+"%" ;
			return status;
		}
		
		if(!recordMap.containsKey(key)){
			return key;
		}
		String str = (String) recordMap.get(key);
		String [] agent_status = {"就绪","小休","后处理","登录","登出","呼入"};
		String [] callType = {"内部呼叫","外呼","呼入"};
		String [] batch_status = {"未开始","进行中","已完成"};
		String [] activity_status = {"待执行","进行中","暂停","完成"};
		String [] roster_status = {"未外呼","外呼中","外呼完成"};
		if("agent_status".equals(key)){
			int num = Integer.parseInt(str);
			if(0<=num && num<=5){
				status = agent_status[num];
			}else status = str + "";
		}else if("callType".equals(key)){
			int num = Integer.parseInt(str);
			if(0<=num && num<=2){
				status = callType[num];
			}else status = "其他";
		}else if("batch".equals(table_title)){//批次状态 {"0-未开始","1-进行中","2-已完成"};
			if("status".equals(key)){
				int num = Integer.parseInt(str);
				if(0<=num && num<=2){
					status = batch_status[num];
				}else status = str + "";
			}else status = str + "";
		}else if("activity".equals(table_title)){//活动状态 {"0-待执行","1-进行中","2-暂停","3-完成"};
			if("status".equals(key)){
				int num = Integer.parseInt(str);
				if(0<=num && num<=3){
					status = activity_status[num];
				}else status = str + "";
			}else status = str + "";
		}else if("roster".equals(table_title)){//活动明细状态 {"0-未外呼","1-外呼中","2-外呼完成"};
			if("status".equals(key)){
				int num = Integer.parseInt(str);
				if(0<=num && num<=2){
					status = roster_status[num];
				}else status = str + "";
			}else status = str + "";
		}else if(str == null){
			status = "";
		}else{
			status = str + "";
		}
		if("null".equals(status)){
			status = "";
		}
		return status;
	}
	
	public static String getIsoStr(String str){
		String resault = "";
		try {
			resault = new String(str.getBytes(),"iso-8859-1");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resault;
	}
	
}
