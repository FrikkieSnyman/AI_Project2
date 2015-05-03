import java.util.*;
/**
 * Class for Genetic that handles trading for a trader used by the Genetic Algorithm
 * @author Frikkie Snyman - 13028741
 */
public class GeneticAgent extends Thread{
	
	public Agent agent = null;
	public Chart[] chart = null;
	public boolean generationDone = false;
	public long money = 100000*100;
	public long fitness = 0;
	public Integer shares = 0;
	public Integer id;
	/**
	 * Constructor for a GeneticAgent
	 * @param  id ID of trrader
	 */
	public GeneticAgent(Integer id){
		this.id = id;
	}
	/**
	 * Starts the thread and trades on all charts
	 */
	public void run(){
		generationDone = false;
		for (int k = 0; k < chart.length; ++k){
			LinkedList<Brick> renkoChart = chart[k].getRenkoChart();
			// LinkedList<Brick> renkoChart = chart.getRenkoChart();
			
			for (int i = 0; i < renkoChart.size()-4; ++i){
				String binary = "";
				for (int j = 0; j < 4; ++j){
					if (renkoChart.get(i+j).ud == UD.UP){
						binary = binary.concat("0");
					} else {
						binary = binary.concat("1");
					}
				}
				takeAction(Integer.parseInt(binary,2),renkoChart.get(i+4));
			}
			fitness += determineFitness(renkoChart);
			// System.out.println(" Trader " + id + " " + (double)fitness/100);
			agent.fitness += fitness;
			money = 100000*100;
		}

		generationDone = true;
	}
	/**
	 * Determines fitness of trader
	 * @param  renkoChart Chart of which fitness is determines
	 * @return            Fitness of trader
	 */
	private long determineFitness(LinkedList<Brick> renkoChart){
		Integer lastDay = renkoChart.get(renkoChart.size()-1).day.highPrice + renkoChart.get(renkoChart.size()-1).day.lowPrice;
		Double average = (double)lastDay/2;
		return (long)(money + (shares * average));
	}
	/**
	 * Decide what to do
	 * @param action Integer value of action to be taken
	 * @param day    Day on which action is taken
	 */
	private void takeAction(Integer action,Brick day){
		BSH bsh = agent.bsh[action];

		switch (bsh) {
			case BUY: 
				// System.out.println(id + "trader Buying...");
				buy(day);
				break;
			case SELL:
				sell(day);
				// System.out.println(id + "trader Selling...");
				break;
			case HOLD:
				// System.out.println(id + "trader Holding...");
				return;
			default: 
				break;
			
		}
	}
	/**
	 * Buys shares
	 * @param day Day on which shares are bought
	 */
	private void buy(Brick day){
		Integer sum = day.day.highPrice + day.day.lowPrice;
		Integer sharePrice = (int)Math.round((double)sum/2);

		Integer suggestedAmount = (int)Math.round(money / sharePrice);
		Integer fees = 0;
		boolean pass = bought(suggestedAmount,sharePrice,fees);
		while(!pass){
			pass = bought(--suggestedAmount,sharePrice,fees);
		}
		money -= sharePrice * suggestedAmount;

		Integer tradeAmount = sharePrice * suggestedAmount;

		Integer stt = (int) Math.round(0.0025*tradeAmount);
		Integer brokerageFee = (int) Math.round(0.005*tradeAmount);
		if (brokerageFee < 7000){
			brokerageFee = 7000;
		}
		Integer strate = 1158;
		Integer ipl = (int) Math.round(0.000002*tradeAmount);
		Integer vat = (int) Math.round(0.14 * (stt + brokerageFee + strate + ipl));
		fees = vat + stt + brokerageFee + strate + ipl;

		money -= fees;
		shares += suggestedAmount;
	}
	/**
	 * Determines wheter shares can be bought
	 * @param  suggestedAmount Amount of shares that is suggested to be bought
	 * @param  sharePrice      Price of shares
	 * @return                 Returns true if the amount of shares can be bought, else returns false
	 */
	private boolean bought(Integer suggestedAmount, Integer sharePrice, Integer fees){
		Integer tradeAmount = sharePrice * suggestedAmount;

		Integer stt = (int) Math.round(0.0025*tradeAmount);
		Integer brokerageFee = (int) Math.round(0.005*tradeAmount);
		if (brokerageFee < 7000){
			brokerageFee = 7000;
		}
		Integer strate = 1158;
		Integer ipl = (int) Math.round(0.000002*tradeAmount);
		Integer vat = (int) Math.round(0.14 * (stt + brokerageFee + strate + ipl));
		Integer total = tradeAmount + vat + stt + brokerageFee + strate + ipl;

		return (money > total);
	}
	/**
	 * Sells shares
	 * @param day Day on which shares are sold
	 */
	private void sell(Brick day){
		Integer sum = day.day.highPrice + day.day.lowPrice;
		Integer sharePrice = (int)Math.round((double)sum/2);
		Integer sellValue = sharePrice * shares;

		Integer brokerageFee = (int) Math.round(0.005*sellValue);
		if (brokerageFee < 7000){
			brokerageFee = 7000;
		}
		Integer strate = 1158;
		Integer ipl = (int) Math.round(0.000002*sellValue);
		Integer vat = (int) Math.round(0.14 * (brokerageFee + strate + ipl));

		Integer total = sellValue - vat - brokerageFee - strate - ipl;
		shares = 0;
		money += total;
	}

}