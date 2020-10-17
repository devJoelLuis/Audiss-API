package audinss.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import audinss.entidades.Artigo;
import audinss.response.Response;
import audinss.servico.ArtigoService;

@RestController
@RequestMapping("/artigos")
public class ArtigoController {
	
	@Autowired
	private ArtigoService service;
	
	
	
	@DeleteMapping
	public ResponseEntity<Response<String>> excluirArtigo(
	   @PathVariable Long id		
			){
		Response<String> response = service.excluirArtigo(id);
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	
	
	@DeleteMapping("/lista")
	public ResponseEntity<Response<String>> excluirListaArtigos(
	   @RequestBody List<Artigo> artigos		
			){
		Response<String> response = service.excluirListaArtigos(artigos);
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	
	
	
	
	
	
	
	
	

}//fecha classe
