package redleaf;

import java.util.List;

public interface Reportable {
	public boolean isEnabled();
	public String getName();
	public List<? extends ReportLinable> runQuery();
	public String getReportFilename();
	public String getJrResourceStream();
}
