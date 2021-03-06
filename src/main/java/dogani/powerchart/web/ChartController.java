package dogani.powerchart.web;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.JFreeChart;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import dogani.powerchart.chart.ChartHandler;

@Controller
@RequestMapping(value = "/chart")
public class ChartController {
	@RequestMapping(value = "/create/{chartId}", method = {
	    RequestMethod.GET }, produces = "application/json; chartset=UTF-8")
	@ResponseBody
	public Object render(HttpServletRequest request, @PathVariable("chartId") String chartId) throws Exception {
		ChartHandler chartHandler = new ChartHandler();
		
		File[] files = new File[] {new File("D:\\Develop\\workspace\\powerchart\\data\\test.tcx")};
		
		JFreeChart chart = chartHandler.create(files);
		request.getSession().setAttribute(chartId, chart);

		return chartId;
	}

	@RequestMapping(value = "/render/{chartId}", method = { RequestMethod.GET }, produces = "image/png")
	@ResponseBody
	public void render(HttpServletRequest request, HttpServletResponse response, @PathVariable("chartId") String chartId)
	    throws IOException {
		JFreeChart chart = (JFreeChart) request.getSession().getAttribute(chartId);
		ChartHandler.stream(chart, response);
	}

	@RequestMapping(value = "/page/{chartId}")
	public String page(@PathVariable("chartId") String chartId, Map<String, Object> model) throws IOException {
		model.put("chartId", chartId);
		return "chart/page";
	}
}
