package redleaf;

//import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class RedleafViewModel {
	
	private ReportParams params = new ReportParams(
			Instant.ofEpochSecond(1570795200),
			Instant.ofEpochSecond(1570815000),
			15137L);
	public ReportParams getParams() { return params; }
	public void setParams(ReportParams params) { this.params = params; }
	
	private ListModelList<Reportable> reportModel=new ListModelList<Reportable>();
	public ListModelList<Reportable> getReportModel() { return reportModel; }
	public void setReportModel(ListModelList<Reportable> reportModel) {
		this.reportModel = reportModel;
	}
	
	@AfterCompose
	public void getReports() {
		Weld weld = new Weld();
		WeldContainer container = weld.initialize();
		System.out.println("I am getting the report list ...");
		ReportEngine engine = container.instance().select(ReportEngine.class).get();
		List<Reportable> reportList=engine.getReports();
		for(Reportable report:reportList) reportModel.add(report);
		weld.shutdown();
	}
	
	private Reportable selectedReport=null;
	public Reportable getSelectedReport() { return selectedReport; }
	public void setSelectedReport(Reportable selectedReport) {
		this.selectedReport = selectedReport;
	}
	
	@Init
	public void init() {
		System.out.println("I have initialized the view model");
	}
	
	@Command
	@NotifyChange("params")
	public void reload() {
		params = new ReportParams(
				Instant.ofEpochSecond(1570795200),
				Instant.ofEpochSecond(1570815000),
				15137L);
	}
	
	@Command
	public void save() {
		
		System.out.println("The selected report is "+this.getSelectedReport().getName());
		
		HashMap<String,Object> reportParams = new HashMap<String,Object>();
		if(selectedReport != null) {
			if(selectedReport.getName().equals("Productivity")) {
				System.out.println("The new fork truck driver is "+this.getParams().getDriver());
				System.out.println("The start time is "+this.getParams().getStart().toInstant().toEpochMilli());
				System.out.println("The stop time is "+this.getParams().getStop().toInstant().toEpochMilli());
			}
			File f=this.makePdf(reportParams, selectedReport);
			if(f!=null) {
				try {
					System.out.println("Time to download!");
					Filedownload.save(f,"application/pdf");
				} catch (FileNotFoundException e) { e.printStackTrace(); }
			} else {
				Messagebox.show("\""+this.selectedReport.getName()+"\" report is still being developed!");
			}
		}
		
	}
	
	public File makePdf(
			HashMap<String,Object> reportParams, Reportable r) {
		try {
			if(r.getJrResourceStream()==null) r.runQuery();
			else {
				JasperPrint jprint = this.runReport(reportParams, 
						r.getJrResourceStream(),r.runQuery());
				System.out.println("My report file name is:  "+r.getReportFilename());
				JasperExportManager.exportReportToPdfFile(jprint, r.getReportFilename());
			}
		} catch (JRException e1) { e1.printStackTrace(); }
		if(r.getJrResourceStream()!=null) {
			File f=new File(r.getReportFilename());
			byte[] buffer = new byte[(int) f.length()];
			FileInputStream fs;
			try {
				fs = new FileInputStream(f);
				fs.read(buffer);
				fs.close();
			} 
			catch (FileNotFoundException e) {	e.printStackTrace(); } 
			catch (IOException e) { e.printStackTrace(); }
			//ByteArrayInputStream is = new ByteArrayInputStream(buffer);
			return f;
		} else {
			System.out.println("The report is still being developed");
			return null;
		}
	}
	
	private JasperPrint runReport(
			HashMap<String,Object> reportParams,
			String jrResourceStream,
			List<? extends Influxable> reportData) throws JRException {
		InputStream jasperStream = this.getClass().getClassLoader()
				.getResourceAsStream(jrResourceStream);
		try {
			return (JasperPrint) JasperFillManager
					.fillReport(jasperStream,reportParams,
							new JRBeanCollectionDataSource(reportData));
		} catch (JRException e) { throw e; }
	}
	
}
