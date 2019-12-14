package reports.forkTruck_status;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;

import com.google.gson.Gson;

import redleaf.ReportLinable;
import redleaf.PinState;
import redleaf.RLSingle;
import redleaf.Reportable;
import redleaf.SpeedNForceV1;

public class ForkTruckStatusReport implements Reportable {

	public boolean isEnabled() { return true; }
	public String getName() { return "Fork Truck Bypass Status"; }
	
	private HashMap<String,Integer> trucks = new HashMap<String,Integer>();
	
	public void forks() {
		try {
			File forkFile=new File(getClass().getClassLoader().getResource("forklift.csv").getFile());
			BufferedReader csvReader = new BufferedReader(new FileReader(forkFile));
			String row=csvReader.readLine();
			while ((row = csvReader.readLine()) != null) {
			    String[] data = row.split(",");
			    System.out.println(data[0]+" = "+data[1]);
			    String pattern="[^0-9]*([0-9]+)[^0-9]*";
			    String c;
			    try {
				    trucks.put(
				    		data[1].toUpperCase(), 
				    		Integer.parseInt(data[0].replaceAll(pattern, "$1")));
				    if(data[1].length()>1) {
				    	c=data[1].substring(0, 1);
				    	if(!c.equals("0")) {
						    trucks.put(
						    		"0"+data[1].toUpperCase(), 
						    		Integer.parseInt(data[0].replaceAll(pattern, "$1")));
				    	}
				    }
				    	
				    	
			    } catch (NumberFormatException e1) {}
			}
			csvReader.close();		
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	public ForkTruckStatusReport() {
		this.forks();
	}

	public List<? extends ReportLinable> runQuery() {
		List<TruckBypassEntry> truckList = new ArrayList<TruckBypassEntry>();
		List<PinState> bypassList = new ArrayList<PinState>();
		InfluxDB influx = InfluxDBFactory.connect(
				RLSingle.getInstance().getPrefs().getInfluxUrl(),
				RLSingle.getInstance().getPrefs().getInfluxUser(),
				RLSingle.getInstance().getPrefs().getInfluxPasswd()
			);
		influx.setDatabase(RLSingle.getInstance().getPrefs().getInfluxDb());
		String sqlQuery = "select last(*) from \"Pin State Report\" where "
				+ "time >= now()-1w and Device=~/04020/ group by Device";
		Query query = new Query(sqlQuery);
		TimeUnit timeUnit = TimeUnit.NANOSECONDS;
		QueryResult r = influx.query(query, timeUnit);
		InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
		bypassList=resultMapper.toPOJO(r, PinState.class);
		Gson gson = new Gson();
		int mySize=bypassList.size();
		System.out.println("ForkTruckStatus: "+mySize);
		if(mySize>0) {
			//System.out.println(gson.toJson(truckList.get(0)));
			for(PinState ps : bypassList) {
				ps.setFriendlyId(trucks.get(ps.getDevice()));
				if(ps.getFriendlyId()==null || ps.getFriendlyId()==9999) {
					//System.out.println("My device is "+ps.getDevice());
					ps.setFriendlyId(trucks.get("0"+ps.getDevice()));
				}
				String sqlQuery2 = "select last(*) from \"SpeedNforce V1\" "
						+ "where Device = '%s' "
						+ "group by Device";
				Query query2 = new Query(sqlQuery2);
				QueryResult r2 = influx.query(query2, timeUnit);
				List<SpeedNForceV1> speedEntry = resultMapper.toPOJO(r2, SpeedNForceV1.class);
				TruckBypassEntry tbe = new TruckBypassEntry();
				tbe.setLiftId(ps.getFriendlyId());
				tbe.setLiftTag(ps.getDevice());
				tbe.setMgrOvride(ps.getIo4());
				tbe.setTime(ps.getTime());
				if(speedEntry.size()>0) {
					tbe.setOperCardId(speedEntry.get(0).getCardId());
					tbe.setOperLogonTime(speedEntry.get(0).getTime());
					tbe.setOperName("N/A");
				} else {
					tbe.setOperCardId("N/A");
					tbe.setOperLogonTime(null);
					tbe.setOperName("N/A");
				}
				truckList.add(tbe);
			}
			Collections.sort(truckList,new Comparator<TruckBypassEntry>() {
				public int compare(TruckBypassEntry obj1,TruckBypassEntry obj2) {
					return obj1.getLiftId().compareTo(obj2.getLiftId());
				}
			});
		}
		return truckList;
	}

	private String reportFilename=null;

	public String getReportFilename() {
		if(reportFilename==null) {
			String reportPath=RLSingle.getInstance().getDeploymentDirectory();
			System.out.println("The report path is "+reportPath);
			String reportFilename="ForkTruckStatus-"+System.currentTimeMillis()+".pdf";
			this.reportFilename=reportPath+reportFilename;
		}
		return reportFilename;
	}

	public String getJrResourceStream() {
		return "reports/forkTruck_status/TruckBypassStatus.jasper";
	}

}
