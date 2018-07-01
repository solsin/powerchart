package dogani.powerchart.data;

import java.util.LinkedList;
import java.util.List;

import lombok.Data;

@Data
public class ChartData {
	public String date;
	public List<Double> datas = new LinkedList<>();
}
