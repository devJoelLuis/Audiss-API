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

import audinss.entidades.Usuario;
import audinss.response.Response;
import audinss.servico.UsuarioService;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
	
	@Autowired
	private UsuarioService service;
	
	
   // cadastrar	
   @PreAuthorize("hasAnyRole('ADMIN')") 	
   @PostMapping
   public ResponseEntity<Response<Usuario>> cadastrar(
		   @Valid @RequestBody Usuario u
		   ) {
	   Response<Usuario> response = service.cadastrar(u);
	   if (response.getErros().size() > 0) {
		   return ResponseEntity.badRequest().body(response);
	   }
	   return ResponseEntity.ok(response);
   }
   
   
   
   // alterar
   @PreAuthorize("hasAnyRole('ADMIN')") 	
   @PutMapping
   public ResponseEntity<Response<Usuario>> editar(
		   @Valid @RequestBody Usuario u
		   ) {
	   Response<Usuario> response = service.editar(u);
	   if (response.getErros().size() > 0) {
		   return ResponseEntity.badRequest().body(response);
	   }
	   return ResponseEntity.ok(response);
   }
   
   
   // deletar
   @PreAuthorize("hasAnyRole('ADMIN')") 	
   @DeleteMapping("/{id}")
   public ResponseEntity<Response<String>> delete(
		  @PathVariable int id
		   ) {
	   Response<String> response = service.excluir(id);
	   if (response.getErros().size() > 0) {
		   return ResponseEntity.badRequest().body(response);
	   }
	   return ResponseEntity.ok(response);
   }
   
   
   
   // get all
   @PreAuthorize("hasAnyRole('ADMIN')") 	
   @GetMapping
   public ResponseEntity<Response<Page<Usuario>>> getAll(
		   @RequestParam(value="page", defaultValue="0") int page,
		   @RequestParam(value="size", defaultValue="20") int size,
		   @RequestParam(value="nome", defaultValue="") String nome
		   ) {
	   Response<Page<Usuario>> response = service.getAll(page, size, nome);
	   if (response.getErros().size() > 0) {
		   return ResponseEntity.badRequest().body(response);
	   }
	   return ResponseEntity.ok(response);
   }
   
   
   
   // get by id
   @GetMapping("/{id}")
   public ResponseEntity<Response<Usuario>> getById(
		   @PathVariable int id
		   ) {
	   Response<Usuario> response = service.getById(id);
	   if (response.getErros().size() > 0) {
		   return ResponseEntity.badRequest().body(response);
	   }
	   return ResponseEntity.ok(response);
   }
   
   
   // get by email
   @GetMapping("/email")
   public ResponseEntity<Response<Usuario>> getByEmail(
		  @RequestParam(value="email", defaultValue="") String email
		   ) {
	   Response<Usuario> response = service.getByEmail(email);
	   if (response.getErros().size() > 0) {
		   return ResponseEntity.badRequest().body(response);
	   }
	   return ResponseEntity.ok(response);
   }
   
   
   
   // get all id cliente
   @GetMapping("/cliente/{idcliente}")
   public ResponseEntity<Response<List<Usuario>>> getAllIdcliente(
		   @PathVariable int idcliente
		   ) {
	   Response<List<Usuario>> response = service.getAllIdCliente(idcliente);
	   if (response.getErros().size() > 0) {
		   return ResponseEntity.badRequest().body(response);
	   }
	   return ResponseEntity.ok(response);
   }
   

}//fecha classe
