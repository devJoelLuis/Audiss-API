package audinss.entidades;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Entity
@Table(name="legislacao")
@Data
public class Legislacao implements Serializable {
	private static final long serialVersionUID = 1L;
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@NotNull(message="a legislação é obrigatória")
	@Column(length=80)
	private String legislacao;
	
	@Column(name="exige_data_inicio", columnDefinition = "TINYINT(1)")
	private boolean exigeDataInicio;
	
	@OneToMany(mappedBy = "legislacao", cascade = CascadeType.PERSIST)
	@JsonIgnoreProperties("legislacao")
	private List<Artigo> artigos = new ArrayList<>();
	
	
	

}//fecha classe
