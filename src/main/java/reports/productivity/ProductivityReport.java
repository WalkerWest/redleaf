package reports.productivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;
import org.influxdb.impl.InfluxDBResultMapper;

import com.google.gson.Gson;

import redleaf.Position;
import redleaf.RLSingle;
import redleaf.ReportLinable;
import redleaf.Reportable;
import redleaf.SpeedNForceV1;

public class ProductivityReport implements Reportable {

	public String getName() { return "Q3: Productivity"; }
	public String getReportFilename() { return null; }
	public String getJrResourceStream() { return null; }
	public boolean isEnabled() { return true; }
	
	// 0.0 140.0 -30.0 250.0 0.0 3.8
	float xLow=(float) 0.0,   xHigh=(float) 140.0, 
		  yLow=(float) -30.0, yHigh=(float) 250.0, 
		  zLow=(float) 0.0,   zHigh=(float) 3.8;
	
	private static String start= "1571860800000000000";
	private static String stop= "1571862240000000000";

	private HashMap<String,String> palletMap = new HashMap<String,String>();
	private HashMap<String,String> truckMap = new HashMap<String,String>();
	private List<String> validDevs=new ArrayList<String>();
	private HashMap<String,List<Position>> positions = new HashMap<String,List<Position>>();
	private HashMap<Integer,LinkedList<SpeedNForceV1>> 
		drivers = new HashMap<Integer,LinkedList<SpeedNForceV1>>(); 
	private HashMap<Integer,HashMap<String,List<Instant>>> 
		cardToLift = new HashMap<Integer,HashMap<String,List<Instant>>>();
	private HashMap<String,HashMap<Integer,List<Instant>>> 
		liftToCard = new HashMap<String,HashMap<Integer,List<Instant>>>();		
	
	public List<ReportLinable> runQuery() {
		Gson gson = new Gson();

		getMapsFromForkCsv();
		System.out.println("The palletMap is:" +gson.toJson(palletMap));
		System.out.println("The truckMap is:" +gson.toJson(truckMap));		
		
		getValidDevs();
		System.out.println("Valid devices: "+gson.toJson(validDevs));
		
		
		getPositions();
		System.out.println("Position entries per truck:");
		for(String dev:validDevs) {
			if(positions.containsKey(dev)) 
				System.out.println("    "+dev+": "+positions.get(dev).size());
			else System.out.println("    "+dev+": 0");
		}
		
		getDrivers();
		
		// So, card_time_pairs is a map between the driver card # and time entries
		// ... and card_serial_pairs is a map between the card and the forklift devs;
		
		// Now, it's time for:
		// {
		//		18782: [('0402000A', 1571860803.505762, 1571861863.544454)], 
		//		24427: [('0402000B', 1571860801.529504, 1571862236.664051)],
		//		...
		// }
		
		buildTimeMaps();
		System.out.println(gson.toJson(cardToLift));
		System.out.println(gson.toJson(liftToCard));
		
		calcProductivity();
		return null;
	}
	
	public void calcProductivity() {
		return;
	}

	public void getMapsFromForkCsv() {
		try {
			File forkFile=new File(getClass().getClassLoader().getResource("forklift.csv").getFile());
			BufferedReader csvReader = new BufferedReader(new FileReader(forkFile));
			String row=csvReader.readLine();
			while ((row = csvReader.readLine()) != null) {
			    String[] data = row.split(",");
			    if(data.length>=3 && !data[2].equals(""))
			    	palletMap.put(data[2].toUpperCase(),data[1].toUpperCase());
			    truckMap.put(data[1].toUpperCase(),data[0]);
			}
			csvReader.close();		
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	public void getValidDevs() {
		List<String> availDevs = new ArrayList<String>();
		InfluxDB influx = InfluxDBFactory.connect(
				//"http://192.168.174.28:8086","nouser",""
				RLSingle.getInstance().getPrefs().getInfluxUrl(),
				RLSingle.getInstance().getPrefs().getInfluxUser(),
				RLSingle.getInstance().getPrefs().getInfluxPasswd()
			);
		influx.setDatabase(RLSingle.getInstance().getPrefs().getInfluxDb());
		String queryStr="show tag values from \"Position\" with key = \"Device\" "
				+ "where time >= "+start+" and time <= "+stop;
		Query query = new Query(queryStr);
		TimeUnit timeUnit = TimeUnit.NANOSECONDS;
		QueryResult r = influx.query(query, timeUnit);
		for (Result res : r.getResults()) {
			if(res!=null && res.getSeries()!=null) {
				for (Series ser : res.getSeries()) {
					List<List<Object>> myVals = ser.getValues();
					for(List<Object> entry : myVals)
						availDevs.add(((String)entry.get(1)).toUpperCase());
				}
			}
		}
		for(String dev : availDevs) if(truckMap.containsKey(dev)) validDevs.add(dev);
	}
	
	public void getPositions() {
		InfluxDB influx = InfluxDBFactory.connect(
				//"http://192.168.174.28:8086","nouser",""
				RLSingle.getInstance().getPrefs().getInfluxUrl(),
				RLSingle.getInstance().getPrefs().getInfluxUser(),
				RLSingle.getInstance().getPrefs().getInfluxPasswd()
			);
		influx.setDatabase(RLSingle.getInstance().getPrefs().getInfluxDb());
		String sqlQuery = "select X,Y,Z from Position "
				+ "where Quality>0 and \"Anchor Count\" > 5 and "
				+ "time >= %s and time <= %s and Device='%s' "
				+ "group by \"Destination Address\" fill(-99999)";
		for (String dev : validDevs) {
			String queryStr=String.format(sqlQuery, start, stop, dev);
			Query query = new Query(queryStr);
			TimeUnit timeUnit = TimeUnit.NANOSECONDS;
			QueryResult r = influx.query(query, timeUnit);
			InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
			List<Position> posList=resultMapper.toPOJO(r, Position.class);
			if(posList.size()>0) positions.put(dev, posList);
		}
	}
	
	public void getDrivers() {
		InfluxDB influx = InfluxDBFactory.connect(
				//"http://192.168.174.28:8086","nouser",""
				RLSingle.getInstance().getPrefs().getInfluxUrl(),
				RLSingle.getInstance().getPrefs().getInfluxUser(),
				RLSingle.getInstance().getPrefs().getInfluxPasswd()
			);
		influx.setDatabase(RLSingle.getInstance().getPrefs().getInfluxDb());
		String cardQuery ="select \"Card ID\", \"Device\" from \"SpeedNforce V1\" where "
				+ "time >= %s and time <= %s and Device='%s'";
		for (String dev : validDevs) {
			String queryStr=String.format(cardQuery, start, stop, dev);
			Query query = new Query(queryStr);
			TimeUnit timeUnit = TimeUnit.NANOSECONDS;
			QueryResult r = influx.query(query, timeUnit);
			InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
			List<SpeedNForceV1> speedList=resultMapper.toPOJO(r, SpeedNForceV1.class);
			for (SpeedNForceV1 entry : speedList) {
				if(!drivers.containsKey(Integer.parseInt(entry.getCardId()))) {
					LinkedList<SpeedNForceV1> myList = new LinkedList<SpeedNForceV1>();
					myList.add(entry);
					drivers.put(Integer.parseInt(entry.getCardId()),myList);
				}
				else drivers.get(Integer.parseInt(entry.getCardId())).add(entry);
			}
		}
		//for (Integer key : drivers.keySet()) Collections.sort(drivers.get(key));
	}
	
	public void buildTimeMaps() {
		for(Integer card:drivers.keySet()) {
			LinkedList<SpeedNForceV1> entryList = drivers.get(card);
			for(SpeedNForceV1 entry : entryList) {
				
				if(!cardToLift.containsKey(card)) {
					HashMap<String,List<Instant>> myEntry = new HashMap<String,List<Instant>>();
					myEntry.put(entry.getDevice(), Arrays.asList(entry.getTime(),entry.getTime()));
					cardToLift.put(card, myEntry);
				}
				else if(!cardToLift.get(card).containsKey(entry.getDevice())) {
					cardToLift.get(card).put(entry.getDevice(), Arrays.asList(entry.getTime(),entry.getTime()));
				} else {
					Instant curStart = cardToLift.get(card).get(entry.getDevice()).get(0);
					Instant curStop = cardToLift.get(card).get(entry.getDevice()).get(1);
					Instant newTime = entry.getTime();
					List<Instant> newTimes = new LinkedList<Instant>();
					if(newTime.isBefore(curStart)) newTimes.add(newTime);
					else newTimes.add(curStart);
					if(newTime.isAfter(curStop)) newTimes.add(newTime);
					else newTimes.add(curStop);
					cardToLift.get(card).replace(entry.getDevice(),newTimes);
				}
				
				if(!liftToCard.containsKey(entry.getDevice())) {
					HashMap<Integer,List<Instant>> myEntry = new HashMap<Integer,List<Instant>>();
					myEntry.put(card, Arrays.asList(entry.getTime(),entry.getTime()));
					liftToCard.put(entry.getDevice(), myEntry);
				}
				else if(!liftToCard.get(entry.getDevice()).containsKey(card)) {
					liftToCard.get(entry.getDevice()).put(card, Arrays.asList(entry.getTime(),entry.getTime()));
				} else {
					Instant curStart = liftToCard.get(entry.getDevice()).get(card).get(0);
					Instant curStop = liftToCard.get(entry.getDevice()).get(card).get(1);
					Instant newTime = entry.getTime();
					List<Instant> newTimes = new LinkedList<Instant>();
					if(newTime.isBefore(curStart)) newTimes.add(newTime);
					else newTimes.add(curStart);
					if(newTime.isAfter(curStop)) newTimes.add(newTime);
					else newTimes.add(curStop);
					liftToCard.get(entry.getDevice()).replace(card,newTimes);
				}
			}
		}
	}

}
