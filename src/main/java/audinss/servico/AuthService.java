package audinss.servico;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import audinss.entidades.DelayForgotSenha;
import audinss.entidades.Usuario;
import audinss.repository.DelayForgotRepository;
import audinss.repository.UsuarioRepository;
import audinss.response.Response;



@Service
public class AuthService {
	@Autowired
	private UsuarioRepository repoUser;
	
	
	private BCryptPasswordEncoder pe = new BCryptPasswordEncoder();
	
	@Autowired
	private MailerService emailService;
	
	@Autowired
	private DelayForgotRepository repoF;
	
	private final int DELAY_SENHA = 10;
	
	private Random rand = new Random();
	
	public Response<String> sendNewPassword(String email){
		Response<String> response = new Response<>();
	try {
		Optional<Usuario> userOp  = repoUser.findByEmail(email);
		if(!userOp.isPresent()) {
			throw new Exception("email não encontrado");
		}
		Usuario user = userOp.get();
		DelayForgotSenha dfs = new DelayForgotSenha();
		Optional<DelayForgotSenha> fdOp = repoF.findTop1ByEmailOrderByIdDesc(email);
		if(fdOp.isPresent()) {
			dfs = fdOp.get();
		    if(dfs.getHoraRequisicao().plusMinutes(DELAY_SENHA).isAfter(LocalDateTime.now())) {
		    	Duration dif = Duration.between(LocalDateTime.now(), dfs.getHoraRequisicao().plusMinutes(DELAY_SENHA));
		    	throw new Exception("Uma nova senha foi requerida recentimente aguarde "+ dif.toMinutes() + " minutos para requerir uma nova senha");
		    } else {
		    	dfs.setEmail(email);
				dfs.setHoraRequisicao(LocalDateTime.now());
				repoF.save(dfs);
		    }
		} else {
			dfs.setEmail(email);
			dfs.setHoraRequisicao(LocalDateTime.now());
			repoF.save(dfs);
		}
		
		String newPass = newPassword();
		user.setSenha(pe.encode(newPass));
		repoUser.save(user);
		
		emailService.sendNewPasswordEmail(user, newPass);
		response.setDados("ok");
		return response;
	} catch (Exception e) {
		response.getErros().add("Erro: " + e.getMessage());
		return response;
	}
		
	}

	private String newPassword() {
		char[] vet = new char[8];
		for(int i=0; i<8; i++) {
			vet[i] = randomChar();
		}
		return new String(vet);
	}

	private char randomChar() {
		int opt = rand.nextInt(3);
		if(opt == 0) { //gera um digito
			return (char) (rand.nextInt(10) + 48);
		} else
        if(opt == 1) { //gera uma letra maiuscula
        	return (char) (rand.nextInt(26) + 65);
		} else { //gera letra minuscula
        	return (char) (rand.nextInt(26) + 97);
		}
	}
}
