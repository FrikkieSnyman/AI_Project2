import java.util.*;
/**
 * Class for Hill Climb
 * @author Frikkie Snyman - 13028741
 */
public class HillClimbAI extends AI{
	public Agent initialAgent;
	/**
	 * Constructor for HillClimbAI class
	 * @param  chart     Array of charts on which trading must occur
	 * @param  bestAgent Reference to the bestAgent
	 */
	public HillClimbAI(Chart[] chart, Agent bestAgent){
		super(chart,bestAgent);
		initialAgent = new Agent();
	}
	/**
	 * Sets the algorithm in motion
	 * @return Returns the best agent as determined by algorithm
	 */
	public Agent start(){
		boolean best = false;
		long fitness = runThrough(initialAgent);
		System.out.println("Initial: " + fitness);
		Agent bestAgentSoFar = initialAgent;

		while (!best){
			LinkedList<Agent> neighbours = determineNeighbours(bestAgentSoFar);
			Agent tmp = bestAgentSoFar;
			for (int i = 0; i < neighbours.size(); ++i){
				Agent testNeighbour = neighbours.get(i);
				long neighbourFitness = runThrough(testNeighbour);
				System.out.println(Arrays.toString(neighbours.get(i).bsh) + "\t " + neighbourFitness);
				if (neighbourFitness > fitness){
					System.out.println("Better neighbour found");
					best = false;
					bestAgentSoFar = testNeighbour;
					fitness = neighbourFitness;
					break;
				}
			}
			if (compareAgents(bestAgentSoFar,tmp)){	// No better neighbour found
				best = true;
			}
		}
		return bestAgentSoFar;
	}
	/**
	 * Compares agents for equality
	 * @param  agent1 Agent to be compared
	 * @param  agent2 Agent to be compared to
	 * @return        true if equal, false otherwise
	 */
	private boolean compareAgents(Agent agent1, Agent agent2){
		for (int i = 0; i < 32; ++i){
			if (agent1.bsh[i] != agent2.bsh[i]){
				return false;
			}
		}

		return true;
	}
	/**
	 * Creates a list of neighbours
	 * @param  agent Agent on which neighbours are based
	 * @return       LinkedList of Agent
	 */
	private LinkedList<Agent> determineNeighbours(Agent agent){
		LinkedList<Agent> neigbours = new LinkedList<>();
		
		for (int i = 0; i < 32; ++i){
			Agent temp = new Agent();
			for (int j = 0; j < 32; ++j){
				temp.bsh[j] = agent.bsh[j];
			}

			for (int j = 0; j < i+1; ++j){
				temp.bsh[j] = BSH.values()[(temp.bsh[j].ordinal()+(i+j))%3];
			}
			neigbours.add(temp);
		}
		return neigbours;
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
	/**
	 * Trades through Charts and determines fitness
	 * @param  agent Agent that trades
	 * @return       Fitness of agent
	 */
	private long runThrough(Agent agent){
		for (int k = 0; k < chart.length; ++k){
			LinkedList<Brick> renkoChart = chart[k].getRenkoChart();
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
			agent.fitness += determineFitness(renkoChart,agent);
			agent.money = 100000*100;
		}
		return agent.fitness;
	}

	/**
	 * Determins the fitness of the agent after trading
	 * @param  renkoChart Chart from which fitness must be evaluated
	 * @return            Fitness of trader
	 */
	private long determineFitness(LinkedList<Brick> renkoChart, Agent agent){
		Integer lastDay = renkoChart.get(renkoChart.size()-1).day.highPrice + renkoChart.get(renkoChart.size()-1).day.lowPrice;
		Double average = (double)lastDay/2;
		return (long)(agent.money + (agent.shares * average));
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
				buy(day,agent);
				break;
			case SELL:
				sell(day,agent);
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
	 * @param agent Agent that buys
	 */
	private void buy(Brick day, Agent agent){
		Integer sum = day.day.highPrice + day.day.lowPrice;
		Integer sharePrice = (int)Math.round((double)sum/2);

		Integer suggestedAmount = (int)Math.round(agent.money / sharePrice);
		boolean pass = bought(suggestedAmount,sharePrice,agent);
		while(!pass){
			pass = bought(--suggestedAmount,sharePrice,agent);
		}
		agent.money -= sharePrice * suggestedAmount;
		agent.shares += suggestedAmount;

		Integer tradeAmount = sharePrice * suggestedAmount;

		Integer stt = (int) Math.round(0.0025*tradeAmount);
		Integer brokerageFee = (int) Math.round(0.005*tradeAmount);
		if (brokerageFee < 7000){
			brokerageFee = 7000;
		}
		Integer strate = 1158;
		Integer ipl = (int) Math.round(0.000002*tradeAmount);
		Integer vat = (int) Math.round(0.14 * (stt + brokerageFee + strate + ipl));
		Integer fees = vat + stt + brokerageFee + strate + ipl;

		agent.money -= fees;
	}
	/**
	 * Determines wheter shares can be bought
	 * @param  suggestedAmount Amount of shares that is suggested to be bought
	 * @param  sharePrice      Price of shares
	 * @param agent 		   Agent which buys
	 * @return                 Returns true if the amount of shares can be bought, else returns false
	 */
	private boolean bought(Integer suggestedAmount, Integer sharePrice, Agent agent){
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

		return (agent.money > total);
	}
	/**
	 * Sells shares
	 * @param day 	Day on which shares are sold
	 * @param agent Agent that sells
	 */
	private void sell(Brick day, Agent agent){
		Integer sum = day.day.highPrice + day.day.lowPrice;
		Integer sharePrice = (int)Math.round((double)sum/2);
		Integer sellValue = sharePrice * agent.shares;

		Integer brokerageFee = (int) Math.round(0.005*sellValue);
		if (brokerageFee < 7000){
			brokerageFee = 7000;
		}
		Integer strate = 1158;
		Integer ipl = (int) Math.round(0.000002*sellValue);
		Integer vat = (int) Math.round(0.14 * (brokerageFee + strate + ipl));

		Integer total = sellValue - vat - brokerageFee - strate - ipl;
		agent.shares = 0;
		agent.money += total;
	}
}