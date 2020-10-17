package audinss.servico;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import audinss.entidades.AliquotasMes;
import audinss.entidades.Movimento;
import audinss.entidades.TotalMesCalculado;
import audinss.repository.AliquotasMesRepository;
import audinss.repository.MovimentoRepository;

@Service
public class CalculoMesService {
	

	
	@Autowired
	private AliquotasMesRepository repoAliq;
	
	@Autowired
	private MovimentoRepository repoMov;
	
	
   public TotalMesCalculado calcularMes(int ano, int mes, int clienteid,
			String legislacao) {
	   try {
		   
		   TotalMesCalculado totalMes = new TotalMesCalculado();
		   
		  
			
			//pegar aliquota
			AliquotasMes al = repoAliq.buscarAliquotaPorMesAnoClienteIdLegislacaoMovimento(mes, ano, clienteid, legislacao);
			Double totalProventoIntegralRecuperar = 0.00;
		 if (al != null) {	
			 // buscar movimentos
			 List<Movimento> mvs = repoMov.findByAliquotasMesIdAndLegislacaoOrderByCodigoAsc(al.getId(), legislacao);
			totalMes.setPatrimonialPorcentagem(al.getInssEmpresa());
			totalMes.setRatPorcentagem(al.getInssRat());
			totalMes.setTerceirosPorcentagem(al.getInssTerceiros());
			
			
			
			for (Movimento m: mvs) {
				if (m.getValorRecuperar() > 0) {
					// adiciona o valor a recuperar somente da empresa(patronal)
					if (m.getValorRecuperar() != 0) {
						totalProventoIntegralRecuperar += m.getValorProvento();
					}
				
				}
				totalMes.setTotalProvento(totalMes.getTotalProvento() + m.getValorProvento()); ;
				totalMes.setBaseCalculo(totalMes.getBaseCalculo() + m.getBaseCalcInss());
				totalMes.setValorInss(totalMes.getValorInss() + m.getValorInssFp());
				totalMes.setPagar(totalMes.getPagar() + m.getValorPagar());
				totalMes.setRecuperar(totalMes.getRecuperar() + m.getValorRecuperar());
				// calcula a o valor total para descontar do total da base de calculo
				if (m.getFc().equals("NT") && m.getFp().equals("T")) {
					totalMes.setDescontoBase( totalMes.getDescontoBase() + m.getBaseCalcInss());
				}
			}
			

		    if (al.getInssEmpresa() != null && al.getInssEmpresa() > 0 
		    		&& totalProventoIntegralRecuperar > 0) {
		    	totalMes.setVrOriginalPatronal((totalProventoIntegralRecuperar * al.getInssEmpresa()) / 100);
		    }
		    if (al.getInssTerceiros() != null && al.getInssTerceiros() > 0 
		    		&& totalProventoIntegralRecuperar > 0) {
		    	totalMes.setVrOriginalTerceiros((totalProventoIntegralRecuperar * al.getInssTerceiros()) / 100);
		    }
		    if (al.getInssRat() != null && al.getInssRat() > 0 
		    		&& totalProventoIntegralRecuperar > 0) {
		    	totalMes.setVrOriginalRat((totalProventoIntegralRecuperar * al.getInssRat()) / 100);
		    }
		 }
		 
		 
			return totalMes;
		
	} catch (Exception e) {
		return null;
	}
   }
	
	
	

}//fecha classe
