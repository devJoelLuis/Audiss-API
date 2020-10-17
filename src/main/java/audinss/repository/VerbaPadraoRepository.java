package audinss.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import audinss.entidades.VerbaPadrao;

public interface VerbaPadraoRepository extends JpaRepository<VerbaPadrao, Integer> {


	Long countByCodigoAndClienteId(String codigo, Integer id);

	VerbaPadrao findByCodigoAndClienteId(String codigo, Integer id);

	List<VerbaPadrao> findAllByOrderByCodigoAsc();
	
	
	@Query(value = "SELECT * FROM verba_padrao WHERE cliente_id = :id ORDER BY id ASC", nativeQuery = true)
	List<VerbaPadrao> buscarPorClientePorId(@Param("id") int id);

}
