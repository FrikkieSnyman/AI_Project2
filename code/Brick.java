/**
 * Class which represents a brick on a Renko chart
 * @author Frikkie Snyman - 13028741
 */
public class Brick{
	public Day day;
	public UD ud;
	/**
	 * Constructor for Brick class
	 * @param  day Day object for which brick is generated
	 * @param  ud  UD enum whether brick goes UP or DOWN
	 */
	public Brick(Day day, UD ud){
		this.day = day;
		this.ud = ud;
	}
	/**
	 * Generates a string of Brick object
	 * @return String of object
	 */
	public String toString(){
		return day.toString() + " " + ud.toString() + " \n";
	}
}