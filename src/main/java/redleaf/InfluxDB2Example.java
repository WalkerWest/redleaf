package redleaf;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.Map;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;
import org.influxdb.annotation.TimeColumn;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;
import org.influxdb.impl.InfluxDBResultMapper;

import com.google.gson.Gson;

public class InfluxDB2Example implements Runnable {
	
	private static String start= /*"2019-10-11T05:00:00-07:00"; //*/ "1570795200000000000";
	private static String stop= /*"2019-10-11T12:30:00-05:00"; //*/  "1570815000000000000";
	
	public static List<String> getData() {
		
		List<String> availDevs = new ArrayList<String>();
		
		//String start= /*"2019-10-11T05:00:00-07:00"; //*/ "1570795200000000000";
		//String stop= /*"2019-10-11T12:30:00-05:00"; //*/  "1570815000000000000";
		//                                                 1574633382069000000
		//                                                 1574632535302732741
		InfluxDB influx = InfluxDBFactory.connect(
				//"http://192.168.174.28:8086","nouser",""
				"http://localhost:8086","agsft","agsft1234"
			);
		influx.setDatabase("cuwb");
		String queryStr="show tag values from \"Position\" with key = \"Device\" "
				+ "where time >= "+start+" and time <= "+stop;
		System.out.println("The queryStr is: "+queryStr);
		Query query = 
				//new Query("select * from h2o_feet LIMIT 5","NOAA_water_database");
				new Query(queryStr);
		TimeUnit timeUnit = TimeUnit.NANOSECONDS;
		QueryResult r = influx.query(query, timeUnit);
		//System.out.println(r.getError());
		Gson gson = new Gson();
		for (Result res : r.getResults()) {
			//System.out.println(res.getError());
			//System.out.println(res.getSeries().size());
			if(res!=null && res.getSeries()!=null) {
				for (Series ser : res.getSeries()) {
					//System.out.println(gson.toJson(ser.getColumns()));
					List<List<Object>> myVals = ser.getValues();
					for(List<Object> entry : myVals)
						availDevs.add(((String)entry.get(1)).toUpperCase());
					System.out.println(gson.toJson(availDevs));
				}
			}
		}
		/*
		InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
		List<WaterResult> waterList=resultMapper.toPOJO(r, WaterResult.class);
		for (WaterResult w : waterList) {
			//System.out.print(w.getTime().toString()+" - ");
			System.out.print(w.getLevelDescription()+" - ");
			System.out.print(w.getLocation()+" - ");
			System.out.println(w.getWaterLevel());
		}
		*/
		return availDevs;
	}
	
	private List<String> devs=null;
	
	public InfluxDB2Example(List<String> devs) {
		this.devs=devs;
	}
	
	@SuppressWarnings("unchecked")
	public void run() {
		InfluxDB influx = InfluxDBFactory.connect(
				//"http://192.168.174.28:8086","nouser",""
				"http://localhost:8086","agsft","agsft1234"
			);
		influx.setDatabase("cuwb");
		HashMap<String,List<Position>> netappData = new HashMap<String,List<Position>>();
		String sqlQuery = "select X,Y,Z from Position "
				+ "where Quality>0 and \"Anchor Count\" > 5 and "
				+ "time >= %s and time <= %s and Device='%s' "
				+ "group by \"Destination Address\" fill(-99999)";
		for (String dev : this.devs) {
			String queryStr=String.format(sqlQuery, start, stop, dev);
			System.out.println("The query string is: "+queryStr);
			Query query = new Query(queryStr);
			TimeUnit timeUnit = TimeUnit.NANOSECONDS;
			QueryResult r = influx.query(query, timeUnit);
			InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
			List<Position> posList=resultMapper.toPOJO(r, Position.class);
			if(posList.size()>0) {
				System.out.println(posList.size());
				netappData.put(dev, posList);
			}
			/*
			for (Result res : r.getResults()) {
				if(res!=null && res.getSeries()!=null) {
					for (Series ser : res.getSeries()) {
						System.out.println(gson.toJson(ser.getTags()));
						System.out.println(gson.toJson(ser.getColumns()));
						List<List<Object>> myVals = ser.getValues();
						//for(List<Object> entry : myVals)
						//	System.out.println(gson.toJson(entry));
						
						//	availDevs.add(((String)entry.get(1)).toUpperCase());
						//System.out.println(gson.toJson(availDevs));
					}
				}
			}
			*/
			
		}
		RLSingle.getInstance().doneWithPoints();
		String cardQuery ="select \"Card ID\", \"Device\" from \"SpeedNforce V1\" where "
				+ "time >= %s and time <= %s and Device='%s'";
		int mySize=0;
		//HashMap<Integer,List<Instant>> cardTimePairs = new HashMap<Integer,List<Instant>>();
		//HashMap<Integer,List<String>> cardSerialPairs = new HashMap<Integer,List<String>>();
		HashMap<Integer,LinkedList<SpeedNForceV1>> cardSpeedNForcePairs = 
				new HashMap<Integer,LinkedList<SpeedNForceV1>>(); 
		for (String dev : this.devs) {
			String queryStr=String.format(cardQuery, start, stop, dev);
			System.out.println("The query string is: "+queryStr);
			Query query = new Query(queryStr);
			TimeUnit timeUnit = TimeUnit.NANOSECONDS;
			QueryResult r = influx.query(query, timeUnit);
			InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
			List<SpeedNForceV1> speedList=resultMapper.toPOJO(r, SpeedNForceV1.class);
			Gson gson = new Gson();
			mySize=speedList.size();
			System.out.println(mySize);
			if(mySize>0) {
				System.out.println(gson.toJson(speedList.get(0)));
				System.out.println(gson.toJson(speedList.get(1)));
			}
			for (SpeedNForceV1 entry : speedList) {
				
				if(!cardSpeedNForcePairs.containsKey(entry.getCardId())) {
					LinkedList<SpeedNForceV1> myList = new LinkedList<SpeedNForceV1>();
					myList.add(entry);
					cardSpeedNForcePairs.put(entry.getCardId(),myList);
				}
				else 
					cardSpeedNForcePairs.get(entry.getCardId()).add(entry);
				/*
				if(!cardTimePairs.containsKey(entry.getCardId())) {
					cardTimePairs.put(entry.getCardId(), Arrays.asList(entry.getTime()));
					cardSerialPairs.put(entry.getCardId(), Arrays.asList(entry.getDevice()));
				} else {
					cardTimePairs.get(entry.getCardId()).add(entry.getTime());
					cardSerialPairs.get(entry.getCardId()).add(entry.getDevice());
				}
				*/
			}
		}
		for (Integer key : cardSpeedNForcePairs.keySet()) {
			Gson gson = new Gson();
			//System.out.println("Natural list: "+gson.toJson(cardSpeedNForcePairs.get(key)));
			Collections.sort(cardSpeedNForcePairs.get(key));
			//System.out.println(" Sorted list: "+gson.toJson(cardSpeedNForcePairs.get(key)));
			Instant low = cardSpeedNForcePairs.get(key).getFirst().getTime();
			Instant high = cardSpeedNForcePairs.get(key).getLast().getTime();
			DateFormat df = new SimpleDateFormat("dd MMM yyyy hh:mm:ss:SSS zzz");
			
			Double myLow = (double)low.toEpochMilli()/1000000;
			Double myHigh = (double)high.toEpochMilli()/1000000;
			
			Long myLowLong = low.toEpochMilli()/1000000;
			Long myLowSecs = myLowLong/1000000;
			Long leftOver=(low.toEpochMilli()%1000000)*1000000;
			Long secsLeftOver = (myLowLong%1000000);
			/*
			System.out.print(low.getNano()+" - "+low.toEpochMilli()+" - "+low.getEpochSecond()+" - "+low.getEpochSecond()/1000+
					" - "+myLow+" ("+leftOver+") - "+myLowSecs+" ("+secsLeftOver+leftOver+") - ");
			System.out.println("Low is "+df.format(myLow)); //+" ... and high is: "+Timestamp.from(high));
			*/
			System.out.print("Low is "+df.format(myLow));
			System.out.println(" ... and High is "+df.format(myHigh));
		}
		
	}
	
}
