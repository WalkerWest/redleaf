package redleaf;

import java.time.Instant;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;
import org.influxdb.annotation.TimeColumn;

@Measurement(name="h2o_feet")
public class WaterResult implements ReportLinable {

	/*
	@TimeColumn
	@Column(name="time")
	private Instant time;
	public Instant getTime() { return time; }
	*/

	@Column(name="level description")
	private String levelDescription;
	public String getLevelDescription() { return levelDescription; }

	@Column(name="water_level")
	private Long waterLevel;
	public Long getWaterLevel() { return waterLevel; }

	@Column(name="location")
	private String location;
	public String getLocation() { return location; }
	
	public WaterResult(/*Instant time,*/String desc,Long level,String loc) {
		/*this.time=time; */this.levelDescription=desc; this.waterLevel=level; this.location=loc;
	}
	
	public WaterResult() {}

}
