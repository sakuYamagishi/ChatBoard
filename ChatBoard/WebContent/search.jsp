<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
    
<%@ page import="java.sql.*"%>
<%@ page import="com.mysql.jdbc.Connection"%>
<%@ page import="com.mysql.jdbc.Statement"%>

<%@ page import="saku.util.*"%>

<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>

<%
	// ログインセッションを判別するブロック
	
	String target = request.getRequestURI();

	if (session == null) {
		session = request.getSession(true);
		session.setAttribute("target", target);

		response.sendRedirect("./index.jsp");
	} else {
		Object loginCheck = session.getAttribute("login");
		if (loginCheck == null) {
			session.setAttribute("target", target);
			response.sendRedirect("./index.jsp");
		}
	}
%>

<%
	request.setCharacterEncoding("utf-8");

	List<ChatResponse> resultList = new ArrayList<ChatResponse>();

	Connection con = null;
	Statement st = null;


	try {
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		
		// 外部設定ファイルにすべき?
		// ユーザーの権限毎の役割分けも必要
		String url = "jdbc:mysql://localhost/chatboard";
		String user = "root";
		String pass = "root";
		
		con = (Connection) DriverManager.getConnection(url, user, pass);
		st = (Statement) con.createStatement();

		String keyword = request.getParameter("key_word");
		String sql = "select * from " + request.getParameter("thread_id") + " where article like '%" + keyword + "%'";
		ResultSet rs = st.executeQuery(sql);
	
		while (rs.next()) {
			resultList.add(new ChatResponse(rs.getInt("number"),rs.getString("name"), rs.getString("res_datetime"), rs.getString("article")));
		}
		
		rs.close();
	} catch (ClassNotFoundException e) {
		e.printStackTrace();
	} catch (InstantiationException e) {
		e.printStackTrace();
	} catch (IllegalAccessException e) {
		e.printStackTrace();
	} catch (SQLException e) {
		e.printStackTrace();
	} finally {
		try {
			if (st != null) {
				st.close();
			}
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	String beforePage = "./read.jsp?id=" + request.getParameter("thread_id") + "&res=" + request.getParameter("res_number");

%>
<!DOCTYPE html>
<html lang="ja">
<head>
	<META CHARSET="UTF-8">
	<title>ChatBoard</title>
</head>

<body>
	<h1>検索結果：<%= resultList.size() %>件</h1>
	<br>
	<p><a href="<%= beforePage %>">元のページへ戻る</a></p>
	<%
		for (ChatResponse cr : resultList) {
			out.print("<dt>");
			out.print(cr.getHeader());
			out.print("</dt>");
			out.print("<dd>");
			out.print(cr.article);
			out.print("</dd>");
		}
	%>
</body>
</html>