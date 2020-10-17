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
import javax.validation.constraints.NotNull;

import lombok.Data;


@Entity
@Table(name="movimento")
@Data
public class Movimento implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="valor_provento", precision=15, scale=2)
	private Double valorProvento;
	
	@Column(name="base_calc_inss", precision=15, scale=2)
	private Double baseCalcInss;
	
	@Column(name="valor_inss_fp", precision=15, scale=2)
	private Double valorInssFp;
	
	@Column(name="valor_recuperar", precision=15, scale=2)
	private Double valorRecuperar;
	
	@Column(name="valor_pagar", precision=15, scale=2)
	private Double valorPagar;
	
	@ManyToOne
	@JoinColumn(name="aliquotas_mes_id")
	private AliquotasMes aliquotasMes;
	
	
	@Column(length = 2)
	@NotNull(message="a fp é obrigatória")
	private String fp;
	
	@Column(length = 2)
	@NotNull(message="a fc é obrigatória")
	private String fc;
	
	@NotNull(message="o código é obrigatório")
	private String codigo;
	
	@Column(length = 80)
	@NotNull(message="a descrição é obrigatória")
	private String descricao;
	
	@Column(length = 45)
	@NotNull(message="o tipo é obrigatório")
	private String tipo;
	
	
	@Column(length = 80)
	@NotNull(message="a legislação é obrigatória")
	private String legislacao;
	
	
	
	
	
	
	public void alteraMovimetoPorVerbaPadrao(VerbaPadrao vp) {
		this.fp = vp.getFp();
		this.codigo = vp.getCodigo();
		this.descricao = vp.getDescricao();
		this.tipo = vp.getTipo();
	}
	
	
	

}// fecha classe
