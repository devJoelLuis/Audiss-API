package audinss.servico;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import audinss.entidades.AliquotasMes;
import audinss.repository.AliquotasMesRepository;
import audinss.response.Response;

@Service
public class AliquotasMesService {
	
	@Autowired
	private AliquotasMesRepository repo;
	
	
	// get all aliquotas cliente id e ano
	public Response<List<AliquotasMes>> getAllClienteIdFp(int idCliente, int ano) {
		Response<List<AliquotasMes>> response = new Response<>();
		try {
			List<AliquotasMes> als = repo.findByAnoAndFolhaTipoAndClienteIdOrderByMesAsc(ano, "FP", idCliente);
			response.setDados(als);
			return response;
		} catch (Exception e) {
			response.getErros().add("Ocorreu um erro ao tentar consultar as alíquotas pelo id do cliente: "+ e.getMessage());
			return response;
		}
	}


	
	
	

}// fecha classe
