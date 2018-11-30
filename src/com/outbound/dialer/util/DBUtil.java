package com.outbound.dialer.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.outbound.common.Util;



/**
 * 验证数据库信息
 * @author duanlsh
 *
 */
public abstract class DBUtil {

	/**
	 * 链接数据库
	 * @return
	 */
	public static Connection getConnection(String driver, String url, String user, String pwd) {
        Connection conn = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, pwd);
        } catch (ClassNotFoundException e) {
        	e.printStackTrace();
        	Util.error(DBUtil.class, "Exception  function:getConnection",e);
        } catch (SQLException e) {
        	e.printStackTrace();
        	Util.error(DBUtil.class, "Exception  function:getConnection",e);
        }
        return conn;
    }
	
	/**
	 * 关闭所有链接
	 * @param rs
	 * @param stmt
	 * @param conn
	 */
	public static void closeAll(ResultSet rs, Statement stmt, Connection conn) {
        try {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
        	Util.error(DBUtil.class, "Exception  function:closeAll",e);
        } finally {
            rs = null;
            stmt = null;
            conn = null;
        }
    }
	
}
