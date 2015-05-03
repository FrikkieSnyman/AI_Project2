import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.Charset;
/**
 * Class which handles files and generates a chart for them
 * @author Frikkie Snyman - 13028741
 */
public class FileHandler{

	private Chart generatedChart;
	/**
	 * FileHandler constructor
	 * @param  path String path to fil
	 */
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
	/**
	 * Gets the generated chart
	 * @return Chart that was generated from the given file
	 */
	public Chart getChart(){
		return generatedChart;
	}
}