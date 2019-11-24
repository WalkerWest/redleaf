package redleaf;

import java.util.List;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;

public class InfluxDB2Example {
	
	public static void getData() {
		InfluxDB influx = InfluxDBFactory.connect("http://192.168.174.28:8086","nouser","");
		Query query = new Query("select * from h2o_feet LIMIT 5","NOAA_water_database");
		QueryResult r = influx.query(query);
		InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
		List<WaterResult> waterList=resultMapper.toPOJO(r, WaterResult.class);
		for (WaterResult w : waterList) {
			//System.out.print(w.getTime().toString()+" - ");
			System.out.print(w.getLevelDescription()+" - ");
			System.out.print(w.getLocation()+" - ");
			System.out.println(w.getWaterLevel());
		}
	}

}
