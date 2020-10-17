package audinss.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import audinss.entidades.Permissao;

public interface PermissaoRepository extends JpaRepository<Permissao, Integer> {

	boolean existsByPermissao(String permissao);

	Permissao findByPermissao(String permissao);

}
