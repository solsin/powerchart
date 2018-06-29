package dogani.powerchart.chart;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChartHandler {
	public JFreeChart create(File[] logFiles) throws Exception {
		XYLineChart xyLineChart = new XYLineChart();
		return xyLineChart.create(logFiles);
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
