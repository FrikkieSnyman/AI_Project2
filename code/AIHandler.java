import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.Charset;

public class AIHandler{
	private Chart[] chart;
	private Agent bestAgent;

	public AIHandler(Chart[] _chart){
		chart = _chart;
		bestAgent = new Agent();
	}

	public void ga(Integer population, Integer generations, Integer selectionStrategy, Integer tournamentSize, Double crossover, Double mutation, Double gap){
		Agent[] bestAgents = new Agent[chart.length];
		for (int i = 0; i < chart.length; ++i){
			bestAgents[i] = new Agent();
		}
		GeneticAI geneticAi = new GeneticAI(chart, bestAgent, bestAgents, population, generations, selectionStrategy, tournamentSize, crossover, mutation, gap);
		System.out.println("Best agent: "  + " \nwith fitness " + (double)(geneticAi.bestAgent.fitness)/100);
		
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



// TESTING

	public long money = 100000*100;
	public long fitness = 0;
	public Integer shares = 0;

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

	private long determineFitness(LinkedList<Brick> renkoChart){
		Integer lastDay = renkoChart.get(renkoChart.size()-1).day.highPrice + renkoChart.get(renkoChart.size()-1).day.lowPrice;
		Double average = (double)lastDay/2;
		return (long)(money + (shares * average));
	}

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