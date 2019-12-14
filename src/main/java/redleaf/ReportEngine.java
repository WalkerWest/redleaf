package redleaf;

import java.util.List;
import java.util.ArrayList;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

public class ReportEngine {
	
	@Inject @Any
	Instance<Reportable> reports;
	
	public List<Reportable> getReports() {
		List<Reportable> returnList = new ArrayList<Reportable>();
		for (Reportable report:reports) {
			System.out.println(report.getName());
			if(report.isEnabled()) returnList.add(report);
		}
		return returnList;
	}

}
