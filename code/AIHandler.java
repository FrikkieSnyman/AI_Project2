public class AIHandler{
	private Chart[] chart;
	private AI ai;
	private Agent bestAgent;

	public AIHandler(Chart[] _chart){
		chart = _chart;
		// ai = new AI(chart,bestAgent);
	}

	public void ga(Integer population, Integer generations, Integer selectionStrategy, Integer tournamentSize, Double crossover, Double mutation, Double gap){
		GeneticAI geneticAi = new GeneticAI(chart, bestAgent, population, generations, selectionStrategy, tournamentSize, crossover, mutation, gap);

	}
}