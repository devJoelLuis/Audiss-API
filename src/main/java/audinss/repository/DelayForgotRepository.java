package audinss.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import audinss.entidades.DelayForgotSenha;

public interface DelayForgotRepository extends JpaRepository<DelayForgotSenha, Long> {
	
	Optional<DelayForgotSenha> findByEmail(String email);

	Optional<DelayForgotSenha> findTop1ByEmailOrderByIdDesc(String email);

}
