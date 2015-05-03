import java.util.Random;
import java.util.*;
/**
 * Class for Genetic Algorithm
 * @author Frikkie Snyman - 13028741
 */
public class GeneticAI extends AI{
	/**
	 * Constructor for GeneticAI. Also starts execution of evolution
	 * @param  chart             Array of charts to evolve on
	 * @param  bestAgent         Reference to best agent
	 * @param  bestAgents        Array of best agents - not used by class
	 * @param population        Size of the population to be used by the Genetic Algorithm
	 * @param generations       The amount of generations throught which the traders must evolve
	 * @param selectionStrategy Selection Strategy to be used by the crossover function. 1 = random, 2 = tournament, 3 = fitness-proportional, 4 = rank-based
	 * @param tournamentSize    Size of the tournament sample, ignored if selection strategy is not 2
	 * @param crossover         Probability of crossover occuring
	 * @param mutation          Probability of mutation occuring
	 * @param gap               Percentage of generation that survives due to elitism
	 */
	public GeneticAI(Chart[] chart, Agent bestAgent,Agent[] bestAgents, Integer population, Integer generations, Integer selectionStrategy, Integer tournamentSize, Double crossover, Double mutation, Double gap){
		super(chart,bestAgent);

		GeneticAgent[] agents = new GeneticAgent[population];
		
		for (int j = 0; j < agents.length; ++j){
			agents[j] = new GeneticAgent(j);
			agents[j].agent = new Agent();
		}
		bestAgent = new Agent();

		for (int g = 0; g < generations; ++g){
			// for (int i = 0; i < this.chart.length; ++i){
			// 	Chart currentChart = super.chart[i];
			
				boolean block = true;

				for (int j = 0; j < population; ++j){
					Agent tmp = agents[j].agent;
					agents[j] = new GeneticAgent(j);
					agents[j].agent = tmp;
					agents[j].agent.fitness = 0;
					agents[j].chart = super.chart;
					// agents[j].chart = currentChart;
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
					if (agents[j].fitness > bestAgent.fitness){
						this.bestAgent = agents[j].agent;
					}
				}
				calculateNextGeneration(agents, population, selectionStrategy, tournamentSize, crossover, mutation, gap);
			// }

		}
	}
	/**
	 * Calculates the next generation by evolving the current one
	 * @param agents            Array of trader agents to evolve
	 * @param population        Size of population
	 * @param selectionStrategy Selection strategy to be used
	 * @param tournamentSize    Size of tournament if tournament selection is in use, ignored otherwise
	 * @param crossover         Probability of crossover occuring
	 * @param mutation          Probability of mutation occuring
	 * @param gap               Percentage of population that must survive due to elitism
	 */
	private void calculateNextGeneration(GeneticAgent[] agents, Integer population, Integer selectionStrategy, Integer tournamentSize, Double crossover, Double mutation, Double gap){
		GeneticAgent[] oldGeneration = new GeneticAgent[population];
		for (int i = 0; i < population; ++i){
			oldGeneration[i] = new GeneticAgent(i);
			oldGeneration[i].agent = new Agent();
			for (int j = 0; j < 32; ++j){
				oldGeneration[i].agent.bsh[j] = agents[i].agent.bsh[j];
				oldGeneration[i].fitness = agents[i].fitness;
				oldGeneration[i].agent.fitness = agents[i].agent.fitness;
			}
		}
		GeneticAgent[] survivors = elitism(agents,gap);
		for (int i = 0; i < population-1; i += 2 ){
			GeneticAgent[] parents = selectParents(oldGeneration,selectionStrategy,tournamentSize);
			crossover(agents,parents,crossover,i);
		}
		mutate(agents,mutation);
		replaceWeaklings(agents,survivors, gap);
	}
	/**
	 * Generates a set of parents to be used for crossover
	 * @param  agents            Array of trader agents
	 * @param  selectionStrategy Selection strategy in use: 1 = random, 2 = tournament, 3 = fitness-proportional, 4 = Rank-based
	 * @param  tournamentSize    Size of tournament if applicable, ignored otherwise
	 * @return                   Array of 2 parent agents
	 */
	private GeneticAgent[] selectParents(GeneticAgent[] agents, Integer selectionStrategy, Integer tournamentSize){
		GeneticAgent[] parents = new GeneticAgent[2];
		int rand;
		int rand2;
		switch (selectionStrategy) {
			case 1:		// Random
				rand = randInt(0,agents.length-1);
				parents[0] = agents[rand];
				rand2 = randInt(0,agents.length-1);
				while (rand2 == rand){
					rand2 = randInt(0,agents.length-1);
				}
				parents[1] = agents[rand2];
				break;
			case 2:		// Tournament
				GeneticAgent[] chosenOnes = new GeneticAgent[tournamentSize];
				for (int i = 0; i < tournamentSize; ++i){
					rand = randInt(0,agents.length-1);
					chosenOnes[i] = agents[rand];
				}

				GeneticAgent best = chosenOnes[0];
				for (int i = 1; i < tournamentSize; ++i){
					if (chosenOnes[i].agent.fitness > best.agent.fitness){
						best = chosenOnes[i];
					}
				}

				for (int i = 0; i < tournamentSize; ++i){
					rand = randInt(0,agents.length-1);
					chosenOnes[i] = agents[rand];
				}

				GeneticAgent best2 = chosenOnes[0];
				for (int i = 1; i < tournamentSize; ++i){
					if (chosenOnes[i].agent.fitness > best2.agent.fitness){
						best2 = chosenOnes[i];
					}
				}
				parents[0] = best;
				parents[1] = best2;
				break;
			case 3:		// Fitness-proportional
				long totalFitness = 0;
				for (int i = 0; i < agents.length; ++i){
					totalFitness += agents[i].fitness;

				}
				Double totFit = (double) totalFitness/100;
				LinkedList<Integer> wheel = new LinkedList<>();

				for (int i = 0; i < agents.length; ++i){
					int size = (int) Math.round((agents[i].fitness/totFit)*100);

					for (int j = 0; j < size; ++j){
						wheel.add(i);
					}
				}

				rand = randInt(0,wheel.size()-1);
				parents[0] = agents[wheel.get(rand)];
				rand2 = randInt(0,wheel.size()-1);
				while (rand2 == rand){
					rand2 = randInt(0,wheel.size()-1);
				}
				parents[1] = agents[wheel.get(rand2)];
			
				break;
			case 4:		// Rank-based
				sortBasedOnFitness(agents);
				LinkedList<GeneticAgent> ranks = new LinkedList<>();
				for (int i = 0; i < agents.length; ++i){
					for (int j = 0; j <= i; ++j){
						ranks.add(agents[i]);
					}
				}

				rand = randInt(0,ranks.size()-1);
				parents[0] = ranks.get(rand);
				rand2 = randInt(0,ranks.size()-1);
				while (rand2 == rand){
					rand2 = randInt(0,ranks.size()-1);
				}
				parents[1] = ranks.get(rand2);
				break;
			default:
				break;
			
		}
		return parents;
	}
	/**
	 * Sorts an array of agents based on their fitness
	 * @param agents Array of agents to be sorted
	 */
	private void sortBasedOnFitness(GeneticAgent[] agents){
		int length = agents.length;
		boolean flag = true;
		GeneticAgent temp;

		while (flag){
			flag = false;
			for (int j = 0; j < agents.length-1; ++j){
				if (agents[j].fitness > agents[j+1].fitness){
					temp = agents[j];
					agents[j] = agents[j+1];
					agents[j+1] = temp;
					flag = true;
				}
			}

		}
	}
	/**
	 * Replaces weakest agents by the fittest agents due to elitism
	 * @param agents    Array of agents
	 * @param survivors Fittest agents
	 * @param gap       Percentage of population that must survive
	 */
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
	/**
	 * Elitism function that determines which agents were the strongest due to fitness
	 * @param  agents Array of agents on which to determine the fittest
	 * @param  gap    Percentage of population that must survive
	 * @return        Array of agents that were deemed fittest
	 */
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
	/**
	 * Performs crossover on agents
	 * @param child     Array of children which must undergo crossover
	 * @param parents   Array of parents which must be crossed
	 * @param crossover Probability of crossover occuring
	 * @param index     Index of children in the child array
	 */
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
	/**
	 * Mutates agents
	 * @param children Array to be mutated
	 * @param mutation Probability of mutation occuring
	 */
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
	/**
	 * Generates a random integer within a range
	 * @param  min Minimum of range inclusive
	 * @param  max Maximum of range inclusive
	 * @return     A random integer within range
	 */
	private static int randInt(int min, int max){
		Random rand = new Random();
		int randomNum = rand.nextInt((max-min) + 1) + min;
		return randomNum;
	}
}