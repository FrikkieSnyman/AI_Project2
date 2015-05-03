import java.util.*;
/**
 * Chart class that holds a full chart and Renko chart
 * @author Frikkie Snyman - 13028741
 */
public class Chart{
	private LinkedList<Brick> udChart;
	private LinkedList<Day> dateChart;
	private Integer range;
	private Integer blockSize;
	/**
	 * Constructor for Chart class
	 * @param  data Data for which chart must be generated in format date\tclosingPrice\thighPrice\tlowPrice\tvolume
	 */
	public Chart(List<String> data){
		dateChart = new LinkedList<>();
		udChart = new LinkedList<>();
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
	/**
	 * Helper function to generate a Renko chart from the dateChart
	 */
	public void renko(){
		Integer stockPrice = dateChart.get(0).closingPrice;
		
		for (int i = 0; i < dateChart.size(); ++i){
			Day currentDay = dateChart.get(i);
			Integer change = Math.abs(currentDay.closingPrice - stockPrice);
			Integer j = 0;

			while (change >= blockSize){
				if (currentDay.closingPrice > stockPrice){
					udChart.add(new Brick(dateChart.get(i),UD.UP));
					stockPrice += blockSize;
				} else {
					udChart.add(new Brick(dateChart.get(i),UD.DOWN));
					stockPrice -= blockSize;
				}
				change = Math.abs(currentDay.closingPrice - stockPrice);
			}
		}
		
	}
	/**
	 * Retrieves Renko chart associated with this object
	 * @return Renko chart
	 */
	public LinkedList<Brick> getRenkoChart(){
		return udChart;
	}
}