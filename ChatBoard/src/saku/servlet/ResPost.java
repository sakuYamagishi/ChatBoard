package saku.servlet;

import java.io.IOException;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import saku.util.*;
import twitter4j.*;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

@WebServlet("/ResPost")
public class ResPost extends HttpServlet {
       
	Connection con = null;
	Statement st = null;
	
	private long postLimit = (long) (1000000000 * 6.7);

    public ResPost() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		//**********************************************//
		String target = request.getRequestURI();
		HttpSession session = request.getSession(false);

		if (session == null){
			session = request.getSession(true);
			session.setAttribute("target", target);

			response.sendRedirect("./index.jsp");
		} else {
			Object loginCheck = session.getAttribute("login");
			if (loginCheck == null){
				session.setAttribute("target", target);
				response.sendRedirect("./index.jsp");
			}
		}
		//**********************************************//
		// 連続投稿を避ける為の時間計測
		// レスが投稿出来なかった時の処理が必要（ユーザーに分かる）
		
		long latestPostTime = session.getAttribute("latest_post_time") == null ? (System.nanoTime() - (postLimit + 3000000000L)) : (long) session.getAttribute("latest_post_time");
		long elapsedTime = System.nanoTime() - latestPostTime;
		if (elapsedTime < postLimit) {
			// 制限時間以内
			response.sendRedirect("./read.jsp?id=" + request.getParameter("thread_id") + "&res=" + request.getParameter("res_number"));
		} else {
			// 制限時間以上経過
			
			request.setCharacterEncoding("UTF-8");

			int maxMoveResNumber = Integer.parseInt(request.getParameter("max_move_res_number"));
			int maxResNumber = 1;
			
			try {
				
				String sql = "insert into " + request.getParameter("thread_id") + "(name, res_datetime, article) values('" + request.getParameter("usr_name") + "','" + Utilities.timeStamp() + "','" + request.getParameter("message") + "')" ;
				st.executeUpdate(sql);
				
				ResultSet rs = st.executeQuery("select count(*) as num from " + request.getParameter("thread_id"));
				rs.next();
				maxResNumber = rs.getInt("num");
				
				session.setAttribute("latest_post_time", System.nanoTime());

				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			// 指定するレス番号は書き込んだ時点での最新件数分（ページャー指定）が表示される番号
			int latestResNum = maxResNumber - maxMoveResNumber <= 0 ? 1 : maxResNumber - maxMoveResNumber + 1;
			// 元のページへリダイレクト
			// Tweetにも使いたいのでこの位置
			String url = "./read.jsp?id=" + request.getParameter("thread_id") + "&res=" + latestResNum;

			String postTweet = (String) request.getParameter("post-tweet");
			boolean isPostTweet = postTweet == null ? false : true;
			if (isPostTweet) {
				// 文字制限どこかで入れる必要有り
				// フォーム自体は自由度残したい
				// 
		        String text = request.getParameter("message");
//		        String text = request.getParameter("message") + " - " + "http://example.com/chatboard/" + url.substring(2, url.length());
		        Twitter twitter = (Twitter)request.getSession().getAttribute("twitter");
		        try {
		            twitter.updateStatus(text);
		        } catch (TwitterException e) {
		            throw new ServletException(e);
		        }
			}
			// 投稿したスレッドに戻ります
			response.sendRedirect(url);
		}
	}

	@Override
	public void init() throws ServletException {
		super.init();
		
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		
			String url = "jdbc:mysql://localhost/chatboard";
			String user = "root";
			String pass = "root";
		
			con = (Connection) DriverManager.getConnection(url, user, pass);
			st = (Statement) con.createStatement();
			
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void destroy() {
		super.destroy();
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
}
