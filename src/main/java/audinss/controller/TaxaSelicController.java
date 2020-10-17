package audinss.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import audinss.DTO.TaxasSelicDTO;
import audinss.response.Response;
import audinss.servico.TaxaService;

@RestController
@RequestMapping("/taxas-selic")
public class TaxaSelicController {
	
	@Autowired
	private TaxaService service;
	
	
	@GetMapping
	public ResponseEntity<Response<List<TaxasSelicDTO>>> getAllDto() {
		Response<List<TaxasSelicDTO>> response = service.getAllTaxasDTO();
		if (response.getErros().size() > 0) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	
	

}//fecha classe
