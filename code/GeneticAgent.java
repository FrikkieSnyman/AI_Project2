import java.util.*;

public class GeneticAgent extends Thread{
	
	public Agent agent = null;
	public Chart chart = null;
	public boolean generationDone = false;
	public long money = 100000*100;
	public long fitness;
	public Integer shares = 0;
	public Integer id;
	public GeneticAgent(Integer id){
		this.id = id;
	}

	public void run(){
		generationDone = false;
		LinkedList<Brick> renkoChart = chart.getRenkoChart();
		
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
		determineFitness(renkoChart);
		System.out.println(id + " " + fitness + " " + money);
		generationDone = true;
	}

	private void determineFitness(LinkedList<Brick> renkoChart){
		Integer lastDay = renkoChart.get(renkoChart.size()-1).day.highPrice + renkoChart.get(renkoChart.size()-1).day.lowPrice;
		Double average = (double)lastDay/2;
		fitness =(long)(money + (shares * average));
	}

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

	private void buy(Brick day){
		Integer sum = day.day.highPrice + day.day.lowPrice;
		Integer sharePrice = (int)Math.round((double)sum/2);

		Integer suggestedAmount = (int)Math.round(money / sharePrice);
		boolean pass = bought(suggestedAmount,sharePrice);
		while(!pass){
			pass = bought(--suggestedAmount,sharePrice);
		}
		money -= sharePrice * suggestedAmount;
		shares += suggestedAmount;
	}

	private boolean bought(Integer suggestedAmount, Integer sharePrice){
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