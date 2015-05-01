public class Brick{
	public Day day;
	public UD ud;
	public Brick(Day day, UD ud){
		this.day = day;
		this.ud = ud;
	}
	public String toString(){
		return day.toString() + " " + ud.toString() + " \n";
	}
}