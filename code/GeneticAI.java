public class GeneticAI extends AI{

	public GeneticAI(Chart[] chart, Agent bestAgent, Integer population, Integer generations, Integer selectionStrategy, Integer tournamentSize, Double crossover, Double mutation, Double gap){
		super(chart,bestAgent);

		GeneticAgent[] agents = new GeneticAgent[population];
		
		for (int j = 0; j < agents.length; ++j){
			agents[j] = new GeneticAgent();
			agents[j].agent = new Agent();
		}

		for (int i = 0; i < this.chart.length; ++i){
			Chart currentChart = super.chart[i];
			
			for (int g = 0; g < generations; ++g){
				boolean block = true;

				for (int j = 0; j < population; ++j){
					Agent tmp = agents[j].agent;
					agents[j] = new GeneticAgent();
					agents[j].agent = tmp;
					agents[j].chart = currentChart;
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

				calculateNextGeneration(agents, selectionStrategy, tournamentSize, crossover, mutation, gap);
			}
		}
	}

	private void calculateNextGeneration(GeneticAgent[] agents, Integer selectionStrategy, Integer tournamentSize, Double crossover, Double mutation, Double gap){

	}
}