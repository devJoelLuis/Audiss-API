package audinss.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import audinss.entidades.FolhaCorrecao;

public interface FolhaCorrecaoRepository extends JpaRepository<FolhaCorrecao, Integer> {
	
	
	@Query(value = "SELECT * FROM folha_correcao WHERE legislacao_id = :id", nativeQuery = true)
	List<FolhaCorrecao> buscarPorLegislacaoId(@Param("id") int idleg);
	
	@Query(value = "SELECT * FROM folha_correcao WHERE verba_padrao_id = :id", nativeQuery = true)
	List<FolhaCorrecao> buscarPorVerbaPadraoId(@Param("id") int idverba);
	
	
	@Query(value = "SELECT * FROM folha_correcao WHERE legislacao_id = :idl and cliente_id = :idc", nativeQuery = true)
	List<FolhaCorrecao> buscarPorLegislacaoIdClienteId(@Param("idl") int idleg, @Param("idc") int idcliente);

	Long countByLegislacaoId(int id);

	Long countByLegislacaoIdAndClienteId(Integer id, int clienteid);

	Boolean existsByClienteId(int clienteid);

	FolhaCorrecao findByVerbaPadraoCodigoAndLegislacaoLegislacaoAndClienteId(String codigo, String legislacao,
			Integer idcliente);

	@Query(value="SELECT DISTINCT l.id, f.data_inicio FROM folha_correcao f INNER JOIN "
			+ "legislacao l ON f.legislacao_id = l.id  WHERE f.cliente_id = ?1", nativeQuery = true)
	List<Object[]> buscarLegislacoesFcClienteId(Integer idcliente);

	List<FolhaCorrecao> findByClienteIdAndVerbaPadraoId(Integer idcliente, int id);
	
	

	
	

}//fecha classe
