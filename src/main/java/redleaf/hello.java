package redleaf;

import java.sql.ResultSet;
import java.sql.SQLException;

public class hello {
	
	public void sayHi() {
		RLSingle.getInstance().printTestCounter();
		System.out.println("Hello, world!");
		
		String dbVersion=null;
		ResultSet dbInfo;
		try {
			dbInfo = QueryExecutor.query("select * from msdb.dbo.sysdac_instances "
					+ "where instance_name='Loader'");
			while(dbInfo.next()) {
				dbVersion=dbInfo.getString("type_version");
			}
		} 
		catch (SQLException e) { e.printStackTrace(); } 
		catch (QueryTimeoutException e) { e.printStackTrace(); }
		
		System.out.print("The database version is ... ");
		if (dbVersion==null) System.out.println("NOT AVAILABLE!");
		else System.out.println(dbVersion);
		
	}
	
	public String sayHiString() {
		return ("Hey there");
	}

}
