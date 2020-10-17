package audinss.servico;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import audinss.entidades.Artigo;
import audinss.repository.ArtigoRepository;
import audinss.response.Response;

@Service
public class ArtigoService {
	
	@Autowired
	private ArtigoRepository repo;

	public Response<String> excluirArtigo(Long id) {
		Response<String> response = new Response<>();
		try {
			Artigo artigo = repo.findById(id)
					.orElseThrow(() -> new Exception("não foi possível encontrar um artigo com o id "+ id));
			repo.delete(artigo);
			response.setDados("ok");
			return response;
		} catch (Exception e) {
			response.getErros().add("Ocorreu um erro ao tentar excluir um artigo: "+ e.getMessage());
			return response;
		}
	}
	
	
	public Response<String> excluirListaArtigos(List<Artigo> artigos) {
		Response<String> response = new Response<>();
		try {
			
			repo.deleteAll(artigos);
			response.setDados("ok");
			return response;
		} catch (Exception e) {
			response.getErros().add("Ocorreu um erro ao tentar excluir uma lista de artigos: "+ e.getMessage());
			return response;
		}
	}
	

}//fecha classe
