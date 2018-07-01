package dogani.powerchart.data;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.math.NumberUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogExtractor {
	public LogExtractor() {
		
	}
	
	public ChartContainer extract(String folder) throws Exception {
		ChartContainer container = new ChartContainer();
		File dir = new File("C:/Develop/workspace/powerchart/data", folder);
		File[] files = dir.listFiles();
		for (File file : files) {
 			container.filenames.add(file.getName());
			addDatasetFromLog(file, container);
		}
		
		return container;
	}

	private void addDatasetFromLog(File file, ChartContainer container) throws Exception {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		Document doc = dBuilder.parse(file);

		doc.getDocumentElement().normalize();

		NodeList nodeList = doc.getElementsByTagName("Trackpoint");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			NodeList childNodeList = node.getChildNodes();

			RideLog rideLog = extractRideLog(childNodeList);
			log.info("time:{}, convertedDay:{}, watt:{}", rideLog.time, rideLog.watt);
			container.add(rideLog.time, rideLog.watt);
		}
	}
	
	private RideLog extractRideLog(NodeList trackNode) {
		RideLog log = new RideLog();
		for (int i = 0; i < trackNode.getLength(); i++) {
			Node node = trackNode.item(i);
			String name = node.getNodeName();
			String value = node.getTextContent();
			if ("Time".equals(name)) {
//				log.time = value.substring(11, value.length() - 1);
				if (value.length() == "2018-06-29T23:41:47.000Z".length()) {
					value = value.substring(0, "2018-06-29T23:41:47".length())+"Z";
				}
				log.time = value;
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
