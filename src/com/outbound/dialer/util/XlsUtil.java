package com.outbound.dialer.util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

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
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("sheet0");
		sheet.setDefaultColumnWidth(20);

		List<String> headers = new ArrayList<String>();
		headers.add("标题1");
		headers.add("标题2");

		createHeader(wb, sheet, headers);

		List<String> contents = new ArrayList<String>();
		contents.add("231");
		contents.add("2322");
		
		HSSFCellStyle cellStyle = wb.createCellStyle();
		HSSFFont f = wb.createFont();
		f.setFontHeightInPoints((short) 9);
		f.setFontName("微软雅黑");
		cellStyle.setFont(f);
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

		createRowValue(wb, sheet, 1, contents, cellStyle);
		createRowValue(wb, sheet, 2, contents, cellStyle);

		OutputStream output = response.getOutputStream();
		response.reset();
		response.setHeader("Content-disposition", "attachment; filename=details.xls");
		response.setContentType("application/msexcel");
		wb.write(output);
		output.close();

		wb.close();
	}

	public static void exportDNSXls(HttpServletResponse response) throws IOException {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("sheet0");
		sheet.setDefaultColumnWidth(20);

		List<String> headers = new ArrayList<String>();
		headers.add("禁呼号码");

		createHeader(wb, sheet, headers);
		
		OutputStream output = response.getOutputStream();
		response.reset();
		response.setHeader("Content-disposition", "attachment; filename=DNC Template.xls");
		response.setContentType("application/msexcel");
		wb.write(output);
		output.close();

		wb.close();
	}
	
	public static void exportRosterInfoXls(List<String> headers, List<List<String>> contents, String filePath) throws IOException {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("sheet0");
		sheet.setDefaultColumnWidth(20);

		createHeader(wb, sheet, headers);
		HSSFCellStyle cellStyle = wb.createCellStyle();
		HSSFFont f = wb.createFont();
		f.setFontHeightInPoints((short) 9);
		f.setFontName("微软雅黑");
		cellStyle.setFont(f);
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		
		for(int i=0; i< contents.size(); i++){
			List<String> rowInfo = contents.get(i);
			createRowValue(wb, sheet, i+1, rowInfo, cellStyle);
		}
		
		wb.write(new File(filePath));
		wb.close();
		
	}

	public static void exportRosterXls(HttpServletResponse response, List<String> headers, String filename  ) throws IOException {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("sheet0");
		sheet.setDefaultColumnWidth(20);

		createHeader(wb, sheet, headers);

		OutputStream output = response.getOutputStream();
		response.reset();
		response.setHeader("Content-disposition", "attachment; filename="+filename +".xls");
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
	
	public static void main(String[] args) {
		File file = new File("http://172.16.2.229/aaa.txt");
		System.out.println(file.length());
	}
}
