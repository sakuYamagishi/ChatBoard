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
	<h1>ログイン画面</h1>
	<form id="login" method="POST" action="./Login">
		ユーザー名:<input type="text" name="usr_name">
		<br>
		パスワード:<input type="password" name="pass">
		<input type="submit" name="submit" value="ログイン">
	</form>
	<a href="./signup.jsp">ユーザー登録</a>
	<br>
    <a href="./TwitterLogin"><img src="./images/Sign-in-with-Twitter-darker.png" alt="Twitterでログインする" /></a>
    <br>
    <a href="./FacebookLogin">Facebookでログインする</a>
</body>
</html>