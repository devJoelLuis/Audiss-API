package audinss.servico;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import audinss.entidades.Usuario;

@Component
public class MailerService {
	
	@Value("${default.sender}")
	private String sender;
	
	@Autowired
	private JavaMailSender mailSender;

	public void sendNewPasswordEmail(Usuario user, String newPass) {
		
		try {
			MimeMessage mime = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mime, "UTF-8");
			helper.setFrom(sender);
			helper.setTo(user.getEmail());
			helper.setSubject("Solicitação de nova senha para os sistema Audinss");
			helper.setText("A nova senha para o usuário "+ user.getEmail() + " é "+ newPass, false);
			mailSender.send(mime);
			System.out.printf("email enviado!!!");
		} catch (Exception e) {
			throw new RuntimeException("problemas com enviou de email: "+ e.getMessage());
		}
	}
	

}//fecha classe
