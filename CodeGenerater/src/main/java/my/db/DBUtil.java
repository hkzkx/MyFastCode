package my.db;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DBUtil {
	protected final Log log = LogFactory.getLog(DBUtil.class);

	private static int poolSize = 5;

	private static ThreadLocal<LinkedList<Connection>> poolConn = new ThreadLocal<LinkedList<Connection>>();
	private ThreadLocal<Connection> currentConn = new ThreadLocal<Connection>();

	Properties propers = new Properties();

	private String thisDbKey;

	public DBUtil(DBKey key) {
		this.thisDbKey = key.value();

		// FileReader fr = null;
		Reader fr = null;
		try {
			// fr = new
			// InputStreamReader(DBUtil.class.getClass().getResourceAsStream(thisDbKey));
			String path = DBUtil.class.getResource(thisDbKey).getPath();
			// log.info(path);
			fr = new FileReader(new File(path));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		// config.set(propers);
		try {
			propers.load(fr);
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void init() {
		String url = propers.getProperty("db.driverUrl");
		String driver = propers.getProperty("db.driver");
		String user = propers.getProperty("db.user");
		String pwd = propers.getProperty("db.password");

		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			log.info("class not found," + driver);
		}
		try {
			LinkedList<Connection> connList = new LinkedList<Connection>();
			for (int i = 0; i < poolSize; i++) {
				Connection conn = DriverManager.getConnection(url, user, pwd);
				conn.setAutoCommit(false);
				log.info(url + " connection auto commit? " + conn.getAutoCommit());
				connList.add(conn);
			}
			poolConn.set(connList);

		} catch (SQLException e) {
			log.info("连接数据库失败，user:" + user + ",pwd:" + pwd);
		}
	}

	private synchronized Connection getConnection() {
		Connection conn = currentConn.get();
		if (conn != null)
			return conn;

		LinkedList<Connection> connPool = poolConn.get();
		if (connPool == null || connPool.isEmpty()) {
			init();
			connPool = poolConn.get();
		}
		currentConn.set(connPool.pollFirst());
		return currentConn.get();
	}

	public ResultSet query(String sql) {
		log.info(sql);
		Connection conn = getConnection();

		Statement st;
		try {
			st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			return rs;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public String queryOneColumn(String sql) {
		log.info(sql);
		Connection conn = getConnection();

		Statement st;
		try {
			st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			if (rs != null) {

				while (rs.next()) {
					return rs.getString(1);
				}
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	public boolean execute(String sql) {
		log.debug(sql);
		Connection conn = getConnection();
		Statement st;
		try {
			st = conn.createStatement();
			boolean ok = st.execute(sql);
			return ok;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public java.sql.PreparedStatement getPreparedStatement(String sql) {
		try {
			return getConnection().prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void commit() {
		Connection conn = currentConn.get();
		if (conn == null)
			throw new IllegalArgumentException("线程中无连接，不能提交事务");
		try {
			if (!conn.isClosed()) {
				conn.commit();
				conn.close();
			}
			currentConn.remove();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public void close() {
		Connection conn = currentConn.get();
		try {
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
			currentConn.remove();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public void rollback() {
		Connection conn = currentConn.get();
		if (conn == null)
			throw new IllegalArgumentException("线程中无连接，不能回退事务");
		try {
			conn.rollback();
			conn.close();
			currentConn.remove();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

}
