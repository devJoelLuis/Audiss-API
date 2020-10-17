package audinss.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class InitialSqlInserts {
	
	@PersistenceContext
	private EntityManager em;
	
	@Transactional
	public void inserts() {
		
		em.createNativeQuery("SET @@foreign_key_checks = 0;").executeUpdate(); //desativa verificação de chave estrangeira para delete
        em.createNativeQuery("SET SQL_SAFE_UPDATES = 0;").executeUpdate(); //desativa verificação de chava estrangeira para update
        
        // PERMISSOES
        em.createNativeQuery("DELETE FROM permissao;").executeUpdate();
        em.createNativeQuery("INSERT IGNORE INTO  permissao (id, permissao) VALUES (1, 'ROLE_ADMIN'); ").executeUpdate();
        em.createNativeQuery("INSERT IGNORE INTO  permissao (id, permissao) VALUES  (2, 'ROLE_USER'); ").executeUpdate();
        
        // USUÁRIO PADRÃO
        em.createNativeQuery("DELETE FROM usuario WHERE id = 1;").executeUpdate();
        em.createNativeQuery("INSERT IGNORE INTO usuario (id, email, nome, senha, obs) "
        		+ "VALUES (1, 'sistema.audinss@gmail.com', 'USUÁRIO MASTER', "
        		+ "'$2a$10$qbUD/96/sBCRjhyo9Xojrumnnmet6GHOEsf.eHjHrURIHmhbB50ru', 	"
        		+ "'Esse é o usuário master e não deve ser removido');").executeUpdate();
        
        //ADICIONAR PERMISSÃO ADMIN AO MASTER ID 1
        em.createNativeQuery("INSERT IGNORE INTO usuario_permissao (usuario_id, permissao_id) VALUES (1, 1);").executeUpdate();
        
        em.createNativeQuery("SET @@foreign_key_checks = 1;").executeUpdate(); // reativa verificação
        em.createNativeQuery("SET SQL_SAFE_UPDATES = 1;").executeUpdate();// reativa verificação
		
	}//fecha method inserts
	

}//fecha classe
