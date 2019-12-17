package redleaf;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;
import org.influxdb.annotation.TimeColumn;

// select mean("Sonar Range") as "Sonar Average" from "Pallet Sensor V1" where Device ='04010035' and "Sonar Range">1 and time >now()-7d group by time(1h)
// 

@Measurement(name="Pallet Sensor V1",timeUnit=TimeUnit.NANOSECONDS)
public class PalletSensorV1 {

	@TimeColumn
	@Column(name="time")
	private Instant time;
	public Instant getTime() {
		Instant now = Instant.now();
		long milliDif = now.toEpochMilli()-(time.toEpochMilli()/1000000);
		return now.minus(milliDif,ChronoUnit.MILLIS); 
	}
	
	@Column(name="Sonar Range") private float sonarRange;
	public float getSonarRange() { return sonarRange; }
	public void setSonarRange(float sonarRange) { this.sonarRange = sonarRange; }
	
	@Column(name="Sonar Average") private float sonarAvg;
	public float getSonarAvg() { return sonarAvg; }
	public void setSonarAvg(float sonarAvg) { this.sonarAvg = sonarAvg; }

	@Column(name="Device") private String device;
	public void setDevice(String device) { this.device=device.toUpperCase(); }
	public String getDevice() { return device; }

}
