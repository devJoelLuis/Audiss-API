package audinss.entidades;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;


@Entity
@Table(name="usuario")
@Data
public class Usuario implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@NotNull(message="O e-mail do usuário é obrigatório")
	@Column(unique=true, length=80)
	private String email;
	
	@NotNull(message="O nome do usuário é obrigatório")
	@Column(unique=true, length=80)
	private String nome;
	
	@Column(length=255)
	private String senha;
	
	@Column(length=255)
	private String obs;
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="usuario_permissao", joinColumns = @JoinColumn(name="usuario_id"),
	inverseJoinColumns = @JoinColumn(name = "permissao_id"))
	private List<Permissao> permissoes = new ArrayList<>();
	
	@ManyToMany
	@JoinTable(name="usuario_cliente", joinColumns = @JoinColumn(name="usuario_id"),
	inverseJoinColumns = @JoinColumn(name = "cliente_id"))
	private List<Cliente> clientes = new ArrayList<>();
	
	
	
	
	

	@JsonIgnore
	public String getSenha() {
		return senha;
	}
	@JsonProperty
	public void setSenha(String senha) {
		this.senha = senha;
	}
	

}//fecha classe
