import java.util.*;

public class GeneticAgent extends Thread{
	
	public Agent agent = null;
	public Chart chart = null;
	public boolean generationDone = false;
	public long money = 100000;
	public long fitness;
	public long shares = 0;
	
	public GeneticAgent(){}

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
		System.out.println(Thread.currentThread().getId() + " " + fitness);
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
				// System.out.println(Thread.currentThread().getId() + "trader Buying...");
				buy(day);
				break;
			case SELL:
				sell(day);
				// System.out.println(Thread.currentThread().getId() + "trader Selling...");
				break;
			case HOLD:
				// System.out.println(Thread.currentThread().getId() + "trader Holding...");
				return;
			default: 
				break;
			
		}
	}

	private void buy(Brick day){
		Integer sum = day.day.highPrice + day.day.lowPrice;
		Double sharePrice = (double)sum/2;
	}

	private void sell(Brick day){

	}

}