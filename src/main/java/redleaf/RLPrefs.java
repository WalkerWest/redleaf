package redleaf;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class RLPrefs {
	
	private String ErpDbSrv;
	private String ErpDbPort;
	private String ErpDbName;
	private String ErpDbUser;
	private String ErpDbPasswd;

	private Preferences pref;

	public RLPrefs(String path) {
	    System.setProperty("java.util.prefs.PreferencesFactory", 
	    		FilePreferencesFactory.class.getName());
	    System.setProperty("net.infotrek.util.prefs.FilePreferencesFactory.file", 
	    		path+"readleaf-prefs.txt");
	    pref=Preferences.userNodeForPackage(RLPrefs.class);
		initVals();
	}
	
	public void initVals() {
		
		System.out.println("I am reading name / values pairs stored in preferences file...");
		String[] keys;
		try {
			keys = pref.keys();
			for (String key : keys) {
				System.out.println(key + " = " + pref.get(key, "missing?"));
			}
		} catch (BackingStoreException e) { e.printStackTrace(); }
		System.out.println("Done reading preferences file!");

		final int keyNumber=5;
		String[] prefKeys=new String[keyNumber];
		
		prefKeys[0]="ErpDbSrv";
		prefKeys[1]="ErpDbPort";
		prefKeys[2]="ErpDbName";
		prefKeys[3]="ErpDbUser";
		prefKeys[4]="ErpDbPasswd";
		
		for ( String myKey : prefKeys ) {
			//System.out.println(myKey);
			//System.out.println("myPref is "+myPref.toString());
			
			if (pref.get(myKey, "")==null || pref.get(myKey,"").equals("")) {
				if(myKey.equals("ErpDbSrv")) pref.put(myKey, "loader.walkerwest.net");				
				else if(myKey.equals("ErpDbPort")) pref.put(myKey, "1433");				
				else if(myKey.equals("ErpDbName")) pref.put(myKey, "Loader");				
				else if(myKey.equals("ErpDbUser")) pref.put(myKey, "admin");				
				else if(myKey.equals("ErpDbPasswd")) pref.put(myKey, "******");
			}
			
			if(myKey.equals("ErpDbSrv")) this.setErpDbSrv(pref.get(myKey,""));				
			else if(myKey.equals("ErpDbPort")) this.setErpDbPort(pref.get(myKey,""));
			else if(myKey.equals("ErpDbName")) this.setErpDbName(pref.get(myKey,""));
			else if(myKey.equals("ErpDbUser")) this.setErpDbUser(pref.get(myKey,""));				
			else if(myKey.equals("ErpDbPasswd")) this.setErpDbPasswd(pref.get(myKey,""));
			
		}
		
	}
	
	public String getErpDbSrv() { return ErpDbSrv; }
	public String getErpDbPort() { return ErpDbPort; }
	public String getErpDbName() { return ErpDbName; }
	public String getErpDbUser() { return ErpDbUser; }
	public String getErpDbPasswd() { return ErpDbPasswd; }
	public void setErpDbSrv(String erpDbSrv,boolean storePref) {
		if(storePref) pref.put("ErpDbSrv", erpDbSrv);
		ErpDbSrv = erpDbSrv;
	}
	public void setErpDbSrv(String erpDbSrv) {
		this.setErpDbSrv(erpDbSrv, false);
	}
	
	public void setErpDbPort(String erpDbPort,boolean storePref) {
		if(storePref) pref.put("ErpDbPort", erpDbPort);
		ErpDbPort = erpDbPort;
	}
	public void setErpDbPort(String erpDbPort) {
		this.setErpDbPort(erpDbPort, false);
	}

	public void setErpDbName(String erpDbName,boolean storePref) {
		if(storePref) pref.put("ErpDbName", erpDbName);
		ErpDbName = erpDbName;
	}
	public void setErpDbName(String erpDbName) {
		this.setErpDbName(erpDbName, false);
	}
	
	public void setErpDbUser(String erpDbUser,boolean storePref) {
		if(storePref) pref.put("ErpDbUser", erpDbUser);
		ErpDbUser = erpDbUser;
	}
	public void setErpDbUser(String erpDbUser) {
		this.setErpDbUser(erpDbUser, false);
	}
	
	public void setErpDbPasswd(String erpDbPasswd,boolean storePref) {
		if(storePref) pref.put("ErpDbPasswd", erpDbPasswd);
		ErpDbPasswd = erpDbPasswd;
	}
	public void setErpDbPasswd(String erpDbPasswd) {
		this.setErpDbPasswd(erpDbPasswd, false);
	}

}
