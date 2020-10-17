package audinss.entidades;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;



@Entity
@Table(name="cliente")
@Data
public class Cliente implements Serializable {
	private static final long serialVersionUID = 1L;
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@NotNull(message="O nome do cliente é obrigatório")
	@Column(length=80, unique=true)
	private String nome;
	
	@NotNull(message="O cnpj do cliente é obrigatório")
	@Column(length=45, unique=true)
	private String cnpj;
	
	@Column(length=80)
	private String email;
	
	@Column(length=45)
	private String telefone1;
	
	@Column(length=45)
	private String telefone2;
	
	@Column(length=80)
	private String rua;
	
	@Column(length=80)
	private String bairro;
	
	@Column(length=80)
	private String cidade;
	
	@Column(length=45)
	private String cep;
	
	@Column(length=80)
	private String estado;
	
	@Column(length=80)
	private String complemento;
	
	private String logotipo;
	
	@Column(length=255)
	private String obs;
	
	private LocalDate inicio;
	private LocalDate fim;
	
	

} // fecha classe
