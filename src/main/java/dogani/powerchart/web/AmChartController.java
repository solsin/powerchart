package dogani.powerchart.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import dogani.powerchart.data.ChartContainer;
import dogani.powerchart.data.LogExtractor;

@Controller
@RequestMapping(value = "/amchart")
public class AmChartController {
	@RequestMapping(value = "/page/{chartId}")
	public String page(@PathVariable("chartId") String chartId, Map<String, Object> model) throws Exception {
//		LogExtractor extractor = new LogExtractor();
//		ChartContainer container = extractor.extract(chartId);
		
		model.put("chartId", chartId);
//		model.put("chartCotainer", container);
		
		return "amchart/page";
	}
	
	@RequestMapping(value = "/data/{chartId}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ChartContainer data(HttpServletRequest request, @PathVariable("chartId") String chartId) throws Exception {
		HttpSession session = request.getSession();
		ChartContainer container = (ChartContainer)session.getAttribute(ChartContainer.class.getName());
		
		if (container == null) {
			LogExtractor extractor = new LogExtractor();
			container = extractor.extract(chartId);
			session.setAttribute(ChartContainer.class.getName(), container);
		}

		
		return container;
	}
}
