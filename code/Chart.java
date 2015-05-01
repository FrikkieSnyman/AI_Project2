import java.util.*;

public class Chart{
	private HashMap<Day,UD> udChart;
	private LinkedList<Day> dateChart;
	private Integer range;
	private Integer blockSize;
	public Chart(List<String> data){
		dateChart = new LinkedList<>();
		udChart = new HashMap<>();
		Integer max = Integer.parseInt(data.get(0).split("\t")[1].trim());
		Integer min = Integer.parseInt(data.get(0).split("\t")[1].trim());

		for (int i = 0; i < data.size(); ++i){
			String day = data.get(i);
			if (day.contains("\t")){
				String[] split = day.split("\t");
				Day addThis = new Day(split[0].trim(),split[1].trim(),split[2].trim(),split[3].trim(),i);
				dateChart.add(addThis);
				if (addThis.closingPrice > max){
					max = addThis.closingPrice;
				} else if (addThis.closingPrice < min){
					min = addThis.closingPrice;
				}
			}

		}
		range = max - min;
		blockSize = (int) Math.round(0.01*range);
		renko();
	}

	public HashMap<Day,UD> renko(){
		Integer stockPrice = dateChart.get(0).closingPrice;
		
		for (int i = 0; i < dateChart.size(); ++i){
			Day currentDay = dateChart.get(i);
			Integer change = Math.abs(currentDay.closingPrice - stockPrice);
			Integer j = 0;

			while (change >= blockSize){
				Day tmp = new Day(dateChart.get(i).date,dateChart.get(i).closingPrice,dateChart.get(i).highPrice,dateChart.get(i).lowPrice,dateChart.get(i).index);
				tmp.count = j++;
				if (currentDay.closingPrice > stockPrice){
					udChart.put(tmp,UD.UP);
					stockPrice += blockSize;
				} else {
					udChart.put(tmp,UD.DOWN);
					stockPrice -= blockSize;
				}
				change = Math.abs(currentDay.closingPrice - stockPrice);
			}
		}
		return udChart;
	}
}