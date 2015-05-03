import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.Charset;
/**
 * Class which handles all AI interactions
 * @author Frikkie Snyman - 13028741
 */
public class AIHandler{
	private Chart[] chart;
	private Agent bestAgent;
/**
 * Constructor for AIHanlder class
 * @param  _chart Array of charts on which traders must trad
 */
	public AIHandler(Chart[] _chart){
		chart = _chart;
		bestAgent = new Agent();
	}
/**
 * Function to be called if a Genetic Algorithm is prompted to trade on the chart
 * @param population        Size of the population to be used by the Genetic Algorithm
 * @param generations       The amount of generations throught which the traders must evolve
 * @param selectionStrategy Selection Strategy to be used by the crossover function. 1 = random, 2 = tournament, 3 = fitness-proportional, 4 = rank-based
 * @param tournamentSize    Size of the tournament sample, ignored if selection strategy is not 2
 * @param crossover         Probability of crossover occuring
 * @param mutation          Probability of mutation occuring
 * @param gap               Percentage of generation that survives due to elitism
 */
	public void ga(Integer population, Integer generations, Integer selectionStrategy, Integer tournamentSize, Double crossover, Double mutation, Double gap){
		Agent[] bestAgents = new Agent[chart.length];
		for (int i = 0; i < chart.length; ++i){
			bestAgents[i] = new Agent();
		}
		GeneticAI geneticAi = new GeneticAI(chart, bestAgent, bestAgents, population, generations, selectionStrategy, tournamentSize, crossover, mutation, gap);
		System.out.println("Best agent: "  + " with fitness " + (double)(geneticAi.bestAgent.fitness)/100);
		
		List<String> lines = null;			
		List<String> allLines = null;
		try{
			lines = Files.readAllLines(Paths.get("../../files/trader.txt"), Charset.defaultCharset());
			allLines = Files.readAllLines(Paths.get("../../files/allTraders.txt"), Charset.defaultCharset());
			if (Double.parseDouble(lines.get(0)) < (double)(geneticAi.bestAgent.fitness)/100){
				PrintWriter writer = new PrintWriter("../../files/trader.txt", "UTF-8");
				writer.println((double)(geneticAi.bestAgent.fitness)/100);
				for (int i = 0; i < geneticAi.bestAgent.bsh.length; ++i){
					BSH tmp = geneticAi.bestAgent.bsh[i];
					switch (tmp) {
						case BUY:
							writer.print("B");
							break;
						case SELL:
							writer.print("S");
							break;
						case HOLD:
							writer.print("H");
							break;
						default:
							break;						
					}
				}
				writer.close();
			}

			PrintWriter allwriter = new PrintWriter("../../files/allTraders.txt");
			allLines.add(String.valueOf((double)(geneticAi.bestAgent.fitness)/100));
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < geneticAi.bestAgent.bsh.length; ++i){
				BSH tmp = geneticAi.bestAgent.bsh[i];
				switch (tmp) {
					case BUY:
						sb.append("B");
						break;
					case SELL:		
						sb.append("S");
						break;
					case HOLD:		
						sb.append("H");
						break;
					default:
						break;						
				}
			}
			sb.append(" " + selectionStrategy);
			allLines.add(sb.toString());
			for (int i = 0; i < allLines.size(); ++i){
				allwriter.println(allLines.get(i));
			}
			allwriter.close();

		} catch (IOException e){
			System.out.println("Problem opening file");
			e.printStackTrace();
		}

	}
/**
 * Function to be called if Hill Climb is prompted to trade on the charts
 */
	public void hc(){
		HillClimbAI ai = new HillClimbAI(chart, bestAgent);
		bestAgent = ai.start();
		System.out.println("Best found: " + (double)bestAgent.fitness/100);

		List<String> lines = null;			
		List<String> allLines = null;
		try{
			lines = Files.readAllLines(Paths.get("../../files/trader.txt"), Charset.defaultCharset());
			allLines = Files.readAllLines(Paths.get("../../files/allTraders.txt"), Charset.defaultCharset());
			if (Double.parseDouble(lines.get(0)) < (double)(bestAgent.fitness)/100){
				PrintWriter writer = new PrintWriter("../../files/trader.txt", "UTF-8");
				writer.println((double)(bestAgent.fitness)/100);
				for (int i = 0; i < bestAgent.bsh.length; ++i){
					BSH tmp = bestAgent.bsh[i];
					switch (tmp) {
						case BUY:
							writer.print("B");
							break;
						case SELL:
							writer.print("S");
							break;
						case HOLD:
							writer.print("H");
							break;
						default:
							break;						
					}
				}
				writer.close();
			}

			PrintWriter allwriter = new PrintWriter("../../files/allTraders.txt");
			allLines.add(String.valueOf((double)(bestAgent.fitness)/100));
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bestAgent.bsh.length; ++i){
				BSH tmp = bestAgent.bsh[i];
				switch (tmp) {
					case BUY:
						sb.append("B");
						break;
					case SELL:		
						sb.append("S");
						break;
					case HOLD:		
						sb.append("H");
						break;
					default:
						break;						
				}
			}
			sb.append(" HC");
			allLines.add(sb.toString());
			for (int i = 0; i < allLines.size(); ++i){
				allwriter.println(allLines.get(i));
			}
			allwriter.close();

		} catch (IOException e){
			System.out.println("Problem opening file");
			e.printStackTrace();
		}

	}



// TESTING

	public long money = 100000*100;
	public long fitness = 0;
	public Integer shares = 0;
/**
 * Function that trades a single agent over all charts
 * @param charts Array of charts
 * @param agent  Agent to trade
 */
	public void tradeOverAll(Chart[] charts, Agent agent){
		for (int k = 0; k < charts.length; ++k){
			LinkedList<Brick> renkoChart = charts[k].getRenkoChart();
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
				takeAction(Integer.parseInt(binary,2),renkoChart.get(i+4),agent);
			}
			fitness = determineFitness(renkoChart);
			System.out.println("Stock "+k + (double)fitness/100);
			agent.fitness = fitness;
			money = 100000*100;
		}

	}
	
	/**
	 * Determins the fitness of the agent after trading
	 * @param  renkoChart Chart from which fitness must be evaluated
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
	 * @param agent  Agent that takes the action
	 */
	private void takeAction(Integer action,Brick day, Agent agent){
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
		boolean pass = bought(suggestedAmount,sharePrice);
		while(!pass){
			pass = bought(--suggestedAmount,sharePrice);
		}
		money -= sharePrice * suggestedAmount;
		shares += suggestedAmount;
	}
	/**
	 * Determines wheter shares can be bought
	 * @param  suggestedAmount Amount of shares that is suggested to be bought
	 * @param  sharePrice      Price of shares
	 * @return                 Returns true if the amount of shares can be bought, else returns false
	 */
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
		if (sellValue < brokerageFee){
			return;
		}
		Integer strate = 1158;
		Integer ipl = (int) Math.round(0.000002*sellValue);
		Integer vat = (int) Math.round(0.14 * (brokerageFee + strate + ipl));

		Integer total = sellValue - vat - brokerageFee - strate - ipl;
		shares = 0;
		money += total;
	}

}