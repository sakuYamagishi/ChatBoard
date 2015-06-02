package saku.servlet;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

@WebServlet("/SignUp")
public class SignUp extends HttpServlet {
	
	Connection con = null;
	Statement st = null;
       
    public SignUp() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		session.setAttribute("login","false");
	    response.sendRedirect("./index.jsp");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String req_usr_name = request.getParameter("usr_name");
		req_usr_name = req_usr_name == null ? "" : req_usr_name;

		String req_pass = request.getParameter("pass");
		req_pass = req_pass == null ? "" : req_pass;
		
		
		String sql = "select usr_name from account where usr_name = '" +  req_usr_name + "'";
		ResultSet rs = null;
		try {
			rs = st.executeQuery(sql);
			HttpSession session = request.getSession(false);
			
			// ResultSetはnullを返さず、nextで値がない場合にfalseを返す
			if (!rs.next()) {
				Cookie cookie = new Cookie("usr_name", req_usr_name);
				cookie.setMaxAge(60 * 60 * 24 * 3);
				response.addCookie(cookie);

				sql = "insert into account(usr_name, password) values('" + req_usr_name + "','" + req_pass + "')";
				st.executeUpdate(sql);
				
			    session.setMaxInactiveInterval(60 * 10);
			    session.setAttribute("login","true");
			    response.sendRedirect((String) session.getAttribute("target"));
			} else {
			    session.setAttribute("login","false");
			    response.sendRedirect("./index.jsp");
			}
			
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
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
