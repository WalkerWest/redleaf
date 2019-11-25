package redleaf;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;

import org.apache.commons.dbcp.BasicDataSource;

public class RLSingle {
	
	private RLSingle() {}
	private static class SingletonHelper {
		private static final RLSingle INSTANCE = new RLSingle();
	}
	public static RLSingle getInstance() {
		return SingletonHelper.INSTANCE;
	}
	
	public void doneWithPoints() {
		System.out.println("We're done querying the points!");
	}
	
	private int testCounter=0;
	public int getTestCounter() { return testCounter; }
	public void setTestCounter(int testCounter) { this.testCounter = testCounter; }
	public void printTestCounter() {
		this.testCounter++;
		System.out.println("The singleton has been called "+this.getTestCounter()+" times!");
		//System.out.println("The reportPath is "+this.getReportPath());
	}
	
	private String deploymentDirectory; 
	public String getDeploymentDirectory() { return deploymentDirectory; }
	private String configPath;
	public String getConfigPath() { return configPath; }
	private String reportPath;
	public String getReportPath() { return reportPath; }
	private RLPrefs prefs;
	public RLPrefs getPrefs() { return prefs; }
	private BasicDataSource ds = new BasicDataSource();
	//public Connection getConnection() throws SQLException { return ds.getConnection(); }
	
	private ServletContext dwContext;
	//public ServletContext getDwContext() { return dwContext; }
	public void setDwContext(ServletContext dwContext) { 
		this.dwContext = dwContext;
		
		this.deploymentDirectory=dwContext.getRealPath("/");
		
		String basePath=this.deploymentDirectory;
		basePath=basePath.substring(0,basePath.lastIndexOf(File.separator));
		basePath=basePath.substring(0,basePath.lastIndexOf(File.separator));
		basePath=basePath.substring(0,basePath.lastIndexOf(File.separator));
		basePath=basePath.substring(0,basePath.lastIndexOf(File.separator)+1);
		this.configPath=basePath;

		if(!deploymentDirectory.endsWith(File.separator)) deploymentDirectory+=File.separator;
		reportPath=deploymentDirectory+"reports"+File.separator;
		
		System.out.println("The deploymentDirectory is "+deploymentDirectory);
		System.out.println("The configPath is "+configPath);
		System.out.println("The reportPath is "+reportPath);
		
		prefs = new RLPrefs(this.configPath);
		/*
		ds.setDriverClassName("net.sourceforge.jtds.jdbc.Driver");
		ds.setUrl("jdbc:jtds:sqlserver://"
				+ prefs.getErpDbSrv() + ":" + prefs.getErpDbPort()+ "/" 
				+ prefs.getErpDbName()
		);
		ds.setUsername(prefs.getErpDbUser());
		ds.setPassword(prefs.getErpDbPasswd());
		ds.setMinIdle(5);
		ds.setMaxIdle(10);
		ds.setMaxOpenPreparedStatements(100);
		*/
		
	}
	

}
