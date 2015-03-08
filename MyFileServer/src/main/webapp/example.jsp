<%@ page language="java" contentType="text/html; charset=utf8"
    pageEncoding="utf8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf8">
<title>Insert title here</title>
</head>
<body>
	<form action="/upload" enctype="multipart/form-data" method="post">
		<input type="file" name="file1"/><br>
		<input type="file" name="file2"/><br>
		<input type="file" name="file3"/><br>
		<input type="hidden" name="userId" value="1"/>
		<input type="hidden" name="userType" value="A"/>
		<input type="submit"  value="上传"/>
	</form>
</body>
</html>