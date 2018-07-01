package dogani.powerchart.data;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class ChartContainer {
	int totalCount;
	Collection<String> filenames = new LinkedList<>();
	
	@JsonIgnore
	Map<String, ChartData> dataMap = new TreeMap<>();
	public int filenum;
	
	public void add(String date, int index, Double data) {
		ChartData chartData = dataMap.get(date);
		if (chartData == null) {
			chartData = new ChartData();
			chartData.date = date;
			chartData.datas = new Double[filenum];
			for(int i=0; i<filenum; i++) {
				chartData.datas[i] = 0D;
			}
			dataMap.put(date, chartData);
			totalCount++;
		}
		chartData.datas[index] = data;
	}
	
	@JsonGetter
	public Collection<ChartData> getChartData() {
		LinkedList<ChartData> datas = new LinkedList<>();
		
		dataMap.forEach((k,v)->{
			datas.add(v);
		});
		
		return datas;
	}
}
