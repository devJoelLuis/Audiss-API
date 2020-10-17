package audinss.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import audinss.entidades.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

	Page<Cliente> findAllByOrderByNomeAsc(Pageable pageable);


	Page<Cliente> findByNomeContainingIgnoreCaseOrderByNomeAsc(Pageable pageable, String param);


	Page<Cliente> findByCnpjContainingIgnoreCaseOrderByNomeAsc(Pageable pageable, String param);



	List<Cliente> findByIdGreaterThanOrderByNomeAsc(int i);


	boolean existsByNome(String nome);


	Cliente findByNome(String nome);




}
