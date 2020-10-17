package audinss.DTO;

public class VerbaRelatorioDTO {
	
	private String codigo;
	private String descricao;
	private String fc;
	private String fp;
	private String valorProvento;
	private String baseCalculo;
	private String valorInss;
	private String recuperar;
	private String pagar;
	
	
	
	
	
	
	
	
	
	
	public VerbaRelatorioDTO() {
	}



	public VerbaRelatorioDTO(String codigo, String descricao, String fc, String fp, String valorProvento,
			String baseCalculo, String valorInss, String recuperar, String pagar) {
		this.codigo = codigo;
		this.descricao = descricao;
		this.fc = fc;
		this.fp = fp;
		this.valorProvento = valorProvento;
		this.baseCalculo = baseCalculo;
		this.valorInss = valorInss;
		this.recuperar = recuperar;
		this.pagar = pagar;
	}
	
	
	
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getFc() {
		return fc;
	}
	public void setFc(String fc) {
		this.fc = fc;
	}
	public String getFp() {
		return fp;
	}
	public void setFp(String fp) {
		this.fp = fp;
	}
	public String getValorProvento() {
		return valorProvento;
	}
	public void setValorProvento(String valorProvento) {
		this.valorProvento = valorProvento;
	}
	public String getBaseCalculo() {
		return baseCalculo;
	}
	public void setBaseCalculo(String baseCalculo) {
		this.baseCalculo = baseCalculo;
	}
	public String getValorInss() {
		return valorInss;
	}
	public void setValorInss(String valorInss) {
		this.valorInss = valorInss;
	}
	public String getRecuperar() {
		return recuperar;
	}
	public void setRecuperar(String recuperar) {
		this.recuperar = recuperar;
	}
	public String getPagar() {
		return pagar;
	}
	public void setPagar(String pagar) {
		this.pagar = pagar;
	}
	
	

}
