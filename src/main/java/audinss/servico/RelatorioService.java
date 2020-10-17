package audinss.servico;

import java.io.InputStream;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Service;

import audinss.DTO.RelatorioAnexoUnicoDTO;
import audinss.DTO.RelatorioDemostrativoCreditoDTO;
import audinss.entidades.Artigo;
import audinss.entidades.Cliente;
import audinss.entidades.Legislacao;
import audinss.entidades.TaxaSelic;
import audinss.entidades.TotalMesCalculado;
import audinss.entidades.Usuario;
import audinss.repository.ClienteRepository;
import audinss.repository.LegislacaoRepository;
import audinss.repository.TaxaSelicRepository;
import audinss.repository.UsuarioRepository;
import audinss.response.Response;
import audinss.utils.UtilsMethods;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class RelatorioService {
	
	@Autowired
	private TaxaSelicRepository repoTaxa;
	
	@Autowired
	private CalculoMesService serviceCalculoMes;
	
	@Autowired
	private ClienteRepository repocli;
	
		
	@Autowired
	private UsuarioRepository repoUser;
	
	@Autowired
	private LegislacaoRepository repoLeg;
	
		
	private String idUsuarioLogado = "";
	private List<String> permissoesUsuario = new ArrayList<>();
	
	
	
	 //PEGAR USUÁRIO LOGADO FAZENDO REQUISIÇÕES NA API
	@SuppressWarnings("unchecked")
	public void pegarNomeEIdUsuarioLogado() throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();        
		Object details = authentication.getDetails();   
		if ( details instanceof OAuth2AuthenticationDetails ) {
		    OAuth2AuthenticationDetails oAuth2AuthenticationDetails = (OAuth2AuthenticationDetails)details;
			Map<String, Object> decodedDetails = (Map<String, Object>)oAuth2AuthenticationDetails.getDecodedDetails();
		    System.out.println(decodedDetails);
		    idUsuarioLogado = decodedDetails.get("iduser").toString();
		    permissoesUsuario = (List<String>) decodedDetails.get("authorities");
            
		}
       }

	
	
	
	

	// relatorio demonstrativo de crédito anual tema 163 do STF
	public Response<byte[]> relatorioCreditoDemostrativoTema163(String dataFimSelic, int ano, int clienteid,
			String legislacao) {
		Response<byte[]> response = new Response<>();
		
		
		try {
			
			verificarPermissaoUsuario(clienteid);
			
			Cliente cliente = getCliente(clienteid);
			
			NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

			Map<String, Object> params = new HashMap<>();
			//byte[] bytesImg = Base64.decodeBase64(al.getCliente().getLogotipo());
			params.put("LOGO", cliente.getLogotipo().replace("data:application/octet-stream;base64,",""));
			params.put("MES", "1" );
			params.put("ANO", String.format("%d", ano));
            List<RelatorioDemostrativoCreditoDTO> fields = new ArrayList<>();
            
            //data fim aliquota
            LocalDate dataFimAcumulado = LocalDate.parse(dataFimSelic);
			
				
				//data inicio do calculo
	            LocalDate dataInicio = LocalDate.of(ano, 3, 1); // calculo começa sempre no terceiro mês
	            
	            Double totalVrOriginal = 0.00;
	            Double totalJurosDoMes = 0.00;
	            Double totalCalculado = 0.00;
	            float acumuladoTerceiroMes = 0;
	            String acumulado = "";
	            
				for (int i = 1; i <= 13; i++) {
					String mesRef = UtilsMethods.mesRef(i, ano);
					float acumuladoDouble = getAcumuladoSelic(dataInicio.getMonthValue(), dataInicio.getYear(), dataFimAcumulado );
					if (i == 1) { 
						//data inicio  = fevereiro do ano seguinte
					  acumuladoTerceiroMes = getAcumuladoSelic( 2, dataInicio.getYear() + 1, dataFimAcumulado );	
					}
					TotalMesCalculado totalMes = serviceCalculoMes.calcularMes(ano, i, clienteid, legislacao);
					
					totalVrOriginal += totalMes.getRecuperar();
					
					// criar as strings para o relatório
					String vrOriginal = formatter.format(totalMes.getRecuperar());
					String jurosMes = "R$ 0,00";
					String totalMesString = "R$ 0,00";
					String porcPatronal = String.format("%.2f", totalMes.getPatrimonialPorcentagem()) + "%" ;
					String porcRat = String.format("%.2f", totalMes.getRatPorcentagem()) + "%" ;
					String porcTerceiros = String.format("%.2f", totalMes.getTerceirosPorcentagem()) + "%" ;
					String vrOriginalPatronal = formatter.format(totalMes.getVrOriginalPatronal());
					String vrOriginalTerceiros = formatter.format(totalMes.getVrOriginalTerceiros());
					String vrOriginalRat = formatter.format(totalMes.getVrOriginalRat());
					String jurosMesPatronal = "R$ 0,00";
					String jurosMesTerceiros = "R$ 0,00";
					String jurosMesRat = "R$ 0,00";
					String totalPatronal = "R$ 0,00";
					String totalTerceiros = "R$ 0,00";
					String totalRat = "R$ 0,00";
					
					if (i < 13) {
						//se vrOriginal for maior que zero
						if (totalMes.getRecuperar() > 0 && acumuladoDouble > 0) {
							Double porcentagem = (totalMes.getRecuperar() * acumuladoDouble) / 100;
							totalJurosDoMes += porcentagem;
							jurosMes = formatter.format(porcentagem);
							Double subTotal = porcentagem + totalMes.getRecuperar();
							totalCalculado += subTotal;
							totalMesString = formatter.format(subTotal);
						}
						//se tiver valor no juroMesPatronal adiciona na variável
						if (totalMes.getVrOriginalPatronal() > 0) {
							Double temp = (totalMes.getVrOriginalPatronal() * acumuladoDouble) / 100;
							jurosMesPatronal = formatter.format(temp);
							totalPatronal = formatter.format(temp + totalMes.getVrOriginalPatronal());
						}
						//se tiver valor no juroMesTerceiros adiciona na variável
						if (totalMes.getVrOriginalTerceiros() > 0) {
							Double temp = (totalMes.getVrOriginalTerceiros() * acumuladoDouble) / 100 ;
							jurosMesTerceiros = formatter.format(temp);
							totalTerceiros = formatter.format(temp + totalMes.getVrOriginalTerceiros());
						}
						//se tiver valor no juroMesRat adiciona na variável
						if (totalMes.getVrOriginalRat() > 0) {
							Double temp = (totalMes.getVrOriginalRat() * acumuladoDouble) / 100 ;
							jurosMesRat = formatter.format(temp);
							totalRat = formatter.format(temp + totalMes.getVrOriginalRat());
							
						}
						
					    acumulado = String.format("%.2f", acumuladoDouble).replace(".", ",") + "%";
					} else {
						//se vrOriginal for maior que zero
						if (totalMes.getRecuperar() > 0 && acumuladoTerceiroMes > 0) {
							Double porcentagem = (totalMes.getRecuperar() * acumuladoTerceiroMes) / 100;
							totalJurosDoMes += porcentagem;
							jurosMes = formatter.format(porcentagem);
							Double subTotal = porcentagem + totalMes.getRecuperar();
							totalCalculado += subTotal;
							totalMesString = formatter.format(subTotal);
						}
						//se tiver valor no juroMesPatronal adiciona na variável
						if (totalMes.getVrOriginalPatronal() > 0) {
							Double temp = (totalMes.getVrOriginalPatronal() * acumuladoTerceiroMes) / 100;
							jurosMesPatronal = formatter.format(temp);
							totalPatronal = formatter.format(temp + totalMes.getVrOriginalPatronal());
						}
						//se tiver valor no juroMesTerceiros adiciona na variável
						if (totalMes.getVrOriginalTerceiros() > 0) {
							Double temp = (totalMes.getVrOriginalTerceiros() * acumuladoTerceiroMes) / 100 ;
							jurosMesTerceiros = formatter.format(temp);
							totalTerceiros = formatter.format(temp + totalMes.getVrOriginalTerceiros());
						}
						//se tiver valor no juroMesRat adiciona na variável
						if (totalMes.getVrOriginalRat() > 0) {
							Double temp = (totalMes.getVrOriginalRat() * acumuladoTerceiroMes) / 100 ;
							jurosMesRat = formatter.format(temp);
							totalRat = formatter.format(temp + totalMes.getVrOriginalRat());
							
						}
						
					    acumulado = String.format("%.2f", acumuladoTerceiroMes).replace(".", ",") + "%";
					}
					
					RelatorioDemostrativoCreditoDTO field = new RelatorioDemostrativoCreditoDTO(mesRef, 
							acumulado, vrOriginal, jurosMes, totalMesString, porcPatronal, 
						porcRat, porcTerceiros, vrOriginalPatronal, vrOriginalRat, vrOriginalTerceiros, 
						jurosMesPatronal, jurosMesRat, jurosMesTerceiros, totalPatronal, totalRat, totalTerceiros);
					fields.add(field);
					dataInicio = dataInicio.plusMonths(1);
									
				}
				// List<Movimento> movs = getMovimentosAnoSelicIdAliquota
				
				params.put("TOTALVRORIGINAL", formatter.format(totalVrOriginal));
				params.put("JUROSDOMES", formatter.format(totalJurosDoMes));
				params.put("TOTAL", formatter.format(totalCalculado));
				params.put("LEGISLACAO", legislacao);
				
				//gerar o relatorio
				InputStream inputStream = this.getClass().getResourceAsStream("/relatorios/tema163.jasper");
				JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, params,
						new JRBeanCollectionDataSource(fields));
				response.setDados(JasperExportManager.exportReportToPdf(jasperPrint));
				return response;
			
           
            
         
		} catch (Exception e) {
			response.getErros().add("Ocorreu o seguinte erro ao tentar gerar o relatório demostrativo: " + e.getMessage());
			return response;
		}

	}// FECHA GERAR RELATORIO DEMONSTRATIVO
	
	
	
	
	
	private void verificarPermissaoUsuario(int clienteid) throws Exception {
		pegarNomeEIdUsuarioLogado();
		if (permissoesUsuario.contains("ROLE_ADMIN")) {
			return;
		}
		if (idUsuarioLogado == null || idUsuarioLogado.equals("")) {
			throw new Exception("não foi possível localizar o id do usuário logado");
		}
		int idusuario = Integer.parseInt(idUsuarioLogado);
		
		Usuario user = getUser(idusuario);	
		Cliente cli = getCliente(clienteid);
		
		if (cli.getFim().isAfter(LocalDate.now())) {
		  throw new Exception("A licença do cliente "+ cli.getNome()+" está expirada.");
		}
		
		if (user.getClientes().contains(cli)) {
			return;
		} else {
			throw new Exception("usuário sem permissão para gerar relatório do cliente com id: "+ clienteid);
		}
	}





	private Usuario getUser(int idusuario) throws Exception {
		Usuario user = repoUser.findById(idusuario)
				.orElseThrow(() -> new Exception("não foi possível encontrar um usuário logado com o id: "+ idusuario));
		return user;
	}





	// gerar relatório anexo único
	public Response<byte[]> relatorioAnexoUnico(String dataFimSelic, int ano, int clienteid,
			String legislacao) {
		Response<byte[]> response = new Response<>();
		try {
			
			verificarPermissaoUsuario(clienteid);
			
			Cliente cliente = getCliente(clienteid);
			NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
			
			

			Map<String, Object> params = new HashMap<>();
			params.put("LOGO", cliente.getLogotipo().replace("data:application/octet-stream;base64,",""));
			params.put("ANO", String.format("%d", ano));
			
			List<RelatorioAnexoUnicoDTO> fields = new ArrayList<>();
			
			
			 //data fim aliquota
            LocalDate dataFimAcumulado = LocalDate.parse(dataFimSelic);
			
				
			//data inicio do calculo
	        LocalDate dataInicio = LocalDate.of(ano, 3, 1); // calculo começa sempre no terceiro mês
	            
	        Double totalVrOriginal = 0.00;
	        Double totalJurosDoMes = 0.00;
	        Double totalCalculado = 0.00;
	        float acumuladoTerceiroMes = 0;
	        String artigos = getArtigos(legislacao);
	        
	        for (int i = 1; i <= 13; i++) {
	        	
	        	String mesRef = UtilsMethods.mesRef(i, ano);
				float acumuladoDouble = getAcumuladoSelic(dataInicio.getMonthValue(), dataInicio.getYear(), dataFimAcumulado );
				TotalMesCalculado totalMes = serviceCalculoMes.calcularMes(ano, i, clienteid, legislacao);
				
				if (i == 13) {
				  // data inicio do calculo é fevereiro do ano seguinte	
				  acumuladoTerceiroMes = getAcumuladoSelic(2, dataInicio.getYear() + 1, dataFimAcumulado );
				}
				
				totalVrOriginal += totalMes.getRecuperar();
				
				// criar as strings para o relatório
				String vrOriginal = formatter.format(totalMes.getRecuperar());
				String totalMesString = "R$ 0,00";
				String vrOriginalPatronal = formatter.format(totalMes.getVrOriginalPatronal());
				String vrOriginalTerceiros = formatter.format(totalMes.getVrOriginalTerceiros());
				String vrOriginalRat = formatter.format(totalMes.getVrOriginalRat());
				//String jurosMesPatronal = "R$ 0,00";
				//String jurosMesTerceiros = "R$ 0,00";
				//String jurosMesRat = "R$ 0,00";
				String totalPatronal = "R$ 0,00";
				String totalTerceiros = "R$ 0,00";
				String totalRat = "R$ 0,00";
				
				if (i < 13) {
					//se vrOriginal for maior que zero
					if (totalMes.getRecuperar() > 0 && acumuladoDouble > 0) {
						Double porcentagem = (totalMes.getRecuperar() * acumuladoDouble) / 100;
						totalJurosDoMes += porcentagem;
						Double subTotal = porcentagem + totalMes.getRecuperar();
						totalCalculado += subTotal;
						totalMesString = formatter.format(subTotal);
					}
					//se tiver valor no juroMesPatronal adiciona na variável
					if (totalMes.getVrOriginalPatronal() > 0) {
						Double temp = (totalMes.getVrOriginalPatronal() * acumuladoDouble) / 100;
						//jurosMesPatronal = formatter.format(temp);
						totalPatronal = formatter.format(temp + totalMes.getVrOriginalPatronal());
					}
					//se tiver valor no juroMesTerceiros adiciona na variável
					if (totalMes.getVrOriginalTerceiros() > 0) {
						Double temp = (totalMes.getVrOriginalTerceiros() * acumuladoDouble) / 100 ;
						//jurosMesTerceiros = formatter.format(temp);
						totalTerceiros = formatter.format(temp + totalMes.getVrOriginalTerceiros());
					}
					//se tiver valor no juroMesRat adiciona na variável
					if (totalMes.getVrOriginalRat() > 0) {
						Double temp = (totalMes.getVrOriginalRat() * acumuladoDouble) / 100 ;
						//jurosMesRat = formatter.format(temp);
						totalRat = formatter.format(temp + totalMes.getVrOriginalRat());
						
					}
				} else {
					//calcula o mês 13
					//se vrOriginal for maior que zero
					if (totalMes.getRecuperar() > 0 && acumuladoTerceiroMes > 0) {
						Double porcentagem = (totalMes.getRecuperar() * acumuladoTerceiroMes) / 100;
						totalJurosDoMes += porcentagem;
						Double subTotal = porcentagem + totalMes.getRecuperar();
						totalCalculado += subTotal;
						totalMesString = formatter.format(subTotal);
					}
					//se tiver valor no juroMesPatronal adiciona na variável
					if (totalMes.getVrOriginalPatronal() > 0) {
						Double temp = (totalMes.getVrOriginalPatronal() * acumuladoTerceiroMes) / 100;
						//jurosMesPatronal = formatter.format(temp);
						totalPatronal = formatter.format(temp + totalMes.getVrOriginalPatronal());
					}
					//se tiver valor no juroMesTerceiros adiciona na variável
					if (totalMes.getVrOriginalTerceiros() > 0) {
						Double temp = (totalMes.getVrOriginalTerceiros() * acumuladoTerceiroMes) / 100 ;
						//jurosMesTerceiros = formatter.format(temp);
						totalTerceiros = formatter.format(temp + totalMes.getVrOriginalTerceiros());
					}
					//se tiver valor no juroMesRat adiciona na variável
					if (totalMes.getVrOriginalRat() > 0) {
						Double temp = (totalMes.getVrOriginalRat() * acumuladoTerceiroMes) / 100 ;
						//jurosMesRat = formatter.format(temp);
						totalRat = formatter.format(temp + totalMes.getVrOriginalRat());
						
					}
				}
				
				RelatorioAnexoUnicoDTO rau = new RelatorioAnexoUnicoDTO(mesRef, vrOriginal, totalMesString, 
						vrOriginalPatronal, vrOriginalRat, vrOriginalTerceiros, totalPatronal, 
						totalRat, totalTerceiros, artigos);
	        	
				fields.add(rau);
	        	
	        }//fecha for
	        
	        params.put("TOTALVRORIGINAL", formatter.format(totalVrOriginal));
			params.put("TOTAL", formatter.format(totalCalculado));
			params.put("LEGISLACAO", legislacao);
			
			
			
			//gerar o relatorio
			InputStream inputStream = this.getClass().getResourceAsStream("/relatorios/anexo_unico.jasper");
			JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, params,
					new JRBeanCollectionDataSource(fields));
			response.setDados(JasperExportManager.exportReportToPdf(jasperPrint));
			return response;
		
	   
			
		} catch (Exception e) {
			response.getErros().add("Ocorreu o seguinte erro ao tentar gerar o relatório anexo único: " + e.getMessage());
			return response;
		}
	}
	
	
	
	

	private String getArtigos(String legislacao) throws Exception {
		
		//pegar os artigos
		Legislacao leg = repoLeg.findByLegislacao(legislacao);
		
		StringBuilder build = new StringBuilder();
		int count = 1;
		for(Artigo am: leg.getArtigos()) {
			build.append(am.getDescricao());
			if (count < leg.getArtigos().size()) {
			   build.append(", e ");	
			}
			count++;
		}
		
		return build.toString();
	}





	private Cliente getCliente(int clienteid) throws Exception {
		Cliente cli = repocli.findById(clienteid).orElseThrow(
				()-> new Exception("não foi possível encontrar um cliente com o id "+ clienteid));
		return cli;
	}







	private float getAcumuladoSelic(int mes, int ano, LocalDate dataFimAcumulado) {
		
		float totalSelic = 0.00f;
		
		
		LocalDate dataInicio = LocalDate.of(ano, mes, 1);
		
		// buscar taxas
		do {
			TaxaSelic taxa = repoTaxa.findByAnoAndMes(dataInicio.getYear(), dataInicio.getMonthValue());
			if (taxa != null) totalSelic += taxa.getTaxa();
			dataInicio = dataInicio.plusMonths(1);			
		} while (dataInicio.isBefore(dataFimAcumulado));
		
		
		return totalSelic;
	}
	
	
	

}// fecha classe
