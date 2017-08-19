package com.chenhuazhan.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.chenhuazhan.dao.StudentDao;
import com.chenhuazhan.entity.Student;
import com.chenhuazhan.service.StudentService;

@Service
public class StudentServiceImpl implements StudentService{

	@Resource
	private StudentDao studentDao;
	
	public List<Student> getList(){
		return studentDao.getList();
	}
	
	public Long getTotal() {
		return studentDao.getTotal();
	}

	public Integer add(Student student) {
		return studentDao.add(student);
	}

	public Integer update(Student student) {
		return studentDao.update(student);
	}

	public Integer delete(Integer ids) {
		return studentDao.delete(ids);
	}
	
	//导入execl文件
	public Integer importData(Student student){
		return studentDao.importData(student);
	}

	public List<?> exportData() {
		return studentDao.exportData();
	}

	
}
