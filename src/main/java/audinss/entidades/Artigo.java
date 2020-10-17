package audinss.entidades;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Entity
@Table(name = "artigo")
@Data
public class Artigo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(message = "A descrição do artigo é obrigatória.")
	private String descricao;
	
	@ManyToOne
	@JoinColumn(name = "legislacao_id")
	@JsonIgnoreProperties("artigos")
	private Legislacao legislacao;

}//fecha classe
