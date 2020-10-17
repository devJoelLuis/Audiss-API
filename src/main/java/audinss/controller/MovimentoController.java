package audinss.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

import audinss.DTO.AliquotaDTO;
import audinss.DTO.MovimentoLista;
import audinss.entidades.Movimento;
import audinss.response.Response;
import audinss.servico.MovimentoService;

@RestController
@RequestMapping("/movimentos")
public class MovimentoController {
	
	
	@Autowired
	private MovimentoService service;
	
	
	
	@PutMapping
	public ResponseEntity<Response<Movimento>> alterar(
		@RequestBody Movimento m	
			) {
		Response<Movimento> response = service.alterar(m);
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		} 
		return  ResponseEntity.ok(response);
		
	}
	
	
	
	@GetMapping("/cliente")
	public ResponseEntity<Response<List<Movimento>>> getMovimentoLegislacaoClienteAnoMes(
			@RequestParam(value="ano", defaultValue="0") int ano,
			@RequestParam(value="mes", defaultValue="0") int mes,
			@RequestParam(value="legislacao", defaultValue="") String leg,
			@RequestParam(value="idcliente", defaultValue="0") int idcliente
			) {
		Response<List<Movimento>> response = service.getMovimentoLegislacaoClienteAnoMes(ano, mes, leg, idcliente);
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	
	
	@GetMapping("/cliente/fp")
	public ResponseEntity<Response<List<Movimento>>> getMovimentoFpClienteAnoMes(
			@RequestParam(value="ano", defaultValue="0") int ano,
			@RequestParam(value="mes", defaultValue="0") int mes,
			@RequestParam(value="idcliente", defaultValue="0") int idcliente
			) {
		Response<List<Movimento>> response = service.getMovimentoFpClienteAnoMes(ano, mes, idcliente);
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	
	@GetMapping("/aliquota/{id}")
	public ResponseEntity<Response<List<Movimento>>> getMovimentoAliquotaId(
			@PathVariable int id
			) {
		Response<List<Movimento>> response = service.getMovimentoAliquotaId(id);
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	
	@GetMapping("/relatoriofc")
	public ResponseEntity<byte[]> relatorioFc(
			@RequestParam(value="ano", defaultValue="0") int ano,
			@RequestParam(value="mes", defaultValue="0") int mes,
			@RequestParam(value="idaliquota", defaultValue="0") int idaliquota,
			@RequestParam(value="legislacao", defaultValue="") String legislacao
			) {
		Response<byte[]> response = service.relatoriofcPorLegislacao(ano, mes, idaliquota, legislacao);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
				.body(response.getDados());
	}
	
	
	
	
	// get FC pelo id da aliquota e legislação, mes e ano
	@GetMapping("/legislacao")
	public ResponseEntity<Response<List<Movimento>>> getMovimentoLegislacaoAnoMesAliquotaId(
			@RequestParam(value="ano", defaultValue="0") int ano,
			@RequestParam(value="mes", defaultValue="0") int mes,
			@RequestParam(value="idaliquota", defaultValue="0") int idaliquota,
			@RequestParam(value="legislacao", defaultValue="") String legislacao
			) {
		Response<List<Movimento>> response = service.getMovimentoLegislacaoAnoMesAliquotaId(ano, mes, idaliquota, legislacao);
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	
	
	
	// get FC pelo id da aliquota e legislação, mes e ano
	@GetMapping("/legislacao/cliente")
	public ResponseEntity<Response<List<AliquotaDTO>>> getMovimentoLegislacaoAnoCliente(
				@RequestParam(value="clienteid", defaultValue="0") int clienteid,
				@RequestParam(value="ano", defaultValue="0") int ano,
				@RequestParam(value="legislacao", defaultValue="") String legislacao
				) {
			Response<List<AliquotaDTO>> response = service.getMovimentoLegislacaoAnoClienteId(ano, clienteid, legislacao);
			if (response.getErros().size() > 0) {
				return ResponseEntity.badRequest().body(response);
			}
			return ResponseEntity.ok(response);
	}
		
	
	
	
	
	@PostMapping("/lista")
	public ResponseEntity<Response<List<Movimento>>> cadastraAll(
			@RequestBody MovimentoLista mvl
			) {
		Response<List<Movimento>> response = service.cadastraAll(mvl);
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	
	
	
	
	@PutMapping("/lista")
	public ResponseEntity<Response<List<Movimento>>> alterarAll(
			@RequestBody MovimentoLista mvl
			) {
		Response<List<Movimento>> response = service.alterarAll(mvl);
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	
	
	
	
	@DeleteMapping("/lista/{idAliquota}")
	public ResponseEntity<Response<String>> deleteAll(
			@PathVariable int idAliquota
			) {
		Response<String> response = service.excluirAll(idAliquota);
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	
	@GetMapping("/anos")
	public ResponseEntity<Response<List<Integer>>> buscarAnosComMovimento(
			@RequestParam(value="clienteid", defaultValue="0") int clienteid
			){
		Response<List<Integer>> response = service.buscarAnosMovimento(clienteid);
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/anosfp")
	public ResponseEntity<Response<List<Integer>>> buscarAnosComMovimentoFp(
			@RequestParam(value="clienteid", defaultValue="0") int clienteid
			){
		Response<List<Integer>> response = service.buscarAnosMovimentoFp(clienteid);
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	/*
		@GetMapping("/legislacoes-anexo-unico")
		public ResponseEntity<Response<List<String>>> buscarLegislacoesDoMovimentosComAnexoUnico(){
			Response<List<String>> response = service.buscarLegislacaoMovimentosComArtigo();
			if (response.getErros().size() > 0) {
				return ResponseEntity.badRequest().body(response);
			}
			return ResponseEntity.ok(response);
		}
	
	*/
	
	@GetMapping("/legislacoes")
	public ResponseEntity<Response<List<String>>> buscarLegislacoesDoMovimentos(
		  @RequestParam(value="idcliente", defaultValue = "0") int idcliente	
			){
		Response<List<String>> response = service.buscarLegislacaoMovimentos(idcliente);
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	
	
	

}//fecha classe
