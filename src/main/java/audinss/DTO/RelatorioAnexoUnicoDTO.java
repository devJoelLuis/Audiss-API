package audinss.DTO;

import lombok.Data;

@Data
public class RelatorioAnexoUnicoDTO {
	
	private String mesRef;
	private String vrOriginal;
	private String totalMes;
	private String vrOriginalPatronal;
	private String vrOriginalRat;
	private String vrOriginalTerceiros;
	private String totalPatronal;
	private String totalRat;
	private String totalTerceiros;
	private String artigo;
	
	
	public RelatorioAnexoUnicoDTO(String mesRef, String vrOriginal, String totalMes, String vrOriginalPatronal,
			String vrOriginalRat, String vrOriginalTerceiros, String totalPatronal, String totalRat,
			String totalTerceiros, String artigo) {
		super();
		this.mesRef = mesRef;
		this.vrOriginal = vrOriginal;
		this.totalMes = totalMes;
		this.vrOriginalPatronal = vrOriginalPatronal;
		this.vrOriginalRat = vrOriginalRat;
		this.vrOriginalTerceiros = vrOriginalTerceiros;
		this.totalPatronal = totalPatronal;
		this.totalRat = totalRat;
		this.totalTerceiros = totalTerceiros;
		this.artigo = artigo;
	}
	
	
	
	
	
}
