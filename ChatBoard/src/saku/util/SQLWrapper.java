package saku.util;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

/**
 * MySQLへのアクセスをまとめて請け負うシングルトンクラス
 * tomcatの管理外なのでいい方法ではない
 * 2含めてどちらも使わない
 * 
 * @deprecated
 * @author saku
 */
public class SQLWrapper {
	
	private static SQLWrapper wrapper = null;
	private Timer timer;
	
	private Connection con = null;
	private Statement st = null;

	public static SQLWrapper getInstance() {
		if (wrapper == null) {
			wrapper = new SQLWrapper();
		}
		return wrapper;
	}

	private SQLWrapper() {
		timer = new Timer();
		timer.schedule(new TimerDestructor(), 1000 * 60 * 60 * 6);
		
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			
			// 外部設定ファイルにすべき?
			// ユーザーの権限毎の役割分けも必要
			String url = "jdbc:mysql://localhost/chatboard";
			String user = "root";
			String pass = "root";
			
			con = (Connection) DriverManager.getConnection(url, user, pass);
			st = (Statement) con.createStatement();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public ResultSet executeQuery(String sql) {
		try {
			return st.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public int executeUpdate(String sql) {
		try {
			return st.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	private class TimerDestructor extends TimerTask {
		@Override
		public void run() {
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
			wrapper = null;
		}
	}
}
