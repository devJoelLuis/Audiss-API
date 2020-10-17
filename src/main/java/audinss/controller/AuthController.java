package audinss.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import audinss.DTO.EmailForgotDTO;
import audinss.response.Response;
import audinss.servico.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {
	
	@Autowired
	private AuthService authService;
	
	@PostMapping("/forgot")
	public ResponseEntity<Response<String>> forgot(
	@Valid @RequestBody EmailForgotDTO efdto
			){
		Response<String> response =	authService.sendNewPassword(efdto.getEmail());
		if(response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
			
		
	}

}
