package redleaf;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;
import org.influxdb.annotation.TimeColumn;

@Measurement(name="SpeedNforce V1",timeUnit=TimeUnit.NANOSECONDS)
public class SpeedNForceV1 implements Comparable,ReportLinable {
	
	@TimeColumn
	@Column(name="time")
	private Instant time;
	public Instant getTime() {
		/*
		Long myLowLong = time.toEpochMilli()/1000;
		Long myLowSecs = myLowLong/1000;
		Long leftOver=(time.toEpochMilli()%1000)*1000;
		Long secsLeftOver = (myLowLong%1000);
		return Instant.ofEpochSecond(myLowSecs/1000,(secsLeftOver+leftOver)/1000);
		*/
		Instant now = Instant.now();
		long milliDif = now.toEpochMilli()-(time.toEpochMilli()/1000000);
		return now.minus(milliDif,ChronoUnit.MILLIS); 
	}

	@Column(name="Card ID") private Integer regCardId;	
	@Column(name="last_Card ID") private Integer lastCardId;
	private String cardId;
	public String getCardId() {
		if (regCardId!=null) cardId=regCardId.toString();
		else if (lastCardId!=null) cardId=lastCardId.toString();
		return cardId;
	}
	
	@Column(name="Device")
	private String device;
	public void setDevice(String device) { this.device=device.toUpperCase(); }
	public String getDevice() { return device; }
	
	public int compareTo(Object arg0) {
		return this.getTime().compareTo(((SpeedNForceV1)arg0).getTime());
	}

}
