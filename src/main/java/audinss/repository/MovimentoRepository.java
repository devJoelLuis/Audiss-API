package audinss.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import audinss.entidades.AliquotasMes;
import audinss.entidades.Movimento;

public interface MovimentoRepository extends JpaRepository<Movimento, Long> {


	List<Movimento> findByLegislacaoIgnoreCaseAndAliquotasMesClienteIdAndAliquotasMesMesAndAliquotasMesAnoOrderByCodigoAsc(String leg,
			int idcliente, int mes, int ano);
	
	List<Movimento> findByAliquotasMesFolhaTipoAndAliquotasMesClienteIdAndAliquotasMesMesAndAliquotasMesAno(String folhaTipo,
			int idcliente, int mes, int ano);

	List<Movimento> findByAliquotasMesId(int idAliquota);

	List<Movimento> findByAliquotasMesIdOrderByCodigoAsc(int idAliquota);

	List<Movimento> findByAliquotasMesIdAndAliquotasMesAnoAndAliquotasMesMesAndLegislacaoOrderByIdAsc(
			int idaliquota, int ano, int mes, String legislacao);

	List<Movimento> findByLegislacaoAndAliquotasMesClienteIdAndAliquotasMesMesAndAliquotasMesAnoOrderByIdAsc(String string,
			int idcliente, int mes, int ano);

	Movimento findByAliquotasMesIdAndCodigoAndLegislacao(Integer id, String codigo, String legislacao);

	List<Movimento> findByAliquotasMesClienteIdAndAliquotasMesAnoAndLegislacaoOrderByCodigoAsc(int clienteid, int ano,
			String legislacao);

	List<Movimento> findByAliquotasMesClienteIdAndLegislacao(Integer id, String legislacao);

	List<Movimento> findByAliquotasMesIdAndLegislacaoOrderByCodigoAsc(int idAliquota, String string);

	Long countByAliquotasMesAnoAndAliquotasMesMesAndCodigoAndDescricaoAndLegislacao(int ano, int mes, String codigo,
			String descricao, String legislacao);

	Long countByAliquotasMesClienteIdAndLegislacao(Integer id, String legislacao);

	Long countByAliquotasMesClienteIdAndAliquotasMesAnoAndAliquotasMesMesAndCodigoAndDescricaoAndLegislacao(Integer id,
			int ano, int mes, String codigo, String descricao, String legislacao);

	@Query("FROM AliquotasMes am WHERE EXISTS (FROM Movimento mv WHERE mv.aliquotasMes.id = am.id AND mv.legislacao != 'FP' ) AND am.cliente.id = ?1")
	List<AliquotasMes> buscarAliquotasMovimentoClienteLegislacao(int clienteid);


	@Query("FROM Movimento m WHERE m.legislacao != 'FP' AND m.legislacao != '' "
			+ "AND m.legislacao != null "
			+ "AND m.aliquotasMes.cliente.id = ?1")
	List<Movimento> buscarLegislacoesMovimentos(int idcliente);
	
	@Query(value = "SELECT DISTINCT m.legislacao AS legislacao FROM movimento m "
			+ "INNER JOIN aliquotas_mes alm ON m.aliquotas_mes_id = alm.id AND alm.cliente_id = ?1", nativeQuery = true)
	List<Object[]> buscarLegislacoesMovimentosComFp(int idcliente);



    @Query("FROM Movimento m WHERE m.legislacao = ?3 AND "
    		+ "m.aliquotasMes.cliente.id = ?2 AND "
    		+ "m.aliquotasMes.ano >= ?1")
	List<Movimento> buscarMovimentoFp(int anoEntrega, Integer idcliente, String leg);
    
    
    @Query("FROM AliquotasMes am WHERE EXISTS (FROM Movimento mv WHERE mv.aliquotasMes.id = am.id AND mv.legislacao = 'FP') "
    		+ "AND am.cliente.id = ?1")
	List<AliquotasMes> buscarAliquotasMovimentoClienteFP(int clienteid);

    @Query("FROM Movimento m WHERE m.aliquotasMes.cliente.id = ?1 AND "
    		+ "m.codigo = ?2 ")
	List<Movimento> buscarMovimentosClienteIdCodigoDescricao(Integer idcliente, String codigo);
    
    
   
    @Query("FROM Movimento m WHERE m.aliquotasMes.cliente.id = ?1 AND "
    		+ "m.legislacao = ?2  AND "
    		+ "m.descricao = ?3")
	List<Movimento> buscarMovimentoClienteIdLegislacaoDescricao(Integer idcliente, String legislacao, String descricao);

    @Query( value="SELECT * FROM movimento WHERE legislacao = ?1", nativeQuery = true )
	List<Movimento> buscarMovimentoPorLegislacao(String legislacao);

	boolean existsByAliquotasMesIdAndDescricao(Integer id, String descricao);


	List<Movimento> findByAliquotasMesIdAndLegislacao(int idAliquota, String string);
    
 

	
   

	

}
