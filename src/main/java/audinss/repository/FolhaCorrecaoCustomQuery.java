package audinss.repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import audinss.DTO.FcLegislacaoDtInicioDTO;

@Repository
public class FolhaCorrecaoCustomQuery {
	
	private FolhaCorrecaoRepository repoFc;

	public FolhaCorrecaoCustomQuery(FolhaCorrecaoRepository repoFc) {
		this.repoFc = repoFc;
	}
	
	//List<FcLegislacaoDtInicioDTO>
	public List<FcLegislacaoDtInicioDTO> buscarPorLegislacaoEClienteIdFC(Integer idcliente) {
		   
		  List<Object[]> objs =  repoFc.buscarLegislacoesFcClienteId(idcliente);
		  
		  System.out.println(objs);
		  
		  List<FcLegislacaoDtInicioDTO> ret = new ArrayList<>();
		  for (Object[] obj: objs) {
			  FcLegislacaoDtInicioDTO flc = new FcLegislacaoDtInicioDTO();
			  flc.setLegislacaoId((Integer)obj[0]);
			  flc.setDataInicio((LocalDate)((Date) obj[1]).toLocalDate());
			  ret.add(flc);
		  }
		  return ret;
		  
	}
	
	

}
