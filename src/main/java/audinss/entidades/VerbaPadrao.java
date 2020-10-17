package audinss.entidades;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;


@Entity
@Table(name="verba_padrao")
@Data
public class VerbaPadrao implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@NotBlank(message="O código da verba é obrigatório")
	private String codigo;
	
	@NotNull(message="A descrição é obrigatório")
	@Column(length=80)
	private String descricao;
	
	@NotNull(message="O tipo da verba é obrigatório")
	@Column(length=45)
	private String tipo;
	
	@NotNull(message="A fp da verba é obrigatório")
	@Column(length=2)
	private String fp;
	
		
	@Column(length=255)
	private String obs;
	
	@ManyToOne
	@JoinColumn(name="cliente_id")
	private Cliente cliente;
	
	@Column(nullable = false, columnDefinition = "TINYINT(1)")
	private boolean configurada = false;
	

	
}// fecha classe
