package audinss.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import audinss.entidades.AliquotasMes;
import audinss.response.Response;
import audinss.servico.AliquotasMesService;

@RestController
@RequestMapping("/aliquotas")
public class AliquotasMesController {
	
	@Autowired
	private AliquotasMesService service;
	
	// get all  por idcliente e ano 
	@GetMapping("/clientefp")
	public ResponseEntity<Response<List<AliquotasMes>>> getAllClienteIdfp(
			@RequestParam(value="idcliente", defaultValue="0") int idcliente,
			@RequestParam(value="ano", defaultValue="0") int ano
			) {
		Response<List<AliquotasMes>> response = service.getAllClienteIdFp(idcliente, ano);
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	

}//fecha classe
