package audinss.DTO;

import lombok.Data;

@Data
public class RelatorioDemostrativoCreditoDTO {
	
	private String mesRef;
	private String acumulado;
	private String vrOriginal;
	private String jurosMes;
	private String totalMes;
	private String porcPatronal;
	private String porcRat;
	private String porcTerceiros;
	private String vrOriginalPatronal;
	private String vrOriginalRat;
	private String vrOriginalTerceiros;
	private String jurosMesPatronal;
	private String jurosMesRat;
	private String jurosMesTerceiros;
	private String totalPatronal;
	private String totalRat;
	private String totalTerceiros;
	
	
	
	public RelatorioDemostrativoCreditoDTO(String mesRef, String acumulado, String vrOriginal, String jurosMes,
			String totalMes, String porcPatronal, String porcRat, String porcTerceiros, String vrOriginalPatronal,
			String vrOriginalRat, String vrOriginalTerceiros, String jurosMesPatronal, String jurosMesRat,
			String jurosMesTerceiros, String totalPatronal, String totalRat, String totalTerceiros) {
		this.mesRef = mesRef;
		this.acumulado = acumulado;
		this.vrOriginal = vrOriginal;
		this.jurosMes = jurosMes;
		this.totalMes = totalMes;
		this.porcPatronal = porcPatronal;
		this.porcRat = porcRat;
		this.porcTerceiros = porcTerceiros;
		this.vrOriginalPatronal = vrOriginalPatronal;
		this.vrOriginalRat = vrOriginalRat;
		this.vrOriginalTerceiros = vrOriginalTerceiros;
		this.jurosMesPatronal = jurosMesPatronal;
		this.jurosMesRat = jurosMesRat;
		this.jurosMesTerceiros = jurosMesTerceiros;
		this.totalPatronal = totalPatronal;
		this.totalRat = totalRat;
		this.totalTerceiros = totalTerceiros;
	}
	
	
}
