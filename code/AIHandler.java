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
		
		try{
			lines = Files.readAllLines(Paths.get("../../files/trader.txt"), Charset.defaultCharset());
			if (Double.parseDouble(lines.get(0)) < (double)(geneticAi.bestAgent.fitness)/100){
				PrintWriter writer = new PrintWriter("../../files/trader.txt", "UTF-8");
				writer.println((double)(geneticAi.bestAgent.fitness)/100);
				// writer.println(Arrays.toString(geneticAi.bestAgent.bsh));
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
		} catch (IOException e){
			System.out.println("Problem opening file");
			e.printStackTrace();
		}

	}
}