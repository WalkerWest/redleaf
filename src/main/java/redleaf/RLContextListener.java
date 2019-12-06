package redleaf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.google.gson.Gson;

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
	}

}
