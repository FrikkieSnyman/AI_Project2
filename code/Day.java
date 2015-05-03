import java.util.*;
import java.text.*;
/**
 * Class which represents a day of a stock
 * @author Frikkie Snyman - 13028741
 */
public class Day{

	public Date date;
	public Integer closingPrice;
	public Integer highPrice;
	public Integer lowPrice;
	public Integer count;
	public Integer index;

	/**
	 * Constructor for day
	 * @param  _date         Date of the day
	 * @param  _closingPrice Closing price of the day
	 * @param  _highPrice    High price on the day
	 * @param  _lowPrice     Low price on the day
	 * @param  _index        Index on which object appears in a list if applicable
	 */
	public Day(String _date, String _closingPrice, String _highPrice, String _lowPrice, Integer _index){
		setDate(_date);
		closingPrice = Integer.parseInt(_closingPrice);
		highPrice = Integer.parseInt(_highPrice);
		lowPrice = Integer.parseInt(_lowPrice);
		count = 0;
		index = _index;
	}
	/**
	 * Constructor for day
	 * @param  _date         Date of the day
	 * @param  _closingPrice Closing price of the day
	 * @param  _highPrice    High price on the day
	 * @param  _lowPrice     Low price on the day
	 * @param  _index        Index on which object appears in a list if applicable
	 */
	public Day(Date _date, Integer _closingPrice, Integer _highPrice, Integer _lowPrice, Integer _index){
		date = _date;
		closingPrice = _closingPrice;
		highPrice = _highPrice;
		lowPrice = _lowPrice;
		count = 0;
		index = _index;
	}
	/**
	 * Sets date for object
	 * @param _date Date to which object must be set to
	 */
	public void setDate(String _date){
		try{
			date = format.parse(_date);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * Generates a string of the object
	 * @return Returns a String representation of object
	 */
	public String toString(){
		return date.toString() + " CP: " + closingPrice + " HP: " + highPrice + " LP: " + lowPrice + "\n";
	}
	

	private DateFormat format = new SimpleDateFormat("d-M-y", Locale.ENGLISH);
}