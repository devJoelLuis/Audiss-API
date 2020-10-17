package audinss.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import audinss.entidades.VerbaPadrao;
import audinss.response.Response;
import audinss.servico.VerbaPadraoService;

@RestController
@RequestMapping("/verbas")
public class VerbaPadraoController {

	@Autowired
	private VerbaPadraoService service;
	
	
	//post
	@PostMapping
	public ResponseEntity<Response<VerbaPadrao>> cadastrar(@Valid @RequestBody VerbaPadrao v) {
		Response<VerbaPadrao> response = service.cadastrar(v);
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);

	}
	
	//put
	@PutMapping
	public ResponseEntity<Response<VerbaPadrao>> alterar(@Valid @RequestBody VerbaPadrao v) {
		Response<VerbaPadrao> response = service.alterar(v);
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);

	}
	
	
	
	
	
	// alterar todos movimento
	@PutMapping("/movimentos")
	public ResponseEntity<Response<VerbaPadrao>> alterarVerbaEMovimentos(@Valid @RequestBody VerbaPadrao v) {
		Response<VerbaPadrao> response = service.alterarVerbaEMovimentos(v);
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);

	}
	
	
	
	
	
	//delete
	@DeleteMapping("/{id}")
	public ResponseEntity<Response<String>> delete(@PathVariable int id) {
		Response<String> response = service.excluir(id);
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);

	}
	
	//getid
	@GetMapping("/verba/{id}")
	public ResponseEntity<Response<VerbaPadrao>> getById(@PathVariable int id) {
		Response<VerbaPadrao> response = service.getId(id);
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);

	}
	
	//getall
	@GetMapping
	public ResponseEntity<Response<List<VerbaPadrao>>> getByAll() {
		Response<List<VerbaPadrao>> response = service.getAll();
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);

	}
	
	//getall
		@GetMapping("/cliente/{id}")
		public ResponseEntity<Response<List<VerbaPadrao>>> getByAllcliente(
				@PathVariable int id
				) {
			Response<List<VerbaPadrao>> response = service.getAllclienteid(id);
			if (response.getErros().size() > 0) {
				return ResponseEntity.badRequest().body(response);
			}
			return ResponseEntity.ok(response);

		}
	
	
}//fecha classe
