package audinss.entidades;

import lombok.Data;

@Data
public class TotalMesCalculado {
	
	
	private double totalProvento = 0.00;
	private double baseCalculo = 0.00;
	private double valorInss = 0.00;
	private double pagar = 0.00;
	private double recuperar = 0.00;
	private double descontoBase = 0.00;
	
	private double patrimonialPorcentagem = 0.00;
	private double ratPorcentagem = 0.00;
	private double terceirosPorcentagem = 0.00;
	
	private double vrOriginalPatronal = 0.00;
	private double vrOriginalRat = 0.00;
	private double vrOriginalTerceiros = 0.00;
	

}//fecha classe
