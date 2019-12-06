package redleaf;

import java.time.Instant;
import java.util.Date;

public class ReportParams {
	private Date start;
	public Date getStart() {	return start; }
	public void setStart(Date start) { this.start = start; }
	
	private Date stop;
	public Date getStop() { return stop; }
	public void setStop(Date stop) { this.stop = stop; }

	private Long driver;
	public Long getDriver() { return driver; }
	public void setDriver(Long driver) { this.driver = driver; }
	
	public ReportParams() { }
	
	public ReportParams(Instant start, Instant stop,Long driver) {
		this.start=Date.from(start); this.stop=Date.from(stop); this.driver=driver;
	}

}
