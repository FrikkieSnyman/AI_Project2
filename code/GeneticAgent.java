public class GeneticAgent extends Thread{
	
	public Agent agent = null;
	public Chart chart = null;
	public boolean generationDone = false;

	public GeneticAgent(){

	}

	public void run(){
		generationDone = false;
		System.out.println("Calculation generation...");
		generationDone = true;
	}
}