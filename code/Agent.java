import java.util.Random;
/**
 * Agent class which is used to trade on charts
 * @author Frikkie Snyman - 13028741
 */
public class Agent{
	public BSH[] bsh;
	public long fitness = 0;
	public long money = 100000*100;	// For hillclimber
	public Integer shares = 0;	// For hillclimber
	/**
	 * Constructor for Agent class
	 */
	public Agent(){
		bsh = new BSH[32];

		for (int i = 0; i < 32; ++i){
			int rand = randInt(0,2);
			switch (rand){
				case 0:
					bsh[i] = BSH.BUY;
					break;
				case 1:
					bsh[i] = BSH.SELL;
					break;
				case 2:
					bsh[i] = BSH.HOLD;
					break;
				default:
					bsh[i] = BSH.HOLD;
					break;
			}
		}

	}
	/**
	 * Generates a random number in a range
	 * @param  min Inclusive minimum range
	 * @param  max Inclusive maximum range
	 * @return     Returns a random integer between min and max
	 */
	private static int randInt(int min, int max){
		Random rand = new Random();
		int randomNum = rand.nextInt((max-min) + 1) + min;

		return randomNum;
	}
}