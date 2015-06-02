<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
    
<%
	String target = request.getRequestURI();

	if (session == null){
		session = request.getSession(true);
		session.setAttribute("target", target);

	} else {
		Object loginCheck = session.getAttribute("login");
		if (loginCheck == null){
			session.setAttribute("target", target);
		}
	}
%>

<!DOCTYPE html>
<html lang="ja">
<head>
	<META CHARSET="UTF-8">
	<title>ChatBoard</title>
</head>
<body>
	<h1>新しいスレッドを立てる</h1>
	<form id="new-thread" method="POST" action="./BBS">
		タイトル：<input type="text" name="thread_title" value="タイトル">
		<input type="submit" name="submit" value="Bring up New Thread">
		<br>
		名前：<input type="text" name="usr_name" value="name">
		<br>
		内容：<textarea name="message" rows="5" cols="64"></textarea>
	</form>
</body>
</html>