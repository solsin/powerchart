package dogani.powerchart.data;

import java.text.ParseException;
import java.util.Date;

import org.jfree.data.time.Second;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class RideLog {
//	final SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DDThh:mm:ssZ");
	final DateTimeFormatter dateFormat = ISODateTimeFormat.dateTimeNoMillis();

	public String time;
	public double distance;
	public int hr;
	public int cadence;
	public double speed;
	public double watt;
	
	public Date getTime() throws ParseException {
		return new Date(dateFormat.parseMillis(time));
	}
	
	public Second getSecond() throws ParseException {
		return new Second(getTime());
	}
}
