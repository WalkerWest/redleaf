package redleaf;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;
import org.influxdb.annotation.TimeColumn;

@Measurement(name="Position",timeUnit=TimeUnit.NANOSECONDS)
public class Position {
	
	public Position() { }
	
	@Column(name="X")
	private Long x;
	public Long getX() { return x; }
	
	@Column(name="Y")
	private Long y;
	public Long getY() { return y; }
	
	@Column(name="Z")
	private Long z;
	public Long getZ() { return z; }
	
	@TimeColumn
	@Column(name="time")
	private Instant time;
	public Instant getTime() { return time; }
		
}
