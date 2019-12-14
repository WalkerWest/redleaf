package reports.forkTruck_status;

import java.time.Instant;

import redleaf.ReportLinable;

public class TruckBypassEntry implements ReportLinable {

	private Instant time;
	private String liftTag;
	private Integer liftId;
	private Boolean mgrOvride;
	private String operCardId;
	private String operName;
	private Instant operLogonTime;
	
	public Instant getTime() { return time; }
	public String getLiftTag() { return liftTag; }
	public Integer getLiftId() { return liftId; }
	public Boolean getMgrOvride() { return mgrOvride; }
	public String getOperCardId() { return operCardId; }
	public String getOperName() { return operName; }
	public Instant getOperLogonTime() { return operLogonTime; }
	
	public void setTime(Instant time) { this.time = time; }
	public void setLiftTag(String liftTag) { this.liftTag = liftTag; }
	public void setLiftId(Integer liftId) { this.liftId = liftId; }
	public void setMgrOvride(Boolean mgrOvride) { this.mgrOvride = mgrOvride; }
	public void setOperCardId(String operCardId) { 
		this.operCardId = operCardId; }
	public void setOperName(String operName) { this.operName = operName; }
	public void setOperLogonTime(Instant operLogonTime) { 
		this.operLogonTime = operLogonTime; }

}
