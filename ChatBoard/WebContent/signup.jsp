<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
    
<%
	String target = request.getRequestURI();

	if (session == null) {
		session = request.getSession(true);
		session.setAttribute("target", target);

	} else {
		Object loginCheck = session.getAttribute("login");
		if (loginCheck == null) {
			session.setAttribute("target", target);
		} else if (((String) session.getAttribute("login")).equals("true")) {
			// ログインしているのであれば何れかのスレッドを開く
			response.sendRedirect("./read.jsp?id=th000001&res=1");
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
	<h1>ユーザー登録</h1>
	<form id="sign-up" method="POST" action="./SignUp">
		ユーザー名:<input type="text" name="usr_name">
		<br>
		パスワード:<input type="password" name="pass">
		<input type="submit" name="submit" value="登録">
	</form>
</body>
</html>