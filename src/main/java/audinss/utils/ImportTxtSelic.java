package audinss.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ImportTxtSelic {

	public static void main(String[] args) throws IOException {

		try (BufferedReader reader = new BufferedReader(
				new FileReader("c:\\selic\\selic_janeiro_de_1995_a_dezembro_de_2003.txt"))) {
			
            
			//String s = "";
			int mes = 1;
			String line;
			while ((line = reader.readLine()) != null) {
				//System.out.println(line);
				String[] taxas = line.split("%");
				int anoInicio = 1995;
				for (String tx: taxas) {
					System.out.println("ano " + anoInicio + ", mês "+ mes + ", taxa "+ tx.trim().replace(",", "."));
					anoInicio += 1;
				}
				mes += 1;
				
			}

		}

	}

}
