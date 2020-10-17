package audinss.entidades;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;




@Entity
@Table(name="folha_correcao")
@Data
public class FolhaCorrecao implements Serializable {
	private static final long serialVersionUID = 1L;
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@Column(length=2)
	@NotNull(message="A fc é obrigatória")
	private String fc;
	
	@ManyToOne(cascade=CascadeType.DETACH)
	@JoinColumn(name="verba_padrao_id")
	private VerbaPadrao verbaPadrao;
	
	@ManyToOne
	@JoinColumn(name="legislacao_id")
	private Legislacao legislacao;
	
	@Column(name="data_inicio")
	private LocalDate dataInicio;
	
	@ManyToOne
	@JoinColumn(name="cliente_id")
	private Cliente cliente;
	
	@Column(nullable = false, columnDefinition = "TINYINT(1)")
	private boolean configurado = false;
	


}//fecha classe
