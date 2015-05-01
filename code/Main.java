import java.text.*;
import java.util.*;

public class Main{
	public static void main(String[] args) {		
		Chart[] renkoChart = new Chart[3];

		FileHandler fileHander = new FileHandler("../../files/agl.dat", renkoChart[0]);
		fileHander = new FileHandler("../../files/ALSI40.dat", renkoChart[1]);
		fileHander = new FileHandler("../../files/bil.dat", renkoChart[2]);
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
		} else { // HC

		}
	}
}