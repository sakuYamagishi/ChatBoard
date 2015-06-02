package saku.servlet;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import saku.util.Utilities;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

@WebServlet("/BBS")
public class BBS extends HttpServlet {
       
	Connection con = null;
	Statement st = null;

    public BBS() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		session.setAttribute("login","false");
		response.sendRedirect("./index.jsp");
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
		request.setCharacterEncoding("UTF-8");
		
		String req_thread_title = request.getParameter("thread_title");
		if (req_thread_title == null) {
			// タイトル那珂ったら戻ってもらう
			response.sendRedirect("./bbs.jsp");
		}
		
		System.out.println(req_thread_title);
		String newThreadID = "";
		String sql = "";
		
		try {
			sql = "select count(*) as num from thread_list";
			System.out.println(sql);
			ResultSet rs = st.executeQuery(sql);
			rs.next();
			int currentThreadCount = rs.getInt("num");
			System.out.println("num " + currentThreadCount);
			
			// 現在のスレッド数をインクリメントして次のスレッドIDを作成
			newThreadID = "th" + this.fixDigit(6, currentThreadCount + 1);
			
			// スレッドリストにスレッドIDとタイトルを追加
			sql = "insert into thread_list(thread, title) values('" + newThreadID + "','" + request.getParameter("thread_title") + "')";
			System.out.println(sql);
			st.executeUpdate(sql);

			// 新しいスレッドテーブルを作成（構造だけコピー）
			sql = "create table " + newThreadID + " like th000001";
			System.out.println(sql);
			st.executeUpdate(sql);

			// 作成した新しいスレッドテーブルに1レス目を投稿
			sql = "insert into " + newThreadID + "(name, res_datetime, article) values('" + request.getParameter("usr_name") + "','" + Utilities.timeStamp() + "','" + request.getParameter("message") + "')";
			System.out.println(sql);
			st.executeUpdate(sql);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		response.sendRedirect("./read.jsp?id=" + newThreadID + "&res=1");
	}
	
	@Override
	public void init() throws ServletException {
		super.init();
		
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		
			String url = "jdbc:mysql://localhost/chatboard";
			String user = "root";
			String pass = "yasenbaka";
		
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
	
	// targetを指定された桁(digit)までゼロを付ける
	private String fixDigit(int digit, int target) {
		String ans = Integer.toString(target);
		while (ans.length() < digit) {
			ans = "0" + ans;
		}
		return ans;
	}
}
