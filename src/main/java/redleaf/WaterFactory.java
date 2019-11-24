package redleaf;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

public class WaterFactory {
	
	public static List<WaterResult> getData() {
		WaterResult result1=new WaterResult(/*Instant.parse("2015-08-18T00:00:00Z"),*/"below 3 feet",2L,"santa_monica");
		WaterResult result2=new WaterResult(/*Instant.parse("2015-08-18T00:00:00Z"),*/"between 6 and 9 feet",8L,"coyote_creek");
		WaterResult result3=new WaterResult(/*Instant.parse("2015-08-18T00:06:00Z"),*/"below 3 feet",2L,"santa_monica");		
		WaterResult result4=new WaterResult(/*Instant.parse("2015-08-18T00:06:00Z"),*/"between 6 and 9 feet",8L,"coyote_creek");
		WaterResult result5=new WaterResult(/*Instant.parse("2015-08-18T00:12:00Z"),*/"below 3 feet",2L,"santa_monica");		
		return Arrays.asList(result1,result2,result3,result4,result5);
	}
	
	public WaterFactory() { }

}
