package dogani.powerchart.data;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.math.NumberUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogExtractor {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	Calendar calendar;
	Map<String, Integer> timeAdjust = new HashMap<>();
	
	public LogExtractor() {
		calendar = Calendar.getInstance();
	}
	
	private String getFilename(File file) {
		String name = file.getName();
		name = name.substring(0, (name.length() - ".tcx".length()));
		
		return name;
	}
	
	public void addTimeAdjust(String key, int value) {
		timeAdjust.put(key, value);
	}
	
	public ChartContainer extract(String folder) throws Exception {
		ChartContainer container = new ChartContainer();
		File dataDir = new File(System.getProperty("user.dir"), "data");
		if (!dataDir.exists()) {
			log.warn("dataDir[{}] doesn't exist. create it.", dataDir);
			dataDir.mkdir();
		}
		File dir = new File(dataDir, folder);
		File[] files = dir.listFiles();
		container.filenum = files.length;
		int index = 0;
		
		for (File file : files) {
			String name = getFilename(file);
 			container.filenames.add(name);
			addDatasetFromLog(file, index, container);
			index++;
		}
		
		return container;
	}

	private void addDatasetFromLog(File file, int index, ChartContainer container) throws Exception {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		Document doc = dBuilder.parse(file);

		doc.getDocumentElement().normalize();

		NodeList nodeList = doc.getElementsByTagName("Trackpoint");
		String name = getFilename(file);
		int adjusted = timeAdjust.get(name) == null ? 0 : timeAdjust.get(name);
		
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			NodeList childNodeList = node.getChildNodes();

			RideLog rideLog = extractRideLog(childNodeList, adjusted);
			log.info("time:{}, convertedDay:{}, watt:{}", rideLog.time, rideLog.watt);
			container.add(rideLog.time, index, rideLog.watt);
//			container.add(i, index, rideLog.time, rideLog.watt);
		}
	}
	
	private RideLog extractRideLog(NodeList trackNode, int timeAdjust) throws ParseException {
		RideLog log = new RideLog();
		for (int i = 0; i < trackNode.getLength(); i++) {
			Node node = trackNode.item(i);
			String name = node.getNodeName();
			String value = node.getTextContent();
			if ("Time".equals(name)) {
//				log.time = value.substring(11, value.length() - 1);
				if (value.length() == "2018-06-29T23:41:47.000Z".length()) {
					value = value.substring(0, "2018-06-29T23:41:47".length());
				} else {
					value = value.substring(0, value.length()-1);
				}
				value = value.replaceAll("T", " ");
				Date date = sdf.parse(value);
				calendar.setTime(date);
				calendar.add(Calendar.SECOND, timeAdjust);
				log.time = sdf.format(calendar.getTime());
				
			} else if ("DistanceMeters".equals(name)) {
				log.distance = NumberUtils.toDouble(value);
			} else if ("HeartRateBpm".equals(name)) {
				log.hr = NumberUtils.toInt(value);
			} else if ("Cadence".equals(name)) {
				log.cadence = NumberUtils.toInt(value);
			} else if ("Extensions".equals(name)) {
				NodeList extensionList = node.getChildNodes();
				for (int j = 0; j < extensionList.getLength(); j++) {
					node = extensionList.item(j);
					name = node.getNodeName();

					if ("TPX".equals(name) || "ns3:TPX".equals(name)) {
						NodeList extensionChildList = node.getChildNodes();
						for (int k = 0; k < extensionChildList.getLength(); k++) {
							Node extensionNode = extensionChildList.item(k);
							name = extensionNode.getNodeName();
							if ("Speed".equals(name) || "ns3:Speed".equals(name)) {
								log.speed = NumberUtils.toDouble(extensionNode.getTextContent());
							} else if ("Watts".equals(name) || "ns3:Watts".equals(name)) {
								log.watt = NumberUtils.toDouble(extensionNode.getTextContent());
							}
						}
					}
				}
			}
		}

		return log;
	}
}
