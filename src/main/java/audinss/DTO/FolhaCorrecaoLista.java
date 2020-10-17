package audinss.DTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import audinss.entidades.FolhaCorrecao;

public class FolhaCorrecaoLista implements Serializable {
	private static final long serialVersionUID = 1L;
	
	
	List<FolhaCorrecao> fcs = new ArrayList<>();


	public List<FolhaCorrecao> getFcs() {
		return fcs;
	}


	public void setFcs(List<FolhaCorrecao> fcs) {
		this.fcs = fcs;
	}
	
	
	
}
