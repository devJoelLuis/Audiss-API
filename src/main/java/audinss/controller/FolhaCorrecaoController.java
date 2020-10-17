package audinss.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import audinss.DTO.FolhaCorrecaoLista;
import audinss.entidades.FolhaCorrecao;
import audinss.response.Response;
import audinss.servico.FolhaCorrecaoService;

@RestController
@RequestMapping("/correcoes")
public class FolhaCorrecaoController {
	
	@Autowired
	private FolhaCorrecaoService service;
	
	//post
	@PostMapping
	public ResponseEntity<Response<FolhaCorrecao>> cadastrar(
			@RequestBody FolhaCorrecao fc
			) {
		Response<FolhaCorrecao> response = service.cadastrar(fc);
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	
	
	
	// salva uma lista de folhas de correcao
	@PostMapping("/fcs")
	public ResponseEntity<Response<List<FolhaCorrecao>>> cadastrarLista(
			@RequestBody FolhaCorrecaoLista fcs
			) {
		Response<List<FolhaCorrecao>> response = service.cadastrarLista(fcs);
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	
	
	//put altera uma lista de folha de correcao
	@PutMapping("/fcs")
	public ResponseEntity<Response<List<FolhaCorrecao>>> alterarLista(
			@RequestBody FolhaCorrecaoLista fcs
			) {
		Response<List<FolhaCorrecao>> response = service.alterarLista(fcs);
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	
	//put altera uma lista de folha de correcao e os movimentos ja registrados
		@PutMapping("/fcsmovimentos")
		public ResponseEntity<Response<List<FolhaCorrecao>>> alterarListaEMoviemntos(
				@RequestBody FolhaCorrecaoLista fcs
				) {
			Response<List<FolhaCorrecao>> response = service.alterarListaMovimentos(fcs);
			if (response.getErros().size() > 0) {
				return ResponseEntity.badRequest().body(response);
			}
			return ResponseEntity.ok(response);
		}
		
	
	
	
	
	//delete
	@DeleteMapping("/folha")
	public ResponseEntity<Response<String>> excluirFolha(
	@RequestParam(value="idcliente", defaultValue="0") int idcliente,
	@RequestParam(value="idlegislacao", defaultValue="0") int idleg
			) {
		Response<String> response = service.excluirFolha(idcliente, idleg);
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	
	//getid
	@GetMapping("/{id}")
	public ResponseEntity<Response<FolhaCorrecao>> getbyId(
			@PathVariable int id
			) {
		Response<FolhaCorrecao> response = service.getById(id);
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	//getall
	
	
	//get all legislacao id
	@GetMapping("/legislacao/{idleg}")
	public ResponseEntity<Response<List<FolhaCorrecao>>> getAllLegislacaoId(
			@PathVariable int idleg
			) {
		Response<List<FolhaCorrecao>> response = service.getAllLegislacaoId(idleg);
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	
	//get all legislacao id  e cliente id
		@GetMapping("/legislacao-cliente")
		public ResponseEntity<Response<List<FolhaCorrecao>>> getAllLegislacaoIdClienteId(
				@RequestParam(value="idcliente", defaultValue="0") int idcliente,
				@RequestParam(value="idlegislacao", defaultValue="0") int idleg
				) {
			Response<List<FolhaCorrecao>> response = service.getAllLegislacaoIdClienteId(idleg, idcliente);
			if (response.getErros().size() > 0) {
				return ResponseEntity.badRequest().body(response);
			}
			return ResponseEntity.ok(response);
		}
		
		
		@GetMapping("/fc-existe")
		public ResponseEntity<Response<Boolean>> verificarFcExisteClienteId(
				@RequestParam(value="idcliente", defaultValue="0") int idcliente
				) {
			Response<Boolean> response = service.verificarExisteFcClienteId(idcliente);
			if (response.getErros().size() > 0) {
				return ResponseEntity.badRequest().body(response);
			}
			return ResponseEntity.ok(response);
		}
	
	

}//fecha classe
