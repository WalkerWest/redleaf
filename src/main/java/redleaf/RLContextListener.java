package redleaf;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import redleaf.WaterResult;

public class RLContextListener implements ServletContextListener {

	//@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	//@Override
	public void contextInitialized(ServletContextEvent arg0) {
		RLSingle.getInstance().setDwContext(arg0.getServletContext());
		RLSingle.getInstance().printTestCounter();
		//InfluxDB2Example.getData();
		for (WaterResult w : WaterFactory.getData()) {
			System.out.print(w.getLocation());
		}
	}

}
