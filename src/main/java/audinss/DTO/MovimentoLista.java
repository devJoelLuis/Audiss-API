package audinss.DTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import audinss.entidades.Movimento;

public class MovimentoLista implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private List<Movimento> movimentos = new ArrayList<>();

	public List<Movimento> getMovimentos() {
		return movimentos;
	}

	public void setMovimentos(List<Movimento> movimentos) {
		this.movimentos = movimentos;
	}
	
	
	

}// fecha classe
