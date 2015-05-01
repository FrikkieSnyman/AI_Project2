import java.util.*;
import java.text.*;

public class Day{

	public Date date;
	public Integer closingPrice;
	public Integer highPrice;
	public Integer lowPrice;
	public Integer count;
	public Integer index;


	public Day(String _date, String _closingPrice, String _highPrice, String _lowPrice, Integer _index){
		setDate(_date);
		closingPrice = Integer.parseInt(_closingPrice);
		highPrice = Integer.parseInt(_highPrice);
		lowPrice = Integer.parseInt(_lowPrice);
		count = 0;
		index = _index;
	}

	public Day(Date _date, Integer _closingPrice, Integer _highPrice, Integer _lowPrice, Integer _index){
		date = _date;
		closingPrice = _closingPrice;
		highPrice = _highPrice;
		lowPrice = _lowPrice;
		count = 0;
		index = _index;
	}

	public void setDate(String _date){
		try{
			date = format.parse(_date);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public String toString(){
		return date.toString() + " CP: " + closingPrice + " HP: " + highPrice + " LP: " + lowPrice + "\n";
	}
	

	private DateFormat format = new SimpleDateFormat("d-M-y", Locale.ENGLISH);
}