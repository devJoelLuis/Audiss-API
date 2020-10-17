package audinss.DTO;

import java.io.Serializable;
import java.time.LocalDate;

public class ClienteDashDto implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String nome;
	private String obs;
	private LocalDate inicio;
	private LocalDate fim;
	private LocalDate datServidor;
	
	
	public ClienteDashDto() {
		
	}
	
	
	
	
	
	public ClienteDashDto(Integer id, String nome) {
		this.id = id;
		this.nome = nome;
	}





	public Integer getId() {
		return id;
	}





	public void setId(Integer id) {
		this.id = id;
	}





	public String getNome() {
		return nome;
	}





	public void setNome(String nome) {
		this.nome = nome;
	}





	public String getObs() {
		return obs;
	}





	public void setObs(String obs) {
		this.obs = obs;
	}





	public LocalDate getInicio() {
		return inicio;
	}





	public void setInicio(LocalDate inicio) {
		this.inicio = inicio;
	}





	public LocalDate getFim() {
		return fim;
	}





	public void setFim(LocalDate fim) {
		this.fim = fim;
	}





	public LocalDate getDatServidor() {
		return datServidor;
	}





	public void setDatServidor(LocalDate datServidor) {
		this.datServidor = datServidor;
	}





	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}





	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClienteDashDto other = (ClienteDashDto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
	
}// fecha classe
