package audinss.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import audinss.entidades.AliquotasMes;

public interface AliquotasMesRepository extends JpaRepository<AliquotasMes, Integer> {

	List<AliquotasMes> findByAnoAndFolhaTipoAndClienteIdOrderByMesAsc(int ano, String string, int idCliente);
	
	
	@Query(value = "SELECT al.id, al.mes, al.ano, al.obs FROM aliquotas_mes al inner join movimento mv "
			+ "on al.id = mv.aliquotas_mes_id where al.ano = :p1 and al.cliente_id = :p2 and mv.legislacao = :p3 group by id order by al.mes asc"
			, nativeQuery = true)
	List<?> findAliquotasGroupByIdAliquota( @Param("p2") int clienteid, @Param("p1") int ano, @Param("p3") String legislacao);


	Optional<AliquotasMes> findByClienteIdAndAnoAndMes(Integer id, int ano, int mes);


	@Query("FROM AliquotasMes am WHERE EXISTS (FROM Movimento mv WHERE mv.aliquotasMes.id = am.id AND mv.legislacao = ?4) "
			+ "AND am.cliente.id = ?3 AND ano = ?2 AND mes= ?1")
	AliquotasMes buscarAliquotaPorMesAnoClienteIdLegislacaoMovimento(int mes, int ano, int clienteid,
			String legislacao);

	
     @Query("FROM AliquotasMes am WHERE EXISTS ( FROM Movimento mv WHERE mv.aliquotasMes.id = am.id AND mv.legislacao = ?3 ) "
     		+ "AND am.cliente.id = ?1 AND am.ano = ?2")
	List<AliquotasMes> buscarAliquotasClienteAnoLegislacao(int clienteid, int ano, String legislacao);


	List<AliquotasMes> findByClienteId(Integer idcliente);


}
