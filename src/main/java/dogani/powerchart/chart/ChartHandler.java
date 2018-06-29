package dogani.powerchart.chart;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChartHandler {
	public JFreeChart create() throws Exception {
		XYLineChart xyLineChart = new XYLineChart();
		return xyLineChart.create();
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
