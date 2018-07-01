package dogani.powerchart.chart;

import java.io.File;
import java.text.SimpleDateFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.math.NumberUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import dogani.powerchart.data.RideLog;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LineChart {
	public JFreeChart create() throws Exception {
		String title = "powerchart";
		String categoryAxisLabel = "time";
		String valueAxisLabel = "power";
		DefaultCategoryDataset dataset = createDataset();
		JFreeChart chart = ChartFactory.createLineChart(title, categoryAxisLabel, // x-axis label
		    valueAxisLabel, // y-axis label
		    dataset, PlotOrientation.VERTICAL, true, // legend
		    true, // tooltype
		    false // generate urls
		);

		// CategoryPlot plot = (CategoryPlot) chart.getPlot();
		// CategoryAxis axis = plot.getDomainAxis();
		XYPlot plot = (XYPlot) chart.getPlot();
		DateAxis axis = (DateAxis) plot.getDomainAxis();
		axis.setTickUnit(new DateTickUnit(DateTickUnitType.MINUTE, 5, new SimpleDateFormat("HH:mm")));

		return chart;
	}

	private DefaultCategoryDataset createDataset() throws Exception {
		String series1 = "Vistor";
		String series2 = "Unique Visitor";

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		addDatasetFromLog(dataset, series1);

		return dataset;
	}

	private void addDatasetFromLog(DefaultCategoryDataset dataset, String series) throws Exception {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		File file = new File("D:\\Develop\\workspace\\powerchart\\data\\test.tcx");
		Document doc = dBuilder.parse(file);

		doc.getDocumentElement().normalize();

		NodeList nodeList = doc.getElementsByTagName("Trackpoint");
		String time = null;
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			NodeList childNodeList = node.getChildNodes();

			RideLog rideLog = extractRideLog(childNodeList);
			dataset.addValue(rideLog.watt, series, rideLog.time);
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

					if ("TPX".equals(name)) {
						NodeList extensionChildList = node.getChildNodes();
						for (int k = 0; k < extensionChildList.getLength(); k++) {
							Node extensionNode = extensionChildList.item(k);
							name = extensionNode.getNodeName();
							if ("Speed".equals(name)) {
								log.speed = NumberUtils.toDouble(extensionNode.getTextContent());
							} else if ("Watts".equals(name)) {
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
