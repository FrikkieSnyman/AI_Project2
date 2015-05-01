import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.Charset;

public class FileHandler{

	private Chart generatedChart;

	public FileHandler(String path){
		List<String> lines = null;			
		
		try{
			lines = Files.readAllLines(Paths.get(path), Charset.defaultCharset());
		} catch (IOException e){
			System.out.println("Problem opening file");
			e.printStackTrace();
		}

		generatedChart = new Chart(lines);
		// _generatedChart = generatedChart.renko();
	}

	public Chart getChart(){
		return generatedChart;
	}
}