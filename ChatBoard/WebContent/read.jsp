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

	Connection con = null;
	Statement st = null;

	List<LinkSet> threadList = new ArrayList<LinkSet>();
	List<ChatResponse> resList = new ArrayList<ChatResponse>();
	
	CheckString cs = new CheckString();

	// 表示するレスの開始位置
	int startResNumber = cs.isResponseNumber(request.getParameter("res"));
	
	// ページャーで表示する件数
	int maxMoveResNumber = 5;
	
	// 表示するスレッド（テーブル名）
	String reqThread = cs.isThreadID(request.getParameter("id"));
	
	// スレッドのレス数
	int maxResNumber = 1;

	// クッキーからログイン中のユーザー名を取得
	String loginUserName = "";
	Cookie[] cookies = request.getCookies();
	for (Cookie c : cookies) {
		if (c.getName().equals("usr_name")) {
			loginUserName = c.getValue();
		}
	}
	try {
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		
		// 外部設定ファイルにすべき?
		// ユーザーの権限毎の役割分けも必要
		String url = "jdbc:mysql://localhost/chatboard";
		String user = "root";
		String pass = "root";
		
		con = (Connection) DriverManager.getConnection(url, user, pass);
		st = (Statement) con.createStatement();

		// 左サイドに表示するスレッド一覧
		ResultSet rs = st.executeQuery("select * from thread_list");
		while (rs.next()) {
			threadList.add(new LinkSet(rs.getString("thread"), rs.getString("title")));
		}

		// スレッドの開始位置から表示件数分だけ取得
		rs = st.executeQuery("select * from " + reqThread + " where " + startResNumber + "<=number and number<" + (maxMoveResNumber + startResNumber));
		while (rs.next()) {
			ChatResponse res = new ChatResponse(rs.getInt("number"),rs.getString("name"), rs.getString("res_datetime"), rs.getString("article"));
			resList.add(res);
		}

		// スレッドのレス数取得
		rs = st.executeQuery("select count(*) as num from " + reqThread);
		rs.next();
		maxResNumber = rs.getInt("num");
		
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

%>
<!DOCTYPE html>
<html lang="ja">
<head>
	<META CHARSET="UTF-8">
	<title>ChatBoard</title>
	<link rel="stylesheet" type="text/css" href="style.css">
</head>

<body>
	
	<div id="global-nav">
		<ul>
		<li>ログインしているユーザー：<%= loginUserName %></li>
		<li><a href="./Logout">[ログアウトする]</a></li>
		<li><a href="./bbs.jsp" style="text-align:center;background:#449977">新しいスレッドを立てる</a></li>
		<li>
		<form id="search" method="POST" action="./search.jsp">
			<input type="hidden" name="thread_id" value="<%= reqThread %>">
			<input type="hidden" name="res_number" value="<%= startResNumber %>">
			<input type="text" name="key_word" value="">
			<input type="submit" name="submit" value="検索">
		</form>
		</li>
		
		</ul>
	</div>	

	<div id="main-container">
		<div id="left-side">
			<ul id="thread-list">
			<%
				for (LinkSet ls : threadList) {
					out.print("<li class=\"thread-item\"><a href=\"read.jsp?id=");
					out.print(ls.link);
					out.print("&res=1\">");
					out.print(ls.title);
					out.print("</a></li>");
				}
			%>
			<%-- <li><a href="read.jsp?id=th123456&res=001></a></li> --%>
			</ul>
		</div>
		
		<div id="center">
			<dl class="thread">
			<%
				for (ChatResponse cr : resList) {
					out.print("<dt>");
					out.print(cr.getHeader());
					out.print("</dt>");
					out.print("<dd>");
					out.print(cr.article);
					out.print("</dd>");
				}
			
				if (resList.size() == 0) {
					out.print("<p>指定されたスレッドのレスは削除されたか、存在しません。</p>");
				}
			
			%>
			</dl>

			<div id="pager">
				<ul>
				<li><a href="read.jsp?id=<%= reqThread %>&res=1">最初のレス</a></li>
				<li><a href="read.jsp?id=<%= reqThread %>&res=<%= startResNumber - maxMoveResNumber <= 0 ? 1 : startResNumber - maxMoveResNumber %>">前の<%= maxMoveResNumber %>件</a></li>
				<li><a href="read.jsp?id=<%= reqThread %>&res=<%= 995 < startResNumber ? 995 : startResNumber + maxMoveResNumber%>">次の<%= maxMoveResNumber %>件</a></li>
				<li><a href="read.jsp?id=<%= reqThread %>&res=<%= maxResNumber - maxMoveResNumber <= 0 ? 1 : maxResNumber - maxMoveResNumber + 1%>">最新の<%= maxMoveResNumber %>件</a></li>
				</ul>
			</div>
				
			<form id="res-post" method="POST" action="./ResPost">
				<input type="hidden" name="thread_id" value="<%= reqThread %>">
				<input type="hidden" name="res_number" value="<%= startResNumber %>">
				<input type="hidden" name="max_move_res_number" value="<%= maxMoveResNumber %>">
				<input type="submit" name="submit" value="書き込む">
				名前：<input type="text" name="usr_name" value="name">
				Twitterにも投稿する：<input type="checkbox" name="post-tweet">
				<br>
				<textarea name="message" rows="5" cols="64"></textarea>
			</form>
		</div>
		
		<div id="null-box"></div>
	</div>
</body>

</html>
