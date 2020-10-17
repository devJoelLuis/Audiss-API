package audinss.entidades;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="taxa_selic", schema="audinss")
@Data
public class TaxaSelic implements Serializable {
	private static final long serialVersionUID = 1L;
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	private int mes;
	private int ano;
	
	@Column(name="taxa", precision=10, scale=2)
	private float taxa;

	
	
	

}//fecha classe
