package com.chenhuazhan.dao;

import java.util.List;

import com.chenhuazhan.entity.Student;

public interface StudentDao {

	//查询
	List<Student> getList();
	
	//总记录数
	Long getTotal();
	
	//新增
	Integer add(Student student);
	
	//修改
	Integer update(Student student);
	
	//删除
	Integer delete(Integer ids);
	
	//导入execl文件
	Integer importData(Student student);
	
	//导出Execl文件
	List<?> exportData();
}
