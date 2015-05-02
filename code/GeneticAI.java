import java.util.Random;
import java.util.Arrays;

public class GeneticAI extends AI{

	public GeneticAI(Chart[] chart, Agent bestAgent,Agent[] bestAgents, Integer population, Integer generations, Integer selectionStrategy, Integer tournamentSize, Double crossover, Double mutation, Double gap){
		super(chart,bestAgent);

		GeneticAgent[] agents = new GeneticAgent[population];
		
		for (int j = 0; j < agents.length; ++j){
			agents[j] = new GeneticAgent(j);
			agents[j].agent = new Agent();
		}
		bestAgent = new Agent();
		// bestAgents = new Agent[this.chart.length];
		
		// for (int i = 0; i < bestAgents.length; ++i){
		// 	bestAgents[i] = new Agent();
		// }
		// for (int i = 0; i < this.chart.length; ++i){
		// 	Chart currentChart = super.chart[i];
			
			for (int g = 0; g < generations; ++g){
				boolean block = true;

				for (int j = 0; j < population; ++j){
					Agent tmp = agents[j].agent;
					agents[j] = new GeneticAgent(j);
					agents[j].agent = tmp;
					agents[j].chart = super.chart;
					agents[j].start();
				}

				while(block){ // Wait for each thread to finish its generation
					for (int j = 0; j < population; ++j){
						if (!agents[j].generationDone){
							block = true;
							break;
						}
						block = false;
					}
				}

				for (int j = 0; j < agents.length; ++j){
					// System.out.println("Agent " +j + " fitness = " + (double)agents[j].fitness/100);
					if (agents[j].fitness > bestAgent.fitness){
						this.bestAgent = agents[j].agent;
					}
				}
				calculateNextGeneration(agents, population, selectionStrategy, tournamentSize, crossover, mutation, gap);
			// }

		}
	}

	private void calculateNextGeneration(GeneticAgent[] agents, Integer population, Integer selectionStrategy, Integer tournamentSize, Double crossover, Double mutation, Double gap){
		GeneticAgent[] oldGeneration = new GeneticAgent[population];
		for (int i = 0; i < population; ++i){
			oldGeneration[i] = new GeneticAgent(i);
			oldGeneration[i].agent = new Agent();
			for (int j = 0; j < 32; ++j){
				oldGeneration[i].agent.bsh[j] = agents[i].agent.bsh[j];
			}
		}

		GeneticAgent[] survivors = elitism(agents,gap);

		for (int i = 0; i < population-1; i += 2 ){
			// System.out.println(i + " :" + Arrays.toString(agents[i].agent.bsh) + " \n" + (i+1) + " :" + Arrays.toString(agents[i+1].agent.bsh));
			GeneticAgent[] parents = selectParents(oldGeneration,selectionStrategy,tournamentSize);
			crossover(agents,parents,crossover,i);
			// System.out.println(i + " :" + Arrays.toString(agents[i].agent.bsh) + " \n" + (i+1) + " :" + Arrays.toString(agents[i+1].agent.bsh));
		}
		mutate(agents,mutation);

		replaceWeaklings(agents,survivors, gap);
	}

	private GeneticAgent[] selectParents(GeneticAgent[] agents, Integer selectionStrategy, Integer tournamentSize){
		GeneticAgent[] parents = new GeneticAgent[2];
		
		switch (selectionStrategy) {
			case 1:		// Random
				int rand = randInt(0,agents.length-1);
				parents[0] = agents[rand];
				int rand2 = randInt(0,agents.length-1);
				while (rand2 == rand){
					rand2 = randInt(0,agents.length-1);
				}
				parents[1] = agents[rand2];
				break;

			case 2:		// Tournament

				break;

			case 3:		// Fitness-proportional

				break;

			case 4:		// Rank-based

				break;

			default:
				break;
			
		}
		return parents;
	}

	private void replaceWeaklings(GeneticAgent[] agents, GeneticAgent[] survivors, Double gap){
		Integer numSurvivors = (int) Math.round(gap*agents.length);
		GeneticAgent[] weaklings = new GeneticAgent[numSurvivors];
		Integer[] indeces = new Integer[numSurvivors];

		for (int i = 0; i < numSurvivors; ++i){
			weaklings[i] = agents[i];
			indeces[i] = i;
		}

		for (int i = numSurvivors; i < agents.length; ++i){
			for (int j = 0; j < weaklings.length; ++j){
				if (agents[i].fitness < weaklings[j].fitness){
					weaklings[j] = agents[i];
					indeces[j] = i;
					break;
				}
			}
		}
		for (int i = 0; i < indeces.length; ++i){
			agents[indeces[i]] = survivors[i];
		}

	}

	private GeneticAgent[] elitism(GeneticAgent[] agents, Double gap){
		Integer numSurvivors = (int) Math.round(gap*agents.length);
		GeneticAgent[] survivors = new GeneticAgent[numSurvivors];

		for (int i = 0; i < numSurvivors; ++i){
			survivors[i] = agents[i];
		}

		for (int i = numSurvivors; i < agents.length; ++i){
			for (int j = 0; j < survivors.length; ++j){
				if (agents[i].fitness > survivors[j].fitness){
					survivors[j] = agents[i];
					break;
				}
			}
		}

		return survivors;
	}

	private void crossover(GeneticAgent[] child, GeneticAgent[] parents, Double crossover, int index){
		Integer prob = ((Double)(crossover*100)).intValue();
		Integer rand = randInt(0,100);
		if (rand.compareTo(prob) <= 0){
			// Crossover take place
			child[index] = parents[0];
			child[index+1] = parents[1];
			int crossoverPoint = 15;
			int j = 0;
			for (int i = crossoverPoint+1; i < 32; ++i){
				BSH tmp = child[index].agent.bsh[j];
				child[index].agent.bsh[j] = child[index+1].agent.bsh[i];
				child[index+1].agent.bsh[i] = tmp;
				j++;
			}

		}

	}

	private void mutate(GeneticAgent[] children, Double mutation){
		Integer prob = ((Double)(mutation*100)).intValue();
		
		for (int i = 0; i < children.length; i++){
			GeneticAgent tmp = children[i];
			for (int j = 0; j < tmp.agent.bsh.length; ++j){
				Integer rand = randInt(0,100);
				if (rand.compareTo(prob) <= 0){		// Mutate child
					rand = randInt(0,2);
					switch (rand){
						case 1:
							tmp.agent.bsh[j] = BSH.BUY;
							break;
						case 2:
							tmp.agent.bsh[j] = BSH.SELL;
							break;
						case 3:
							tmp.agent.bsh[j] = BSH.HOLD;
							break;
						default:
							tmp.agent.bsh[j] = BSH.HOLD;
							break;
					}
				}
			}
		}
	}

	private static int randInt(int min, int max){
		Random rand = new Random();
		int randomNum = rand.nextInt((max-min) + 1) + min;
		return randomNum;
	}
}