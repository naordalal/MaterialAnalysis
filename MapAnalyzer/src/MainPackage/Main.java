package MainPackage;

import java.io.IOException;

public class Main {

	public static void main(String[] args) 
	{
		/*String fileName = "C:/Users/naordalal/Desktop/SO_ALL_customer_ALL_SO_clean.txt";
		 try {
			for (String line : Files.readAllLines(Paths.get(fileName),Charset.forName("IBM862")))
			 {
				 System.out.println(line.split("\\|").length);
				List<String> x = Arrays.asList(line.split("\\|")).stream().map(s -> s.trim()).filter(s->!s.equals("")).collect(Collectors.toList());
				 x.stream().forEach(s -> System.out.print(s + "|"));
			 }
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		 
		 Analyzer analyzer = new Analyzer();
		 try {
			analyzer.analyze();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
