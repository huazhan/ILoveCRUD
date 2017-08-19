package com.chenhuazhan.util;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 导入execl工具类
 * @author chz
 *
 */
public class ImportExeclUtil {

	private final static String EXECL2003 = ".xls"; //2003-版本的execl
	private final static String EXECL2007 = ".xlsx"; //2007+版本的execl
	
	/**
	 * 通过IO流获取execl中的数据，组成List<List<Object>>对象
	 * @param is
	 * @param fileName
	 * @return
	 * @throws Exception 
	 */
	public List<List<Object>> getListByExecl(InputStream is,String fileName) throws Exception{
		List<List<Object>> list = null;
		Workbook wb = this.getWorkbook(is, fileName);
		if (wb == null) {
			throw new Exception("Execl为空！");
		}
		Sheet sheet = null; //execl的sheet页
		Row row = null;	//sheet页的行
		Cell cell = null; //行上的单元格
		String str = "";
		
		list = new ArrayList<List<Object>>();
		//遍历Execl中的Sheet
		for (int i = 0; i < wb.getNumberOfSheets(); i++) {
			sheet = wb.getSheetAt(i);
			if (sheet == null) {
				continue;
			}
			//遍历当前sheet中的所有的合并区域
	         for (int j = sheet.getNumMergedRegions() - 1; j >= 0; j--) {
	             CellRangeAddress region = sheet.getMergedRegion(j);
	             Row firstRow = sheet.getRow(region.getFirstRow());
	             Cell firstCellOfFirstRow = firstRow.getCell(region.getFirstColumn());
	             //如果第一个单元格的是字符串
	             if (firstCellOfFirstRow.getCellType() == Cell.CELL_TYPE_STRING) {
	                 str = firstCellOfFirstRow.getStringCellValue();
	             }
	             //设置第一行的值为，拆分后的每一行的值
	             for (Row row2 : sheet) {
	                 for (Cell cell2 : row2) {
	                      if (region.isInRange(cell2.getRowIndex(), cell2.getColumnIndex())){
	                          cell2.setCellType(Cell.CELL_TYPE_STRING);
	                          cell2.setCellValue(str);
	                      }
	                 }
	             }
	         }
	         //遍历当前sheet中的所有的行
	         for (Row row3 : sheet) {
	        	//遍历所有的列
	        	//row.getRowNum()>0表示有两行记录以上，下标从0开始。因为第一行为表头，第二行开始才是需要的数据
				if(row3.getRowNum()>0){
					List<Object> li = new ArrayList<Object>();
					for (Cell cell3 : row3) {
						li.add(this.getCellValue(cell3));
					} 
					list.add(li);
				}
	         }
		}
		wb.close();
		return list;
	}
	
	/**
	 * 根据上传文件的后缀，自适应上传文件的版本
	 * @param is
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public Workbook getWorkbook(InputStream is,String fileName) throws Exception{
		Workbook wb = null;
		String fileType = fileName.substring(fileName.lastIndexOf("."));
		if (EXECL2003.equals(fileType)) {
			wb = new HSSFWorkbook(is);
		}else if(EXECL2007.equals(fileType)){
			wb = new XSSFWorkbook(is);
		}else{
			throw new Exception("解析的文件格式有误！");
		}
		return wb;
	}
	
	/**
	 * 对单元格中的数值进行格式化
	 * @param cell
	 * @return
	 */
	public Object getCellValue(Cell cell){
		Object value = null;
		DecimalFormat df = new  DecimalFormat("0"); //格式化number String字符
		DecimalFormat df2 = new  DecimalFormat("0.00"); //格式化数字字符
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //日期格式化
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING:
			value = cell.getRichStringCellValue().getString();
			break;
		case Cell.CELL_TYPE_NUMERIC:
			if ("General".equals(cell.getCellStyle().getDataFormatString())) {
				value = df.format(cell.getNumericCellValue());
			}else if("m/d/yy".equals(cell.getCellStyle().getDataFormatString())){
				value = sdf.format(cell.getDateCellValue());
			}else{
				value = df2.format(cell.getNumericCellValue());
			}
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			value = cell.getBooleanCellValue();
			break;
		case Cell.CELL_TYPE_BLANK:
			value = "";
			break;
		default:
			break;
		}
		
		return value;
	}
}
