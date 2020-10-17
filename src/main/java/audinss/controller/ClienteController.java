package audinss.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import audinss.DTO.ClienteDTO;
import audinss.DTO.ClienteDashDto;
import audinss.entidades.Cliente;
import audinss.response.Response;
import audinss.servico.ClienteService;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

	@Autowired
	private ClienteService service;
	
	

	// get all
	@PreAuthorize("hasAnyRole('ADMIN')")
	@GetMapping
	public ResponseEntity<Response<Page<Cliente>>> getAll(
            @RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "param", defaultValue = "") String param) {
		Response<Page<Cliente>> response = service.getAll(page, size, param);
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);

	}
	
	
	
	
	
    // alterar 
	@PreAuthorize("hasAnyRole('ADMIN')")
	@PutMapping
	public ResponseEntity<Response<Cliente>> alterar(@Valid @RequestBody Cliente c) {
		Response<Cliente> response = service.alterar(c);
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);

	}
	

	// getById
	@PreAuthorize("hasAnyRole('ADMIN')")
	@GetMapping("/cliente/{id}")
	public ResponseEntity<Response<Cliente>> getById(@PathVariable int id) {
		Response<Cliente> response = service.getById(id);
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);

	}
	
	
	

	// post
	@PreAuthorize("hasAnyRole('ADMIN')")
	@PostMapping
	public ResponseEntity<Response<Cliente>> cadastrar(@Valid @RequestBody Cliente c) {
		Response<Cliente> response = service.cadastrar(c);
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);

	}
	
	
	

	// delete
	@PreAuthorize("hasAnyRole('ADMIN')")
	@DeleteMapping("/cliente/{id}")
	public ResponseEntity<Response<String>> excluir(@PathVariable int id) {
		Response<String> response = service.excluir(id);
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	
	@PreAuthorize("hasAnyRole('ADMIN')")
	@GetMapping("/clientesdto")
	public ResponseEntity<Response<List<ClienteDTO>>> getAllDto() {
		Response<List<ClienteDTO>> response = service.getAllDto();
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	
	
	
	@PreAuthorize("hasAnyRole('USER')")
	@GetMapping("/clientesdto-user/{iduser}")
	public ResponseEntity<Response<List<ClienteDTO>>> getAllDtoUser(@PathVariable int iduser) {
		Response<List<ClienteDTO>> response = service.getAllDtoUser(iduser);
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/clientesdash")
	public ResponseEntity<Response<List<ClienteDashDto>>> getAllDashDtoUser() {
		Response<List<ClienteDashDto>> response = service.getAllDashDtoUser();
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	
	//get se licença é valida
	@GetMapping("/licenca/{idcli}")
	public ResponseEntity<Response<Boolean>> getLicença(
			@PathVariable int idcli
			) {
		Response<Boolean> response = service.getLicencaIsValid(idcli);
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	

}// fecha classe
