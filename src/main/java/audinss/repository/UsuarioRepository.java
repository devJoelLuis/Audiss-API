package audinss.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import audinss.entidades.Cliente;
import audinss.entidades.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

	Optional<Usuario> findByEmail(String email);

	boolean existsByEmail(String email);

	List<Usuario> findByClientesOrderByNomeAsc(Cliente cliente);

	Page<Usuario> findAllByOrderByNomeAsc(Pageable pageable);

	Page<Usuario> findByNomeContainingOrderByNomeAsc(String nome, Pageable pageable);

	Long countByEmail(String email);


}
