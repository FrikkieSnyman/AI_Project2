import java.util.Random;

public class Agent{
	public BSH[] bsh;
	public long fitness = 0;
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

	private static int randInt(int min, int max){
		Random rand = new Random();
		int randomNum = rand.nextInt((max-min) + 1) + min;

		return randomNum;
	}
}