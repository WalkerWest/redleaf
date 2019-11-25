package redleaf;

import java.time.Instant;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;
import org.influxdb.annotation.TimeColumn;

@Measurement(name="SpeedNforce V1")
public class SpeedNForceV1 {
	
	@TimeColumn
	@Column(name="time")
	private Instant time;
	public Instant getTime() { return time; }
	
	@Column(name="Card ID")
	private Integer cardId;
	public Integer getCardId() { return cardId;	}
	
	@Column(name="Device")
	private String device;
	public String getDevice() { return device; }

}
