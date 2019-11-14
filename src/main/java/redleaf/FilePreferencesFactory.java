package redleaf;

import java.io.File;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

public class FilePreferencesFactory  implements PreferencesFactory
{
	  private static final Logger log = Logger.getLogger(FilePreferencesFactory.class.getName());
	 
	  Preferences rootPreferences;
	  public static final String SYSTEM_PROPERTY_FILE =
	    "net.infotrek.util.prefs.FilePreferencesFactory.file";
	 
	  public Preferences systemRoot()
	  {
	    return userRoot();
	  }
	 
	  public Preferences userRoot()
	  {
	    if (rootPreferences == null) {
	      log.finer("Instantiating root preferences");
	 
	      rootPreferences = new FilePreferences(null, "");
	    }
	    return rootPreferences;
	  }
	 
	  private static File preferencesFile;
	 
	  public static File getPreferencesFile()
	  {
	    if (preferencesFile == null) {
	      String prefsFile = System.getProperty(SYSTEM_PROPERTY_FILE);
	      if (prefsFile == null || prefsFile.length() == 0) {
	        prefsFile = System.getProperty("user.home") + File.separator + ".fileprefs";
	      }
	      preferencesFile = new File(prefsFile).getAbsoluteFile();
	      System.out.println("FilePreferencesFactory: Preferences file is " + preferencesFile);
	    }
	    return preferencesFile;
	  }
	 /*
	  public static void main(String[] args) throws BackingStoreException
	  {
	    System.setProperty("java.util.prefs.PreferencesFactory", FilePreferencesFactory.class.getName());
	    System.setProperty(SYSTEM_PROPERTY_FILE, "myprefs.txt");
	 
	    Preferences p = Preferences.userNodeForPackage(my.class);
	 
	    for (String s : p.keys()) {
	      System.out.println("p[" + s + "]=" + p.get(s, null));
	    }
	 
	    p.putBoolean("hi", true);
	    p.put("Number", String.valueOf(System.currentTimeMillis()));
	  }
	  */
	}
