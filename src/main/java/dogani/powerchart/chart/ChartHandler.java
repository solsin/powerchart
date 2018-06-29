package dogani.powerchart.chart;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChartHandler {
	public JFreeChart create() {
		String title = "powerchart";
		String categoryAxisLabel = "time";
		String valueAxisLabel = "power";
		DefaultCategoryDataset dataset = createDataset();
		return ChartFactory.createLineChart(title, categoryAxisLabel, valueAxisLabel, dataset);
	}

	private DefaultCategoryDataset createDataset() {
		String series1 = "Vistor";
		String series2 = "Unique Visitor";

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		dataset.addValue(200, series1, "2016-12-19");
		dataset.addValue(150, series1, "2016-12-20");
		dataset.addValue(100, series1, "2016-12-21");
		dataset.addValue(210, series1, "2016-12-22");
		dataset.addValue(240, series1, "2016-12-23");
		dataset.addValue(195, series1, "2016-12-24");
		dataset.addValue(245, series1, "2016-12-25");

		dataset.addValue(150, series2, "2016-12-19");
		dataset.addValue(130, series2, "2016-12-20");
		dataset.addValue(95, series2, "2016-12-21");
		dataset.addValue(195, series2, "2016-12-22");
		dataset.addValue(200, series2, "2016-12-23");
		dataset.addValue(180, series2, "2016-12-24");
		dataset.addValue(230, series2, "2016-12-25");

		return dataset;
	}

	public static void stream(JFreeChart chart, HttpServletResponse response) throws IOException {
		log.info("start: chart rendering to response");
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ChartUtils.writeChartAsPNG(bos, chart, 800, 400);
		OutputStream out = new BufferedOutputStream(response.getOutputStream());
		out.write(bos.toByteArray());
		out.flush();
		out.close();
	}
}
