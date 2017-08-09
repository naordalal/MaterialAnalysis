package MainPackage;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		String fileName = "C:/Users/naordalal/Desktop/SO_2003_customer_OPEN_SO_clean.txt";
		 try {
			for (String line : Files.readAllLines(Paths.get(fileName),Charset.forName("IBM862")))
			 {
				 System.out.println(line.split("\\|").length);
				List<String> x = Arrays.asList(line.split("\\|")).stream().map(s -> s.trim()).filter(s->!s.equals("")).collect(Collectors.toList());
				 x.stream().forEach(s -> System.out.print(s + "|"));
			 }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
