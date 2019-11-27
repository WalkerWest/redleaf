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

	private HashMap<String,String> palletMap = new HashMap<String,String>();
	private HashMap<String,String> truckMap = new HashMap<String,String>();
	
	// 0.0 140.0 -30.0 250.0 0.0 3.8
	float xLow=(float) 0.0,   xHigh=(float) 140.0, 
		  yLow=(float) -30.0, yHigh=(float) 250.0, 
		  zLow=(float) 0.0,   zHigh=(float) 3.8;

	//@Override
	public void contextInitialized(ServletContextEvent arg0) {
		RLSingle.getInstance().setDwContext(arg0.getServletContext());
		RLSingle.getInstance().printTestCounter();
		/*
		try {
			File forkFile=new File(getClass().getClassLoader().getResource("forklift.csv").getFile());
			BufferedReader csvReader = new BufferedReader(new FileReader(forkFile));
			String row=csvReader.readLine();
			while ((row = csvReader.readLine()) != null) {
			    String[] data = row.split(",");
			    System.out.println(data[0]+" = "+data[1]);
			    if(data.length>=3 && !data[2].equals(""))
			    	palletMap.put(data[2].toUpperCase(),data[1].toUpperCase());
			    truckMap.put(data[1].toUpperCase(),data[0]);
			    // do something with the data
			}
			csvReader.close();		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Gson gson = new Gson();
		System.out.println("The palletMap is:" +gson.toJson(palletMap));
		System.out.println("The truckMap is:" +gson.toJson(truckMap));		
		List<String> availDevs=InfluxDB2Example.getData();
		List<String> validDevs=new ArrayList<String>();
		for(String dev : availDevs) {
			if(truckMap.containsKey(dev)) validDevs.add(dev);
			//if(truckMap.containsKey(dev)) validDevs.add(truckMap.get(dev));
		}
		System.out.println("Valid devices: "+gson.toJson(validDevs));
		InfluxDB2Example myInflux=new InfluxDB2Example(validDevs);
		Thread t = new Thread(myInflux);
		t.start();
		for (WaterResult w : WaterFactory.getData()) {
			System.out.print(w.getLocation());
		}
		*/
	}

}
