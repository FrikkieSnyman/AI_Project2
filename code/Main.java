import java.text.*;
import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.Charset;

public class Main{
	public static void main(String[] args) {		
		Chart[] renkoChart = new Chart[3];
		// Chart[] renkoChart = new Chart[1];

		FileHandler fileHander = new FileHandler("../../files/agl.dat");
		renkoChart[0] = fileHander.getChart();
		fileHander = new FileHandler("../../files/ALSI40.dat");
		renkoChart[1] = fileHander.getChart();
		fileHander = new FileHandler("../../files/bil.dat");
		renkoChart[2] = fileHander.getChart();
		// fileHander = new FileHandler("../../files/gfi.dat", renkoChart[3]);

		AIHandler aihandler = new AIHandler(renkoChart);
		System.out.println("1. Genetic AI\n2. Hillclimb AI");
		String choice = System.console().readLine();
		
		if (choice.equals("1")){ // GA
			System.out.println("Population size:");
			Integer population = Integer.parseInt(System.console().readLine());
			System.out.println("Maximum number of generations:");
			Integer generations = Integer.parseInt(System.console().readLine());

			System.out.println("Selection strategy:\n1. Random\n2. Tournament\n3. Fitness-proportional\n4. Rank-based");
			Integer strat = Integer.parseInt(System.console().readLine());
			Integer tournamentSize = 0;
			switch (strat) {
				case 2:
					System.out.println("Tournament size:");
					tournamentSize = Integer.parseInt(System.console().readLine());
					break;
				default:
					break;
				
			}
			System.out.println("Crossover probability:");
			Double crossover = Double.parseDouble(System.console().readLine());

			System.out.println("Mutation rate:");
			Double mutation = Double.parseDouble(System.console().readLine());

			System.out.println("Generation gap:");
			Double gap = Double.parseDouble(System.console().readLine());

			aihandler.ga(population, generations, strat, tournamentSize, crossover, mutation, gap);
		} else if (choice.equals("2")){ // HC
			aihandler.hc();
		} else {
			try{

				Chart[] testChart = new Chart[4];

				fileHander = new FileHandler("../../files/agl.dat");
				testChart[0] = fileHander.getChart();
				fileHander = new FileHandler("../../files/ALSI40.dat");
				testChart[1] = fileHander.getChart();
				fileHander = new FileHandler("../../files/bil.dat");
				testChart[2] = fileHander.getChart();
				fileHander = new FileHandler("../../files/gfi.dat");
				testChart[3] = fileHander.getChart();
				Agent agent = new Agent();
				List<String> lines = Files.readAllLines(Paths.get("../../files/test.txt"), Charset.defaultCharset());
				for (int i = 0; i < lines.get(1).length(); ++i){
					char tmp = lines.get(1).charAt(i);
					switch (tmp) {
						case 'B':
							agent.bsh[i] = BSH.BUY;
							break;
						case 'S':
							agent.bsh[i] = BSH.SELL;
							break;
						case 'H':
							agent.bsh[i] = BSH.HOLD;
							break;
						default:
							break;						
					}
				}
				aihandler.tradeOverAll(testChart,agent);
			} catch (IOException e){
				System.out.println("Problem opening file");
				e.printStackTrace();
			}
		}
	}
}