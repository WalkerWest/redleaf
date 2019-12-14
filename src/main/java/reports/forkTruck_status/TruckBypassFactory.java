package reports.forkTruck_status;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

public class TruckBypassFactory {
	
	public static List<TruckBypassEntry> getData() {
		TruckBypassEntry tbe1 = new TruckBypassEntry();
		tbe1.setLiftTag("01040382"); tbe1.setMgrOvride(true); tbe1.setLiftId(69); 
		tbe1.setTime(Instant.now().minus(1, ChronoUnit.DAYS));
		tbe1.setOperCardId("31116"); tbe1.setOperName("Aric");
		tbe1.setOperLogonTime(Instant.now().minus(1, ChronoUnit.DAYS));		
		TruckBypassEntry tbe2 = new TruckBypassEntry();
		tbe2.setLiftTag("01040372"); tbe2.setMgrOvride(false); tbe2.setLiftId(68);
		tbe2.setTime(Instant.now().minus(25, ChronoUnit.HOURS));
		tbe2.setOperCardId("31112"); tbe2.setOperName("Bryan");
		tbe2.setOperLogonTime(Instant.now().minus(25, ChronoUnit.HOURS));
		return Arrays.asList(tbe1,tbe2);
	}

}
