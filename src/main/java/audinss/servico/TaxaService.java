package audinss.servico;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import audinss.DTO.TaxasSelicDTO;
import audinss.entidades.TaxaSelic;
import audinss.repository.TaxaSelicRepository;
import audinss.response.Response;

@Service
public class TaxaService {
	
	
	
	private TaxaSelicRepository repo;
	
	
	
	
	
	
	public TaxaService(TaxaSelicRepository repo) {
	   this.repo = repo;	
	}

	private static final String ENDERECO_RECEITA_ACUMULADO_SELIC = 
			"https://receita.economia.gov.br/orientacao/tributaria/pagamentos-e-parcelamentos/taxa-de-juros-selic";
	
	
	
	
	
	
	
	@Scheduled(cron = "0 0 23 2 * *")// segundos | minutos | horas | dia do mês | mês | dias da semana
	public boolean atualizarTaxasSelic() {
		  try {
				
		    	Document doc = Jsoup
		    			 .connect(ENDERECO_RECEITA_ACUMULADO_SELIC)
		    			 .get();
				
				Element tables = doc.getElementsByClass("listing").get(0);
				 
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
							Float taxa = 0.00f;
							try {
								taxa = Float.parseFloat(
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
							
							if (taxa > 0) {
								

								if (!repo.existsByAnoAndMes(ano, rowIndex)) {
									TaxaSelic tx = new TaxaSelic();
									tx.setAno(ano);
									tx.setMes(rowIndex);
									tx.setTaxa(taxa);
									repo.save(tx);
								}
								
							}
							
							
						}
						
					}
					
					 
				 }
				 return true;
				
			} catch (IOException e) {
				System.out.println(e.getMessage());
				return false;
			}
			}
	
	
	
	public Response<List<TaxasSelicDTO>> getAllTaxasDTO() {
		Response<List<TaxasSelicDTO>> response = new Response<>();
		try {
			atualizarTaxasSelic();
			List<TaxasSelicDTO> taxasDto = new ArrayList<>();
			for (int ano = 2070; ano >= 1995; ano--) {
				TaxasSelicDTO txdto = new TaxasSelicDTO();
				if (repo.existsByAno(ano)) {
					txdto.setAno(ano);
				  for (int mes = 1; mes <= 12; mes++) {
					  TaxaSelic taxa = repo.findByAnoAndMes(ano, mes);
					  if (taxa != null) {
						  txdto.getTaxas().add(taxa.getTaxa());
					  }
				  }
				  taxasDto.add(txdto);
				}
			}
		
		   	
			response.setDados(taxasDto);
			return response;
		} catch (Exception e) {
			response.getErros().add("Ocorreu o seguinte erro ao tentar consultar as taxas selic: "+e.getMessage());
			return response;
		}
	}

	
	
	
}//fecha classe
