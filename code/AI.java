/**
 * AI class which holds an array of the charts and a reference to the best trading agent thus far
 * @author Frikkie Snyman - 13028741
 */
public class AI{
	Chart[] chart;
	Agent bestAgent;
	/**
	 * Constructor for AI class
	 * @param  chart     Array of renko charts to be used in by trading agents
	 * @param  bestAgent Where best agent must be stored
	 */
	public AI(Chart[] chart, Agent bestAgent){
		this.chart = chart;
		this.bestAgent = bestAgent;
	}


}