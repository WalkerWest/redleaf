package redleaf;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class RLContextListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		RLSingle.getInstance().setDwContext(arg0.getServletContext());
		RLSingle.getInstance().printTestCounter();
	}

}
