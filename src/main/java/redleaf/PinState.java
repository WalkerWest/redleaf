package redleaf;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;
import org.influxdb.annotation.TimeColumn;

@Measurement(name="Pin State Report",timeUnit=TimeUnit.NANOSECONDS)
public class PinState implements ReportLinable {
	
	@TimeColumn
	@Column(name="time")
	private Instant time;
	public Instant getTime() { 
		Instant now = Instant.now();
		long milliDif = now.toEpochMilli()-(time.toEpochMilli()/1000000);
		return now.minus(milliDif,ChronoUnit.MILLIS); 
	}
	public void setTime(Instant time) {	this.time = time; }

	@Column(name="Device")
	private String device;
	public void setDevice(String device) { this.device=device.toUpperCase(); }
	public String getDevice() { return device; }

	public Boolean getIo1() { return io1; }
	public void setIo1(Boolean io1) { this.io1 = io1; }
	public Boolean getIo2() { return io2; }
	public void setIo2(Boolean io2) { this.io2 = io2; }
	public Boolean getIo3() { return io3; }
	public void setIo3(Boolean io3) { this.io3 = io3; }
	public Boolean getIo4() { return io4; }
	public void setIo4(Boolean io4) { this.io4 = io4; }
	public Boolean getIo5() { return io5; }
	public void setIo5(Boolean io5) { this.io5 = io5; }
	public Boolean getIo6() { return io6; }
	public void setIo6(Boolean io6) { this.io6 = io6; }
	public Boolean getIo7() { return io7; }
	public void setIo7(Boolean io7) { this.io7 = io7; }
	public String getNetworkTime() { return networkTime; }
	public void setNetworkTime(String networkTime) {
		this.networkTime = networkTime;
	}
	public Integer getPinBank() { return pinBank; }
	public void setPinBank(Integer pinBank) {
		this.pinBank = pinBank;
	}
	public Integer getPinState() { return pinState; }
	public void setPinState(Integer pinState) {
		this.pinState = pinState;
	}

	@Column(name="IO1") private Boolean io1;
	@Column(name="IO2") private Boolean io2;
	@Column(name="IO3") private Boolean io3;
	@Column(name="IO4") private Boolean io4;
	@Column(name="IO5") private Boolean io5;
	@Column(name="IO6") private Boolean io6;
	@Column(name="IO7") private Boolean io7;
	@Column(name="Network Time") private String networkTime;
	@Column(name="Pin Bank") private Integer pinBank;
	@Column(name="Pin State") private Integer pinState;
	
	public PinState() { }
	
	private Integer friendlyId=9999;
	public Integer getFriendlyId() { return friendlyId;	}
	public void setFriendlyId(Integer friendlyId) {
		if(friendlyId!=null) this.friendlyId = friendlyId;
	}
	
	private String dockStateString;
	public String getDockStateString() {
		if (this.io5==null || this.io6==null) this.dockStateString="No Comms";
		else if (this.io5==true && this.io6==false) this.dockStateString="Locked";
		else if (this.io5==false && this.io6==true) this.dockStateString="Unlocked";
		else if ((this.io5==true && this.io6==true)) this.dockStateString="Toggling";
		else if ((this.io5==false && this.io6==false)) this.dockStateString="Toggling";
		return this.dockStateString;
	}
	public void setDockStateString(String dockStateString) { 
		this.dockStateString=dockStateString;
	}
	
	private String truckBypassString;
	public String getTruckBypassString() {
		if (this.io4==null) this.truckBypassString="NO COMMS";
		else if(this.io4==true) this.truckBypassString="TRUE";
		else if(this.io4==false) this.truckBypassString="FALSE";
		return this.truckBypassString;
	}
	public void setTruckBypassString(String truckBypassString) {
		this.truckBypassString = truckBypassString;
	}
	
}
