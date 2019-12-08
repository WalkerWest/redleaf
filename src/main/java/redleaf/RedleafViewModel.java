package redleaf;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.media.AMedia;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.ListModelList;

import com.google.gson.Gson;

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
	
	private HashMap<String,Integer> doors = new HashMap<String,Integer>();
	
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

	@Init
	public void init() {
		System.out.println("I have initialized the view model");
		doors.put("01040300",70);
		doors.put("01040382",69);
		doors.put("01040372",68);
		doors.put("01040344",67);
		doors.put("01040383",66);
		doors.put("0104036F",65);
		doors.put("0104035B",64);
		doors.put("01040380",63);
		doors.put("01040399",62);
		doors.put("0104039F",61);
		doors.put("0104039B",60);
		doors.put("01040392",59);
		doors.put("0104032A",58);
		doors.put("01040387",57);
		doors.put("01040395",56);
		doors.put("01040358",55);
		doors.put("010402F1",54);
		doors.put("01040379",53);
		doors.put("01040374",52);
		doors.put("0104034F",51);
		doors.put("01040373",50);
		doors.put("01040368",49);
		doors.put("0104037B",48);
		doors.put("0104035D",47);
		doors.put("01040313",46);
		doors.put("0104036C",45);
		doors.put("01040370",44);
		doors.put("010402F3",43);
		doors.put("0104035F",42);
		doors.put("01040377",41);
	}
	
	@Command
	@NotifyChange("params")
	public void reload() {
		params = new ReportParams(
				Instant.ofEpochSecond(1570795200),
				Instant.ofEpochSecond(1570815000),
				15137L);
		
	}
	
	private HashMap<String,String> palletMap = new HashMap<String,String>();
	private HashMap<String,String> truckMap = new HashMap<String,String>();
	
	// 0.0 140.0 -30.0 250.0 0.0 3.8
	float xLow=(float) 0.0,   xHigh=(float) 140.0, 
		  yLow=(float) -30.0, yHigh=(float) 250.0, 
		  zLow=(float) 0.0,   zHigh=(float) 3.8;
	
	private List<PinState> dockList = new ArrayList<PinState>();
	
	public void dockStatus() {
		InfluxDB influx = InfluxDBFactory.connect(
				//"http://192.168.174.28:8086","nouser",""
				"http://localhost:8086","agsft","agsft1234"
			);
		influx.setDatabase("cuwb");
		HashMap<String,List<Position>> netappData = new HashMap<String,List<Position>>();
		String sqlQuery = "select last(*) from \"Pin State Report\" where time >= now()-2000ms and Device=~/01040/ group by Device";
		Query query = new Query(sqlQuery);
		TimeUnit timeUnit = TimeUnit.NANOSECONDS;
		QueryResult r = influx.query(query, timeUnit);
		InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
		dockList=resultMapper.toPOJO(r, PinState.class);
		Gson gson = new Gson();
		int mySize=dockList.size();
		System.out.println(mySize);
		if(mySize>0) {
			System.out.println(gson.toJson(dockList.get(0)));
			for(PinState ps : dockList) {
				ps.setFriendlyId(doors.get(ps.getDevice()));
			}
			Collections.sort(dockList,new Comparator<PinState>() {
				public int compare(PinState obj1,PinState obj2) {
					return obj1.getFriendlyId().compareTo(obj2.getFriendlyId());
				}
			});
		}
	}
	
	public void pryorStuff() {
		try {
			File forkFile=new File(getClass().getClassLoader().getResource("forklift.csv").getFile());
			BufferedReader csvReader = new BufferedReader(new FileReader(forkFile));
			String row=csvReader.readLine();
			while ((row = csvReader.readLine()) != null) {
			    String[] data = row.split(",");
			    System.out.println(data[0]+" = "+data[1]);
			    if(data.length>=3 && !data[2].equals(""))
			    	palletMap.put(data[2].toUpperCase(),data[1].toUpperCase());
			    truckMap.put(data[1].toUpperCase(),data[0]);
			}
			csvReader.close();		
		} catch (IOException e) { e.printStackTrace(); }
		Gson gson = new Gson();
		System.out.println("The palletMap is:" +gson.toJson(palletMap));
		System.out.println("The truckMap is:" +gson.toJson(truckMap));		
		List<String> availDevs=InfluxDB2Example.getData();
		List<String> validDevs=new ArrayList<String>();
		for(String dev : availDevs) {
			if(truckMap.containsKey(dev)) validDevs.add(dev);
			//if(truckMap.containsKey(dev)) validDevs.add(truckMap.get(dev));
		}
		System.out.println("Valid devices: "+gson.toJson(validDevs));
		InfluxDB2Example myInflux=new InfluxDB2Example(validDevs);
		//Thread t = new Thread(myInflux);
		//t.start();
		myInflux.run();
		for (WaterResult w : WaterFactory.getData()) {
			System.out.print(w.getLocation());
		}
	}
	
	@Command
	public void save() {
		System.out.println("The new fork truck driver is "+this.getParams().getDriver());
		System.out.println("The start time is "+this.getParams().getStart().toInstant().toEpochMilli());
		System.out.println("The stop time is "+this.getParams().getStop().toInstant().toEpochMilli());
		//this.pryorStuff();
		this.dockStatus();
		String reportPath=RLSingle.getInstance().getDeploymentDirectory();
		System.out.println("The report path is "+reportPath);
		String reportFilename="TVTest"+System.currentTimeMillis()+".pdf";
		HashMap<String,Object> reportParams = new HashMap<String,Object>();		
		ByteArrayInputStream is=this.makePdf(reportParams, reportPath+reportFilename);
		try {
			System.out.println("Time to download!");
			Filedownload.save(f,"application/pdf");
		} catch (FileNotFoundException e) { e.printStackTrace(); }
	}
	
	private String jrResourceStream="redleaf/DockStatus.jasper";
	public String getJrResourceStream() { return jrResourceStream; }
	
	private JasperPrint runReport(
			HashMap<String,Object> reportParams,
			String jasperResourceStream) throws JRException {
		InputStream jasperStream = this.getClass().getClassLoader()
				.getResourceAsStream(this.jrResourceStream);
		try {
			return (JasperPrint) JasperFillManager
					.fillReport(jasperStream,reportParams,
							/* new JRBeanCollectionDataSource(WaterFactory.getData())*/
							new JRBeanCollectionDataSource(dockList));
		} catch (JRException e) { throw e; }
	}
	
	File f;
	String reportName;
	
	public ByteArrayInputStream makePdf(
			HashMap<String,Object> reportParams,
			String reportName) {
		try {
			JasperPrint jprint = this.runReport(reportParams, 
					this.getJrResourceStream());
			/*
			System.out.println("The start index is "
					+reportParams.get("MachineStatusShiftIndexStart"));
			System.out.println("The stop index is "
					+reportParams.get("MachineStatusShiftIndexStop"));
			*/
			JasperExportManager.exportReportToPdfFile(jprint, reportName);
		} catch (JRException e1) { e1.printStackTrace(); }
		this.reportName=reportName;
		f=new File(reportName);
		byte[] buffer = new byte[(int) f.length()];
		FileInputStream fs;
		try {
			fs = new FileInputStream(f);
			fs.read(buffer);
			fs.close();
		} 
		catch (FileNotFoundException e) {	e.printStackTrace(); } 
		catch (IOException e) { e.printStackTrace(); }
		ByteArrayInputStream is = new ByteArrayInputStream(buffer);
		return is;
	}
	
}
