package reports.dock_status;

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

import redleaf.PinState;
import redleaf.Position;
import redleaf.RLSingle;
import redleaf.Reportable;

public class DockStatusReport implements Reportable {
	
	private HashMap<String,Integer> doors = new HashMap<String,Integer>();
	public DockStatusReport() {
		doors.put("01040300",70);
		doors.put("01040382",69);
		doors.put("01040372",68);
		doors.put("01040344",67);
		doors.put("01040383",66);
		doors.put("0104036F",65);
		doors.put("0104035B",64);
		doors.put("01040380",63);
		doors.put("01040399",62);
		doors.put("0104039F",61);
		doors.put("0104039B",60);
		doors.put("01040392",59);
		doors.put("0104032A",58);
		doors.put("01040387",57);
		doors.put("01040395",56);
		doors.put("01040358",55);
		doors.put("010402F1",54);
		doors.put("01040379",53);
		doors.put("01040374",52);
		doors.put("0104034F",51);
		doors.put("01040373",50);
		doors.put("01040368",49);
		doors.put("0104037B",48);
		doors.put("0104035D",47);
		doors.put("01040313",46);
		doors.put("0104036C",45);
		doors.put("01040370",44);
		doors.put("010402F3",43);
		doors.put("0104035F",42);
		doors.put("01040377",41);
	}

	public String getName() { return "Dock Status"; }
	
	public List<PinState> runQuery() {
		List<PinState> dockList = new ArrayList<PinState>();
		InfluxDB influx = InfluxDBFactory.connect(
				"http://localhost:8086","agsft","agsft1234"
			);
		influx.setDatabase("cuwb");
		HashMap<String,List<Position>> netappData = 
				new HashMap<String,List<Position>>();
		String sqlQuery = "select last(*) from \"Pin State Report\" where "
				+ "time >= now()-2000ms and Device=~/01040/ group by Device";
		Query query = new Query(sqlQuery);
		TimeUnit timeUnit = TimeUnit.NANOSECONDS;
		QueryResult r = influx.query(query, timeUnit);
		InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
		dockList=resultMapper.toPOJO(r, PinState.class);
		Gson gson = new Gson();
		int mySize=dockList.size();
		System.out.println(mySize);
		if(mySize>0) {
			System.out.println(gson.toJson(dockList.get(0)));
			for(PinState ps : dockList) {
				ps.setFriendlyId(doors.get(ps.getDevice()));
			}
			Collections.sort(dockList,new Comparator<PinState>() {
				public int compare(PinState obj1,PinState obj2) {
					return obj1.getFriendlyId().compareTo(obj2.getFriendlyId());
				}
			});
		}
		return dockList;
	}
	
	private String reportFilename=null;

	public String getReportFilename() {
		if(reportFilename==null) {
			String reportPath=RLSingle.getInstance().getDeploymentDirectory();
			System.out.println("The report path is "+reportPath);
			String reportFilename="DockLockStatus-"+System.currentTimeMillis()+".pdf";
			this.reportFilename=reportFilename;
		}
		return reportFilename;
	}

	public String getJrResourceStream() {
		return "reports/dock_status/DockStatus.jasper";
	}

}
