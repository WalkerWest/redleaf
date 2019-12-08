package redleaf;

import java.util.List;

public interface Reportable {
	public String getName();
	public List<? extends Influxable> runQuery();
	public String getReportFilename();
	public String getJrResourceStream();
}
