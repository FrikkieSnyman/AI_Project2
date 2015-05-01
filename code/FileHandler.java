import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.Charset;

public class FileHandler{

	private Chart generatedChart;

	public FileHandler(String path, Chart _generatedChart){
		List<String> lines = null;			
		
		try{
			lines = Files.readAllLines(Paths.get(path), Charset.defaultCharset());
		} catch (IOException e){
			System.out.println("Problem opening file");
			e.printStackTrace();
		}

		_generatedChart = new Chart(lines);
		// _generatedChart = generatedChart.renko();
	}
}