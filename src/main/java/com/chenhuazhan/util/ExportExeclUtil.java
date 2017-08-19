package com.chenhuazhan.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;

import net.sf.jxls.transformer.XLSTransformer;

/**
 * 导出Execl工具类
 * @author chz
 *
 */
public class ExportExeclUtil {
	
	private static final String DEFAULT_SHEET_NAME = "sheet";// 默认表单名称
    private static final int MAX_ROW_PER_SHEET = 50000;// 每个sheet最大记录数(2003格式的excel文件

	/**
     * 下载excel文件（带单个表头）
     * @param filename 文件名称
     * @param templateFilePath 模板路径
     * @param sheet_name EXCEL文件的sheet名称
     * @param header 表头
     * @param headerKey 表头关键字，对应模板中的代码
     * @param dataList 要输入EXCEL的数据
     * @param dataKey 数据关键字，对应模板中的代码
     * @param response HttpServletResponse对象
     * @param request HttpServletRequest对象
     */
    public static void downloadExcel(String filename,String templateFilePath,
             String sheet_name, String header, String headerKey, List dataList,
             String dataKey,HttpServletResponse response, HttpServletRequest request){
         
         InputStream templateInputStream = null;
         OutputStream outputStream = null;
         try {
             //读取模板文件
             templateInputStream = new FileInputStream(templateFilePath);
             String filePath = request.getSession().getServletContext().getRealPath("")+"\\exportExcel\\"+filename;
             File file = new File(filePath);
             if (!file.getParentFile().exists()) {
                 file.getParentFile().mkdirs();
             }
             if (!file.exists()) {
            	 file.createNewFile();
             }
            outputStream = new FileOutputStream(file);
             
            ExportExeclUtil.generateSingleHeaderExcelByTemplate(outputStream, templateInputStream, sheet_name,
                      header, headerKey, dataList, dataKey, ExportExeclUtil.MAX_ROW_PER_SHEET);
             
         } catch (UnsupportedEncodingException e) {
             e.printStackTrace();
         } catch (IOException e) {
             e.printStackTrace();
         } catch (Exception e) {
             e.printStackTrace();
         }finally {
             if (templateInputStream != null) {
                 try {
                      templateInputStream.close();
                      templateInputStream = null;
                 } catch (IOException e) {
                      e.printStackTrace();
                 }
             }
             if (outputStream != null) {
                 try {
                      outputStream.flush();
                      outputStream.close();
                      outputStream = null;
                 } catch (IOException e) {
                      e.printStackTrace();
                 }
             }
         }
    }
    
    
    /**
     * 根据模板生成excel文件(单个表头)
     *
     * @param destOutputStream
     *            目标文件流
     * @param templateInputStream
     *            模板文件流
     * @param sheet_name
     *             excel文件中sheet的名称
     * @param header
     *            表头（可为空）
     * @param headerKey
     *            表头关键字（可为空）
     * @param dataList
     *            数据
     * @param dataKey
     *            数据关键字
     * @param maxRowPerSheet
     *            每个sheet的最大记录数
     * @throws Exception
     */
    public static Workbook generateSingleHeaderExcelByTemplate(OutputStream destOutputStream,
             InputStream templateInputStream,String sheet_name,
             String header, String headerKey, List dataList, String dataKey,
             int maxRowPerSheet) throws Exception {
         Workbook workbook = null;
         List<List> splitData = null;
         
         Map<String, String> beanMap = new HashMap<String, String>();//用于存放表头
         List<String> sheetNames = new ArrayList<String>();
         
         //判断 excel文件中sheet的名称是否为空，若为空，则使用默认值
         if(sheet_name == null || "".equals(sheet_name))
         {
             sheet_name = DEFAULT_SHEET_NAME;
         }
         
         //判断数据记录数是否超出设置的sheet的最大记录数，若超出，则对数据进行拆分
         if (dataList.size() > maxRowPerSheet) {
             splitData = ExportExeclUtil.splitList(dataList, maxRowPerSheet);
             sheetNames = new ArrayList<String>(splitData.size());
             for (int i = 0; i < splitData.size(); ++i) {
                 sheetNames.add(sheet_name + i);
             }
         } else {
             splitData = new ArrayList<List>();
             sheetNames.add(sheet_name);
             splitData.add(dataList);
         }
         
         //判断用户是否动态设置表头
         if (header != null) {
             beanMap.put(headerKey, header);
         }
         
         //生成文件
         XLSTransformer transformer = new XLSTransformer();
         workbook = transformer.transformMultipleSheetsList(templateInputStream,
                 splitData, sheetNames, dataKey, beanMap, 0);
         workbook.write(destOutputStream);
         
         return workbook;
    }


    private static List<List> splitList(List data, int maxRowPerSheet) {
        List<List> splitData = new ArrayList<List>();
        List sdata = null;
        for (int i = 0; i < data.size(); ++i) {
            if (0 == i % maxRowPerSheet) {
                if (null != sdata) {
                     splitData.add(sdata);
                }
                sdata = new ArrayList(maxRowPerSheet);
            }
            sdata.add(data.get(i));
        }
        if (0 != maxRowPerSheet % data.size()) {
            splitData.add(sdata);
        }
        return splitData;
   }

}
