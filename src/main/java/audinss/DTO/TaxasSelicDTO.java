package audinss.DTO;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class TaxasSelicDTO {
	
	private int ano;
	private List<Float> taxas = new ArrayList<>();

}//fecha classe
