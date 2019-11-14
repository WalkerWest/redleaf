package redleaf;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class QueryExecutor {
	private static Connection conn = null;
	
	public static Connection getConn() {
		int tryCount=0;
		while (tryCount++<3) {
			try {
				if (conn==null || !conn.isValid(5)) {

					RLPrefs pref = RLSingle.getInstance().getPrefs();
					String dbSrv = pref.getErpDbSrv();
					String dbPort = pref.getErpDbPort();
					String dbName = pref.getErpDbName();
					String dbUser = pref.getErpDbUser();
					String dbPwd = pref.getErpDbPasswd();
					String dbUrl = "jdbc:sqlserver://"+dbSrv+":"+dbPort+";databaseName="+dbName+";user="+dbUser+";password="+dbPwd;
					String dbDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
					System.out.println("The dbUrl is "+dbUrl);
					try { Class.forName(dbDriver); } 
					catch (ClassNotFoundException e) { e.printStackTrace(); }
					try { conn = DriverManager.getConnection(dbUrl); } 
					catch (SQLException e1) { e1.printStackTrace(); }
				}
			} catch (SQLException e) {
				if(tryCount==3) e.printStackTrace();
			}
		}
		return conn; 
	}


	public static ResultSet query(String queryText) throws QueryTimeoutException {
		
		System.out.println("The query to run is: "+queryText);

		int tryCount=0;
		Statement qrystmt = null;
		
		if(getConn()!=null) {
			while (qrystmt == null && tryCount++<3) {
				//System.out.println("SQL Query try #"+tryCount);
		   		try {
					qrystmt = getConn().createStatement();
					return qrystmt.executeQuery(queryText); 
				} catch (SQLException e) {
					if(
							e.getMessage().equals("The connection is closed.") ||
							e.getMessage().equals("Connection reset by peer: socket write error")
					) {
						conn=null;
						getConn();
					}
					else if (e.getMessage().equals("The statement did not return a result set.")) {
						return null;
					}
					else e.printStackTrace();
				}
		   		qrystmt = null;
			}		
		} else System.out.println("Well, the getConn() call returned null!");
		
		throw new QueryTimeoutException(null);
	}


}
