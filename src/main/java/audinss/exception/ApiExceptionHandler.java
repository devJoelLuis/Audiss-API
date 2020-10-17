package audinss.exception;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import audinss.response.Response;

@ControllerAdvice
public class ApiExceptionHandler {
	
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Response<String>> entidadeSendoUtilizada(ConstraintViolationException ex) {
		Response<String> response = new Response<>();
		response.getErros().add("Não foi possível remover porque a entidade possui outros registros vinculados.");
		return ResponseEntity.badRequest().body(response);
	}
	
	

}//fecha classe
