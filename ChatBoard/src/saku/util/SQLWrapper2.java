package saku.util;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

/**
 * MySQLへのアクセスをまとめて請け負います
 * インスタンスはtomcat管理になるはず
 * 
 * jspのデータベースアクセスが冗長だと思ったので分けたかったけどデストラクタなかった
 * 
 * @author saku
 * @deprecated
 */
public class SQLWrapper2 {
	
	private Connection con = null;
	private Statement st = null;

	public SQLWrapper2() {
		
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
}
