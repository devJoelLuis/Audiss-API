package audinss.utils;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class ExtractSelic {

	public static void main(String[] args) {
		
       try {
		
    	 Document doc = Jsoup.connect("https://receita.economia.gov.br/orientacao/tributaria/pagamentos-e-parcelamentos/taxa-de-juros-selic").get();
		
		 Element tables = doc.getElementsByClass("listing").get(0);
		 System.out.println(tables);
		 System.out.println("");
		 System.out.println("----------------------------------------------------------");
		 System.out.println("");
		 
		 int ano = 0;
		for (int colAno = 1; ano > -1; colAno++) {
			
			try {
			 ano = Integer.parseInt(tables.getElementsByTag("tr")
					.first().getElementsByTag("td").get(colAno).getElementsByTag("b").first().text());
			} catch (Exception e) {
			  ano = -1;	
			}
			
			if ( ano > 0 ) {
				System.out.println(ano);
				// pegar o valor dos meses
				
				for ( int rowIndex = 1; rowIndex < 13  ; rowIndex++) {
					Double taxa = 0.00;
					try {
						taxa = Double.parseDouble(
								tables.getElementsByTag("tr")
								      .get(rowIndex)
								      .getElementsByTag("td")
								      .get(colAno).text()
								      .replace(",", ".")
								      .replace("%", "")
								);
					} catch (Exception e) {
						
					}
					System.out.println("Ano "+ ano +" Mes: "+ rowIndex +" taxa: "+ taxa);
					
				}
				
				System.out.println("");
				System.out.println("");
			}
			
			 
		 }
		 
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}

}
