package audinss.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import audinss.entidades.Legislacao;

public interface LegislacaoRepository extends JpaRepository<Legislacao, Integer> {

	Long countByLegislacao(String legislacao);

	Long countByLegislacaoAndIdNot(String legislacao, Integer id);

	List<Legislacao> findAllByOrderByLegislacao();

	Legislacao findByLegislacao(String legislacao);

	@Query("FROM Legislacao l WHERE l.artigos.size > 0")
	List<Legislacao> buscarLegislacoesComArtigos();


}
