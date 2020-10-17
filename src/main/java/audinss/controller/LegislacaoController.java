package audinss.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import audinss.entidades.Legislacao;
import audinss.response.Response;
import audinss.servico.LegislacaoService;

@RestController
@RequestMapping("/legislacoes")
public class LegislacaoController {

	@Autowired
	private LegislacaoService service;
	
	
	//post
	@PreAuthorize("hasAnyRole('ADMIN')")
	@PostMapping
	public ResponseEntity<Response<Legislacao>> cadastrar(@Valid @RequestBody Legislacao l) {
		Response<Legislacao> response = service.cadastrar(l);
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);

	}
	
	//put
	@PreAuthorize("hasAnyRole('ADMIN')")
	@PutMapping
	public ResponseEntity<Response<Legislacao>> alterar(@Valid @RequestBody Legislacao l) {
		Response<Legislacao> response = service.alterar(l);
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);

	}
	
	//delete
	@PreAuthorize("hasAnyRole('ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Response<String>> delete(@PathVariable int id) {
		Response<String> response = service.excluir(id);
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);

	}
	
	//getid
	@GetMapping("/{id}")
	public ResponseEntity<Response<Legislacao>> getById(@PathVariable int id) {
		Response<Legislacao> response = service.getId(id);
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);

	}
	
	//getall
	@GetMapping
	public ResponseEntity<Response<List<Legislacao>>> getByAll() {
		Response<List<Legislacao>> response = service.getAll();
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);

	}
	
	
	//getall
		@GetMapping("/artigo")
		public ResponseEntity<Response<List<String>>> getByAllArtigo() {
			Response<List<String>> response = service.getAllArtigo();
			if (response.getErros().size() > 0) {
				return ResponseEntity.badRequest().body(response);
			}
			return ResponseEntity.ok(response);

		}
	
	
}//fecha classe
