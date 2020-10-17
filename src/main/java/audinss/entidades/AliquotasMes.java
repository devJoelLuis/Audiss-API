package audinss.entidades;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;


@Entity
@Table(name="aliquotas_mes", schema="audinss")
@Data
public class AliquotasMes implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@NotNull(message="O mês da aliquota é obrigatório")
	private int mes;
	
	@NotNull(message="O ano da aliquota é obrigatório")
	private int ano;
	
	@Column(name="inss_empresa", precision=3, scale=2)
	private Double inssEmpresa;
	
	@Column(name="inss_terceiros", precision=3, scale=2)
	private Double inssTerceiros;
	
	@Column(name="inss_rat", precision=3, scale=2)
	private Double inssRat;
	
	@Column(name="data_inicio")
	private LocalDate dataInicio;
	
	@Column(name="folha_tipo", length=2)
	private String folhaTipo;
	
	@ManyToOne
	@JoinColumn(name="cliente_id")
	private Cliente cliente;
	
	@Column(length=255)
	private String obs;
	
	@OneToMany(mappedBy = "aliquotasMes")
	@JsonIgnore
	@JsonIgnoreProperties(value = { "aliquotasMes" })
	private List<Movimento> movimentos = new ArrayList<>();
	
	
	

}//fecha classe
