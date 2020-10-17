package audinss.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import audinss.entidades.Artigo;

public interface ArtigoRepository extends JpaRepository<Artigo, Long> {

}
