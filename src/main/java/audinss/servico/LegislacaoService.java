package audinss.servico;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import audinss.entidades.Artigo;
import audinss.entidades.Legislacao;
import audinss.entidades.Movimento;
import audinss.repository.ArtigoRepository;
import audinss.repository.FolhaCorrecaoRepository;
import audinss.repository.LegislacaoRepository;
import audinss.repository.MovimentoRepository;
import audinss.response.Response;

@Service
public class LegislacaoService {
	
	
	@Autowired
	private LegislacaoRepository repo;
	
	@Autowired
	private FolhaCorrecaoRepository repoFc;
	
	@Autowired
	private ArtigoRepository repoArt;
	
	
	
	@Autowired
	private MovimentoRepository repoMov;
	
	
	
	//cadastrar
	@Transactional
	public Response<Legislacao> cadastrar(Legislacao lg) {
		Response<Legislacao> response = new Response<>();
		try {
			
			validarLegisla(lg);
			
			 if (lg.getArtigos() == null || lg.getArtigos().size() == 0) {
				 Legislacao lgb = repo.save(lg);
					response.setDados(lgb);
					return response;
			 }
			 
			 List<Artigo>artigos = lg.getArtigos();
			 lg.setArtigos(new ArrayList<>());
			 lg = repo.save(lg);
			 for(Artigo a: artigos) {
				 a.setLegislacao(lg);
			 }
			lg.setArtigos(artigos);
			response.setDados(lg);
			return response;
			
		} catch (Exception e) {
			response.getErros().add("Ocorreu um erro ao tentar cadastrar uma legislação: "+ e.getMessage());
			return response;
		}
	}
	
	
	private void validarLegisla(Legislacao lg) throws Exception {
		if (lg == null) {
			throw new Exception("a legislação está null");
		}
		Long count = repo.countByLegislacao(lg.getLegislacao());
		if (count > 0) {
			throw new Exception("A legislação "+ lg.getLegislacao() +" já se encontra cadastrada no sistema!!!");
		}
	}


	//editar
	@Transactional
	public Response<Legislacao> alterar(Legislacao lg) {
		Response<Legislacao> response = new Response<>();
		try {
			Long count = repo.countByLegislacaoAndIdNot(lg.getLegislacao(), lg.getId());
			if (count > 0) {
				throw new Exception("A legislação "+ lg.getLegislacao() +" já se encontra cadastrada no sistema!!!");
			}
			Optional<Legislacao> lgOp = repo.findById(lg.getId());
			if (!lgOp.isPresent()) {
				throw new Exception("Não foi possível encontrar uma legislação com o id: "+ lg.getId());
			}
			Legislacao lgb = lgOp.get();
			String descricaoLegislacaoAntiga = lgb.getLegislacao().trim();
			
			// busca os movimetos para modificar o nome da legislacao
			List<Movimento> movimentos = repoMov.buscarMovimentoPorLegislacao(descricaoLegislacaoAntiga);
			
			List<Artigo>artigos = lg.getArtigos();
			lg.setArtigos(new ArrayList<>());
			BeanUtils.copyProperties(lg, lgb, "id");
			lg = repo.save(lgb);
			for (Artigo a: artigos) {
			   a.setLegislacao(lg);
			   repoArt.save(a);
			}
			
			String descricaoLegislacaoNova = lgb.getLegislacao().trim();
			
			for (Movimento m: movimentos) {
			
				//acertar o nome da legislacao e salvar
				m.setLegislacao(descricaoLegislacaoNova);
				repoMov.save(m);
			}
			
			
		    response.setDados(lgb);
			return response;
			
		} catch (Exception e) {
			response.getErros().add("Ocorreu um erro ao tentar alterar uma legislação: "+ e.getMessage());
			return response;
		}
	}
	
	
	
	
	
	//excluir
	public Response<String> excluir(int id) {
		Response<String> response = new Response<>();
		try {
			
			Long count = repoFc.countByLegislacaoId(id);
			if (count > 0) {
				throw new Exception("a legislação possui lançamentos vinculados!!");
			}
			Optional<Legislacao> lgOp = repo.findById(id);
			if (!lgOp.isPresent()) {
				throw new Exception("Não foi possível encontrar uma legislação com o id: "+ id);
			}
			repo.delete(lgOp.get());
			response.setDados("ok");
			return response;
		} catch (Exception e) {
		  response.getErros().add("Ocorreu um erro ao tentar excluir uma legislação: "+ e.getMessage());
		  return response;
		}
	}
	
	
	
	
	
	//getid
	public Response<Legislacao> getId(int id) {
		Response<Legislacao> response = new Response<>();
		try {
			Optional<Legislacao> lgOp = repo.findById(id);
			if (!lgOp.isPresent()) {
				throw new Exception("Não foi possível encontrar uma legislação com o id: "+ id);
			}
			response.setDados(lgOp.get());
			return response;
		} catch (Exception e) {
			response.getErros().add("Ocorreu um erro ao tentar consultar uma legislação: "+ e.getMessage());
			  return response;
		}
		
	}
	
	
	
	//getall
	public Response<List<Legislacao>> getAll() {
		Response<List<Legislacao>> response = new Response<>();
		try {
			response.setDados(repo.findAllByOrderByLegislacao());
			return response;
		} catch (Exception e) {
			response.getErros().add("Ocorreu um erro ao tentar consultar todas as legislação: "+ e.getMessage());
			return response;
		}
	}


	public Response<List<String>> getAllArtigo() {
		 Response<List<String>> response = new Response<>();
		 try {
			
			 List<Legislacao> legs = repo.buscarLegislacoesComArtigos();
			 List<String> legString = new ArrayList<>();
			 
			 for (Legislacao l: legs) {
				 legString.add(l.getLegislacao().trim());
			 }
			 
			 response.setDados(legString);
			 return response;
			 
		} catch (Exception e) {
			response.getErros().add("Ocorreu o seguinte erro ao tentar carregar as legislações com artigos: "+ e.getMessage());
			return response;
		}
	}


	
	

}//fecha classe
