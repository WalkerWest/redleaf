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
		doors.put("01040357",71);
		doors.put("01040250",72);
		doors.put("0104037c",73);
		doors.put("010402c3",74);
		doors.put("01040355",75);
		doors.put("0104036a",76);
		doors.put("01040356",77);
		doors.put("0104036e",78);
		doors.put("01040366",79);
		doors.put("0104032b",80);
		doors.put("01040381",81);
		doors.put("01040389",82);
		doors.put("01040385",83);
		doors.put("01040390",84);
		doors.put("01040327",85);
		doors.put("0104031a",86);
		doors.put("01040369",87);
		doors.put("01040388",88);
		doors.put("0104032d",89);
		doors.put("01040386",90);
		doors.put("010402f9",91);
		doors.put("01040398",92);		
	}

	public String getName() { return "Dock Lock Status"; }
	
	public List<PinState> runQuery() {
		List<PinState> dockList = new ArrayList<PinState>();
		InfluxDB influx = InfluxDBFactory.connect(
				RLSingle.getInstance().getPrefs().getInfluxUrl(),
				RLSingle.getInstance().getPrefs().getInfluxUser(),
				RLSingle.getInstance().getPrefs().getInfluxPasswd()
			);
		influx.setDatabase(RLSingle.getInstance().getPrefs().getInfluxDb());
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
			this.reportFilename=reportPath+reportFilename;
		}
		return reportFilename;
	}

	public String getJrResourceStream() {
		return "reports/dock_status/DockStatus.jasper";
	}

	public boolean isEnabled() { return true; }

}
