package reports.productivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;

import redleaf.InfluxDB2Example;
import redleaf.ReportLinable;
import redleaf.Reportable;
import redleaf.WaterResult;
import reports.water_sample.WaterFactory;

public class ProductivityReport implements Reportable {

	public String getName() {
		return "Productivity";
	}
	
	private HashMap<String,String> palletMap = new HashMap<String,String>();
	private HashMap<String,String> truckMap = new HashMap<String,String>();
	
	// 0.0 140.0 -30.0 250.0 0.0 3.8
	float xLow=(float) 0.0,   xHigh=(float) 140.0, 
		  yLow=(float) -30.0, yHigh=(float) 250.0, 
		  zLow=(float) 0.0,   zHigh=(float) 3.8;

	public List<ReportLinable> runQuery() {
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
			}
			csvReader.close();		
		} catch (IOException e) { e.printStackTrace(); }
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
		//Thread t = new Thread(myInflux);
		//t.start();
		myInflux.run();
		for (WaterResult w : WaterFactory.getData()) {
			System.out.print(w.getLocation());
		}
		return null;
	}

	public String getReportFilename() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getJrResourceStream() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isEnabled() { return false; }

}