<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/themes/icon.css">
<script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/jquery.easyui.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/ajaxfileupload.js"></script>
<title>主页</title>
<style type="text/css">

	body{
		padding-top:10px;
		padding-bottom: 40px;
	}
</style>
<SCRIPT type="text/javascript">
	$(function(){
		
	});
	var url;
	//查询
	function select(){
		$("#dg").datagrid('load',{});
	}
	
	//打开导入数据窗口
	function openimportDataDialog(){
		$("#dlg2").dialog("open").dialog("setTitle","导入学生信息");
		url="${pageContext.request.contextPath}/student/importData.do";
	}
	
	//导入Execl
	function importData(){
		$("#importFileForm").form("submit",{
			url:url,
			onSubmit:function(){
				//return $(this).form("validate");
				closeDialog2();
				$.messager.progress({ 
					       title: '提示', 
					       msg: '文件上传中，请稍候……', 
					       text: '' 
					    });
			},
			success:function(result){
				 $.messager.progress('close');
				 var result=eval('('+result+')');
				 if(result.success){
				 	$.messager.alert("系统提示","导入成功！");
				 }else{
				 	$.messager.alert("系统提示","导入失败！");
				 }
			}
		});
	}
	
	//导出Execl
	function exportData(){	
		$.messager.progress({ 
			       title: '提示', 
			       msg: '文件导出中，请稍候……', 
			       text: '' 
			    });
		$.post(
				"${pageContext.request.contextPath}/student/exportData.do",
				{},
				function(result){
					$.messager.progress('close');
					var filePath = "${pageContext.request.contextPath}/exportExcel/"+result.fileName;
					window.location.href = filePath;
				},
				"json");
	}
	
	
	//打开新增窗口
	function openStudentAddDialog(){
		$("#dlg").dialog("open").dialog("setTitle","添加学生信息");
		url="${pageContext.request.contextPath}/student/save.do";
	}
	
	//打开修改窗口
	function openBlogTypeModifyDialog(){
		var selectedRows=$("#dg").datagrid("getSelections");
		if(selectedRows.length!=1){
			$.messager.alert("系统提示","请选择一个要修改的学生！");
			return;
		}
		var row=selectedRows[0];
		$("#dlg").dialog("open").dialog("setTitle","修改学生信息");
		$("#fm").form("load",row);
		url="${pageContext.request.contextPath}/student/save.do?id="+row.id;
	}
	
	function formatterGender(value){
		return value == '1'?'男':'女';
	}
	
	//保存
	function save(){
		$("#fm").form("submit",{
			url:url,
			onSubmit:function(){
				return $(this).form("validate");
			},
			success:function(result){
				var result=eval('('+result+')');
				if(result.success){
					$.messager.alert("系统提示","保存成功！");
					resetValue();
					$("#dlg").dialog("close");
					$("#dg").datagrid("reload");
				}else{
					$.messager.alert("系统提示","保存失败！");
					return;
				}
			}
		});
	}
	
	//删除
	function deleteStudent(){
		var selectedRows=$("#dg").datagrid("getSelections");
		if(selectedRows.length==0){
			$.messager.alert("系统提示","请选择要删除的数据！");
			return;
		}
		var strIds=[];
		for(var i=0;i<selectedRows.length;i++){
			strIds.push(selectedRows[i].id);
		}
		var ids=strIds.join(",");
		$.messager.confirm("系统提示","您确定要删除这<font color=red>"+selectedRows.length+"</font>条数据吗？",function(r){
			if(r){
				$.post("${pageContext.request.contextPath}/student/delete.do",{ids:ids},function(result){
					if(result.success){
						$.messager.alert("系统提示","数据已成功删除！");							
						$("#dg").datagrid("reload");
					}else{
						$.messager.alert("系统提示","数据删除失败！");
					}
				},"json");
			}
		});
	}
	
	//点击关闭
	function closeDialog(){
		$("#dlg").dialog("close");
		resetValue();
	}
	//点击关闭上传窗口
	function closeDialog2(){
		$("#dlg2").dialog("close");
		resetValue();
	}
	function resetValue(){
		$("#name").val("");
		$("#gender").val("");
		$("#age").val("");
		$("#address").val("");
	}
</SCRIPT>
</head>
<body>
	<table id="dg" title="学生信息管理" class="easyui-datagrid" fit="true"
		fitColumns="true" pagination="true" rownumbers="true"
		url="${pageContent.request.contentPath}/student/getList.do"
		toolbar="#tb">
		<thead>
			<th field="cb" checkbox="true" align="center"></th>
			<th field="name" width="120" align="center" >姓名</th>
			<th field="gender" width="120" align="center" formatter="formatterGender" >性别</th>
			<th field="age" width="120" align="center" >年龄</th>
			<th field="address" width="120" align="center" >地址</th>
		</thead>
	</table>
	<div id="tb">
		<div>
			<a href="javascript:select()" class="easyui-linkbutton" iconCls="icon-search" plain="true">查询</a>
			<a href="javascript:openStudentAddDialog()" class="easyui-linkbutton" iconCls="icon-add" plain="true">新增</a>
			<a href="javascript:openBlogTypeModifyDialog()" class="easyui-linkbutton" iconCls="icon-edit" plain="true">修改</a>
			<a href="javascript:deleteStudent()" class="easyui-linkbutton" iconCls="icon-remove" plain="true">删除</a>
			<a href="javascript:openimportDataDialog()" class="easyui-linkbutton" iconCls="icon-remove" plain="true">导入数据</a>
			<a href="javascript:exportData()" class="easyui-linkbutton" iconCls="icon-remove" plain="true">导出数据</a>
		</div>
	</div>
	

	<div id="dlg" class="easyui-dialog" style="width:650px;height:350px;padding:10px 20px" closed="true" buttons="#dlg-buttons">
		<form id="fm"  method="post" enctype="multipart/form-data">
			<table cellpadding = "5">
			<tr>
				<td>姓名：</td>
				<td>
					<input type="hidden" id="id" name="id" value="${student.id}"/>
					<input class="easyui-textbox" type="text" name="name" ></input>
				</td>
			</tr>
			<tr>
				<td>性别：</td>
				<td>
					<input id="gender" name="gender" class="easyui-combobox" value='1' style="width:100%"
				 data-options="
					data:[{id:'1',text:'男'},{id:'0',text:'女'}],
					valueField:'id',
					textField:'text',
					panelHeight:'auto',
					editable:false
				">
				</td>
			</tr>
			<tr>
				<td>年龄：</td>
				<td>
					<input id="age" name="age" class="easyui-numberspinner" value="18" data-options="increment:1,min:1,max:120,editable:false" style="width:120px;"></input>
				</td>
			</tr>
			<tr>
				<td>地址：</td>
				<td>
					<textarea id="address" name="address"  rows="4" cols="32" ></textarea>
				</td>
			</tr>
		</table>
		</form>
	</div>
	
	<div id="dlg2" class="easyui-dialog" style="width:450px;height:150px;padding:20px 20px" closed="true" buttons="#dlg2-buttons">
		
		<form id="importFileForm" method="post" enctype="multipart/form-data" >
	    	<input type="file" class="easyui-filebox" id="excelFile" name="excelFile" required="true"  style="width:100%">
	    </form>
		
	</div>

	<div id="dlg-buttons">
		<a href="javascript:save()" class="easyui-linkbutton" iconCls="icon-ok" >保存</a>
		<a href="javascript:closeDialog()" class="easyui-linkbutton" iconCls="icon-cancel" >关闭</a>
	</div>
	<div id="dlg2-buttons">
		<a href="javascript:importData()" class="easyui-linkbutton" iconCls="icon-ok" >上传</a>
		<a href="javascript:closeDialog2()" class="easyui-linkbutton" iconCls="icon-cancel" >关闭</a>
	</div>
	
</body>
</html>