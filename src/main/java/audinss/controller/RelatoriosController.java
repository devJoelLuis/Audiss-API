package audinss.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import audinss.response.Response;
import audinss.servico.RelatorioService;

@RestController
@RequestMapping("/relatorios")
public class RelatoriosController {
	
	@Autowired
	private RelatorioService service;
	
	
	
	@GetMapping("/demonstrativo-anual")
	public ResponseEntity<?> demostrativoCreditoAnual(
			@RequestParam(value="legislacao", defaultValue="") String legislacao,
			@RequestParam(value="clienteid", defaultValue="0") int clienteid,
			@RequestParam(value="ano", defaultValue="0") int ano,
			@RequestParam(value="fimAcumulado") String dataFimSelic
			) {
		Response<byte[]> response = service.relatorioCreditoDemostrativoTema163(dataFimSelic, ano, clienteid, legislacao);
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
				.body(response.getDados());
	}
	
	
	@GetMapping("/anexo-unico")
	public ResponseEntity<?> anexoUnico(
			@RequestParam(value="legislacao", defaultValue="") String legislacao,
			@RequestParam(value="clienteid", defaultValue="0") int clienteid,
			@RequestParam(value="ano", defaultValue="0") int ano,
			@RequestParam(value="fimAcumulado") String dataFimSelic
			) {
		Response<byte[]> response = service.relatorioAnexoUnico(dataFimSelic, ano, clienteid, legislacao);
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
				.body(response.getDados());
	}
	
	
	
	
	
	

}//fecha classe
