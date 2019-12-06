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
import java.util.HashMap;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.media.AMedia;
import org.zkoss.zul.Filedownload;

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
	
	private HashMap<String,String> palletMap = new HashMap<String,String>();
	private HashMap<String,String> truckMap = new HashMap<String,String>();
	
	// 0.0 140.0 -30.0 250.0 0.0 3.8
	float xLow=(float) 0.0,   xHigh=(float) 140.0, 
		  yLow=(float) -30.0, yHigh=(float) 250.0, 
		  zLow=(float) 0.0,   zHigh=(float) 3.8;
	
	
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
		this.pryorStuff();
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
	
	private String jrResourceStream="redleaf/TimeValue.jasper";
	public String getJrResourceStream() { return jrResourceStream; }
	
	private JasperPrint runReport(
			HashMap<String,Object> reportParams,
			String jasperResourceStream) throws JRException {
		InputStream jasperStream = this.getClass().getClassLoader()
				.getResourceAsStream(this.jrResourceStream);
		try {
			return (JasperPrint) JasperFillManager
					.fillReport(jasperStream,reportParams,
							new JRBeanCollectionDataSource(WaterFactory.getData()));
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
