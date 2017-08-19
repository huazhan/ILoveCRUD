package com.chenhuazhan.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.chenhuazhan.entity.Student;
import com.chenhuazhan.service.StudentService;
import com.chenhuazhan.util.DateUtil;
import com.chenhuazhan.util.ExportExeclUtil;
import com.chenhuazhan.util.FileUploadUtil;
import com.chenhuazhan.util.ImportExeclUtil;
import com.chenhuazhan.util.ResponseUtil;
import com.github.pagehelper.PageInfo;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
@RequestMapping("/student")
public class StudentController {

	@Resource
	private StudentService studentService;
	
	final Logger logger = LoggerFactory.getLogger(StudentController.class);

	@RequestMapping("/getList")
	public String getList(@RequestParam(value="page",required=false)String page,
			@RequestParam(value="rows",required=false)String rows,
			HttpServletResponse response) throws Exception{
		logger.debug("执行了StudentController方法...");
		Map<String,Object> map = new HashMap<String,Object>();
		List<Student> list = studentService.getList();
		Long total = studentService.getTotal();
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.registerJsonValueProcessor(java.util.Date.class, new DateJsonValueProcessor("yyyy-MM-dd"));
		JSONArray jsonArray = JSONArray.fromObject(list,jsonConfig);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("rows", jsonArray);
		jsonObject.put("total", total);
		ResponseUtil.write(response, jsonObject);
		return null;
	}
	
	@RequestMapping("/save")
	public String save(Student student,HttpServletResponse response) throws Exception{
		System.out.println(">>>>>>>>>>>>>>"+student);
		int resultTotal = 0;
		if (student.getId() == null) { //新增学生
			resultTotal = studentService.add(student);
		}else{ //修改学生
			resultTotal = studentService.update(student);
		}
		
		JSONObject result = new JSONObject();
		if (resultTotal > 0 ) {
			result.put("success", true);
		}else{
			result.put("success", false);
		}
		ResponseUtil.write(response, result);
		return null;
	}
	
	//删除
	@RequestMapping("/delete")
	public String delete(@RequestParam(value="ids",required=false)String ids,HttpServletResponse response) throws Exception{
		JSONObject jsonObject = new JSONObject();
		String[] idsStr = ids.split(",");
		for (int i = 0; i < idsStr.length; i++) {
			studentService.delete(Integer.parseInt(idsStr[i]));
		}
		jsonObject.put("success", true);
		ResponseUtil.write(response, jsonObject);
		return null;
	}
	
	//导入数据
	@RequestMapping("/importData")
	public String importData(@RequestParam("excelFile") MultipartFile execlFile,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		
		JSONObject jsonObject = new JSONObject();
		if (execlFile.isEmpty()) {
			throw new Exception("文件不存在！");
		}
		String projectPath = request.getServletContext().getRealPath("");
		String fileName = DateUtil.getCurrentDateStr()+"."+execlFile.getOriginalFilename().split("\\.")[1];
		String filePath = projectPath+"/WEB-INF/execl/"+fileName;
		File file = new File(filePath);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		if (!file.exists()) {
			file.createNewFile();
		}
		execlFile.transferTo(file);
		InputStream is = new FileInputStream(filePath);
		List<List<Object>> listOb = null;
		
		listOb = new ImportExeclUtil().getListByExecl(is, fileName);
		is.close();
		for (int i = 0; i < listOb.size(); i++) {
			List<Object> list = listOb.get(i);
			Student student = new Student();
			student.setName(list.get(0).toString());
			student.setGender(Integer.parseInt(list.get(1).toString()));
			student.setAge(Integer.parseInt(list.get(2).toString()));
			student.setAddress(list.get(3).toString());
			studentService.importData(student);
			jsonObject.put("success", true);
		}
		
		ResponseUtil.write(response, jsonObject);
		return null;
	}
	
	//导出Execl
	@RequestMapping("/exportData")
	public String exportData(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		
		List<?> list = studentService.exportData();
		
		String projectPath = request.getServletContext().getRealPath("");
		String templateUrl = projectPath+"/WEB-INF/execlTemplate/测试导出.xls";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
        String fileName = "测试导出"+sdf.format(new Date())+".xls";
        ExportExeclUtil.downloadExcel(fileName, templateUrl, null, null,
                 null, list, "dataList", response, request); 
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        jsonObject.put("fileName", fileName);
        ResponseUtil.write(response, jsonObject);
		return null;
	}
	
	
}
