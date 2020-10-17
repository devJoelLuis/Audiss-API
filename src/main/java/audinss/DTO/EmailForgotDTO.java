package audinss.DTO;

import java.io.Serializable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

public class EmailForgotDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@NotEmpty(message="Informe o email")
	@Email(message="Email inválido")
	private String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	
	

}
